package com.example.ygapp_2013;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EatBeanActivity extends Activity {
	private EatBeanAnimationView eatbeanview;
	//private EditText edittext;
	private NumericLimitEditText edittext2;
	private Thread m_workthread=null;
	private Boolean m_threadloop = true;
	class EatBeanAnimationView extends View{
		private Bitmap ball_bitmap;
		private Paint mypaint;
		private float currentx,currenty;
		private int gap;
		public EatBeanAnimationView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			ball_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
			mypaint = new Paint();
			currentx = 0;
			currenty = 60;
			SetThreadSleep(1000);
		}
		public int GetBmpWidth(){
			return ball_bitmap.getWidth();
		}		
		public void SetThreadSleep(int gap){
			this.gap = gap;
		}
		public int GetThreadSleep(){
			return gap;
		}	
		public float GetX(){
			return currentx;
		}		
		public float GetY(){
			return currenty;
		}		
		public void SetX(float x){
			currentx = x;
		}			
		public void SetY(float y){
			currenty = y;
		}	
		public void SetXY(int x,int y){
			currentx = x;
			currenty = y;
		}		
		@Override
		protected
		void onDraw(Canvas canvas){	
			canvas.drawBitmap(ball_bitmap, currentx, currenty, mypaint);
		}		
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eat_bean);
		eatbeanview = new EatBeanAnimationView(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.addContentView(eatbeanview, params);
		
		findViewById(R.id.btn_start).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Button btn = (Button)v;
				if( m_workthread != null ){
					btn.setText(R.string.str_start);
					m_threadloop = false;
					m_workthread.interrupt();
					m_workthread = null;
				}
				else{					
					btn.setText(R.string.str_stop);
					m_threadloop=true;
					m_workthread = new Thread(new Runnable()
					{
						@Override
						public void run() {
							// TODO Auto-generated method stub
							while(m_threadloop){
								int right = eatbeanview.getRight();
								int ballwidth = eatbeanview.GetBmpWidth();
								float currentx = eatbeanview.GetX();
								//float currenty = eatbeanview.GetY();							
								if( currentx < right - ballwidth - 20){
									currentx += 20;									
								}
								else {
									currentx = 0;	
								}
								eatbeanview.SetX(currentx);
								eatbeanview.postInvalidate();
								try {
									Thread.sleep(eatbeanview.GetThreadSleep());
								} 
								catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}							
							}
						}
							
						}					
							
					);
					m_workthread.start();
				}
			}});
		findViewById(R.id.btn_set).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try{
					int gap = Integer.parseInt(edittext2.getText().toString());
					if( gap >=50 && gap <= 5000)						
						eatbeanview.SetThreadSleep(gap);
					else 
						Toast.makeText(getApplicationContext(), "50-5000", Toast.LENGTH_SHORT).show();
				}
				catch(NumberFormatException e){
					e.printStackTrace();					
				}
				
			}});
	
		/*
		edittext = (EditText)findViewById(R.id.editText_send);
		edittext.setHint("50-5000");//ms
		*/
		
		edittext2 = new NumericLimitEditText(this);
		edittext2.setRange(50,5000);
		edittext2.setHint("50-5000");//ms
		LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.addContentView(edittext2, params2);		
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		
	//	int[] location = new int[2];
	//	edittext.getLocationInWindow(location);
	//	int bottom = edittext.getBottom();
	//	eatbeanview.SetY(bottom + 20);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.eat_bean, menu);
		return true;
	}

}
