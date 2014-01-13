package com.example.ygapp_2013;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class ChargeInquire extends Activity {
	//add for storage
	private String m_str_repository = "repository";	
	private String m_str_dest_address = "dest_address";
	private String m_str_cmd = "cmd";
	private String m_str_latest_inquire_time = "inquire_time";	
	private SharedPreferences m_sp = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge_inquire);
		Button btn_inquire = (Button)findViewById(R.id.btn_inquire_charge);

		m_sp = getApplicationContext().getSharedPreferences(m_str_repository, MODE_PRIVATE);
		String dest_address = m_sp.getString(m_str_dest_address, "10086");
		String cmd = m_sp.getString(m_str_cmd, "YECX");
		String lastest_time = m_sp.getString(m_str_latest_inquire_time, null);
		((EditText)findViewById(R.id.editText_inquire_charge_number)).setText(dest_address);
		((EditText)findViewById(R.id.editText_inquire_charge_cmd)).setText(cmd);
		if(lastest_time != null){
			((TextView)findViewById(R.id.textView_recent_inquire_time)).setText("The latest access time is " + lastest_time);
		}
		
		btn_inquire.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String sms_resuslt_str = "com.example.ygapp_2013.ChargeInquireSmsResult";
				Intent intent = new Intent(sms_resuslt_str);
				PendingIntent pendintent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
				String destinationAddress,send_text;
				destinationAddress = ((EditText)findViewById(R.id.editText_inquire_charge_number)).getText().toString();
				send_text = ((EditText)findViewById(R.id.editText_inquire_charge_cmd)).getText().toString();
				
				Calendar calen = Calendar.getInstance();
				String dt = calen.getTime().toString();	
				((TextView)findViewById(R.id.textView_recent_inquire_time)).setText("The latest access time is " + dt);
				
				//save it
				Editor ed = m_sp.edit();
				ed.putString(m_str_dest_address, destinationAddress);
				ed.putString(m_str_cmd, send_text);				
				ed.putString(m_str_latest_inquire_time, dt);				
				ed.commit();
				
				SmsManager sms_manager = SmsManager.getDefault();
				try{
					sms_manager.sendTextMessage(destinationAddress, null, send_text, pendintent, null);
					Toast.makeText(getApplicationContext(), "Sending...", Toast.LENGTH_SHORT).show();
					((Button)findViewById(R.id.btn_inquire_charge)).setEnabled(false);
				}
				catch(IllegalArgumentException e){
					Toast.makeText(getApplicationContext(), "Arguements are error...Please check again", Toast.LENGTH_LONG).show();					
				}
				getApplicationContext().registerReceiver(new BroadcastReceiver() {
					
					@Override
					public void onReceive(Context context, Intent intent) {
						// TODO Auto-generated method stub
						switch(getResultCode()){
						case Activity.RESULT_OK:
							Toast.makeText(getApplicationContext(), "Send Successfully!", Toast.LENGTH_SHORT).show();	
							break;
						default:
							Toast.makeText(getApplicationContext(), "Send Failed!", Toast.LENGTH_SHORT).show();
							break;
						}
					}
				}, new IntentFilter(sms_resuslt_str));
				
				getApplicationContext().registerReceiver(new sms_reciever(),new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.charge_inquire, menu);
		return true;
	}

	private class sms_reciever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				StringBuilder body = new StringBuilder();// 短信内容
				StringBuilder number = new StringBuilder();// 短信发件人
				
				Object[] _pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] message = new SmsMessage[_pdus.length];
				for (int i = 0; i < _pdus.length; i++) {
					message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
				}				
				
				number.append(message[0].getDisplayOriginatingAddress());
				String smsNumber = number.toString();
				if (smsNumber.startsWith("+86")) {
					smsNumber = smsNumber.substring(3);
				}
				// :确认该短信内容是否满足过滤条件
				if ( !smsNumber.equals(m_sp.getString(m_str_dest_address,null))) {
					return;
				}
				
				for (SmsMessage currentMessage : message) {
					body.append(currentMessage.getDisplayMessageBody());					
				}
				String smsBody = body.toString();	
				
				 Pattern p = Pattern.compile("当前余额为"+"([[\\u0030-\\u0039][\\u002E]]+)"+"元");
				 Matcher m = p.matcher(smsBody);
				 if (m.find()) { 
				     String charge = m.group(0); //m.group(1)为金额数字
				     ((TextView)findViewById(R.id.textView_charge)).setText(charge);
				     ((Button)findViewById(R.id.btn_inquire_charge)).setEnabled(true);
				 }				
			}			
		}		
	}
}
