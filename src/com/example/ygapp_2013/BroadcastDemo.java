package com.example.ygapp_2013;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.Toast;

public class BroadcastDemo extends Activity {
	private MyWifiReceiver m_WifiRev = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broadcast_demo);		
	
		m_WifiRev = new MyWifiReceiver();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.broadcast_demo, menu);
		return true;
	}

	@Override
	public void onResume(){
		super.onResume();
		
		String bc_msg = Intent.ACTION_TIME_TICK;
		IntentFilter filter = new IntentFilter();
		filter.addAction(bc_msg);		
		registerReceiver(m_WifiRev, filter);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		unregisterReceiver(m_WifiRev);
	}
	private class MyWifiReceiver extends BroadcastReceiver{
		//private WifiManager mWifiManager;
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if( action.equals(Intent.ACTION_TIME_TICK))
			{
				Calendar calen = Calendar.getInstance();
				String dt = calen.getTime().toString();
				//int diff = calen.getTimeZone().getRawOffset();
				Toast.makeText(context, "Time elapse..." + dt, Toast.LENGTH_SHORT).show();
			}
		}
	}
}

