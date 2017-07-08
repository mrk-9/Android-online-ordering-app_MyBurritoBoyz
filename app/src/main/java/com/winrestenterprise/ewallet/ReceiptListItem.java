package com.winrestenterprise.ewallet;

public class ReceiptListItem {
    public String[] GetCsvFieldNames()
    {
        return new String[]{
                        "MerchantName",
                        "Amount",
                        "TranTime"
                        };
    }
    
    public String MerchantName;
    
    public String Amount;

    public String TranTime;
    
    public ReceiptListItem()
    {
    }
    
    public ReceiptListItem(String csvString)
    {
    	Csv map = new Csv(this.GetCsvFieldNames(),csvString);
    	this.MerchantName = map.get("MerchantName");
    	this.Amount = map.get("Amount");
    	this.TranTime = map.get("TranTime");
    }
}
