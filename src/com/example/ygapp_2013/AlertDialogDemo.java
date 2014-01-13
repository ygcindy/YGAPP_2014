package com.example.ygapp_2013;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AlertDialogDemo extends Activity {
	private AlertDialog m_ad = null;
	
	private class MyArrayAdapter extends ArrayAdapter<String>{
		private int m_viewlayout_id;
		private LayoutInflater mInflater;

		private MyArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			m_viewlayout_id = textViewResourceId;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			String tmp;
			View outview=null;
			
			tmp = getItem(position);
			
	        if (convertView == null) {
	        	try{
	        		outview = (View)mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
	        	}
	        	catch(InflateException e){
	        		e.printStackTrace();	        		
	        	}
	        } else {
	        	outview = (View)convertView;
	        }
	        
	        TextView appname = (TextView)outview.findViewById(android.R.id.text1);
	        appname.setText(tmp + " sth changed.");
	        appname.setTextColor(Color.BLACK);
			return outview;
		}
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert_dialog_demo);
		
		Button btn_alert = (Button) findViewById(R.id.btn_alert);
		btn_alert.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(AlertDialogDemo.this);//activity
				//builder.setMessage("Details");
				builder.setTitle("My alert title");
				String[] str = new String[]{"Male","Female"};
				List<String> list_str = Arrays.asList(str);//new ArrayList<String>();
				//use Arrays.asList(str) instead
//				for(String tmpstr:str){
//					list_str.add(tmpstr);					
//				}
	
//				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_list_item_blacktxt, str);
				MyArrayAdapter adapter = new MyArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list_str);
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ListView lv = m_ad.getListView();
						String str = (String)lv.getItemAtPosition(which);
						Toast.makeText(getApplicationContext(), str/*Integer.toString(which)*/, Toast.LENGTH_SHORT).show();
					}
				});
				builder.setPositiveButton("Return", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//Toast.makeText(getApplicationContext(), Integer.toString(which), Toast.LENGTH_SHORT).show();						
					}
				});
				m_ad = builder.create();
				m_ad.show();
			}
		});
		Button btn_toast = (Button) findViewById(R.id.btn_toast);
		btn_toast.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "I am a toast.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alert_dialog_demo, menu);
		return true;
	}

}
