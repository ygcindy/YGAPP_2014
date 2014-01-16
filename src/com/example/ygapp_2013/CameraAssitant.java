package com.example.ygapp_2013;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

public class CameraAssitant extends Activity  implements SurfaceHolder.Callback{
	private Camera m_camera = null;
	private SurfaceView m_surface_view = null;
	private SurfaceHolder m_surface_holder = null;
	private boolean m_camera_is_previewing = false;
	private String m_image_path = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_assitant);		
		m_image_path = getIntent().getData().toString();
//		Parameters param = m_camera.getParameters();
		m_surface_view = (SurfaceView) findViewById(R.id.surfaceView_camera_assistant);
		m_surface_holder = m_surface_view.getHolder();
		m_surface_holder.addCallback(this);
		m_surface_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		m_camera_is_previewing = false;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		m_camera.stopPreview();
		m_camera.release();
		m_camera = null;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		m_camera = Camera.open();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera_assitant, menu);
		return true;
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if( m_camera == null )
			m_camera = Camera.open();
		
//		try {
//			Parameters param = m_camera.getParameters();
//			m_camera.setParameters(param);
//			m_camera.setPreviewDisplay(m_surface_holder);
//			m_camera.startPreview();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
		
	}
	private class MyPictureCallback implements PictureCallback{

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
            FileOutputStream outSteam=null;
            
            try{
                
//                SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
//                String times=format.format((new Date()));
            
                outSteam=new FileOutputStream(m_image_path);//"/sdcard/"+times+"hid.jpg"
                outSteam.write(data);
                outSteam.close();
                setResult(RESULT_OK);
            }
            catch(FileNotFoundException e)
            {
                Log.d("Camera", "row");
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally{
            	finish();            	
            }
		}	
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if( m_camera == null )
			m_camera = Camera.open();		
		else if( m_camera_is_previewing )
			m_camera.stopPreview();
		
		try {			
			Parameters param = m_camera.getParameters();
			param.setPreviewSize(width, height);
			param.setPictureFormat(ImageFormat.JPEG);
			m_camera.setParameters(param);			
			m_camera.setPreviewDisplay(m_surface_holder);			
			m_camera.startPreview();
			m_camera_is_previewing = true;
			
			m_camera.takePicture(null, null, new MyPictureCallback());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if( m_camera != null ){
			m_camera.stopPreview();
			m_camera.release();
			m_camera = null;
		}
	}

}
