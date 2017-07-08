package com.winrestenterprise.ewallet;

public class CardListItem {
    public String[] GetCsvFieldNames()
    {
        return new String[]{
                        "Name",
                        "Card_Type",
                        "Account",
                        "Icon",
                        "IconVer",
                        "Skin"
                        };
    }
    
    public String Name;
    
    public String Card_Type;

    public String Account;
    
    public String Icon;
    
    public String IconVer;
    
    public String Skin;
    
    public CardListItem()
    {
    }
    
    public CardListItem(String csvString)
    {
    	Csv map = new Csv(this.GetCsvFieldNames(),csvString);
    	this.Name = map.get("Name");
    	this.Card_Type = map.get("Card_Type");
    	this.Account = map.get("Account");
    	this.Icon = map.get("Icon");
    	this.IconVer = map.get("IconVer");
    	this.Skin = map.get("Skin");
    }
    
    public String toCsvString()
    {
    	return String.format("%s,%s,%s,%s,%s,%s",
    			this.Name,
    			this.Card_Type,
    			this.Account,
    			this.Icon,
    			this.IconVer,
    			this.Skin);
    }
}
