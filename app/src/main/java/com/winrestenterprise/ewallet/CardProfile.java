package com.winrestenterprise.ewallet;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class CardProfile {
	
    public String[] GetCsvFieldNames()
    {
        return new String[]{
                        "Id",
                        "Name",
                        "Card_Type",
                        "Account",
                        "Expiry",
                        "AvsInfo",
                        "SecurityData",
                        "Icon",
                        "IconVer",
                        "Skin"
                        };
    }

    public String ToCsvString()
    {
//        StringBuilder sb = new StringBuilder();
//        string[] names = this.GetCsvFieldNames();
//        Type type = this.GetType();
//        for (int i = 0; i < names.Length; i++)
//        {
//            PropertyInfo pi = type.GetProperty(names[i], BindingFlags.Public | BindingFlags.Instance);
//            if (sb.Length > 0)
//                sb.Append(",");
//            object propertyValue = pi.GetValue(this, null);
//            sb.Append(propertyValue == null ? string.Empty : propertyValue.ToString());
//        }
        //return sb.ToString();
    	return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", 
    			this.Id,
    			this.Name,
    			this.Card_Type,
    			this.Account,
    			this.Expiry,
    			this.AvsInfo,
    			this.SecurityData,
    			this.Icon,
    			this.IconVer,
    			(this.Skin == null?"":this.Skin)
    			);
    }
    
    public int User_Id;

    public int Id = -1;
    
    public String Name = "";
    
    public String Card_Type = "";

    public String Account = "";

    public String Masked_Acct = "";

    public String Encrypted_Acct = "";

    public String Expiry = "";

    public String AvsInfo = "";

    public String SecurityData = "";

    public String Icon = "";
    
    public String IconVer = "";
    
    public String Skin = "";
    
    public CardProfile()
    {
    }

    public CardProfile(String csvString)
    {
    	Csv map = new Csv(this.GetCsvFieldNames(),csvString);
    	this.Id = Integer.parseInt(map.get("Id"));
    	this.Name = map.get("Name");
    	this.Card_Type = map.get("Card_Type");
    	this.Account = map.get("Account");
    	this.Card_Type = map.get("Card_Type");
    	this.Expiry = map.get("Expiry");
    	this.AvsInfo = map.get("AvsInfo");
    	this.SecurityData = map.get("SecurityData");
    	this.Icon = map.get("Icon");
    	this.IconVer = map.get("IconVer");
    	this.Skin = map.get("Skin");
    	if(this.Skin != null)
    	{
    		if(this.Skin.toLowerCase().trim().compareTo("null") == 0)
    			this.Skin = "";
    	}
    }

    public void Validate()
    {
//        if (string.IsNullOrEmpty(this.Name))
//            throw new Exception("Display Name can not be empty.");
//        if (string.IsNullOrEmpty(this.Card_Type))
//            throw new Exception("Card Type can not be empty.");
//        if (string.IsNullOrEmpty(this.Account))
//            throw new Exception("Account can not be empty.");
    }
}
