package com.winrestenterprise.ewallet.mbbz;

import com.winrestenterprise.ewallet.ButtonSkin;
import com.winrestenterprise.ewallet.CardListItem;
import com.winrestenterprise.ewallet.CardSkin;
import com.winrestenterprise.ewallet.Csv;
import com.winrestenterprise.ewallet.SingleChildActivity;
import com.winrestenterprise.ewallet.eWallet;
import com.winrestenterprise.ewallet.mbbz.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

public class CardListActivity extends SingleChildActivity {

	public static boolean DeleteCard(Context context, String cardId)
	{
		boolean rslt = false;
		Csv csvResp = eWallet.CallMobilePay(context, "DeleteUserCard", cardId, "Delete User Card");
		if (csvResp.containsKey("Verbiage"))
			rslt = true;
		return rslt;
	}

	public static void ShowCardReceiptList(SingleChildActivity context, String cardId, String cardName)
	{
		Csv csvResp = eWallet.CallMobilePay(context, "GetUserCardReceiptList", cardId, "Get User Card Receipt List");
		if (csvResp.containsKey("ReceiptList"))
		{
			String csvReceiptListStr = csvResp.get("ReceiptList");
			Intent intent = new Intent(context, ReceiptListActivity.class);
			intent.putExtra(eWallet.RECEIPT_LIST, csvReceiptListStr);
			intent.putExtra(eWallet.CARD_NAME, cardName);
			context.startSingleActivityForResult(intent, eWallet.REQUEST_CODE__SHOW_RECEIPTLIST);
		}
		else
		{
			eWallet.ShowDialog(context,
					"No Receipt",
					"No receipt is found in the database for this card."
					);
		}
	}

	private static CharSequence onHoldOptions[] = new CharSequence[] {"Open", "Receipt", "Edit", "Delete"};
	private static CharSequence addCardOptions[] = new CharSequence[] {"Buy new card online", "Scan card activation receipt"};
	
	private static CardSkin[] _cardSkins = null;

	public static CardSkin[] GetPredefinedCardSkins()
	{
		if(CardListActivity._cardSkins == null)
		{
			CardListActivity._cardSkins = new CardSkin[4];

			CardListActivity._cardSkins[0] = new CardSkin();
			CardListActivity._cardSkins[0].CardSufface = new ButtonSkin();
			CardListActivity._cardSkins[0].CardSufface.TextColor = 0xFF000000;
			CardListActivity._cardSkins[0].CardSufface.GradientStartColor = 0xFFEFEFEF;
			CardListActivity._cardSkins[0].CardSufface.GradientEndColor = 0xFF666666;
			CardListActivity._cardSkins[0].ButtonSufface = new ButtonSkin();
			CardListActivity._cardSkins[0].ButtonSufface.TextColor = 0xFFFFFFFF;
			CardListActivity._cardSkins[0].ButtonSufface.GradientStartColor = 0xFF449DEF;
			CardListActivity._cardSkins[0].ButtonSufface.GradientEndColor = 0xFF2F6699;

			CardListActivity._cardSkins[1] = new CardSkin();
			CardListActivity._cardSkins[1].CardSufface = new ButtonSkin();
			CardListActivity._cardSkins[1].CardSufface.TextColor = 0xFFFFFFFF;
			CardListActivity._cardSkins[1].CardSufface.GradientStartColor = 0xFF449DEF;
			CardListActivity._cardSkins[1].CardSufface.GradientEndColor = 0xFF2F6699;
			CardListActivity._cardSkins[1].ButtonSufface = new ButtonSkin();
			CardListActivity._cardSkins[1].ButtonSufface.TextColor = 0xFF000000;
			CardListActivity._cardSkins[1].ButtonSufface.GradientStartColor = 0xFFEFEFEF;
			CardListActivity._cardSkins[1].ButtonSufface.GradientEndColor = 0xFF666666;

			CardListActivity._cardSkins[2] = new CardSkin();
			CardListActivity._cardSkins[2].CardSufface = new ButtonSkin();
			CardListActivity._cardSkins[2].CardSufface.TextColor = 0xFFFFFFFF;
			CardListActivity._cardSkins[2].CardSufface.GradientStartColor = 0xFFEF9D44;
			CardListActivity._cardSkins[2].CardSufface.GradientEndColor = 0xFF99662F;
			CardListActivity._cardSkins[2].ButtonSufface = new ButtonSkin();
			CardListActivity._cardSkins[2].ButtonSufface.TextColor = 0xFF000000;
			CardListActivity._cardSkins[2].ButtonSufface.GradientStartColor = 0xFFEFEFEF;
			CardListActivity._cardSkins[2].ButtonSufface.GradientEndColor = 0xFF666666;

			CardListActivity._cardSkins[3] = new CardSkin();
			CardListActivity._cardSkins[3].CardSufface = new ButtonSkin();
			CardListActivity._cardSkins[3].CardSufface.TextColor = 0xFF000000;
			CardListActivity._cardSkins[3].CardSufface.GradientStartColor = 0xFF9DEF44;
			CardListActivity._cardSkins[3].CardSufface.GradientEndColor = 0xFF66992F;
			CardListActivity._cardSkins[3].ButtonSufface = new ButtonSkin();
			CardListActivity._cardSkins[3].ButtonSufface.TextColor = 0xFF000000;
			CardListActivity._cardSkins[3].ButtonSufface.GradientStartColor = 0xFFEFEFEF;
			CardListActivity._cardSkins[3].ButtonSufface.GradientEndColor = 0xFF666666;

		}
		return CardListActivity._cardSkins;
	}

	public static Drawable GetCardBackground(ButtonSkin skin, float topCornerRadii, float bottomCornerRadii, boolean twoStates, int strokeColor)
	{
		StateListDrawable stateListDrawable = new StateListDrawable();
		GradientDrawable gdPressed = twoStates?
				new GradientDrawable(
						GradientDrawable.Orientation.TOP_BOTTOM,
						new int[] {skin.GradientStartColor,skin.GradientStartColor}):null;
						GradientDrawable gdNormal = 
								new GradientDrawable(
										GradientDrawable.Orientation.TOP_BOTTOM,
										new int[] {skin.GradientStartColor,skin.GradientEndColor});

						if(gdPressed != null)
							gdPressed.setCornerRadii(new float[]{
									topCornerRadii,
									topCornerRadii,
									topCornerRadii,
									topCornerRadii,
									bottomCornerRadii,
									bottomCornerRadii,
									bottomCornerRadii,
									bottomCornerRadii});
						gdNormal.setCornerRadii(new float[]{					
								topCornerRadii,
								topCornerRadii,
								topCornerRadii,
								topCornerRadii,
								bottomCornerRadii,
								bottomCornerRadii,
								bottomCornerRadii,
								bottomCornerRadii});

						if(gdPressed != null)
						{
							stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, gdPressed);
							gdNormal.setStroke(1, strokeColor);// 0xFFFFFFFF);
						}
						else
							gdNormal.setStroke(2, strokeColor);// 0xFFFFFFFF);
						stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, gdNormal);
						return stateListDrawable;
	}

	private String selectedCardId = null; 
	private HashMap<String, CardListItem> dicCardListItem;
	private String savedCsvCardListStr = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try
		{
			setContentView(R.layout.activity_card_list);
			
			ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView1);
			scrollView.setVerticalScrollBarEnabled(false);
			scrollView.setHorizontalScrollBarEnabled(false);

			Intent intent = getIntent();
			String csvCardListStr = intent.getStringExtra(eWallet.CARDLIST);
			String accountState = intent.getStringExtra(eWallet.ACCOUNT_STATE);
			if(!eWallet.StringIsNullOrEmpty(accountState) && accountState.equals("4")){
				this.savedCsvCardListStr = csvCardListStr;
				
				eWallet.ShowDialog(this, 
						"Profile Not Complete", "You profile is not complete, user name and phone number are required for mobile order, please fill in the missing information.",
						new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog,int id) {									
									dialog.cancel();
									CardListActivity.this.editProfile();
								}
							}
						);
			}
			else if(csvCardListStr.length() == 0){
				this.addCard();
			}
			else{
				this.showCardList(csvCardListStr);
			}
		}
		catch(Exception e)
		{
			eWallet.ShowDialog(this, "Error!", e.toString());
		}
	}

	private void showCardList(String csvCardListStr)
	{
		try
		{
			Csv csvCardList = new Csv(csvCardListStr);
			this.dicCardListItem = new HashMap<String, CardListItem>();
			HashMap<String, String> dicDownloadImageNames = new HashMap<String, String>();
			StringBuilder sbDownloadImageParm = new StringBuilder();
			SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			for(String key : csvCardList.keySet())
			{
				CardListItem cardListItem = new CardListItem(csvCardList.get(key));
				this.dicCardListItem.put(key,cardListItem);
				if(cardListItem.Icon != null && cardListItem.Icon.length() > 0)
				{
					if (!dicDownloadImageNames.containsKey(cardListItem.Icon))
					{
						File file = new File(this.getFilesDir(), cardListItem.Icon);
						boolean fileExists = file.exists();
						String iconVerKey = String.format("ICON_VER_%s",cardListItem.Icon);
						String localIconVer = sharedPref.getString(iconVerKey,"");
						if (!fileExists || !localIconVer.equals(cardListItem.IconVer))
						{
							if (sbDownloadImageParm.length() > 0)
								sbDownloadImageParm.append(",");
							sbDownloadImageParm.append(cardListItem.Icon);
							dicDownloadImageNames.put(cardListItem.Icon, null);
						}
					}
				}
			}

			if (sbDownloadImageParm.length() > 0)
			{
				Csv csvRespDownloadImages = eWallet.CallMobilePay(this,"DownloadImages", sbDownloadImageParm.toString(), "Download Images Failed");
				if (csvRespDownloadImages.containsKey("ImageData"))
				{
					SharedPreferences.Editor editor = sharedPref.edit();
					Csv csvImageData = new Csv(csvRespDownloadImages.get("ImageData"));
					for(String imageName : csvImageData.keySet())
					{
						String imageData = csvImageData.get(imageName);
						int firstComma = imageData.indexOf(",");
						String iconVer = imageData.substring(0,firstComma);
						byte[] imageBytes = Base64.decode(imageData.substring(firstComma + 1),Base64.DEFAULT);
						File file = new File(this.getFilesDir(), imageName);
						FileOutputStream fileOutputStream = new FileOutputStream(file);
						fileOutputStream.write(imageBytes, 0, imageBytes.length);
						fileOutputStream.close();
						String iconVerKey = String.format("ICON_VER_%s",imageName);
						editor.putString(iconVerKey, iconVer);
					}
					editor.commit();
				}
			}

			LinearLayout linearLayout_content = (LinearLayout)findViewById(R.id.linearLayout_content);

			linearLayout_content.removeAllViews();
			
			LinearLayout.LayoutParams tvImage_LayoutParams = new LinearLayout.LayoutParams(-1, -2);
			tvImage_LayoutParams.width = 0;
			tvImage_LayoutParams.weight = 0.2f;

			LinearLayout.LayoutParams ll_middle_LayoutParams = new LinearLayout.LayoutParams(-1, -2);
			ll_middle_LayoutParams.width = 0;
			ll_middle_LayoutParams.weight = 0.8f;

			LinearLayout.LayoutParams ll_row_LayoutParams = new LinearLayout.LayoutParams(-1, -2);
			ll_row_LayoutParams.setMargins(8, 2, 8, 0);

			LinearLayout.LayoutParams ll_gap_LayoutParams = new LinearLayout.LayoutParams(-1, 4);
			ll_gap_LayoutParams.setMargins(0, 0, 0, 0);

			for(String key : csvCardList.keySet())
			{
				CardListItem cardListItem = this.dicCardListItem.get(key);

				CardSkin cardSkin = CardListActivity.GetPredefinedCardSkins()[eWallet.ParseToInt(cardListItem.Skin,0)];

				File file = new File(this.getFilesDir(), cardListItem.Icon);
				Drawable img = null;
				if(file.exists())
				{
					img = Drawable.createFromPath(file.getPath());
					img.setBounds( 0, 0, 120, 86 );
				}

				TextView tvImage = new TextView(this);
				tvImage.setCompoundDrawables( img, null, null, null );
				tvImage.setCompoundDrawablePadding(20);
				tvImage.setLayoutParams(tvImage_LayoutParams);

				LinearLayout ll_middle = new LinearLayout(this);
				ll_middle.setPadding(5, 5, 5, 5);
				ll_middle.setOrientation(LinearLayout.VERTICAL);
				ll_middle.setLayoutParams(ll_middle_LayoutParams);
				//ll_middle.setBackgroundColor(0xff00ff00);//for test

				TextView tvName = new TextView(this);
				tvName.setText(cardListItem.Name);
				tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				tvName.setTextColor(cardSkin.CardSufface.TextColor);
				TextView tvAcct = new TextView(this);
				tvAcct.setText(cardListItem.Account);
				tvAcct.setTextColor(cardSkin.CardSufface.TextColor);
				ll_middle.addView(tvName);
				ll_middle.addView(tvAcct);

				LinearLayout ll_row = new LinearLayout(this);
				ll_row.setLayoutParams(ll_row_LayoutParams);
				ll_row.setTag(key);
				ll_row.setBackgroundDrawable(CardListActivity.GetCardBackground(cardSkin.CardSufface,30f,0f,true,0xFFFFFFFF));
				ll_row.setPadding(20, 25, 20, 10);
				ll_row.addView(tvImage);
				ll_row.addView(ll_middle);

				ll_row.setClickable(true);
				ll_row.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						String cardId = (String)v.getTag();
						CardListActivity.this.showCardPayActivity(cardId);
					} 
				});
				ll_row.setOnLongClickListener(new OnLongClickListener(){
					public boolean onLongClick(View v) {
						CardListActivity.this.selectedCardId = (String)v.getTag();

						AlertDialog.Builder builder = new AlertDialog.Builder(CardListActivity.this);
						builder.setTitle("Select an option");
						builder.setCancelable(true);
						builder.setNegativeButton("Cancel", null);
						builder.setItems(CardListActivity.onHoldOptions, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch(which)
								{
								case 0://Scan
									CardListActivity.this.showCardPayActivity(CardListActivity.this.selectedCardId);
									break;
								case 1:
									CardListActivity.ShowCardReceiptList(CardListActivity.this, 
											CardListActivity.this.selectedCardId, 
											CardListActivity.this.dicCardListItem.get(CardListActivity.this.selectedCardId).Name);
									break;
								case 2://Rename
									Intent intent = new Intent(CardListActivity.this, EditCardActivity.class);
									intent.putExtra(eWallet.CARDID, CardListActivity.this.selectedCardId);
									startSingleActivityForResult(intent,eWallet.REQUEST_CODE__EDIT_CARD);
									break;
								case 3://Delete
									eWallet.ShowYesNoDialog(CardListActivity.this, "Delete", "Are you sure you want to delete this card.", new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface dialog,int id) {
											if(CardListActivity.DeleteCard(CardListActivity.this, CardListActivity.this.selectedCardId))
											{
												CardListActivity.this.refreshCardList();
											}
										}
									});
									break;
								}

							}
						});
						builder.show();

						return true;
					}
				});
				linearLayout_content.addView(ll_row);

				LinearLayout ll_gap = new LinearLayout(this);
				ll_gap.setLayoutParams(ll_gap_LayoutParams);
				ll_gap.setBackgroundColor(0xcf9f9f9f);
				linearLayout_content.addView(ll_gap);
			}
		}
		catch(Exception e)
		{
			eWallet.ShowDialog(this, "Error!", e.toString());
		}
	}

	private void showCardPayActivity(String cardId)
	{
		Intent intent = new Intent(CardListActivity.this, CardPayActivity.class);
		intent.putExtra(eWallet.CARDID, cardId);
		startSingleActivityForResult(intent,eWallet.REQUEST_CODE__SHOW_CARD);
	}

	private void refreshCardList()
	{
		Csv csvResp = eWallet.CallMobilePay(this, "GetUserCardList", "Ver=1", "Get User Card List Failed");
		if(csvResp != null)
		{
			if(csvResp.containsKey("CardList"))
			{
	        	TableLayout toolbar = (TableLayout)this.findViewById(R.id.tableLayout_toolBar);
	        	toolbar.requestLayout();
				this.showCardList(csvResp.get("CardList"));
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case eWallet.REQUEST_CODE__SHOW_CARD:
		case eWallet.REQUEST_CODE__ADD_CARD:
		case eWallet.REQUEST_CODE__EDIT_CARD:
//        	TableLayout toolbar = (TableLayout)this.findViewById(R.id.tableLayout_toolBar);
//        	toolbar.requestLayout();
			if(data != null){
				if(data.getStringExtra(eWallet.RELOAD) != null){
					this.refreshCardList();
				}
			}
			break;
		case eWallet.REQUEST_CODE__WEB_ORDER:
			this.refreshCardList();
			break;
		case eWallet.REQUEST_CODE__EDIT_PROFILE:
			if(this.savedCsvCardListStr != null){
				String csvCardListStr = this.savedCsvCardListStr;
				this.savedCsvCardListStr = null;
				if(csvCardListStr.length() == 0){
					this.addCard();
				}
				else{
					this.showCardList(csvCardListStr);
				}
			}
			break;
		default:
		}
	}

	private void okClose(){
		Intent intent = new Intent();
		this.setResult(RESULT_OK,intent);
		this.finish();
	}

	private void editProfile(){
		Intent intent = new Intent(this, EditUserActivity.class);
		startSingleActivityForResult(intent,eWallet.REQUEST_CODE__EDIT_PROFILE);
	}

	private void buyNewCard(){
		Intent intent = new Intent(this, WebOrder.class);
		intent.putExtra(eWallet.CARDID, "");
		intent.putExtra(eWallet.WEB_ORDER_SITE_PATH, eWallet.WEB_ORDER_SITE_PATH__GIFT_CARD_SALE);
		startSingleActivityForResult(intent,eWallet.REQUEST_CODE__WEB_ORDER);
	}
	
	private void addCardFromScanningActivationReceipt(){
		Intent intent = new Intent(this, AddCardActivity.class);
		startSingleActivityForResult(intent,eWallet.REQUEST_CODE__ADD_CARD);
	}
	
	private void addCard(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(CardListActivity.this);
		builder.setTitle("Select an option");
		builder.setCancelable(true);
		builder.setNegativeButton("Cancel", null);
		builder.setItems(CardListActivity.addCardOptions, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which)
				{
				case 0://Buy new card online
					CardListActivity.this.buyNewCard();
					break;
				case 1://Scan card activation receipt
					CardListActivity.this.addCardFromScanningActivationReceipt();
					break;
				}
			}
		});
		builder.show();
	}

	public void button_exit_onClick(View view){
		this.okClose();
	}

	public void button_editProfile_onClick(View view){
		this.editProfile();
	}

	public void button_addCard_onClick(View view){
		this.addCard();
	}

}
