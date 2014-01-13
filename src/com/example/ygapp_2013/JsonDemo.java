package com.example.ygapp_2013;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class JsonDemo extends Activity {
	private TextView tv_json = null;
	private String m_json_url = "http://www.huntertel.cn/jefedownload/android/HT_addsub_update.json";
	private String getContentFromServer()
	{
		String paramString = m_json_url;
		
		  StringBuilder localStringBuilder = new StringBuilder();
		    DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
		    HttpParams localHttpParams = localDefaultHttpClient.getParams();
		    HttpConnectionParams.setConnectionTimeout(localHttpParams, 3000);
		    HttpConnectionParams.setSoTimeout(localHttpParams, 5000);
		    HttpEntity localHttpEntity = null;
			try {
				localHttpEntity = localDefaultHttpClient.execute(new HttpGet(paramString)).getEntity();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    BufferedReader localBufferedReader = null;
		    if (localHttpEntity != null)
				try {
					localBufferedReader = new BufferedReader(new InputStreamReader(localHttpEntity.getContent(), "GB2312"), 8192);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    while (true)
		    {
		      String str = null;
			try {
				str = localBufferedReader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      if (str == null)
		      {
		        try {
					localBufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        return localStringBuilder.toString();
		      }
		      localStringBuilder.append(str + "\n");
		    }
	}	
	private class JsonAsyncTask extends AsyncTask<String,Void,String>
	{
		@Override
		protected String doInBackground(String... params) {
			
			// TODO Auto-generated method stub
			String paramString = params[0];
			
			  StringBuilder localStringBuilder = new StringBuilder();
			    DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
			    HttpParams localHttpParams = localDefaultHttpClient.getParams();
			    HttpConnectionParams.setConnectionTimeout(localHttpParams, 3000);
			    HttpConnectionParams.setSoTimeout(localHttpParams, 5000);
			    HttpEntity localHttpEntity = null;
				try {
					localHttpEntity = localDefaultHttpClient.execute(new HttpGet(paramString)).getEntity();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    BufferedReader localBufferedReader = null;
			    if (localHttpEntity != null)
					try {
						localBufferedReader = new BufferedReader(new InputStreamReader(localHttpEntity.getContent(), "GB2312"), 8192);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    while (true)
			    {
			      String str = null;
				try {
					if( localBufferedReader != null )
						str = localBufferedReader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			      if (str == null)
			      {
			        try {
			        	if( localBufferedReader != null )
			        		localBufferedReader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        String str1 = localStringBuilder.toString();
			        //JsonDemo.this.runOnUiThread(new RunableForUpdate(str1));
			        return str1;
			      }
			      localStringBuilder.append(str + "\n");
			    }
		}

	    protected void onPostExecute(String result) {
	    	JSONTokener jsonParser = new JSONTokener(result); 
	    	try {
				//JSONObject ob = (JSONObject)jsonParser.nextValue();
				JSONArray ja = (JSONArray)jsonParser.nextValue();
				JSONObject ob= ja.getJSONObject(0);
				String str = ob.getString("appname");
				str = ob.getString("apkname");
				
				str = ob.getString("apkUrl");
				str = ob.getString("verCode");
				str = ob.getString("verName");
				
				Log.v("yuangui",ob.getString("appname"));
				Log.v("yuangui",ob.getString("apkname"));
				Log.v("yuangui",ob.getString("apkUrl"));
				Log.v("yuangui",ob.getString("verCode"));
				Log.v("yuangui",ob.getString("verName"));					
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	tv_json.setText(result);	
	    }		
		
	}
	
    private class RunableForUpdate implements Runnable{
    	String m_str;
    	RunableForUpdate(String str)
    	{
    		m_str = str;    		
    	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			tv_json.setText(m_str);
		}  	
    	
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//for asynctask onpostexecute
//		1.http://code.google.com/p/android/issues/detail?id=20915
//		2.http://stackoverflow.com/questions/4280330/onpostexecute-not-being-called-in-asynctask-handler-runtime-exception		
//		try {
//			Class.forName("android.os.AsyncTask");
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_json_demo);
		

		Button fectch = (Button)findViewById(R.id.btn_fetch_json);
		fectch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new JsonAsyncTask().execute(m_json_url);
			}});
		tv_json = (TextView)findViewById(R.id.tv_json_detail);
		
		//new JsonAsyncTask().execute(m_json_url);
//		new Thread(){
//				public void run(){
//					String str = getContentFromServer();
//					JsonDemo.this.runOnUiThread(new RunableForUpdate(str));
//				}}.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.json_demo, menu);
		return true;
	}

}
