package com.example.ygapp_2013;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class CityDistrictData {
	public static final int ENUM_CITY_SHANGHAI = 0;
	public static final int ENUM_CITY_BEIJING = 1;	
	public static final int ENUM_CITY_SHENZHEN = 2;	
	public static final int ENUM_CITY_GUANGZHOU = 3;	
	
	//HashMap顺序不是加入 的顺序，需使用LinkedHashMap
	//cache
	private LinkedHashMap<String,City_District> m_hash;	
	
	/*城市可以一次加载，具体城市的区可先不加载，后续根据城市的选择再动态加载
	 * 
	 */
	private class City_District{
		private String m_city;
		private String[] m_district;	
		City_District(String city){
			setM_city(city);
			m_district = null;
		}
		public String getM_city() {
			return m_city;
		}
		public void setM_city(String m_city) {
			this.m_city = m_city;
		}
	}
	CityDistrictData()
	{
		City_District tmp = null;
		
		m_hash = new LinkedHashMap<String,City_District>();
		
		tmp = new City_District("Shanghai");
		m_hash.put(Integer.toString(ENUM_CITY_SHANGHAI), tmp);
		
		tmp = new City_District("Beijing");
		m_hash.put(Integer.toString(ENUM_CITY_BEIJING), tmp);	
		
		tmp = new City_District("Shenzhen");
		m_hash.put(Integer.toString(ENUM_CITY_SHENZHEN), tmp);
		
		tmp = new City_District("Guangzhou");
		m_hash.put(Integer.toString(ENUM_CITY_GUANGZHOU), tmp);		
	}
	
	public String[] getDistrictCity()
	{
		String[] str = new String[m_hash.size()];
		int str_index = 0;
		Iterator<Entry<String, City_District>> it = m_hash.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, City_District> item = it.next();
			str[str_index++] = item.getValue().m_city;			
		}
		return str;
	}
	public String[] getDistrictByCity(int city)
	{
		String str_city = Integer.toString(city);
		if( m_hash.containsKey(str_city) ){
			String[] tmp_district = m_hash.get(str_city).m_district;
			if( tmp_district != null )
				return tmp_district;
			
			switch (city){
			case ENUM_CITY_SHANGHAI:
				tmp_district = new String[]{"XuHui","LuWan","HuangPu","ZhaBei"};
				break;
			case ENUM_CITY_BEIJING:
				tmp_district = new String[]{"Xuanwu"};
				break;
			case ENUM_CITY_SHENZHEN:
				tmp_district = new String[]{"Futian","Baoan"};
				break;
			case ENUM_CITY_GUANGZHOU:
				tmp_district = new String[]{"Tianhe"};
				break;				
			}
			m_hash.get(str_city).m_district = tmp_district;
			return tmp_district;
		}
		else{
			//to do...
		}
		return null;	
	}
	public String[] getDistrictByIndex(int index)
	{
		Boolean isfound = false;
		Iterator<Entry<String, City_District>> it = m_hash.entrySet().iterator();
		Entry<String, City_District> item = null;
		int count = 0;
		while(it.hasNext()){
			item = it.next();
			if( index == count ){
				isfound = true;
				break;
			}
			else{
				++count;			
			}
		}
		if( isfound ){
			return getDistrictByCity(Integer.parseInt(item.getKey()));			
		}
		return null;		
	}
}
