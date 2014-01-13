package com.example.ygapp_2013;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class broadcast_entity extends BroadcastReceiver{
	//private WifiManager mWifiManager;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if( action.equals(Intent.ACTION_WALLPAPER_CHANGED))
		{
			Toast.makeText(context, "The wallpaper is changed!", Toast.LENGTH_LONG).show();
		}
	}
}