package com.winrestenterprise.ewallet.mbbz;

import com.winrestenterprise.ewallet.CardProfile;
import com.winrestenterprise.ewallet.Csv;
import com.winrestenterprise.ewallet.eWallet;
import com.winrestenterprise.ewallet.mbbz.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class AddCardActivity extends Activity {

    private final int REQUEST_CODE__ZBAR_SCAN = 1;
	private EditText editText_cardNo;
	private EditText editText_activationCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_card);
		
		this.setTitle("Add Card");
		this.editText_cardNo = (EditText) findViewById(R.id.editText_cardNo);
		this.editText_activationCode = (EditText) findViewById(R.id.editText_activationCode);
		
		this.editText_cardNo.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.showSoftInput(AddCardActivity.this.editText_cardNo, 0);
			}
		},50);
	}

	public void button_scan_onClick(View view){
		this.scanCardActivationQrCode();
	}

	private void scanCardActivationQrCode()
	{
		Intent intent = new Intent(this, ZBarScanActivity.class);
		startActivityForResult(intent,REQUEST_CODE__ZBAR_SCAN);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE__ZBAR_SCAN:
            	if(data != null){
                	EditText editText_activationCode = (EditText) findViewById(R.id.editText_activationCode);
            		editText_activationCode.setText(data.getStringExtra(eWallet.SCAN_RESULT));
                }
            default:
        }
    }
	
	public void button_help_onClick(View view){
		eWallet.ShowHelpForCurrentFocusedView(this);
	}
	
	public void button_back_onClick(View view){
		Intent intent = new Intent();
		intent.putExtra(eWallet.RELOAD, "no");
		this.setResult(RESULT_OK,intent);
		this.finish();
	}
	
	public void button_add_onClick(View view){
		try
		{
			String cardNo = this.editText_cardNo.getText().toString();
			if(cardNo == null || cardNo.length() == 0)
			{
				this.editText_cardNo.requestFocus();
				throw new Exception("Please enter card number.");
			}

			String activationCode = this.editText_activationCode.getText().toString();
			if(activationCode == null || activationCode.length() == 0)
			{
				this.editText_activationCode.requestFocus();
				throw new Exception("Please enter activation code.");
			}

			CardProfile cardProfile = new CardProfile();
			cardProfile.Name = "";
			cardProfile.Card_Type = "WinAuthGiftCard";
			cardProfile.Account = cardNo;
			cardProfile.SecurityData = activationCode;

			Csv csvResp = eWallet.CallMobilePay(this, "AddUserCard", cardProfile.ToCsvString(), "Add User Card Failed");
			if(csvResp != null)
			{
				if (csvResp.containsKey("Verbiage"))
				{
					Toast.makeText(getApplicationContext(),"New card is added to the wallet.", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra(eWallet.RELOAD, "yes");
					this.setResult(RESULT_OK,intent);
					this.finish();
				}
			}
		}
		catch(Exception ex)
		{
			eWallet.ShowDialog(this, "Error!", ex.getMessage());
		}
	}
}
