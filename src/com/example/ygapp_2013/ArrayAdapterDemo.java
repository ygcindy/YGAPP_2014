package com.example.ygapp_2013;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArrayAdapterDemo extends Activity {
	private ArrayList<String> m_str_array = null;
	private ArrayAdapter<String> m_array_adapter = null;
	
	private ArrayList<ListData> m_listdata_array = null;
	private ArrayAdapaterDemoList m_listdata_adapter = null;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_array_adapter_demo);
		m_str_array = new ArrayList<String>();
		m_array_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, m_str_array);
		//((ListView)findViewById(R.id.lv_arrayadapterdemo)).setAdapter(m_array_adapter);
		m_listdata_array = new ArrayList<ListData>();
		m_listdata_adapter = new ArrayAdapaterDemoList(this, R.layout.layout_arrayadapter_twoline, m_listdata_array);		
		((ListView)findViewById(R.id.lv_arrayadapterdemo)).setAdapter(m_listdata_adapter);
		((Button)findViewById(R.id.btn_arrayadapterdemo)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//m_str_array.add(((EditText)findViewById(R.id.et_arrayadapterdemo)).getText().toString());
				//m_array_adapter.notifyDataSetChanged();
				ListData item = new ListData();
				item.txt_1 = item.txt_2 = ((EditText)findViewById(R.id.et_arrayadapterdemo)).getText().toString();
				m_listdata_array.add(item);
				m_listdata_adapter.notifyDataSetChanged();
			}
		});
	}

	private class ListData{
		public Drawable img;
		public String txt_1;
		public String txt_2;
	}
	private class ArrayAdapaterDemoList extends ArrayAdapter<ListData>{
		private int m_viewlayout_id;
		private LayoutInflater mInflater;
		
		public ArrayAdapaterDemoList(Context context, int textViewResourceId,
				List<ListData> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			m_viewlayout_id = textViewResourceId;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		private class MyImageButton extends ImageButton{
			private int position;
			public MyImageButton(Context context) {
				super(context);
				// TODO Auto-generated constructor stub
			}
			public void setPosition(int position){
				this.position = position;				
			}
			public int getPostion(){			
				return this.position;
			}
		}
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ListData tmp;
			RelativeLayout outview=null;
			
			tmp = getItem(position);
			
	        if (convertView == null) {
	        	try{
	        		outview = (RelativeLayout)mInflater.inflate(m_viewlayout_id, parent, false);
	        	}
	        	catch(InflateException e){
	        		e.printStackTrace();	        		
	        	}
	        } else {
	        	outview = (RelativeLayout)convertView;
	        }
	        ImageButton iv = (ImageButton)outview.findViewById(R.id.imageButton1);
	        iv.setImageResource(R.drawable.arrayadapter_delete);
	        iv.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ListView lv = ((ListView)findViewById(R.id.lv_arrayadapterdemo));
					//lv.removeViewInLayout((View)(v.getParent()));
					//lv.removeAllViews();
					//m_listdata_array.remove(lv.getSelectedItemPosition());
					//m_listdata_adapter.notifyDataSetChanged();
					View parent = (View)(v.getParent());
					int position = lv.getPositionForView(parent);
					m_listdata_array.remove(position);
					m_listdata_adapter.notifyDataSetChanged();
				}
			});
	        
	        TextView appname = (TextView)outview.findViewById(R.id.txt_web_catcher_storage_status);
	        appname.setText(tmp.txt_1);
	        
	        TextView pacname = (TextView)outview.findViewById(R.id.textView2);
	        pacname.setText(tmp.txt_2);
	        
			return outview;
		}		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.array_adapter_demo, menu);
		return true;
	}

}
