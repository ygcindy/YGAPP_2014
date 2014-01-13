package com.example.ygapp_2013;

import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;

public class NotificationManagerDemo extends Activity {

	private int m_nm_id = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_manager_demo);
		
		findViewById(R.id.btn_nm_add).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addNotification(true);
			}
		});
		
		findViewById(R.id.btn_nm_remove).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String ns = Context.NOTIFICATION_SERVICE;
			     NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			     mNotificationManager.cancel(m_nm_id);
			}
		});		
	}

	private void addNotification(boolean owner){
		String ns = Context.NOTIFICATION_SERVICE;

	     NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	     
	     int icon = R.drawable.ball; //通知图标

	     CharSequence tickerText = "Hello"; //状态栏(Status Bar)显示的通知文本提示

	     long when = System.currentTimeMillis(); //通知产生的时间，会在通知信息里显示

	     Notification notification = new Notification(icon, tickerText, when);
	     Context context = getApplicationContext();

	     CharSequence contentTitle = "My notification";

	     CharSequence contentText = "Hello World!";

	     Intent notificationIntent = new Intent(this, MainActivity.class);

	     PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

	     //如果自定义view，则自行设置成员变量
	     if( owner ){
	    	 notification.contentIntent = contentIntent;
	    	 RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_owner_layout);
	    	 contentView.setTextViewText(R.id.textView_appname, contentTitle);
	    	 contentView.setTextColor(R.id.textView_appname, Color.WHITE);
	    	 contentView.setTextViewText(R.id.textView_pacname, contentText);
	    	 contentView.setTextColor(R.id.textView_pacname, Color.CYAN);
	    	 contentView.setImageViewResource(R.id.app_icon, icon);
	    	 
	    	 
	    	 //可对VIEW内各视图设置回调动作
	    	 Intent image_intent = new Intent();
	    	 image_intent.setClass(this, SpinnerDemo.class);
	    	 PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, image_intent, 0);
	    	 
			contentView.setOnClickPendingIntent(R.id.app_icon, pendingIntent );
	    	 notification.contentView = contentView;
	     }
	     else{
	    	 notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	     }
	     
	     mNotificationManager.notify(m_nm_id, notification);		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification_manager_demo, menu);
		return true;
	}

}
