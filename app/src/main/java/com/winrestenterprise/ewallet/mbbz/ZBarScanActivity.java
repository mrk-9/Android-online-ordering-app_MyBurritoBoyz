/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.winrestenterprise.ewallet.mbbz;

import com.winrestenterprise.ewallet.mbbz.R;

import com.winrestenterprise.ewallet.CameraPreview;
import com.winrestenterprise.ewallet.eWallet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;

import android.widget.TextView;
/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

public class ZBarScanActivity extends Activity
{
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    TextView scanText;

    ImageScanner scanner;

   // private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    } 

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_zbar_scan);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        //setupActionBar();
        
        this.setTitle("QR Code Scanner");
        
        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        scanText = (TextView)findViewById(R.id.scanText);
    }

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    private void setupActionBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//    }
    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	try
//    	{
//    		switch (item.getItemId()) {
//    			case android.R.id.home:
//    				// This ID represents the Home or Up button. In the case of this
//    				// activity, the Up button is shown. Use NavUtils to allow users
//    				// to navigate up one level in the application structure. For
//    				// more details, see the Navigation pattern on Android Design:
//    				//
//    				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
//    				//
//    				//NavUtils.navigateUpFromSameTask(this);
//    				this.finish();
//    				return true;
//    		}
//    	}
//    	catch(Exception e)
//    	{
//    		eWallet.ShowDialog(this, "Error!", e.toString());
//    	}
//        return super.onOptionsItemSelected(item);
//    }
    
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
            public void run() {
                if (previewing)
                    mCamera.autoFocus(autoFocusCB);
            }
        };

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
                    	String scanResult = sym.getData();
                        scanText.setText("barcode result " + scanResult);
                        ZBarScanActivity.this.onScanResult(scanResult);
                    }
                }
            }
        };
    
    private void onScanResult(String scanResult)
    {
    	Intent intent = new Intent();
		intent.putExtra(eWallet.SCAN_RESULT, scanResult);
		this.setResult(RESULT_OK,intent);
		this.finish();
    }
    
    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
            }
        };
}
