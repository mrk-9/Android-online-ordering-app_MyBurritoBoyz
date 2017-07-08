package com.winrestenterprise.ewallet;

public class UserProfile {
	
    public String[] GetCsvFieldNames()
    {
        return new String[]{
                        "Id",
                        "Name",
                        "Phone",
                        "Address",
                        "Zip_Code",
                        "Email"
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
    	return String.format("%s,%s,%s,%s,%s,%s", 
    			this.Id,
    			this.Name,
    			this.Phone,
    			this.Address,
    			this.Zip_Code,
    			this.Email);
    }
    
    public int Id;
    
    public String Name;
    
    public String Phone;

    public String Address;

    public String Zip_Code;

    public String Email;

    public UserProfile()
    {
    }

    public UserProfile(String csvString)
    {
    	Csv map = new Csv(this.GetCsvFieldNames(),csvString);
    	this.Id = Integer.parseInt(map.get("Id"));
    	this.Name = map.get("Name");
    	this.Phone = map.get("Phone");
    	this.Address = map.get("Address");
    	this.Zip_Code = map.get("Zip_Code");
    	this.Email = map.get("Email");
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
