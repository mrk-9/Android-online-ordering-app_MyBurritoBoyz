package com.winrestenterprise.ewallet.mbbz;

import com.winrestenterprise.ewallet.Csv;
import com.winrestenterprise.ewallet.ReceiptListItem;
import com.winrestenterprise.ewallet.SingleChildActivity;
import com.winrestenterprise.ewallet.eWallet;
import com.winrestenterprise.ewallet.mbbz.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ReceiptListActivity extends SingleChildActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receipt_list);
		try
		{
			ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView1);
			scrollView.setVerticalScrollBarEnabled(false);
			scrollView.setHorizontalScrollBarEnabled(false);

			Intent intent = getIntent();
			String csvReceiptListStr = intent.getStringExtra(eWallet.RECEIPT_LIST);
			String cardName = intent.getStringExtra(eWallet.CARD_NAME);
			
			Button btnTitleBar =  (Button) findViewById(R.id.button_titleBar);
			btnTitleBar.setText(String.format("%s - Receipts",cardName));
			
			Csv csvReceiptList = new Csv(csvReceiptListStr);
			
			LinearLayout linearLayout_content = (LinearLayout)findViewById(R.id.linearLayout_content);
			Drawable iconImage = this.getResources().getDrawable( R.drawable.receipt );
			iconImage.setBounds( 0, 0, 60, 60 );
			
			String[] receiptIds = csvReceiptList.keySet();
			for(int i = receiptIds.length - 1; i>-1; i--)
			{
				String key = receiptIds[i];
				ReceiptListItem receiptListItem = new ReceiptListItem(csvReceiptList.get(key));
				
				ImageView iv= new ImageView(this);
				iv.setImageDrawable(iconImage);
				iv.setPadding(10, 10, 0, 0);
				LinearLayout.LayoutParams iv_LayoutParams = new LinearLayout.LayoutParams(-1, -2);
				iv_LayoutParams.width = 0;
				iv_LayoutParams.weight = 0.1f;
				iv.setLayoutParams(iv_LayoutParams);
				//iv.setBackgroundColor(0xff0000ff);//for test
				
				LinearLayout ll_middle = new LinearLayout(this);
				ll_middle.setPadding(20, 5, 5, 5);
				ll_middle.setOrientation(LinearLayout.VERTICAL);
				LinearLayout.LayoutParams ll_middle_LayoutParams = new LinearLayout.LayoutParams(-1, -2);
				ll_middle_LayoutParams.width = 0;
				ll_middle_LayoutParams.weight = 0.6f;
				ll_middle.setLayoutParams(ll_middle_LayoutParams);
				//ll_middle.setBackgroundColor(0xff00ff00);//for test
				
				TextView tvTranTime = new TextView(this);
				tvTranTime.setText(receiptListItem.TranTime);
				tvTranTime.setTextSize(18);			
				tvTranTime.setTextColor(0xff000000);
				TextView tvMerchantName = new TextView(this);
				tvMerchantName.setText(receiptListItem.MerchantName);
				tvMerchantName.setTextColor(0xff000000);
				ll_middle.addView(tvTranTime);
				ll_middle.addView(tvMerchantName);

				TextView tvAmount = new TextView(this);
				tvAmount.setText(receiptListItem.Amount);
				tvAmount.setTextSize(18);
				tvAmount.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
				tvAmount.setPadding(0, 5, 10, 5);
				tvAmount.setTextColor(0xff000000);
				LinearLayout.LayoutParams tvAmount_LayoutParams = new LinearLayout.LayoutParams(-1, -2);
				tvAmount_LayoutParams.width = 0;
				tvAmount_LayoutParams.weight = 0.3f;
				tvAmount.setLayoutParams(tvAmount_LayoutParams);
				
				LinearLayout ll_row = new LinearLayout(this);
				LinearLayout.LayoutParams ll_row_LayoutParams = new LinearLayout.LayoutParams(-1, -2);
				ll_row_LayoutParams.setMargins(0, 0, 0, 1);
				ll_row.setLayoutParams(ll_row_LayoutParams);
				ll_row.setTag(key);
				ll_row.setBackgroundResource(R.layout.background_receipt_list_item_row);
				ll_row.addView(iv);
				ll_row.addView(ll_middle);
				ll_row.addView(tvAmount);
				ll_row.setClickable(true);
				ll_row.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						String receiptId = (String)v.getTag();
						Csv csvResp = eWallet.CallMobilePay(ReceiptListActivity.this, "GetUserCardReceipt", receiptId, "Get User Card Receipt");
	            		if (csvResp.containsKey("Receipt"))
	                    {
	            			Intent intent = new Intent(ReceiptListActivity.this, ReceiptActivity.class);
	            			intent.putExtra(eWallet.RECEIPT_TEXT, csvResp.get("Receipt").replace("\\r", "\r\n"));
	            			ReceiptListActivity.this.startSingleActivityForResult(intent, eWallet.REQUEST_CODE__SHOW_RECEIPT);
	                    }
					} 
				});

				linearLayout_content.addView(ll_row);
			}
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
		eWallet.ShowHelpForActivity(this);
	}
}
