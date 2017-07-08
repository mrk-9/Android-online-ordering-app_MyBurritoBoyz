package com.winrestenterprise.ewallet.mbbz;

import com.winrestenterprise.ewallet.Csv;
import com.winrestenterprise.ewallet.EditTextx;
import com.winrestenterprise.ewallet.eWallet;
import com.winrestenterprise.ewallet.mbbz.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivateWalletActivity extends Activity {

	private enum ActivationMode
	{
		STEP1,
		STEP2,
		ACTIVATED,
	}

	private EditText editText_1;

//	private String activationId;
	private ActivationMode activationMode = ActivationMode.STEP1;
	private void setActivationMode(ActivationMode mode)
	{
		Button button_title = (Button) findViewById(R.id.button_title);
		TextView textView_instruction = (TextView) findViewById(R.id.textView_instruction);
		TextView textView_1 = (TextView) findViewById(R.id.textView_1);
		EditTextx editText_1 = (EditTextx) findViewById(R.id.editText_1);
		Button button_activate = (Button) findViewById(R.id.button_activate);
		this.activationMode = mode;
		switch(mode)
		{
		case STEP1:
			button_title.setText("Activation Step1");
			textView_instruction.setText("Enter your email address then click 'Get New Logon ID', an activation link will be sent to your email address.");
			textView_1.setText("Email");
			//textView_1.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
			button_activate.setText("Get New Logon ID");

			editText_1.Name = "Email";
			SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			editText_1.setText(sharedPref.getString(eWallet.EMAIL,""));

			break;
		case STEP2:
			button_title.setText("Complete Activation");
			textView_instruction.setText("To complete the activation open the activation link in the email message then click 'Logon'.");
			//textView_1.setText("Activation Code");
			textView_1.setHeight(0);
			textView_1.setVisibility(View.INVISIBLE);
			//textView_1.setInputType(InputType.TYPE_CLASS_TEXT);
			button_activate.setText("Logon");
			//editText_1.Name = "ActivationCode";
			editText_1.setHeight(0);
			editText_1.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activate_wallet);

		try
		{
			this.editText_1 = (EditText) findViewById(R.id.editText_1);

			Intent intent = getIntent();
			String activationMode = intent.getStringExtra(eWallet.ACTIVATION_MODE);
			if(activationMode != null && activationMode.equals("ACTIVATION_STEP2"))
			{
				this.setActivationMode(ActivationMode.STEP2);
			}
			else
			{
				this.setActivationMode(ActivationMode.STEP1);
			}
       	
			this.editText_1.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					keyboard.showSoftInput(ActivateWalletActivity.this.editText_1, 0);
				}
			},500);
		}
		catch(Exception e)
		{
			eWallet.ShowDialog(this, "Error!", e.toString());
		}
	}

	public void button_back_onClick(View view){
		this.finish();
	}

	public void button_help_onClick(View view){
		eWallet.ShowHelpForCurrentFocusedView(this);
	}

   
	public void button_activate_onClick(View view){

		EditText editText_1 = (EditText) findViewById(R.id.editText_1);
		Intent intent;
		try
		{
			switch(this.activationMode)
			{
			case STEP1:
				String email = editText_1.getText().toString().trim().replace(". ", ".");
				editText_1.setText(email);
				if (email != null && email.length() > 0)
				{
					intent = new Intent();
					intent.putExtra(eWallet.EMAIL, email);
					
					String phoneNumber = "";
					//TelephonyManager tMgr = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
					//String phoneNumber = tMgr.getLine1Number();
					//if(eWallet.StringIsNullOrEmpty(phoneNumber))
					//	phoneNumber = "";
					
					Csv csvResp = eWallet.CallMobilePay(this, "ActivateUserByLink", String.format("%s=%s",phoneNumber,email), "Logon Failed");
					if (csvResp.containsKey("SysErr"))
					{
						eWallet.ShowDialog(this, "Error!",csvResp.get("SysErr"));
					}
					else
					{
						if (csvResp.containsKey("Verbiage"))
						{
							if(csvResp.containsKey("LogonId"))
							{
								intent.putExtra(eWallet.LOGON_ID, csvResp.get("LogonId"));
								this.setResult(RESULT_OK,intent);
							}
							
							eWallet.ShowDialog(this, "Success!",csvResp.get("Verbiage"),new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {									
									dialog.cancel();
									ActivateWalletActivity.this.finish();
								}
							});
						}
					}
				}
				else
				{
					eWallet.ShowDialog(this, "Email Address","Please enter your email address.");
				}
				break;
			case STEP2:
//				String activationKey = editText_1.getText().toString();
//				if (activationKey != null && activationKey.length() > 0)
//				{
//					Csv csvResp = eWallet.CallMobilePay(this, "ActivateUserStep2", String.format("%s=%s",this.activationId,activationKey), "Logon Failed");
//					if (csvResp.containsKey("SysErr"))
//					{
//						eWallet.ShowDialog(this, "Error!",csvResp.get("SysErr"));
//					}
//					else
//					{
						intent = new Intent();
						//intent.putExtra(eWallet.LOGON_ID, csvResp.get("LogonId"));
						this.setResult(RESULT_OK,intent);
						this.finish();
//					}
//				}
				break;
			default:
				break;
			}

		}
		catch (Exception ex)
		{
			eWallet.ShowDialog(this, "Error!", ex.toString());
		}
	}
}
