package com.winrestenterprise.ewallet.mbbz;

import com.winrestenterprise.ewallet.Csv;
import com.winrestenterprise.ewallet.UserProfile;
import com.winrestenterprise.ewallet.eWallet;
import com.winrestenterprise.ewallet.mbbz.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

public class EditUserActivity extends Activity {

	private UserProfile userProfile;
	private EditText editText_name;
	private EditText editText_email;
	private EditText editText_phone;
	private EditText editText_address;
	private EditText editText_postalCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_user);
		
		this.setTitle("Edit My Profile");
		
		this.editText_name = (EditText) findViewById(R.id.editText_name);
		this.editText_email = (EditText) findViewById(R.id.editText_email);
		this.editText_phone = (EditText) findViewById(R.id.editText_phone);
		this.editText_address = (EditText) findViewById(R.id.editText_address);
		this.editText_postalCode = (EditText) findViewById(R.id.editText_postalCode);
		
		Csv csvResp = eWallet.CallMobilePay(this, "GetUserInfo", "", "GetUserInfo");
		if (csvResp.containsKey("UserInfo"))
        {
			this.userProfile = new UserProfile(csvResp.get("UserInfo"));
			this.editText_name.setText(this.userProfile.Name);
			this.editText_email.setText(this.userProfile.Email);
			this.editText_phone.setText(this.userProfile.Phone);
			this.editText_address.setText(this.userProfile.Address);
			this.editText_postalCode.setText(this.userProfile.Zip_Code);
			
			this.editText_email.setKeyListener(null); 
			//this.editText_phone.setKeyListener(null); 
        }
	}

	private void saveAndClose()
	{
		try
		{
			String name = this.editText_name.getText().toString();
			if(name == null || name.length() == 0)
			{
				this.editText_name.requestFocus();
				throw new Exception("Please enter a name.");
			}

			boolean needToUpdate = false;
			if(!eWallet.IsEqualStrings(this.userProfile.Name,name))
			{
				this.userProfile.Name = name;
				needToUpdate = true;
			}
			
			String phone = this.editText_phone.getText().toString();
			if(!eWallet.IsEqualStrings(this.userProfile.Phone,phone))
			{
				this.userProfile.Phone = phone;
				needToUpdate = true;
			}
			
			String address = this.editText_address.getText().toString();
			if(!eWallet.IsEqualStrings(this.userProfile.Address,address))
			{
				this.userProfile.Address = address;
				needToUpdate = true;
			}
			
			String postalCode = this.editText_postalCode.getText().toString();
			if(!eWallet.IsEqualStrings(this.userProfile.Zip_Code,postalCode))
			{
				this.userProfile.Zip_Code = postalCode;
				needToUpdate = true;
			}

			if(needToUpdate)
			{
				Csv csvResp = eWallet.CallMobilePay(this, "UpdateUserInfo", userProfile.ToCsvString(), "Update User Info Failed");
				if(csvResp != null)
				{
					if (csvResp.containsKey("Verbiage"))
					{
						Toast.makeText(getApplicationContext(),"User Profile Updated.", Toast.LENGTH_SHORT).show();
					}
				}
			}
			
			Intent intent = new Intent();
			this.setResult(RESULT_OK,intent);
			this.finish();
		}
		catch(Exception ex)
		{
			eWallet.ShowDialog(this, "Error!", ex.getMessage());
		}
	}
	
	public void button_help_onClick(View view){
		eWallet.ShowHelpForActivity(this);
	}
	
	public void button_back_onClick(View view){
		this.saveAndClose();
	}
}
