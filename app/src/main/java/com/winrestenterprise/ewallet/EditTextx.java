package com.winrestenterprise.ewallet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextx extends EditText
{
	public String Name;
	public EditTextx(Context context)
	{
		super(context);
	}
	
	public EditTextx(Context context,AttributeSet attrs)
	{
		super(context, attrs);
		int totalAttrs = attrs.getAttributeCount();
		for(int i=0;i<totalAttrs; i++)
		{
			if(attrs.getAttributeName(i).equalsIgnoreCase("name"))
			{
				this.Name = attrs.getAttributeValue(i);
				break;
			}
		}
	}
}
