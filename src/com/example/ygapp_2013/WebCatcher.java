package com.example.ygapp_2013;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.Vibrator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class WebCatcher extends Activity {
	private ArrayList<String> m_pic_url_list = null;
	final private String app_name = "WebCatcher";
	private boolean m_malformed_url = false;
	private int m_picture_count = 0;
	private long m_download_bytes = 0;	
	private String m_web_host = null;
	private String m_web_title = null;	
	private String m_current_download_path = null;
	private MyHandler m_handler = new MyHandler();
	final private String m_filemanager_package = "com.motorola.filemanager";
	final private String m_gallery_package = "com.motorola.gallery";
	//for debug
	final private boolean m_debug_only_catch_content = false;
	final private boolean m_debug_file_path = false;
	final private String m_debug_url = null;//"http://war3.replays.net/";
	
	final private int ENUM_WEB_CATCHER_UPDATE_STORAGE_STATUS = 0;
	final private int PICK_FILE_REQUEST_CODE = 1;
	
	private class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			switch (msg.what){
			case ENUM_WEB_CATCHER_UPDATE_STORAGE_STATUS:
				UpdateExternStorageStatus();  
				break;
			default:				
				break;
			}			
		}		
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_catcher);
		CreateAppFolder();
		m_pic_url_list = new ArrayList<String>();
		
		UpdateExternStorageStatus();  
		findViewById(R.id.btn_webcatcher_catch).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText url_edt =(EditText) findViewById(R.id.editText_webcatcher_url);
				String url = null;
				
				url = url_edt.getText().toString();
				if( !url.isEmpty() ){
					new GetWebContentAsyncTask(WebCatcher.this).execute(url);	
				}
			}
		});
		findViewById(R.id.btn_web_catcher_file_explorer).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = getPackageManager().getLaunchIntentForPackage(m_filemanager_package);
				if( intent != null )
					startActivity(intent);
				else{
					Toast.makeText(getApplicationContext(), m_filemanager_package + "is not found.", Toast.LENGTH_SHORT).show();					
				}
			}
		}
		);
		
		findViewById(R.id.btn_web_catcher_gallery).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = getPackageManager().getLaunchIntentForPackage(m_gallery_package);
				if( intent != null )
					startActivity(intent);
				else{
					Toast.makeText(getApplicationContext(), m_gallery_package + "is not found.", Toast.LENGTH_SHORT).show();					
				}
			}
		}
		);		
		
		findViewById(R.id.btn_web_catcher_url_file).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getBaseContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, "/sdcard");
                
                //can user select directories or not
                //intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
                
                //alternatively you can set file filter
                intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "txt" });
                
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);                
			}
		}
		);			
	}
	
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		if( resultCode == RESULT_OK ){
	    	if( requestCode == PICK_FILE_REQUEST_CODE ){
	    		String sel_path = data.getStringExtra(FileDialog.RESULT_PATH);
	    		Log.v(app_name,"The select url file path is " + sel_path);
	    		
	    		EditText url_edt =(EditText) findViewById(R.id.editText_webcatcher_url);
	    		url_edt.setText(sel_path);
	    		String url_str = GetWebUrlFromFile(sel_path);
	    		if( url_str.isEmpty() ){	    			
	    			return;
	    		}
//	    		if( !url_str.endsWith("\n") )
//	    			url_str += "\n";
	    		String[] url_split_list = url_str.split("\\r?\\n");	   
//	    		int t_length = url_split_list.length;
   				new GetWebContentAsyncTask(WebCatcher.this).execute(url_split_list);
	    	}			
		}  
    }
    
	private void CreateAppFolder()
	{
		String storage_state = Environment.getExternalStorageState();		
		if( !storage_state.equals(Environment.MEDIA_MOUNTED))
			return;
		String filePath;		

		filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + app_name + File.separator;
		File filefolder = new File(filePath);
		if( !filefolder.exists() ){
			filefolder.mkdirs();		
		}
	}
	private void SaveWebContentToFile(String content)
	{		
		String storage_state = Environment.getExternalStorageState();		
		if( !storage_state.equals(Environment.MEDIA_MOUNTED))
			return;
		String filePath = getM_current_download_path();

		if( filePath == null )
			return;
		
		File filefolder = new File(filePath);
		if( !filefolder.exists() ){
			filefolder.mkdirs();		
		}
		filePath += "webcontent.txt";
		
		try {
			FileOutputStream fileoutstream = new FileOutputStream(filePath);
			try {
				fileoutstream.write(content.getBytes());
				fileoutstream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_catcher, menu);
		return true;
	}
	
	private void ProduceWorkPath(){
		String storage_state = Environment.getExternalStorageState();		
		if( !storage_state.equals(Environment.MEDIA_MOUNTED))
			return;
		
		Calendar calen = Calendar.getInstance();
		Date date = calen.getTime();
		SimpleDateFormat simple_df = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());//yyyyMMdd_HHmmss_S
		String datestr = simple_df.format(date);
		System.out.println(datestr);
//		SimpleDateFormat simple_hhmmss = new SimpleDateFormat("HHmmss",Locale.getDefault());//yyyyMMdd_HHmmss_S
//		String hhmmssstr = simple_hhmmss.format(date);		
		
		String path = null;
		if( m_debug_file_path )
			path = Environment.getExternalStorageDirectory().getPath() + File.separator + app_name + File.separator + datestr  + File.separator;
		else
			path = Environment.getExternalStorageDirectory().getPath() + File.separator + app_name + File.separator + datestr  + File.separator + getM_web_title() + File.separator;// + hhmmssstr + File.separator;
	
		setM_current_download_path(path);
	}
	private String GetCharSet(String content){	
//		charset="gb2312" or charset=gb2312"
	       final String charset_REG = "charset\\s*=\\s*\"?([\\w-]+)\\s*\"";//charset\\s*=\\s*(w+)\\s*\""  
	       String charset = null;
		    Pattern pattern = Pattern.compile(charset_REG,Pattern.CASE_INSENSITIVE);//java.util.regex.Pattern  
		    Matcher matcher = pattern.matcher(content);     //java.util.regex.Matcher  
		    if (matcher.find()) {  
		    	charset = matcher.group(1);	 
		    	System.out.println(charset);  
		    }   
		    
		return charset;
	}
	private String StartCatch(String url){				
		//initialize param
		m_malformed_url = false;
		setM_picture_count(0);
		setM_web_host(null);
		setM_current_download_path(null);
		setM_download_bytes(0);
		setM_web_title(null);
		
		//url = "http://bbs.replays.net/thread-2577975-1-1.html";
		if( m_debug_url != null )
			url = m_debug_url;
		Log.v(app_name,"start catch " + url);
		
		InputStream stream = streampost(url);	// http://p.replays.net/page/20131118/1864479.html#p=1
		byte[] data = null;
		
		if( stream == null )
			return null;
		
		try {
			data = readStream(stream);
			if( data == null) {
				throw( new Exception("webcatcher readstream is null"));			
			}
			String str = new String(data);
			String encoded_str = null;
			
			String charset = GetCharSet(str);
			//the android default charset i s utf-8
			if( charset == null ){
				encoded_str = str;			
			}
			else if( charset.compareToIgnoreCase("gb2312") == 0 || charset.compareToIgnoreCase("gbk") == 0 ){				
				encoded_str = new String(data,"GBK");	
			}
			else{
				encoded_str = str;				
			}
			return encoded_str;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**获取指定地址的网页数据 
	 * 返回数据流 
	 */  
	public InputStream streampost(String remote_addr){  
	    URL infoUrl = null;  
	    InputStream inStream = null;  
	    try {  
	        infoUrl = new URL(remote_addr);  
	        URLConnection connection = infoUrl.openConnection();  
	        HttpURLConnection httpConnection = (HttpURLConnection)connection;  
	        int responseCode = httpConnection.getResponseCode();  
	        if(responseCode == HttpURLConnection.HTTP_OK){  
	            inStream = httpConnection.getInputStream();  
	        }  
	        setM_web_host(infoUrl.getHost());	  
	    } catch (MalformedURLException e) {  
	        // TODO Auto-generated catch block  
	    	m_malformed_url = true;
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        // TODO Auto-generated catch block   
	        e.printStackTrace();  
	    }  
	    return inStream;  
	} 	
	public static byte[] readStream(InputStream inputStream) throws Exception {
		byte[] buffer = new byte[8192];
		int len = -1;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		while ((len = inputStream.read(buffer)) != -1) {
		byteArrayOutputStream.write(buffer, 0, len);
		}

		inputStream.close();
		byteArrayOutputStream.close();
		return byteArrayOutputStream.toByteArray();
		}	
	
	public void getHtmlPicture(ArrayList<String> url_list){
		Iterator<String> it = url_list.iterator();
		while(it.hasNext()){
			getHtmlPicture(it.next());
		}
		//SaveWebContentToFile(url_list.toString());
		
	}
	//得到了图片地址并下载图片  
    public void getHtmlPicture(String httpUrl) {  
    URL url;  
    BufferedInputStream in;  
    FileOutputStream file;  

	String storage_state = Environment.getExternalStorageState();		
	if( !storage_state.equals(Environment.MEDIA_MOUNTED))
		return;
	String filePath;

	filePath = getM_current_download_path();
	if( filePath == null )
		return;
	File filefolder = new File(filePath);
	if( !filefolder.exists() ){
		filefolder.mkdirs();		
	}

	try {  
       //String fileName = (String.valueOf(count)).concat(httpUrl.substring(httpUrl.lastIndexOf(".")));//图片文件序号加上图片的后缀名，后缀名用了String内的一个方法来获得  
    	String fileName = httpUrl.substring(httpUrl.lastIndexOf("/") + 1);//这样获得的文件名即是图片链接里图片的名字  
    	
    	String fileoutpath = filePath+fileName;
    	File outfile = new File(fileoutpath);
    	if( outfile.exists() ){
    		System.out.println(app_name + ":skip " + fileName + ".It's already existed.");
    		return;    		
    	}
       url = new URL(httpUrl);  

       in = new BufferedInputStream(url.openStream());  

       file = new FileOutputStream(outfile);  
       int t;  
	   //int in_available = in.available();
       byte[] byte_in = new byte[8192];
       while ((t = in.read(byte_in)) != -1) {  
        file.write(byte_in,0,t);  
        setM_download_bytes(getM_download_bytes() + t);
       }  
       file.close();  
       in.close();
       
       setM_picture_count(getM_picture_count() + 1);
    } catch (MalformedURLException e) {  
       e.printStackTrace();  
    } catch (FileNotFoundException e) {  
       e.printStackTrace();  
    } catch (IOException e) {  
       e.printStackTrace();  
    }  
    }      

    //分析网页代码，找到TITLE  
    public void getUrlTitle(String content) throws IOException {  
       final String WEBTITLE_REG = "<title.*?>(.*?)</title>";
       String title = null;
	    Pattern pattern = Pattern.compile(WEBTITLE_REG,Pattern.CASE_INSENSITIVE);//java.util.regex.Pattern  
	    Matcher matcher = pattern.matcher(content);     //java.util.regex.Matcher  
	    if (matcher.find()) {  
	    	title = matcher.group(1);	 
	    	setM_web_title(title);
	    	System.out.println(matcher.group(1));  
	    } 
	    else{	    	
	    	setM_web_title(getM_web_host());
	    }
    }  
    
  //分析网页代码，找到匹配的网页图片地址  
    public void getUrlList(String content) throws IOException {  

    String searchImgReg = "(?x)(src|background|file)=('|\")/?(([\\w-]+/)*([\\w-]+\\.(jpg|png|gif)))('|\")";//用于在网页代码Content中查找匹配的图片链接。  
    String searchImgReg2 = "(?x)(src|background|file)=('|\")(http://([\\w-]+\\.)+[\\w-]+(:[0-9]+)*(/[\\w-]+)*(/[\\w-]+\\.(jpg|png|gif)))('|\")";  

    m_pic_url_list.clear();
    
    Pattern pattern = Pattern.compile(searchImgReg,Pattern.CASE_INSENSITIVE);//java.util.regex.Pattern  
    Matcher matcher = pattern.matcher(content);     //java.util.regex.Matcher  
    while (matcher.find()) {  
       //System.out.println(matcher.group(3));//输出图片链接地址到屏幕LOGCAT  
       m_pic_url_list.add(matcher.group(3));
    // System.out.println(url);  
    //   this.getHtmlPicture(matcher.group(3));//对象调用getHtmlPicture从网上下载并输出图片文件到指定目录  

    }  

    pattern = Pattern.compile(searchImgReg2);  
    matcher = pattern.matcher(content);  
    while (matcher.find()) {  
    	m_pic_url_list.add(matcher.group(3));
       //System.out.println(matcher.group(3));  
       //this.getHtmlPicture(matcher.group(3));  

    }  
    
    }  
    
    
    public int getM_picture_count() {
		return m_picture_count;
	}

	public void setM_picture_count(int m_picture_count) {
		this.m_picture_count = m_picture_count;
	}

	public String getM_web_host() {
		return m_web_host;
	}

	public void setM_web_host(String m_web_host) {
		this.m_web_host = m_web_host;
	}

	private class GetWebContentAsyncTask extends AsyncTask<String, Void, Void>
	{
		private ProgressDialog pd;
		private Date m_time_begin,m_time_end;
		
		public GetWebContentAsyncTask(Context context){
			m_time_begin = Calendar.getInstance().getTime();
			
			pd = new ProgressDialog(context,ProgressDialog.STYLE_SPINNER);
			
			pd.setTitle("正在加载，请稍候...");
			pd.setCancelable(false);
//			pd.setMax(100);
//			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.show();			
		}
			
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {				
				int i;
				String str;
				for( i = 0; i < params.length; ++i ){
					str = params[i];
					int leret = str.length();
					if( (str != null) && (str.isEmpty() == false)  ){
						String content = StartCatch(params[i]);
						if( content != null ){   					
							getUrlTitle(content);
							ProduceWorkPath();
							if( m_debug_only_catch_content ){
								SaveWebContentToFile(content);
							}					
							else{
								getUrlList(content);
								getHtmlPicture(m_pic_url_list);
							}					
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
	    protected void onPostExecute(Void result) {
	    	m_time_end = Calendar.getInstance().getTime();
	    	pd.dismiss();
	    	
			if( m_malformed_url ){
				Toast.makeText(WebCatcher.this, "The url is invalid.", Toast.LENGTH_SHORT).show();					
			}
			else if( getM_picture_count() > 0 ){
				long elapse_time = (m_time_end.getTime() - m_time_begin.getTime())/1000;
				
				if( elapse_time < 1 )
					elapse_time = 1;
				Toast.makeText(WebCatcher.this, getM_picture_count() + " item(s) are downloaded.Total " + getM_download_bytes()/1024 + " KB." + "Elaspse time:" + elapse_time + "s" , Toast.LENGTH_SHORT).show();
				
				Message msg = new Message();
				msg.what = ENUM_WEB_CATCHER_UPDATE_STORAGE_STATUS;
				m_handler.sendMessage(msg);
			}
			else{
				Toast.makeText(WebCatcher.this,"No item is downloaded.", Toast.LENGTH_SHORT).show();
			}
			Vibrator vib = (Vibrator) WebCatcher.this.getSystemService(Service.VIBRATOR_SERVICE);
			vib.vibrate(1500);
	    }


	}   
	
	private void UpdateExternStorageStatus(){
		TextView tv = (TextView) findViewById(R.id.txt_web_catcher_storage_status);
		String storage_state = Environment.getExternalStorageState();		
		if( !storage_state.equals(Environment.MEDIA_MOUNTED)){
			tv.setText("The storage is no not existed.");
			return;
		}
		File file = Environment.getExternalStorageDirectory();
		StatFs status = new StatFs(file.getPath());
		long available_block = status.getAvailableBlocks();
		long total_block = status.getBlockCount();
		long block_size = status.getBlockSize();
		
		tv.setText(available_block*block_size/1024/1024 + "/" + block_size*total_block/1024/1024 + " MB");		
	}


	public String getM_current_download_path() {
		return m_current_download_path;
	}


	public void setM_current_download_path(String m_current_download_path) {
		this.m_current_download_path = m_current_download_path;
	}


	public long getM_download_bytes() {
		return m_download_bytes;
	}


	public void setM_download_bytes(long m_download_bytes) {
		this.m_download_bytes = m_download_bytes;
	}


	public String getM_web_title() {
		return m_web_title;
	}


	public void setM_web_title(String m_web_title) {
		final int max_length = 20;
		if( m_web_title == null )
			this.m_web_title = m_web_title;		
		else if( m_web_title.length() < max_length )
			this.m_web_title = m_web_title;
		else
			this.m_web_title = m_web_title.substring(0, max_length - 1);
	}
	
	private String GetWebUrlFromFile(String filepath){
		String out_str = "";
		
		try {
			FileInputStream in_stream = new FileInputStream(filepath);			

			byte[] buffer = new byte[2048];	
			int read_numbers = 0;
			do{
				read_numbers = in_stream.read(buffer);
				if( read_numbers > 0 )
					out_str += new String(buffer,0,read_numbers);
			}while( read_numbers != -1 );
			in_stream.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.v(app_name, "GetWebUrlFromFile:" + filepath + " do not exist.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out_str;
	}
}

