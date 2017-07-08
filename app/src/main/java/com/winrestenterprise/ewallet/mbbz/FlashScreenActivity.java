package com.winrestenterprise.ewallet.mbbz;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.winrestenterprise.ewallet.CallSoapThread;
import com.winrestenterprise.ewallet.Csv;
import com.winrestenterprise.ewallet.eWallet;
import com.winrestenterprise.ewallet.mbbz.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class FlashScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CallSoapThread.SetWebServiceUrl(getResources().getString(R.string.web_service_url));

		eWallet.CheckDebugCfg();

		if(eWallet.Debug)
			Toast.makeText(getApplicationContext(),String.format("Debug - Connecting To %s", CallSoapThread.getSoapAddress()), Toast.LENGTH_LONG).show();

//		Parse.initialize(this, "aegfMcodrjUtJPLMqLGj7rTeOfSPeDOdfdVp9MU6", "9jnuW8qJ9wZi6o8JhiQplOezfiJjXkJ8kuJTzzGJ");
		//Parse.initialize(this, "OwNCMlCjMuBoEdT4IPLRN6O8aIcKooml5F4zqmPD", "9999");
		//String iid = ParseInstallation.getCurrentInstallation().getInstallationId();
		//Toast.makeText(getApplicationContext(), iid, Toast.LENGTH_LONG).show();
//		ParseInstallation.getCurrentInstallation().saveInBackground();

//		ParseQuery query = new ParseQuery("test");
//
//		query.findInBackground(new FindCallback<ParseObject>() {
//			public void done(List<ParseObject> objects, ParseException e) {
//				if (e == null) {
//					//objectsWereRetrievedSuccessfully(objects);
//					if (!objects.isEmpty()) {
//						for (ParseObject dealsObject : objects) {
//							// use dealsObject.get('columnName') to access the properties of the Deals object.
//							Toast.makeText(getApplicationContext(), String.format("Parse %s", dealsObject), Toast.LENGTH_LONG).show();
//						}
//					} else {
//						Toast.makeText(getApplicationContext(), "Parse empty", Toast.LENGTH_LONG).show();
//					}
//				} else {
//					//objectRetrievalFailed();
//
//					Toast.makeText(getApplicationContext(), String.format("Parse Error %s", e.getMessage()), Toast.LENGTH_LONG).show();
//				}
//			}
//		});



		if(!eWallet.StringIsNullOrEmpty(eWallet.GetLogonId(this)))
		{
			int logonResult = this.logon();
			if(logonResult == 9)
			{
				Intent intent = new Intent(this, ActivateWalletActivity.class);
				intent.putExtra(eWallet.ACTIVATION_MODE, "ACTIVATION_STEP2");
				startActivityForResult(intent,eWallet.REQUEST_CODE__ACTIVATION_STEP2);
			}
			else if(logonResult != 0)
			{
				//eWallet.SetLogonId(this, "");
				this.showFlashScreenLayout();
			}
		}
		else
		{  			
//			String activationId = eWallet.ReadKeyValueFromDisk(this, eWallet.ACTIVATION_ID);
//			if(!eWallet.StringIsNullOrEmpty(activationId))
//			{
//				Intent intent = new Intent(this, ActivateWalletActivity.class);
//				intent.putExtra(eWallet.ACTIVATION_ID, activationId);
//				startActivityForResult(intent,eWallet.REQUEST_CODE__ACTIVATION_STEP2);
//			}
//			else
//			{
				this.showFlashScreenLayout();
//			}
		}
	}

	private void showFlashScreenLayout()
	{
		setContentView(R.layout.activity_flash_screen);

		Display display = getWindowManager().getDefaultDisplay();
		int displayHeight = display.getHeight();
		int displayWidth = display.getWidth();

		if(displayWidth > displayHeight)
		{
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT);
			layoutParams.setMargins(70, 30, 70, 30);
			View layout = findViewById(R.id.layout_main);
			layout.setLayoutParams(layoutParams);
		}

		TextView tvTitle = (TextView)findViewById(R.id.textView_title);

		String splashTitle = getResources().getString(R.string.splash_title);
		splashTitle = splashTitle.replaceAll("%38", "&");
		//Spanned s= Html.fromHtml("WinRest&reg; eWallet");
		Spanned s= Html.fromHtml(splashTitle);

		SpannableString ss1=  new SpannableString(s);
		ss1.setSpan(new RelativeSizeSpan(1.6f), 0, ss1.length(), 0);
		if(splashTitle.startsWith("WinRest"))
		{
			ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 8, 0);
			ss1.setSpan(new ForegroundColorSpan(0xFF66FFFF), 9, 16, 0);
		}
		tvTitle.setText(ss1); 

		TextView tvCopyRight = (TextView)findViewById(R.id.textView_copyRight);
		s = Html.fromHtml("&copy; 2014 ABS Software Design, INC. All Rights Reserved.");
		ss1 = new SpannableString(s);
		ss1.setSpan(new RelativeSizeSpan(0.5f), 0,s.length(), 0); // set size
		tvCopyRight.setText(ss1);
		
		String app_shortname = getResources().getString(R.string.app_shortname);
		String webRootUrl = CallSoapThread.getWebRootUrl();
		
		TextView textView_p5 = (TextView)findViewById(R.id.textView_p5);
		textView_p5.setText(
	            Html.fromHtml(String.format("Please click the link to read the latest <a href=\"%s/ewallet/help/privacy_policy_%s.html\">Privacy Policy</a> and <a href=\"%s/ewallet/help/terms_of_use_%s.html\">Terms of use</a>",
        				webRootUrl,
        				app_shortname,
        				webRootUrl,
        				app_shortname)));
		textView_p5.setMovementMethod(LinkMovementMethod.getInstance());
		
//		CheckBox checkBox_agree = ( CheckBox ) findViewById( R.id.checkBox_agree );
//		checkBox_agree.setText(
//	            Html.fromHtml(
//	            		String.format("I have read the Privacy Policy and agreed Terms of use",
//	            				webRootUrl,
//	            				app_shortname,
//	            				webRootUrl,
//	            				app_shortname)));
		//checkBox_agree.setMovementMethod(LinkMovementMethod.getInstance());
		
//		checkBox_agree.setOnCheckedChangeListener(new OnCheckedChangeListener()
//		{
//		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//		    {
//		    	Button button_ok = (Button) findViewById( R.id.button_ok );
//		    	button_ok.setEnabled(isChecked); 
//		    	button_ok.setTextColor(isChecked?0xFFFFFFFF:0xFF666666);
////		        if ( isChecked )
////		        {
////		            // perform logic
////		        }
//		    }
//		});
	}

	private int logon()
	{
		int rslt = -1;
		String csvCardListStr = null;
		try
		{
			Csv csvResp = eWallet.CallMobilePay(this, "GetUserCardList", "Ver=1", "Logon Failed");

			if(csvResp != null)
			{
				if(csvResp.containsKey("ActionCode"))
				{
					rslt = Integer.parseInt(csvResp.get("ActionCode"));
				}
				
				if(!csvResp.containsKey("SysErr"))
				{
					if(csvResp.containsKey("CardList"))
					{
						csvCardListStr = csvResp.get("CardList");
					}
					else
					{
						csvCardListStr = "";
					}
				}
			}
		}
		catch(Exception ex)
		{
			eWallet.ShowDialog(this, "Error!", ex.toString());
		}

		if(csvCardListStr != null)
		{
			Intent intent = new Intent(this, CardListActivity.class);
			intent.putExtra(eWallet.CARDLIST, csvCardListStr);
			startActivityForResult(intent,0);
		}

		return rslt;
	}

	private void doActivationStep1(){
		Intent intent = new Intent(this, ActivateWalletActivity.class);
		startActivityForResult(intent,eWallet.REQUEST_CODE__ACTIVATION_STEP1);
	}

	public void button_decline_onClick(View view){
		this.finish();
	}
	
	public void button_accept_onClick(View view){
		this.doActivationStep1();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		boolean shouldFinish = resultCode != RESULT_OK;
		if(!shouldFinish)
		{
			try
			{
				switch (requestCode) {
				case eWallet.REQUEST_CODE__ACTIVATION_STEP1:
					if(data != null)
					{
						String email = data.getStringExtra(eWallet.EMAIL);
						if(email != null)
							eWallet.SaveKeyValueToDisk(this, eWallet.EMAIL, email);
						else
							throw new Exception("EMAIL is not found in Activity Result Data");
//						String activationId = data.getStringExtra(eWallet.ACTIVATION_ID);
//						if(activationId != null)
//							eWallet.SaveKeyValueToDisk(this, eWallet.ACTIVATION_ID, activationId);
//						else
//							throw new Exception("ACTIVATION_ID is not found in Activity Result Data");
//
						String logonId = data.getStringExtra(eWallet.LOGON_ID);
						if(logonId != null)
						{
							eWallet.SetLogonId(this, logonId);
//							eWallet.SaveKeyValueToDisk(this, eWallet.ACTIVATION_ID, "");
						}
						else
							throw new Exception("LOGON_ID is not found in Activity Result Data");
						
						Intent intent = new Intent(this, ActivateWalletActivity.class);
						intent.putExtra(eWallet.ACTIVATION_MODE, "ACTIVATION_STEP2");
						startActivityForResult(intent,eWallet.REQUEST_CODE__ACTIVATION_STEP2);
						
					}
					break;
				case eWallet.REQUEST_CODE__ACTIVATION_STEP2:
//					if(data != null)
//					{
//						String logonId = data.getStringExtra(eWallet.LOGON_ID);
//						if(logonId != null)
//						{
//							eWallet.SetLogonId(this, logonId);
////							eWallet.SaveKeyValueToDisk(this, eWallet.ACTIVATION_ID, "");
//						}
//						else
//							throw new Exception("LOGON_ID is not found in Activity Result Data");

						if(this.logon() == 9)
						{
							Intent intent = new Intent(this, ActivateWalletActivity.class);
							intent.putExtra(eWallet.ACTIVATION_MODE, "ACTIVATION_STEP2");
							startActivityForResult(intent,eWallet.REQUEST_CODE__ACTIVATION_STEP2);
						}
						else
						{
							shouldFinish = true;
						}
//					}
					break;
				default:
					shouldFinish = true;
				}
			}
			catch(Exception e)
			{
				eWallet.ShowDialog(this, "Error!", e.toString());
				shouldFinish = true;
			}
		}

		if(shouldFinish)
			this.finish();
	}
}
