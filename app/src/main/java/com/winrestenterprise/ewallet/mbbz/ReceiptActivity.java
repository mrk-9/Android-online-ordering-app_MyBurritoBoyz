package com.winrestenterprise.ewallet.mbbz;

import com.winrestenterprise.ewallet.eWallet;
import com.winrestenterprise.ewallet.mbbz.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.content.Intent;

public class ReceiptActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        
        Intent intent = getIntent();
		String receiptText = intent.getStringExtra(eWallet.RECEIPT_TEXT);
		Button titleBar =  (Button) findViewById(R.id.button_titleBar);
		//btnTitleBar.setText(String.format("%s - Receipts",cardName));
		titleBar.setText("Receipt");
		
		WebView mWebView = (WebView) findViewById(R.id.webview);
//        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);
        //webSettings.setDisplayZoomControls(false);
        mWebView.loadDataWithBaseURL("not_needed", String.format("<html><head></head><body><pre><strong>%s</strong></pre></body></html>",receiptText), "text/html", "utf-8", "not_needed"); 
    }

	public void button_back_onClick(View view){
		this.finish();
	}
	
	public void button_help_onClick(View view){
		eWallet.ShowHelpForActivity(this);
	}
}
