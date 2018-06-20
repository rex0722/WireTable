package com.study.application.scanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.study.application.R;
import com.study.application.animatedView.MorphView;
import com.study.application.ui.BorrowReturnActivity;
import com.study.application.ui.WelcomeActivity;

import java.util.Collections;
import java.util.List;

public class ScanQrCodeActivity extends AppCompatActivity {

    private final String TAG = "ScanQrCodeActivity";

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private Handler backgroundHandler;
    private Handler scanHandler;
    private HandlerThread backgroundThread;
    private Runnable runnable;
    private MorphView morphView;
    private String scannedTarget;
    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//            Log.d(TAG, "onSurfaceTextureAvailable");
            //open your camera here
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//            Log.d(TAG, "onSurfaceTextureSizeChanged");
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//            Log.d(TAG, "onSurfaceTextureDestroyed");
            cameraDevice.close();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            Log.d(TAG, "onSurfaceTextureUpdated");
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            //This is called when the camera is open
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
//            Log.d(TAG, "close in onDisconnected");
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
//            Log.d(TAG, "close in onError");
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_scan_qr_code);

        getBundleExtra();
        initView();
        initMorphView();
        initQrCodeScanner();
    }

    private void getBundleExtra() {
        Bundle bundle = getIntent().getExtras();
        scannedTarget = bundle.getString("TARGET");
    }

    private void initView() {
        textureView = findViewById(R.id.textureView);
        morphView = findViewById(R.id.morphView);

        textureView.setSurfaceTextureListener(textureListener);
        morphView.setOnClickListener(view -> this.finish());
    }

    private void initQrCodeScanner() {
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

        scanHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (textureView.isAvailable()) {
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(textureView.getBitmap());
                    Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                            .addOnSuccessListener(firebaseVisionBarcodes -> {
                                for (FirebaseVisionBarcode barcode : firebaseVisionBarcodes) {
                                    Log.d(TAG, "for loop!!!");
                                    int valueType = barcode.getValueType();
                                    // See API reference for complete list of supported types
                                    switch (valueType) {
                                        case FirebaseVisionBarcode.TYPE_TEXT:
                                            morphView.performAnimation(R.drawable.vd_scanned);

                                            String text = barcode.getDisplayValue();
                                            Bundle bundle = new Bundle();
                                            Intent intent = new Intent();

                                            bundle.putString(scannedTarget, text);
                                            Log.d(TAG, "scannedTarget : " + scannedTarget);
                                            Log.d(TAG, "text : " + text);
                                            intent.putExtras(bundle);
                                            if (scannedTarget.equals("USER")) {
                                                intent.setClass(ScanQrCodeActivity.this, WelcomeActivity.class);
                                                startActivity(intent);
                                            } else if (scannedTarget.equals("ITEM")) {
                                                intent.setClass(ScanQrCodeActivity.this, BorrowReturnActivity.class);
                                                setResult(BorrowReturnActivity.REQUEST_CODE, intent);
                                            }
                                            ScanQrCodeActivity.this.finish();
                                            break;
                                        default:
                                            String def = barcode.getRawValue();
                                            Log.d(TAG, "def : " + def);
                                            break;
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Log.d(TAG, "QRcode decode : Fail!!!"));
                }
                super.handleMessage(msg);
            }
        };
        runnable = new Runnable() {
            @Override
            public void run() {
                scanHandler.sendEmptyMessage(1);
                scanHandler.postDelayed(this, 750);
            }
        };
        scanHandler.postDelayed(runnable, 0);
    }

    private void initMorphView() {
        morphView.setCurrentId(R.drawable.vd_scanning);
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("Camera Background");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            assert surfaceTexture != null;
            surfaceTexture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(surfaceTexture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(ScanQrCodeActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;

            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScanQrCodeActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(ScanQrCodeActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        scanHandler.removeCallbacks(runnable);
        stopBackgroundThread();
        super.onPause();
    }

}
