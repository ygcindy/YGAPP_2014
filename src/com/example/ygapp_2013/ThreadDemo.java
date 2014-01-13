package com.example.ygapp_2013;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ThreadDemo extends Activity {
	private MyHandler my_handler= new MyHandler();
	private MyAsyncTask my_asynctask = null;
	private ProgressBar pgbar = null;
	private class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			switch (msg.what){
			case 0:
                Toast.makeText(getApplicationContext(), "hello,i am a thread", Toast.LENGTH_SHORT).show();  
				break;				
			}			
		}		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thread);
		pgbar = (ProgressBar) findViewById(R.id.progressBar1);
		pgbar.setMax(100);
		
		View button = (View) findViewById(R.id.btn_set);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 new Thread(){
		            public void run(){  
		            	Log.v("yuangui","hello,i am a thread");
		            	Message msg = new Message();
		            	msg.what=0;
		            	my_handler.sendMessage(msg);
		                //Toast.makeText(getApplicationContext(), "hello,i am a thread", Toast.LENGTH_SHORT).show();  
		            }
				}.start();
			}}
		);
		
		Button taskbtn= (Button)findViewById(R.id.btn_start_asynctask);
		taskbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if( my_asynctask != null )
					return;
				my_asynctask = new MyAsyncTask();
				my_asynctask.execute("100");
			}});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.thread, menu);
		return true;
	}

	private class MyAsyncTask extends AsyncTask<String,Integer,String>
	{
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String aim = params[0];
			int total = Integer.parseInt(aim);
			
			int step = 0;			
			while( step < total ){
				++step;
				publishProgress(step);
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return "reach " + step;
		}
		protected void onPreExecute() {
	    	Toast.makeText(getApplicationContext(), "start...", Toast.LENGTH_LONG).show();
	    }		
	    protected void onPostExecute(String result) {
	    	Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
	    }
	    protected void onProgressUpdate(Integer... values) {
	    	pgbar.setProgress(values[0]);
	    }	    
	}
}
