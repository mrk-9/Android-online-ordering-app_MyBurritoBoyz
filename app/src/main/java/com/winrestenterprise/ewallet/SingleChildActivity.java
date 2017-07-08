package com.winrestenterprise.ewallet;

import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class SingleChildActivity  extends Activity  {

	private final ReentrantLock singleChildLock = new ReentrantLock ();
	private boolean hasChild = false;
	
	public SingleChildActivity() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
	public void startSingleActivityForResult (Intent intent, int requestCode) 
	{
		this.singleChildLock.lock();
		if(!this.hasChild)
		{
			this.startActivityForResult(intent,requestCode);
			this.hasChild = true;
		}
		this.singleChildLock.unlock();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.singleChildLock.lock();
		if(this.hasChild)
		{
			this.hasChild = false;
		}
		this.singleChildLock.unlock();
	}
}
