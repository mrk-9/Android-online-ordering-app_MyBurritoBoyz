package com.winrestenterprise.ewallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Csv {

	private HashMap<String,String> map;
	private List<String> listKeys;
	public Csv()
	{
		this.map = new HashMap<String, String>();
		this.listKeys = new ArrayList<String>();
	}
	
	public Csv(String csvString)
	{
		this.map = new HashMap<String, String>();
		this.listKeys = new ArrayList<String>();
	    String[] valuePairs = csvString.split(",");
	    for (int i = 0; i< valuePairs.length; i++)
	    {
	    	String pair = valuePairs[i];
	        String[] pairStrs = pair.split("=");
	        if (pairStrs.length == 2)
	        {
	        	String key = pairStrs[0].trim();
	        	this.listKeys.add(key);
	        	map.put(key,
	                pairStrs[1].trim().replace("%3d", "=").replace("%2c", ",").replace("%20", " ").replace("%25", "%")
	                );
	        }
	    }
	}
	
	public Csv(String[] keys, String text)
	{
		this.map = new HashMap<String, String>();
		this.listKeys = new ArrayList<String>();
	    String[] values = text.split(",");
	    for (int i = 0; i< values.length; i++)
	    {
	    	this.listKeys.add(keys[i]);
	    	map.put(keys[i],
	    			values[i].trim().replace("%3d", "=").replace("%2c", ",").replace("%20", " ").replace("%25", "%")
	    			);
	    }
	}
	
	public String[] keySet()
	{
		return this.listKeys.toArray(new String[this.listKeys.size()]);
	}
	
	public String get(String key)
	{
		return this.map.get(key);
	}
	
	public void put(String key, String value)
	{
		if(!this.map.containsKey(key))
			this.listKeys.add(key);
		this.map.put(key, value);
	}
	
	public boolean containsKey(String key)
	{
		return this.map.containsKey(key);
	}
	
	public String toCsvString()
	{
		StringBuilder sb = new StringBuilder();
		for(String key:this.listKeys)
		{
			if(sb.length() > 0)
				sb.append(",");
			sb.append(key);
			sb.append("=");
			sb.append(this.map.get(key).replace("%","%25").replace(" ","%20").replace(",","%2c").replace("=","%3d"));
		}
		return sb.toString();
	}
}
