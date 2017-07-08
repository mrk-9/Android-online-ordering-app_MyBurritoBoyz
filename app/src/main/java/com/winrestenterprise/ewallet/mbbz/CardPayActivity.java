package com.winrestenterprise.ewallet.mbbz;

import com.winrestenterprise.ewallet.CardProfile;
import com.winrestenterprise.ewallet.CardSkin;
import com.winrestenterprise.ewallet.Csv;
import com.winrestenterprise.ewallet.SingleChildActivity;
import com.winrestenterprise.ewallet.eWallet;
import com.winrestenterprise.ewallet.mbbz.R;

import java.io.File;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class CardPayActivity extends SingleChildActivity {
	private String cardId;
	private CardProfile cardProfile;
	private TextView textView_name;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pay);
        
        this.textView_name = (TextView) findViewById(R.id.textView_name);
        
        Intent intent = getIntent();
		this.cardId = intent.getStringExtra(eWallet.CARDID);
		this.showCardInfo();
    }

    private void showCardInfo()
    {
		Csv csvResp = eWallet.CallMobilePay(this, "GetUserCardInfo", this.cardId, "Get Card Info");
		if (csvResp.containsKey("CardInfo"))
        {
			this.cardProfile = new CardProfile(csvResp.get("CardInfo"));
			
			CardSkin cardSkin = CardListActivity.GetPredefinedCardSkins()[eWallet.ParseToInt(this.cardProfile.Skin,0)];
			
			TextView textView_acct = (TextView) findViewById(R.id.textView_acct);
			TextView textView_bal = (TextView) findViewById(R.id.textView_bal);
			
			this.setTitle(String.format("Card - %s",this.cardProfile.Name));

			this.textView_name.setText(this.cardProfile.Name);
			this.textView_name.setTextColor(cardSkin.CardSufface.TextColor);
			
			textView_acct.setText(this.cardProfile.Account);
			textView_acct.setTextColor(cardSkin.CardSufface.TextColor);
			textView_acct.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL);

			String balInfo = "";
			if (csvResp.containsKey("BalanceInfo"))
			{
				balInfo = csvResp.get("BalanceInfo");
			}
			textView_bal.setText(balInfo);
			textView_bal.setTextColor(cardSkin.CardSufface.TextColor);

			if(this.cardProfile.Icon != null && this.cardProfile.Icon != "")
			{
				File file = new File(this.getFilesDir(), this.cardProfile.Icon);
				Drawable img = null;
				if(file.exists())
				{
					img = Drawable.createFromPath(file.getPath());
					img.setBounds( 0, 0, 120, 86 );
					//				btn.setCompoundDrawables( img, null, null, null );
					//				btn.setCompoundDrawablePadding(20);
					//				TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
					//				tableLayout.setBackgroundDrawable(img);
					this.textView_name.setCompoundDrawables( img, null, null, null );
					this.textView_name.setCompoundDrawablePadding(20);
				}
			}
			
			TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout_card);
			tableLayout.setBackgroundDrawable(CardListActivity.GetCardBackground(cardSkin.CardSufface,30f,30f,false,0xFFFFFFFF));
			tableLayout.setPadding(2, 20, 2, 30);
			
			//LinearLayout.LayoutParams button_LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			
	        Button btnScan = (Button)findViewById(R.id.button_scan_pay);
	        btnScan.setBackgroundDrawable(CardListActivity.GetCardBackground(cardSkin.ButtonSufface,20f,20f,true,0xFF1F1F1F));
	        btnScan.setTextColor(cardSkin.ButtonSufface.TextColor);
	        btnScan.setPadding(20, 10, 20, 10);
	        //btnScan.setLayoutParams(button_LayoutParams);
	        
	        Button btnOrder = (Button)findViewById(R.id.button_order);
	        btnOrder.setBackgroundDrawable(CardListActivity.GetCardBackground(cardSkin.ButtonSufface,20f,20f,true,0xFF1F1F1F));
	        btnOrder.setTextColor(cardSkin.ButtonSufface.TextColor);
	        btnOrder.setPadding(20, 10, 20, 10);
	        
	        Button btnReload = (Button)findViewById(R.id.button_reload);
	        btnReload.setBackgroundDrawable(CardListActivity.GetCardBackground(cardSkin.ButtonSufface,20f,20f,true,0xFF1F1F1F));
	        btnReload.setTextColor(cardSkin.ButtonSufface.TextColor);
	        btnReload.setPadding(20, 10, 20, 10);
        }
    }   
    
	public void button_order_onClick(View view){
		Intent intent = new Intent(this, WebOrder.class);
		intent.putExtra(eWallet.CARDID, this.cardId);
		intent.putExtra(eWallet.WEB_ORDER_SITE_PATH, eWallet.WEB_ORDER_SITE_PATH__REGULAR_MENU);
		startSingleActivityForResult(intent,eWallet.REQUEST_CODE__WEB_ORDER);
	}
	
	public void button_reload_onClick(View view){
		Intent intent = new Intent(this, WebOrder.class);
		intent.putExtra(eWallet.CARDID, this.cardId);
		intent.putExtra(eWallet.WEB_ORDER_SITE_PATH, eWallet.WEB_ORDER_SITE_PATH__GIFT_CARD_RELOAD);
		startSingleActivityForResult(intent,eWallet.REQUEST_CODE__WEB_ORDER);
	}
	
	public void button_scan_pay_onClick(View view){
		Intent intent = new Intent(this, ZBarScanActivity.class);
		startSingleActivityForResult(intent,eWallet.REQUEST_CODE__ZBAR_SCAN);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean needToRefreshCardInfo=false;
        switch (requestCode) {
            case eWallet.REQUEST_CODE__ZBAR_SCAN:
            	if(data != null)
            		this.payBill(data.getStringExtra(eWallet.SCAN_RESULT));
            	break;
            case eWallet.REQUEST_CODE__EDIT_CARD:
            	if(data != null)
            	{
            		if(data.getStringExtra(eWallet.RELOAD) != null)
            		{
            			this.showCardInfo();
            			Intent intent = new Intent();
            			intent.putExtra(eWallet.RELOAD, "yes");
            			this.setResult(RESULT_OK,intent);
            		}
            	}
            	break;
            case eWallet.REQUEST_CODE__SHOW_RECEIPT:
            	needToRefreshCardInfo = true;
            	((TableLayout)this.findViewById(R.id.tableLayout_toolBar)).requestLayout();
            case eWallet.REQUEST_CODE__WEB_ORDER:
            	if(data != null){
            		if(data.getStringExtra(eWallet.WEB_ORDER_RESULT).equals(eWallet.WEB_ORDER_RESULT__ORDER_PROCESSED))
            			needToRefreshCardInfo = true;
            	}
            	((TableLayout)this.findViewById(R.id.tableLayout_toolBar)).requestLayout();
            	break;
            default:
        }
        if(needToRefreshCardInfo){
        	this.showCardInfo();
        	Toast.makeText(getApplicationContext(),"Balance resfreshed.", Toast.LENGTH_SHORT).show();
        }
    }
	
	private void payBill(String posTranCode)
	{
        if (posTranCode != null) {
        	Csv csvResp = eWallet.CallMobilePay(this, "PayBill", String.format("%s=%s",posTranCode,this.cardId), "Scan&Pay");
        	if(!csvResp.containsKey("SysErr"))
        	{
        		if (csvResp.containsKey("Receipt"))
        		{
        			Intent intent = new Intent(this, ReceiptActivity.class);
        			intent.putExtra(eWallet.RECEIPT_TEXT, csvResp.get("Receipt").replace("\\r", "\r\n"));
        			startSingleActivityForResult(intent,eWallet.REQUEST_CODE__SHOW_RECEIPT);
        		}
        		else if(csvResp.containsKey("ActionCode"))
        		{
        			eWallet.ShowDialog(this,
        					String.format("Transaction Response %s",csvResp.get("ActionCode")),
        					csvResp.get("Verbiage")
        					);
        		}
        	}
        }
	}
	
	public void button_back_onClick(View view){
		this.finish();
	}
	
	public void button_receipt_onClick(View view){
		CardListActivity.ShowCardReceiptList(this, this.cardId, this.cardProfile.Name);
	}
	
	public void button_edit_onClick(View view){
		Intent intent = new Intent(this, EditCardActivity.class);
		intent.putExtra(eWallet.CARDID, this.cardId);
		startSingleActivityForResult(intent,eWallet.REQUEST_CODE__EDIT_CARD);
	}
	
	public void button_delete_onClick(View view){
		eWallet.ShowYesNoDialog(this, "Delete", "Are you sure you want to delete this card.", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog,int id) {
				if(CardListActivity.DeleteCard(CardPayActivity.this, CardPayActivity.this.cardId))
				{
					Intent intent = new Intent();
					intent.putExtra(eWallet.RELOAD, "yes");
					CardPayActivity.this.setResult(RESULT_OK,intent);
					CardPayActivity.this.finish();
				}
			}
		});
	}
	
	public void button_help_onClick(View view){
		eWallet.ShowHelpForActivity(this);
	}
}
