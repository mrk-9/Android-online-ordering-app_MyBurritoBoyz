package com.winrestenterprise.ewallet;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

@SuppressLint("DefaultLocale")
public class eWallet {

	public final static String EXTRA_MESSAGE = "com.android.swallet.MESSAGE";
	public final static String CARDLIST = "com.android.swallet.CARDLIST";
	public final static String ACCOUNT_STATE = "com.android.swallet.ACCOUNT_STATE";
	public final static String RELOAD = "com.android.swallet.RELOAD";

	public final static String APP = "com.android.swallet.APP";
	public final static String EMAIL = "com.android.swallet.EMAIL";
	public final static String ACTIVATION_MODE = "com.android.swallet.ACTIVATION_MODE";
	public final static String ACTIVATION_ID = "com.android.swallet.ACTIVATION_ID";
	public final static String ACTIVATION_ID_EXPIRE = "com.android.swallet.ACTIVATION_ID_EXPIRE";
	public final static String LOGON_ID = "com.android.swallet.LOGON_ID";
	public final static String CARDID = "com.android.swallet.CARDID";
	public final static String CARD_NAME = "com.android.swallet.CARD_NAME";
	public final static String RECEIPT_TEXT = "com.android.swallet.RECEIPT_TEXT";
	public final static String RECEIPT_LIST = "com.android.swallet.RECEIPT_LIST";
	public final static String SCAN_RESULT = "com.android.swallet.SCAN_RESULT";
	public final static String WEB_ORDER_SITE_PATH = "com.android.swallet.WEB_ORDER_SITE_PATH";
	public final static String WEB_ORDER_SITE_PATH__REGULAR_MENU = "/m_Main.aspx";
	public final static String WEB_ORDER_SITE_PATH__GIFT_CARD_SALE = "/m_Main.aspx?ItemType=WinAuthEGiftcardSale";
	public final static String WEB_ORDER_SITE_PATH__GIFT_CARD_RELOAD = "/m_Main.aspx?ItemType=WinAuthGiftcardReload";
	public final static String WEB_ORDER_RESULT = "com.android.swallet.WEB_ORDER_RESULT";
	public final static String WEB_ORDER_RESULT__ORDER_PROCESSED = "com.android.swallet.WEB_ORDER_RESULT__ORDER_PROCESSED";
	public final static String WEB_ORDER_WEBSITE_VER = "com.android.swallet.WEB_ORDER_WEBSITE_VER";
	public final static String WEB_ORDER_NEED_TO_CLEAR_CACHE = "com.android.swallet.WEB_ORDER_NEED_TO_CLEAR_CACHE";

	public static final int REQUEST_CODE__ACTIVATION_STEP1 = 1;
	public static final int REQUEST_CODE__ACTIVATION_STEP2 = 2;
	public static final int REQUEST_CODE__EDIT_PROFILE = 3;
	public static final int REQUEST_CODE__ADD_CARD = 4;
	public static final int REQUEST_CODE__SHOW_CARD = 5;
	public static final int REQUEST_CODE__ZBAR_SCAN = 6;
	public static final int REQUEST_CODE__EDIT_CARD = 7;
	public static final int REQUEST_CODE__SHOW_RECEIPTLIST = 8;
	public static final int REQUEST_CODE__SHOW_RECEIPT = 9;
	public static final int REQUEST_CODE__WEB_ORDER = 10;

	public static final long ONE_MINUTE_IN_MILLIS=60000;

	public static boolean Debug = false;

	public static void CheckDebugCfg() {

		String[] paths = new String[]{"/storage/emulated/0/files","/storage/sdcard0/files"};//this.getFilesDir()

		File file = null;
		for(String path : paths)
		{
			File fileToCheck = new File(path, "winrest.ewallet.debug.cfg");
			if(fileToCheck.exists())
			{
				file = fileToCheck;
				break;
			}
		}

		if(file != null)
		{
			try {
				eWallet.Debug = true;
				FileInputStream fstream = new FileInputStream(file);
				// Get the object of DataInputStream
				DataInputStream dataInputStream = new DataInputStream(fstream);
				InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String line = bufferedReader.readLine();
				while (line != null) {
					String pair = line;
					String[] pairStrs = pair.split("=");
					if (pairStrs.length == 2)
					{
						if(eWallet.IsEqualStrings(pairStrs[0],"web_service_url"))
						{
							CallSoapThread.SetDebugWebServiceUrl(pairStrs[1]);
						}
					}
					line = bufferedReader.readLine();
				}
				bufferedReader.close();
				inputStreamReader.close();
				dataInputStream.close();
				fstream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			eWallet.Debug = false;
			CallSoapThread.SetDebugWebServiceUrl(null);
		}
	}

	public static String ReadKeyValueFromDisk(Context context, String key)
	{
		Context appContext = context.getApplicationContext();
		SharedPreferences sharedPref = appContext.getSharedPreferences(eWallet.APP, Context.MODE_PRIVATE);
		return sharedPref.getString(key,"");
	}

	public static void SaveKeyValueToDisk(Context context, String key, String val)
	{
		Context appContext = context.getApplicationContext();
		SharedPreferences sharedPref = appContext.getSharedPreferences(eWallet.APP, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, val);
		editor.commit();
	}

	private final static ReentrantLock  _lock_logonId = new ReentrantLock ();
	private static String _logonId = null;
	public static String GetLogonId(Context context)
	{
		String rslt = null;
		eWallet._lock_logonId.lock();
		if(eWallet.StringIsNullOrEmpty(eWallet._logonId))
			eWallet._logonId = eWallet.ReadKeyValueFromDisk(context,eWallet.LOGON_ID);
		rslt = eWallet._logonId;
		eWallet._lock_logonId.unlock();
		return rslt;
	}

	public static void SetLogonId(Context context,String logonId)
	{
		eWallet._lock_logonId.lock();
		eWallet._logonId = logonId;
		eWallet.SaveKeyValueToDisk(context, eWallet.LOGON_ID, logonId);
		eWallet._lock_logonId.unlock();
	}

	public static void ShowSplash(Context context)
	{
		//		WebView webView = new WebView(context);
		//		webView.setWebChromeClient(new WebChromeClient() {
		//			   public void onReceivedTitle(WebView view, String title) {
		//			     if(IsEqualStrings(title,"The page cannot be found"))
		//			     {
		//			    	 view.loadDataWithBaseURL("not_needed", 
		//			    			 String.format("<html><head></head><body><pre><strong>Page not found<br/>%s</strong></pre></body></html>",
		//			    					 view.getUrl()), 
		//			    					 "text/html", 
		//			    					 "utf-8", 
		//			    					 "not_needed"); 
		//			     }
		//			   }
		//			 });
		//		
		//        WebSettings webSettings = webView.getSettings();
		//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		//webSettings.setJavaScriptEnabled(true);
		//webSettings.setDomStorageEnabled(true);  
		//webSettings.setSupportZoom(true);
		//webSettings.setBuiltInZoomControls(true);
		//webSettings.setDisplayZoomControls(false);   

		//AlertDialog.Builder builder = new AlertDialog.Builder(context);

		//builder.setTitle("Help");
		//		builder.setCancelable(true);
		//		builder.setInverseBackgroundForced(false);
		//		builder.setCustomTitle(webView);
		//		builder.setNegativeButton("Close", null);
		//builder.setView(webView);


		//Window window = dlg.getWindow();

		//        Window window = dlg.getWindow();
		//        View view = window.getDecorView();
		//        view.setBackgroundColor(0x00efefef);
		//        view.setLayoutParams(params)
		//        final int topPanelId = view.getResources().getIdentifier( "topPanel", "id", "android" );
		//        LinearLayout topPanel = (LinearLayout) view.findViewById(topPanelId);
		//        topPanel.setVisibility(View.GONE);

		//		Dialog dlg = new Dialog(context);
		//		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1,-2);
		//		
		//		TextView tt = new TextView(context);
		//		tt.setText("no title no title");
		//		tt.setBackgroundColor(0xff1eef22);
		//		tt.setTextColor(0xfff00000);
		//		
		//		TextView tv1 = new TextView(context);
		//		tv1.setText("no title no title no title no title no title no title no title no title no title no title no title no title");
		//		tv1.setBackgroundColor(0xffefefef);
		//		tv1.setTextColor(0xff000000);
		//		
		//		LinearLayout linearLayout  = new LinearLayout(context);
		//		linearLayout.setOrientation(LinearLayout.VERTICAL);
		//		linearLayout.addView(tt);
		//		linearLayout.addView(tv1);
		//		
		//		dlg.setContentView(linearLayout);
		//		dlg.show();
		//		
		//		AlertDialog.Builder builder;
		//		AlertDialog alertDialog;

		//LayoutInflater inflater = (LayoutInflater)
		//context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
		//View layout = inflater.inflate(R.layout.activity_flash_screen,null);
		// (ViewGroup) findViewById(R.id.layout_root));
		//FlashScreenActivity.InitializeLayout(layout);

		//		TextView text = (TextView) layout.findViewById(R.id.textView1);
		//		text.setText("Hello, this is a custom dialog!");

		//		WebView webView = (WebView) layout.findViewById(R.id.webView1);
		//		webView.setWebChromeClient(new WebChromeClient() {
		//			   public void onReceivedTitle(WebView view, String title) {
		//			     if(IsEqualStrings(title,"The page cannot be found"))
		//			     {
		//			    	 view.loadDataWithBaseURL("not_needed", 
		//			    			 String.format("<html><head></head><body><pre><strong>Page not found<br/>%s</strong></pre></body></html>",
		//			    					 view.getUrl()), 
		//			    					 "text/html", 
		//			    					 "utf-8", 
		//			    					 "not_needed"); 
		//			     }
		//			   }
		//			 });
		//		webView.loadUrl(url); 
		//		ImageView image = (ImageView) layout.findViewById(R.id.image);
		//		image.setImageResource(R.drawable.android);

		//AlertDialog.Builder builder = new AlertDialog.Builder(context);
		//builder.setView(layout);

//		AlertDialog alertDialog = builder.create();
//		alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		alertDialog.setView(layout, 0, 0, 0, 0);
//		alertDialog.show();
	}

	private static void ShowHelp(Context context, String url)
	{
		WebView webView = new WebView(context);
		webView.setWebChromeClient(new WebChromeClient() {
			public void onReceivedTitle(WebView view, String title) {
				if(IsEqualStrings(title,"The page cannot be found"))
				{
					view.loadDataWithBaseURL("not_needed",
							String.format("<html><head></head><body><pre><strong>Page not found<br/>%s</strong></pre></body></html>",
									view.getUrl()),
							"text/html",
							"utf-8",
							"not_needed");
				}
			}
		});

		WebSettings webSettings = webView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		//webSettings.setJavaScriptEnabled(true);
		//webSettings.setDomStorageEnabled(true);  
		//webSettings.setSupportZoom(true);
		//webSettings.setBuiltInZoomControls(true);
		//webSettings.setDisplayZoomControls(false);
		webView.loadUrl(url);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Help");
		builder.setCancelable(true);
		builder.setNegativeButton("Close", null);
		builder.setView(webView);
		builder.show();
	}

	@SuppressLint("DefaultLocale")
	public static void ShowHelpForActivity(Activity activity)
	{
		String url = String.format("%s/eWallet/Help/%s.html",
				CallSoapThread.getWebRootUrl(),
				activity.getClass().getSimpleName()).toLowerCase();
		ShowHelp(activity,url);
	}

	@SuppressLint("DefaultLocale")
	public static void ShowHelpForCurrentFocusedView(Activity activity)
	{
		Window mWindow = activity.getWindow();
		View currentFocused = mWindow != null ? mWindow.getCurrentFocus() : null;
		if(currentFocused != null && currentFocused.getClass().equals(EditTextx.class))
		{
			EditTextx editTextx = (EditTextx)currentFocused;
			String url = String.format("%s/eWallet/Help/%s_%s.html",
					CallSoapThread.getWebRootUrl(),
					activity.getClass().getSimpleName(),
					editTextx.Name).toLowerCase();

			ShowHelp(activity,url);
		}
	}

	public static boolean StringIsNullOrEmpty(String string)
	{
		return string == null || string.length() == 0;
	}

	public static boolean IsEqualStrings(String stringA, String stringB)
	{
		if(stringA != null && stringB != null)
			return stringA.compareTo(stringB) == 0;
		return stringA == stringB;
	}

	public static int ParseToInt(String s, int defaultVal)
	{
		return s != null && s != ""?Integer.parseInt(s):defaultVal;
	}

	public static Csv CallMobilePay(Context context,String command,String parameter,String errTitle)
	{
		Csv csvResp = null;
		String rslt = ServiceClient.MobilePay(context, command,eWallet.GetLogonId(context), parameter);
		if(rslt != null)
		{
			csvResp = new Csv(rslt);

			if (csvResp.containsKey("SysErr"))
			{
				eWallet.ShowDialog(context,eWallet.StringIsNullOrEmpty(errTitle)?"CallMobilePay":errTitle,csvResp.get("SysErr"));
			}
		}
		return csvResp;
	}

	public static void ShowDialog(Context context, String title, String message)
	{
		ShowDialog(context,title,message,null);
	}

	public static void ShowDialog(Context context, String title, String message, DialogInterface.OnClickListener okCallback)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK",okCallback != null?okCallback:new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});

		AlertDialog ad = alertDialogBuilder.create();

		ad.setTitle(title);
		ad.setMessage(message);
		ad.show();
	}

	public static boolean ShowYesNoDialog(Context context, String title, String message, DialogInterface.OnClickListener yesCallback)
	{
		boolean rslt = false;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder
				.setCancelable(true)
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				})
				.setPositiveButton("Yes",yesCallback)
		;

		AlertDialog ad = alertDialogBuilder.create();

		ad.setTitle(title);
		ad.setMessage(message);
		ad.show();
		return rslt;
	}

}
