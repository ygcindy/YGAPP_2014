package com.example.ygapp_2013;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class SpinnerDemo extends Activity {
	private Spinner sp_city = null;
	private Spinner sp_district = null;
	private CityDistrictData m_citydistrictdata = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spinner_demo);
		
		m_citydistrictdata = new CityDistrictData();
		
		sp_city = (Spinner) findViewById(R.id.spinner_city);
		sp_district = (Spinner) findViewById(R.id.spinner_district);	
		
		String[] cities = m_citydistrictdata.getDistrictCity();//new String[]{"ShangHai","BeiJing","ShenZhen","GuangZhou"};
		ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,cities);
		sp_city.setAdapter(city_adapter);
		
		
		sp_city.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//String sel = (String) parent.getItemAtPosition(position);
				//Toast.makeText(getApplicationContext(),sel, Toast.LENGTH_LONG).show();
				String[] district = m_citydistrictdata.getDistrictByIndex(position);//m_citydistrictdata.getDistrictByCity(CityDistrictData.ENUM_CITY_SHANGHAI);//new String[]{"XuHui","LuWan","HuangPu","ZhaBei"};
				ArrayAdapter<String> district_adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,district);
				sp_district.setAdapter(district_adapter);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.spinner_demo, menu);
		return true;
	}

}
