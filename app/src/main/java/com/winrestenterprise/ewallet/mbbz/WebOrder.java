package com.winrestenterprise.ewallet.mbbz;

import org.apache.http.util.EncodingUtils;

import com.winrestenterprise.ewallet.CallSoapThread;
import com.winrestenterprise.ewallet.eWallet;
import com.winrestenterprise.ewallet.mbbz.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebOrder extends Activity {

	private class OnlineOrderJavascriptInterface {

		private boolean isProcessed = false;
		private String cardId,logonId;
		private String webSiteVersion = null;
		public OnlineOrderJavascriptInterface(String logonId, String cardId) {
			this.logonId = logonId;
			this.cardId = cardId;
		}
		
	    @JavascriptInterface
	    public String GetMobileOrderClientId() { 
	    	return String.format("%s,%s",this.logonId,this.cardId); 
	    }
	    
	    @JavascriptInterface
	    public void SetProssedResult(){
	    	this.isProcessed = true;
	    }
	    
	    @JavascriptInterface
	    public void SetWebSiteVersion(String version){
	    	this.webSiteVersion = version;
	    }
	    
	    public boolean IsProcessed(){
	    	return this.isProcessed;
	    }

	    public String GetWebSiteVersion(){
	    	return this.webSiteVersion;
	    }
	}

	private static class WebViewClientEx extends WebViewClient {
		
		String message = "Page is loading..";
	ProgressDialog pd = null;
	Context context = null;
	WebView view = null;
	public WebViewClientEx() {
	}

	public WebViewClientEx(Context context, String message) {
		super();
		this.context = context;
		this.message = message;
	}
	
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {    
        view.loadUrl(url);    
         return false;
    }

	@Override
	  public void onPageStarted(WebView view, String url, Bitmap favicon) {
	   super.onPageStarted(view, url, favicon);
	   this.view = view;
	   pd = new ProgressDialog(this.context);
	   pd.setTitle("Please wait");
	   pd.setMessage(this.message);
	   pd.show();
	  }

	  @Override
	  public void onPageFinished(WebView view, String url) {
	   super.onPageFinished(view, url);
	   pd.dismiss();
	   this.view.setVisibility(View.VISIBLE);
	  }
	}
	
/*
	    private class LoadWebPageASYNC extends AsyncTask<String, Void, String> { 

        @Override 
        protected String doInBackground(String... urls) { 
            WebOrder.this.loadWebView();
            return null; 
        } 

        @Override 
        protected void onPostExecute(String result) { 
        	
        } 
    } 
	*/
	
	private WebView mWebView;
	private OnlineOrderJavascriptInterface onlineOrderJavascriptInterface;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_order);

        Intent intent = getIntent();
		String cardId = intent.getStringExtra(eWallet.CARDID);
		String webOrderSitePath = intent.getStringExtra(eWallet.WEB_ORDER_SITE_PATH); 
		
		this.onlineOrderJavascriptInterface = new OnlineOrderJavascriptInterface(eWallet.GetLogonId(this),cardId);
		
		this.loadWebView(webOrderSitePath);
    }
    
    private void loadWebView(String webOrderSitePath)
    {
    	this.mWebView = (WebView) findViewById(R.id.webview);
       // WebView mWebView = new WebView(this);
    	this.mWebView.setVisibility(View.INVISIBLE);
        WebSettings webSettings = this.mWebView.getSettings();
        //webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setLoadWithOverviewMode(true);
        //webSettings.setAllowContentAccess(true);
        //webSettings.setUseWideViewPort(false);
        
        webSettings.setSupportZoom(false); 
        
        String needToClearCache = eWallet.ReadKeyValueFromDisk(this, eWallet.WEB_ORDER_NEED_TO_CLEAR_CACHE);
        if(needToClearCache != null && needToClearCache.equals("YES")){
        	this.mWebView.clearCache(true);
        	eWallet.SaveKeyValueToDisk(this, eWallet.WEB_ORDER_NEED_TO_CLEAR_CACHE, "NO");
        }
        	

        //mWebView.clearHistory();
        //mWebView.clearFormData();
        //mWebView.setWebViewClient(new WebViewClient());
        
        this.mWebView.setWebViewClient(new WebViewClientEx(this,"Connecting to Online Store..."));
        
        WebChromeClient webChromeClient = new WebChromeClient(){
            public void onCloseWindow(WebView w){
                super.onCloseWindow(w);
                WebOrder.this.finish();
            }
        };
        
        this.mWebView.setWebChromeClient(webChromeClient);

        
        //mWebView.loadUrl("http://www.nhl.com/ice/m_home.htm#&intcmpid=ftr-mbl");
        //mWebView.loadUrl("http://auction.nhl.com/iSynApp/showHomePage.action?sid=1100803&navid=auctions-nav-homepage");
        this.mWebView.addJavascriptInterface(this.onlineOrderJavascriptInterface , "WinRestEWallet");

        //mWebView.loadUrl("http://onlineorder.abspos.com:466/OnlineStore/m_main.aspx");
        //mWebView.loadUrl("http://192.168.0.11/OnlineStore/m_Main.aspx");
        String webRootUrl = CallSoapThread.getWebRootUrl();
        //String url = webRootUrl.substring(0, webRootUrl.lastIndexOf("/")) + "/OnlineStore/m_Main.aspx";
        //String url = webRootUrl.substring(0, webRootUrl.lastIndexOf("/")) + webOrderSitePath;
        String url = webRootUrl + webOrderSitePath;
        //this.mWebView.loadUrl(url);
        this.mWebView.postUrl(url, 
        		 EncodingUtils.getBytes("param1=value1&param2=value2","BASE64"));
        //mWebView.loadUrl("http://192.168.0.11/OnlineStore/index.html");
    }
    
    @Override
    public void finish () {
    	
    	String webSiteVer = this.onlineOrderJavascriptInterface.GetWebSiteVersion();
    	if(webSiteVer != null)
    	{
    		String lastWebSiteVer = eWallet.ReadKeyValueFromDisk(this, eWallet.WEB_ORDER_WEBSITE_VER);
    		if(!webSiteVer.equals(lastWebSiteVer)){
    			eWallet.SaveKeyValueToDisk(this, eWallet.WEB_ORDER_WEBSITE_VER, webSiteVer);
    			eWallet.SaveKeyValueToDisk(this, eWallet.WEB_ORDER_NEED_TO_CLEAR_CACHE, "YES");
    		}
    	}
    	
        if(this.onlineOrderJavascriptInterface.IsProcessed()){
        	Intent intent = new Intent();
        	intent.putExtra(eWallet.WEB_ORDER_RESULT, eWallet.WEB_ORDER_RESULT__ORDER_PROCESSED);
        	this.setResult(RESULT_OK,intent);
        }
    	super.finish();
    }
}
