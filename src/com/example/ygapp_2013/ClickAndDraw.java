package com.example.ygapp_2013;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class ClickAndDraw extends Activity {
	private YGScreenDrawView bgView;
	
	private class YGScreenDrawView extends View{

		private float clickx,clicky;
		private Paint paint;
		
		public YGScreenDrawView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			paint = new Paint();
			
			clickx = 100;
			clicky = 100;
			this.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if( event.getAction() == MotionEvent.ACTION_DOWN ){
						 clickx = event.getX(0);
						 clicky = event.getY(0);
						
						invalidate();
					}					
					return false;
				}});
		}

		
		@Override
		protected
		void onDraw(Canvas canvas){
			//canvas.drawColor(Color.BLACK);
			canvas.drawCircle(clickx, clicky, 100, paint);
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		bgView = new YGScreenDrawView(this);
		
		setContentView(bgView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.click_and_draw, menu);
		return true;
	}

}
