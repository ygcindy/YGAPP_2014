package com.example.ygapp_2013;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {
	
	private ListView listview;
	private Map<String,String> map;//<text,activity>
	
    private List<String> getAppText(){        
        List<String> app_names = new ArrayList<String>();

        //遍历整个MAP
        Iterator<Entry<String, String>> it = map.entrySet().iterator();
        while(it.hasNext()){
        	Entry<String, String> entry = it.next();
        	app_names.add(entry.getKey());
        }
     
          
        return app_names;
    }	
    private void InitAppMap()
    {
    	//<text,activity>  
    	map.put("Click&Draw", "com.example.ygapp_2013.ClickAndDraw"); 	
    	map.put("Thread", "com.example.ygapp_2013.ThreadDemo");    
       	map.put("EatBean", "com.example.ygapp_2013.EatBeanActivity");     
       	map.put("ClientAssistant", "com.example.ygapp_2013.ClientAssistant");     
       	map.put("WebViewDemo", "com.example.ygapp_2013.WebViewDemo"); 
       	map.put("BroadcastDemo", "com.example.ygapp_2013.BroadcastDemo");       	
       	map.put("JsonDemo", "com.example.ygapp_2013.JsonDemo");    
       	map.put("PackageManagerDemo", "com.example.ygapp_2013.PackageManagerDemo");    
       	map.put("SpinnerDemo", "com.example.ygapp_2013.SpinnerDemo");       
       	map.put("ImageDemo", "com.example.ygapp_2013.ImageDemo");   
       	map.put("AlertDialogDemo", "com.example.ygapp_2013.AlertDialogDemo");    
       	map.put("StorageDemo", "com.example.ygapp_2013.StorageDemo"); 
       	map.put("NotificationManagerDemo", "com.example.ygapp_2013.NotificationManagerDemo"); 
       	map.put("ChargeInquire", "com.example.ygapp_2013.ChargeInquire");        
       	map.put("JniDemo", "com.example.ygapp_2013.JniDemo");      
       	map.put("ArrayAdapterDemo", "com.example.ygapp_2013.ArrayAdapterDemo");     
       	map.put("WebCatcher", "com.example.ygapp_2013.WebCatcher");           	
       	
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		map = new HashMap<String,String>();
		InitAppMap();

		listview = new ListView(this);
		listview.setAdapter(new ArrayAdapter<String>(this,R.layout.list_item_text,getAppText()));
		setContentView(listview);
		
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String activityname;
				String cursel =(String)parent.getItemAtPosition(position);//可获取指定位置元素的信息，与adapter的参数对应
				
				activityname = map.get(cursel);
				Log.v("yuangui",activityname);
				
				Intent intent = new Intent();
				intent.setClassName(getApplicationContext(), activityname);
				startActivity(intent);
			}		
		}	
		);

	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
