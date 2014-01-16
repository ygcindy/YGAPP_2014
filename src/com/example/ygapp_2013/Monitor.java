package com.example.ygapp_2013;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Monitor extends Activity {
	final private String m_app_name = "Monitor";
	final private int m_capture_request_code = 1;
	private ImageView m_capture_image = null;
	private String m_image_path = null;
	private Button m_capture_btn= null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		
		m_capture_btn = (Button) findViewById(R.id.btn_monitor_capture);
		m_capture_btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_image_path = CreateImagePath();
				Intent capture_intent = new Intent();
				capture_intent.setClassName(getApplicationContext(), "com.example.ygapp_2013.CameraAssitant");
				Uri data  = null;
				data = Uri.parse(m_image_path);
				capture_intent.setData(data);
				//startActivity(capture_intent);
				startActivityForResult(capture_intent, m_capture_request_code);
				m_capture_btn.setEnabled(false);
			}
		});
		m_capture_btn.setEnabled(true);
		m_capture_image = (ImageView) findViewById(R.id.imagev_monitor_image);
	}
	private String CreateImagePath(){
		String path = null;
		String storage_state = Environment.getExternalStorageState();		
		if( !storage_state.equals(Environment.MEDIA_MOUNTED))
			return null;

		path = Environment.getExternalStorageDirectory().getPath() + File.separator + m_app_name + File.separator;
		File filefolder = new File(path);
		if( !filefolder.exists() ){
			filefolder.mkdirs();		
		}
		
		Calendar calen = Calendar.getInstance();		
		Date date = calen.getTime();
		SimpleDateFormat simple_df = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault());//yyyyMMdd_HHmmss_S
		String datestr = simple_df.format(date);
		System.out.println(datestr);
		path += datestr + ".jpg";
		
		return path;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);		
		if( resultCode == RESULT_OK ){
			if( requestCode == m_capture_request_code ){
//				BitmapFactory bf = new BitmapFactory();
				Bitmap bt = BitmapFactory.decodeFile(m_image_path);
				if( bt != null ){
					m_capture_image.setImageBitmap(bt);		
				}

			}			
		}
		m_capture_btn.setEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.monitor, menu);
		return true;
	}

}
