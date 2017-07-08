package com.winrestenterprise.ewallet.mbbz;

import com.winrestenterprise.ewallet.CardProfile;
import com.winrestenterprise.ewallet.CardSkin;
import com.winrestenterprise.ewallet.Csv;
import com.winrestenterprise.ewallet.eWallet;
import com.winrestenterprise.ewallet.mbbz.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
//import android.app.ActionBar.LayoutParams;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;

public class EditCardActivity extends Activity {

	private CardProfile cardProfile = null;
	private EditText editText_name;
	private EditText editText_cardNo;
	private Button button_skin;
	private String skin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_card);
		
		//this.setTitle("Edit WinAuth Giftcard");
		this.editText_name = (EditText) findViewById(R.id.editText_name);
		this.editText_cardNo = (EditText) findViewById(R.id.editText_cardNo);
		this.button_skin = (Button) findViewById(R.id.button_skin);
		
        Intent intent = getIntent();
		String cardId = intent.getStringExtra(eWallet.CARDID);
		if(cardId != null && cardId.length()>0)
		{
			Csv csvResp = eWallet.CallMobilePay(this, "GetUserCardInfo", cardId, "Get Card Info");
			if (csvResp.containsKey("CardInfo"))
	        {
				this.cardProfile = new CardProfile(csvResp.get("CardInfo"));
				this.editText_name.setText(this.cardProfile.Name);
				this.editText_cardNo.setText(this.cardProfile.Account);
				this.editText_cardNo.setKeyListener(null);
				this.skin = this.cardProfile.Skin;
				this.showCardAppearance();
	        }
		}
		

		this.editText_name.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.showSoftInput(EditCardActivity.this.editText_name, 0);
			}
		},50);
	}
	
	private void saveAndClose()
	{
		try
		{
			String name = this.editText_name.getText().toString();
			if(name == null || name.length() == 0)
			{
				this.editText_name.requestFocus();
				throw new Exception("Please enter a card name.");
			}
			
			boolean needToUpdate = false;
			
			if(!eWallet.IsEqualStrings(this.cardProfile.Name,name))
			{
				this.cardProfile.Name = name;
				needToUpdate = true;
			}
			
			if(!eWallet.IsEqualStrings(this.cardProfile.Skin,this.skin))
			{
				this.cardProfile.Skin = this.skin;
				needToUpdate = true;
			}
			
			if(needToUpdate)
			{
				Csv csvResp = eWallet.CallMobilePay(this, "UpdateUserCard", this.cardProfile.ToCsvString(), "Update User Card Failed");
				if(csvResp != null)
				{
					if (csvResp.containsKey("Verbiage"))
					{
						//LoginActivity.ShowDialog(this, "Update User Card",csvResp.get("Verbiage"));
						Toast.makeText(getApplicationContext(),"Card is updated", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.putExtra(eWallet.RELOAD, "yes");
						this.setResult(RESULT_OK,intent);
					}
				}
			}
			this.finish();
		}
		catch(Exception ex)
		{
			eWallet.ShowDialog(this, "Error!", ex.getMessage());
		}
	}
	
	public void button_back_onClick(View view){
		this.saveAndClose();
	}
	
	public void button_help_onClick(View view){
		eWallet.ShowHelpForCurrentFocusedView(this);
	}
	
	private void showCardAppearance()
	{
		CardSkin[] predefinedSkins = CardListActivity.GetPredefinedCardSkins();
		CardSkin cardSkin = predefinedSkins[eWallet.ParseToInt(this.skin,0)];
		String sTextColor = String.format("%08x", cardSkin.CardSufface.TextColor).substring(2);
		String styledText = String.format("<big><font color='#%s' style='bold'>Appearance</font></big>",sTextColor);
		this.button_skin.setText(Html.fromHtml(styledText));
		this.button_skin.setBackgroundDrawable(CardListActivity.GetCardBackground(cardSkin.CardSufface,10f,10f,true,0xFF1F1F1F));
		this.button_skin.setPadding(20, 20, 20, 20);
	}
	
	private AlertDialog currentAlertDialog;
	public void button_skin_onClick(View view){
		
		CardSkin[] predefinedSkins = CardListActivity.GetPredefinedCardSkins();
		
		RelativeLayout relativeLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(-1//LayoutParams.FILL_PARENT
				,-2//LayoutParams.WRAP_CONTENT
				);
		relativeLayout.setLayoutParams(relativeLayoutParams);

		for(int i=0;i<predefinedSkins.length;i++)
		{
			CardSkin cardSkin = predefinedSkins[i];
			int id = i+1;
			Button btn = new Button(this); 
			String sTextColor = String.format("%08x", cardSkin.CardSufface.TextColor).substring(2);
			String styledText = String.format("<big><font color='#%s' style='bold'>Face %s</font></big>",sTextColor,id);
			btn.setText(Html.fromHtml(styledText));
			btn.setTextSize(20);
			btn.setId(id);
			RelativeLayout.LayoutParams btnLayoutParams = new RelativeLayout.LayoutParams(-1//LayoutParams.FILL_PARENT
					,-2//LayoutParams.WRAP_CONTENT
					);
			if(id>1)
				btnLayoutParams.addRule(RelativeLayout.BELOW,id-1);

			btn.setLayoutParams(btnLayoutParams);
			btn.setPadding(20, 20, 20, 20);
	
			btn.setBackgroundDrawable(CardListActivity.GetCardBackground(cardSkin.CardSufface,10f,10f,true,0xFF1F1F1F));
			btn.setTag(String.format("%s",i));
			btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Button btn = (Button)v;
					EditCardActivity.this.skin =  (String)btn.getTag();
					EditCardActivity.this.showCardAppearance();
					EditCardActivity.this.currentAlertDialog.dismiss();
				} 
			});

			relativeLayout.addView(btn);
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select a card face");
		builder.setCancelable(true);
		builder.setNegativeButton("Cancel", null);
		builder.setView(relativeLayout);
		this.currentAlertDialog = builder.show();
	}
}
