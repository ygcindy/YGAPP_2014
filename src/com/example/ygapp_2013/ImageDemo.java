package com.example.ygapp_2013;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageDemo extends Activity {
	private ImageView m_iv;
	private int m_scale;
	private float m_degrees;	
	private Bitmap m_curbm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_demo);
		
		m_scale = 1;
		m_degrees = 0;
		m_iv = (ImageView) findViewById(R.id.imageView1);
		m_iv.setImageResource(R.drawable.baby);
		
//		BitmapFactory.Options opt = new BitmapFactory.Options();
//		opt.inJustDecodeBounds = true;
//		BitmapFactory.decodeResource(getResources(), R.drawable.baby,opt);
		
		m_curbm = BitmapFactory.decodeResource(getResources(), R.drawable.baby);
		
		findViewById(R.id.btn_image_sub).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if( m_scale >= 32)
					return;				
				m_scale = m_scale<<1;
				BitmapFactory.Options tmpopt = new BitmapFactory.Options();
				tmpopt.inSampleSize = m_scale;
				m_curbm = BitmapFactory.decodeResource(getResources(), R.drawable.baby,tmpopt );
				m_iv.setImageBitmap(m_curbm);
			}
		});
		findViewById(R.id.btn_image_add).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if( m_scale <= 1)
					return;
				m_scale = m_scale>>1;
				BitmapFactory.Options tmpopt = new BitmapFactory.Options();
				tmpopt.inSampleSize = m_scale;
				m_curbm = BitmapFactory.decodeResource(getResources(), R.drawable.baby,tmpopt );
				m_iv.setImageBitmap(m_curbm);			
			}
		});	
		
		findViewById(R.id.btn_image_rotate).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Matrix matrix = new Matrix();
//				if( m_degrees < 360 ){
//					m_degrees += 90;					
//				}
//				else{
//					m_degrees = 0;
//				}
				matrix.setRotate(90);
				m_curbm = Bitmap.createBitmap(m_curbm, 0, 0, m_curbm.getWidth(), m_curbm.getHeight(), matrix, false);
				m_iv.setImageBitmap(m_curbm);
				//m_iv.setImageMatrix(matrix);		
				//m_iv.invalidate();
			}
		});		
		
		findViewById(R.id.btn_image_rotate_little).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Matrix matrix = new Matrix();
//				if( m_degrees < 360 ){
//					m_degrees += 90;					
//				}
//				else{
//					m_degrees = 0;
//				}
				matrix.setRotate(30);
				m_curbm = Bitmap.createBitmap(m_curbm, 0, 0, m_curbm.getWidth(), m_curbm.getHeight(), matrix, false);
				m_iv.setImageBitmap(m_curbm);				
			}
		});			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_demo, menu);
		return true;
	}	
}
