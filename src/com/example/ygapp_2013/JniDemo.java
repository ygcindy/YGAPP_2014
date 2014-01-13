package com.example.ygapp_2013;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class JniDemo extends Activity {

	public native String stringFromJNI();
	public native String stringFromJNI_2();
	public native int add(int x,int y);
	
	//further development..use class 
	private native int add();
	private class JniDemoAddElement{
		private int x;
		private int y;
		private int result;
		public JniDemoAddElement(int x,int y){
			this.x = x;
			this.y = y;			
		}
		public void add(){
			result = x + y;
		}
		public int getResult(){
			return result;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jni_demo);
		((TextView)findViewById(R.id.tv_jni_text)).setText(stringFromJNI());
		((Button)findViewById(R.id.btn_jni_add)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int x,y,result;
				
				((TextView)findViewById(R.id.tv_jni_add_result)).setText("");
				
				try{
				x = Integer.parseInt(((EditText)findViewById(R.id.et_add_element_1)).getText().toString());
				y = Integer.parseInt(((EditText)findViewById(R.id.et_add_element_2)).getText().toString());
				}
				catch(NumberFormatException e){
					return;					
				}
				JniDemoAddElement addelement = new JniDemoAddElement(x,y);
				result = add(x,y);
				((TextView)findViewById(R.id.tv_jni_add_result)).setText(Integer.toString(result));				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jni_demo, menu);
		return true;
	}

	static{
		System.loadLibrary("hello-jni");		
	}
}
