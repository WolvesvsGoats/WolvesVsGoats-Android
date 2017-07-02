/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 * 
 * http://sourceforge.net/projects/zbar/postdownload?source=dlp
 * http://zbar.sourceforge.net/api/classzbar_1_1Image.html
 * http://zbar.sourceforge.net/api/zbar_8h.html
 */
package com.wolvesvsgoats.objectives;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wolvesvsgoats.MainActivity;
import com.wolvesvsgoats.R;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

/* Import ZBar Class files */

public class CameraActivity extends Activity {
    static {
        System.loadLibrary("iconv");
    }

    TextView scanText;
    Button scanButton;
    ImageScanner scanner;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    barcodeScanned = true;

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("qrcode", sym.getData());
                    setResult(1, i);
                    finish();
                }
            }
        }
    };
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };
    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

		/* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        scanButton = (Button) findViewById(R.id.ScanButton);

        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (barcodeScanned) {
                    barcodeScanned = false;
                    scanText.setText("Scanning...");
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    previewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        setResult(-1, i);
        finish();
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
}
