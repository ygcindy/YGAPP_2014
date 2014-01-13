package com.example.ygapp_2013;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PackageManagerDemo extends Activity {
	private ListView m_lstview = null;
	private AlertDialog m_ad = null;
	private List<PackagelistData> m_package_list=null;
	
	//private PackagelistData m_current_select_item = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_package_manager_demo);			

		(new GetPackageInfoAsyncTask(this)).execute();//后台加载
/*		
		List<PackagelistData> pm_data = new ArrayList<PackagelistData>();
		PackagelistData tmp = new PackagelistData();
		tmp.appname = "appname";
		tmp.pacname = "pacname";
		pm_data.add(tmp);
		MyArrayAdapter adapter = new MyArrayAdapter(this,R.layout.pm_listview_demo_pattern,pm_data);
		m_lstview = (ListView)findViewById(R.id.pm_listView);
		m_lstview.setAdapter(adapter);
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.package_manager_demo, menu);
		return true;
	}

	private class PackagelistData{
		public Drawable draw;
		public String appname;
		public String pacname;
	}
	private class MyArrayAdapter extends ArrayAdapter<PackagelistData>{
		private int m_viewlayout_id;
		private LayoutInflater mInflater;
		
		public MyArrayAdapter(Context context, int textViewResourceId,
				List<PackagelistData> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			m_viewlayout_id = textViewResourceId;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			PackagelistData tmp;
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
	        ImageView iv = (ImageView)outview.findViewById(R.id.app_icon);
	        iv.setImageDrawable(tmp.draw);
	        
	        TextView appname = (TextView)outview.findViewById(R.id.textView_appname);
	        appname.setText(tmp.appname);
	        
	        TextView pacname = (TextView)outview.findViewById(R.id.textView_pacname);
	        pacname.setText(tmp.pacname);
	        
			return outview;
		}
	}
	
	private class GetPackageInfoAsyncTask extends AsyncTask<Void, Void, List<PackagelistData>>
	{
		ProgressDialog pd;
		
		public GetPackageInfoAsyncTask(Context context){
			pd = new ProgressDialog(context,ProgressDialog.STYLE_SPINNER);
			
			pd.setTitle("正在加载，请稍候...");
			pd.setCancelable(false);
//			pd.setMax(100);
//			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.show();
		}
		@Override
		protected List<PackagelistData> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			List<ResolveInfo> apps = null;
			PackageManager pm = getApplicationContext().getPackageManager();
			
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory("android.intent.category.LAUNCHER");
			apps = pm.queryIntentActivities(intent, 0);
			
			List<PackagelistData> pm_data = new ArrayList<PackagelistData>();
			
			int total = apps.size();
			int i;
			ResolveInfo it = null;
			for(i = 0; i < total; ++i ){
				it = apps.get(i);
				PackagelistData tmp = new PackagelistData();
				tmp.draw = it.loadIcon(pm);
				tmp.appname = it.loadLabel(pm).toString();//用户看见的应用程序名称
				tmp.pacname = it.activityInfo.packageName;//包名
				pm_data.add(tmp);
			}
			return pm_data;
		}	
		private class PackageItemSelect_DialogInterface implements DialogInterface.OnClickListener{
			private PackagelistData m_current_select_item = null;

			PackageItemSelect_DialogInterface(PackagelistData item){
				m_current_select_item = item;				
			}
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub				
				if( which == 0 ){
					Uri uri = Uri.parse("package:"+m_current_select_item.pacname);
					Intent intent = new Intent(Intent.ACTION_DELETE,uri);
					startActivity(intent);
					//Toast.makeText(getApplicationContext(), "Uninstall " + m_current_select_item.appname, Toast.LENGTH_SHORT).show();
				}
				else if(which == 1){
					PackageManager pm = getPackageManager();
					String dt = "";
					PackageInfo pi = null;			
					try {
						pi = pm.getPackageInfo(m_current_select_item.pacname, PackageManager.GET_PERMISSIONS);						
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if( pi.requestedPermissions != null ){
						for(String info_item:pi.requestedPermissions){
							try {
								PermissionInfo info = pm.getPermissionInfo(info_item, 0);
								dt += info.loadLabel(pm) + ":" + info.loadDescription(pm) + "\n";
							} catch (NameNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}					
						}
					}
					else{					
						dt = "空";
					}
					new AlertDialog.Builder(PackageManagerDemo.this)
					.setTitle("Details").setMessage((CharSequence)(dt)).setPositiveButton("OK", null).show();
				}				
			} 
			
		}
		
		private class PackageRemoved_Receiver extends BroadcastReceiver{
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Uri uri = intent.getData();
			}			
		}
	    protected void onPostExecute(List<PackagelistData> result) {
	    	pd.dismiss();
	    	//注册广播，当APK移除时需更新数据...to do
	    	//registerReceiver(new PackageRemoved_Receiver(), new IntentFilter(Intent.ACTION_PACKAGE_REMOVED));
	    	m_package_list = result;
	    	
			MyArrayAdapter adapter = new MyArrayAdapter(getApplicationContext(),R.layout.pm_listview_demo_pattern,m_package_list);
			m_lstview = (ListView)findViewById(R.id.pm_listView);
			m_lstview.setAdapter(adapter);	
			m_lstview.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub	
					PackagelistData cur_item = (PackagelistData) parent.getItemAtPosition(position);
					//check...the package may have been removed..
					try {
						getPackageManager().getPackageInfo(cur_item.pacname, 0);
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						 m_package_list.remove(position);
						((BaseAdapter) m_lstview.getAdapter()).notifyDataSetChanged();
						Toast.makeText(getApplicationContext(), cur_item.pacname + " is removed.", Toast.LENGTH_SHORT).show();	
						return;
					}
					
					AlertDialog.Builder builder = new AlertDialog.Builder(PackageManagerDemo.this);//activity
					builder.setTitle(cur_item.appname);
					String[] str = new String[]{"Uninstall","Details"};
					List<String> list_str = Arrays.asList(str);//new ArrayList<String>();
					MyArrayAdapter_PackageOperate adapter = new MyArrayAdapter_PackageOperate(PackageManagerDemo.this, android.R.layout.simple_list_item_1, list_str);
					builder.setAdapter(adapter, new PackageItemSelect_DialogInterface(cur_item));
					builder.setPositiveButton("Return", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//Toast.makeText(getApplicationContext(), Integer.toString(which), Toast.LENGTH_SHORT).show();						
						}
					});
					builder.show();				
				}});

	    }
		
	}
	private class MyArrayAdapter_PackageOperate extends ArrayAdapter<String>{
		private LayoutInflater mInflater;

		public MyArrayAdapter_PackageOperate(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
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
	        appname.setText(tmp);
	        appname.setTextColor(Color.BLACK);
			return outview;
		}
	}		
}
