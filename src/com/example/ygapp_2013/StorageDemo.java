package com.example.ygapp_2013;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class StorageDemo extends Activity {
	private String m_sp_name = "property";
	private OnSharedPreferenceChangeListener m_sp_change_listener = null;
	private EditText m_et = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storage_demo);
		
		m_sp_change_listener = new OnSharedPreferenceChangeListener() {
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), key + " is changed to " + sharedPreferences.getString(key, null), Toast.LENGTH_LONG).show();
			}
		};
		SharedPreferences sp = getApplicationContext().getSharedPreferences(m_sp_name, MODE_PRIVATE);
		sp.registerOnSharedPreferenceChangeListener(m_sp_change_listener);
		
		m_et = (EditText) findViewById(R.id.editText_inquire_charge_number);
		findViewById(R.id.btn_storage_save).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences sp = getApplicationContext().getSharedPreferences(m_sp_name, MODE_WORLD_WRITEABLE);
				Editor ed = sp.edit();
				ed.putString("name", m_et.getEditableText().toString());
				ed.commit();
//				sp.registerOnSharedPreferenceChangeListener(m_sp_change_listener);
			}
		});
		
		findViewById(R.id.btn_storage_read).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences sp = getApplicationContext().getSharedPreferences(m_sp_name, MODE_WORLD_READABLE);
				String name_value;
				name_value = sp.getString("name", null);
				m_et.setText(name_value);
			}
		});		
		
		doSql();
	}
	
	private void doSql()
	{
		ContentValues values = null;
		SQLiteDatabase sqldatabase = this.openOrCreateDatabase("mysqldatabase.db", MODE_PRIVATE, null);
		if( !tabIsExist(sqldatabase,"mytab") ){
			String sql= "create table mytab(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,name2 TEXT NOT NULL)";
			sqldatabase.execSQL(sql);
			values = new ContentValues();
			values.put("name", "gogo");
			values.put("name2", "gui");		
			sqldatabase.insert("mytab", null, values);		
		}
		sqldatabase.close();
	}
	public void onStop()
	{
		super.onStop();
		
		SharedPreferences sp = getApplicationContext().getSharedPreferences(m_sp_name, MODE_PRIVATE);
		sp.unregisterOnSharedPreferenceChangeListener(m_sp_change_listener);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.storage_demo, menu);
		return true;
	}

	/**
     * 判断某张表是否存在
     * @param tabName 表名
     * @return
     */
    public boolean tabIsExist(SQLiteDatabase db,String tabName){
            boolean result = false;
            if(tabName == null){
                    return false;
            }
            Cursor cursor = null;
            try {                    
                    String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
                    cursor = db.rawQuery(sql, null);
                    if(cursor.moveToNext()){
                            int count = cursor.getInt(0);
                            if(count>0){
                                    result = true;
                            }
                    }
                    
            } catch (Exception e) {
                    // TODO: handle exception
            }                
            return result;
    }
}
