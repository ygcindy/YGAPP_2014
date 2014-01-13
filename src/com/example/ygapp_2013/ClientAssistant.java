package com.example.ygapp_2013;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClientAssistant extends Activity{
	private Socket m_Socket=null;
	private String m_IPstr=null;
	private int m_Port;
	private BufferedReader m_BufRead=null;
	private PrintWriter m_printwriter=null;
	//private String m_CurrentInStr=null;
	private MyHandler m_myHandler=null;
	
	private HashMap<Button,String> m_btn_cmd_map=null;
	private Button btn_conn;
	
	private static class ENUM_MESSAGE{
		public final static int DISCONNECTED=0;		
		public final static int CONNECTED=1;	
		
		public final static int TEST_CONNECTED=9;
		public final static int UPDATE_RECV=10;		
		public final static int SEND_FAILED=11;			
	}
	
	private static class CMD_NOTION{
		public final static String TEST = "test";	
		public final static String LOCK_SCREEN = "lockscreen";	//进入锁屏模式
		public final static String RESTART_WINDOW = "restartwindow";//重启电脑	
		public final static String POWER_OFF = "poweroff";		//关机
		public final static String DISABLE_MOUSE = "disablemouse";	//屏蔽鼠标输入
		public final static String ENABLE_MOUSE = "enablemouse";	//激活鼠标
		public final static String DISABLE_KEYPAD = "disablekeypad";//屏蔽键盘输入	
		public final static String ENABLE_KEYPAD = "enablekeypad";	//激活键盘
		public final static String DISABLE_KEYPADMOUSE = "disablekeypadmouse";	//屏蔽鼠标和键盘
		public final static String ENABLE_KEYPADMOUSE = "enablekeypadmouse";	//激活鼠标和键盘		
	}
	
	private class MyHandler extends Handler{
		public void handleMessage(Message msg)
		{
			switch(msg.what){
			case ENUM_MESSAGE.DISCONNECTED:
				//Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
				//btn_conn.setText("Connect");
				break;
			case ENUM_MESSAGE.CONNECTED:
				Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
				//btn_conn.setText("DisConnect");				
				break;
			case ENUM_MESSAGE.UPDATE_RECV:
				String in = (String)msg.obj;
				TextView tv = (TextView)findViewById(R.id.textView_Recv);
				tv.setText(in);				
				break;
			case ENUM_MESSAGE.SEND_FAILED:
				Toast.makeText(getApplicationContext(), "Send Failed!", Toast.LENGTH_SHORT).show();
				break;
			case ENUM_MESSAGE.TEST_CONNECTED:
				int flag = msg.arg1;
				if( flag == 0 )
					Toast.makeText(getApplicationContext(), "Test Failed!", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplicationContext(), "Test Success!", Toast.LENGTH_SHORT).show();		
				break;				
			}			
		}		
	}
	private class BTN_CMD_MAP{
		//private int btn_id;
		private String cmd_str;		
		private Button view_res;
		public BTN_CMD_MAP(int id,String str){
			//btn_id = id;
			cmd_str = str;	
			view_res = (Button) findViewById(id);			
		}
		public String GetCmdStr()
		{			
			return cmd_str;
		}
		public Button GetButton()
		{			
			return view_res;
		}		
	}
	private void InitButtomCmdMap()
	{	
		BTN_CMD_MAP[] btn_map = {
				new BTN_CMD_MAP(R.id.btn_lockscreen,CMD_NOTION.LOCK_SCREEN),	
				new BTN_CMD_MAP(R.id.btn_restartwindow,CMD_NOTION.RESTART_WINDOW),				
				new BTN_CMD_MAP(R.id.btn_poweroff,CMD_NOTION.POWER_OFF),
				new BTN_CMD_MAP(R.id.btn_disable_mouse,CMD_NOTION.DISABLE_MOUSE),		
				new BTN_CMD_MAP(R.id.btn_enable_mouse,CMD_NOTION.ENABLE_MOUSE),		
				new BTN_CMD_MAP(R.id.btn_disable_keypad,CMD_NOTION.DISABLE_KEYPAD),	
				new BTN_CMD_MAP(R.id.btn_enable_keypad,CMD_NOTION.ENABLE_KEYPAD),	
				new BTN_CMD_MAP(R.id.btn_disable_keypadmouse,CMD_NOTION.DISABLE_KEYPADMOUSE),	
				new BTN_CMD_MAP(R.id.btn_enable_keypadmouse,CMD_NOTION.ENABLE_KEYPADMOUSE),				
		};
		int length = btn_map.length;
		int i;
		m_btn_cmd_map = new HashMap<Button,String>();
		for( i = 0; i < length; ++i ){		
			Button btn = btn_map[i].GetButton();
			m_btn_cmd_map.put(btn, btn_map[i].GetCmdStr());
			
			btn.setOnClickListener(
					new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							ClientSendMsgByNewSocket(m_btn_cmd_map.get(v));
						}	
					}
			);				
		}				
	}
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_assistant);
		
		InitButtomCmdMap();
		
		m_IPstr = "192.168.1.102";
		m_Port = 1088;
		
		m_myHandler = new MyHandler();
		btn_conn = (Button) findViewById(R.id.client_assistant_button_connect);
		
		btn_conn.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if( m_Socket != null && !m_Socket.isClosed() ){
							DisConnectServer();							
						}
						else{
							ConnectServer();							
						}						
					}	
				}
		);	
		
		Button btn_send = (Button) findViewById(R.id.client_assistant_button_send);
		btn_send.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						EditText ed = (EditText)findViewById(R.id.editText_send);
						String str = ed.getText().toString();
						ClientSendMsg(str);
					}	
				}
		);		
		
		/*
		Button btn_lockscreen = (Button) findViewById(R.id.btn_lockscreen);
		btn_lockscreen.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ClientSendMsgByNewSocket(CMD_NOTION.LOCK_SCREEN);
					}	
				}
		);		
		*/	
	}
	
	private void ClientSendMsg(String str)
	{	
		if( m_Socket == null || m_Socket.isClosed() || !m_Socket.isConnected() )
			return;
		
		try {
			m_printwriter= new PrintWriter(m_Socket.getOutputStream(), true);
			m_printwriter.println(str);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m_myHandler.sendMessage(m_myHandler.obtainMessage(ENUM_MESSAGE.SEND_FAILED));
		}		
		
	}
	private void ClientSendMsgByNewSocket(final String str)
	{	
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if( m_Socket != null )
					return;
				
				m_Socket = new Socket();//new Socket("192.168.1.102",1088);	
				InetSocketAddress isa = new InetSocketAddress(m_IPstr,m_Port);
				try {
					m_Socket.connect(isa,100);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					m_myHandler.sendMessage(m_myHandler.obtainMessage(ENUM_MESSAGE.SEND_FAILED));
					DisConnectServer();
					return;
				}
			
				if( m_Socket.isConnected() ){
					ClientSendMsg(str);			
				}		
				else{
					m_myHandler.sendMessage(m_myHandler.obtainMessage(ENUM_MESSAGE.SEND_FAILED));
				}
				DisConnectServer();				
			}}).start();

	}	
	protected void onDestroy()
	{
		super.onDestroy();
		
		DisConnectServer();		
	}
	private void DisConnectServer()
	{
		try {	
		if( m_printwriter != null ){
			m_printwriter.close();	
			m_printwriter = null;
		}
		
		if( m_BufRead != null ){
			m_BufRead.close();
			m_BufRead = null;
		}
		
		if( m_Socket != null ){
			//m_Socket.shutdownOutput();
			//m_Socket.shutdownInput();			
			m_Socket.close();
			m_Socket = null;
		}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		m_myHandler.sendMessage(m_myHandler.obtainMessage(ENUM_MESSAGE.DISCONNECTED));
	}
	private void ConnectServer(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					if(m_Socket != null && !m_Socket.isClosed() ){
						return;						
					}
					
					m_Socket = new Socket();//new Socket("192.168.1.102",1088);					

					InetSocketAddress isa = new InetSocketAddress(m_IPstr,m_Port);
					m_Socket.connect(isa,100);					
					
					if( m_Socket.isConnected() ){
					ClientSendMsg(CMD_NOTION.TEST);	
					m_BufRead = new BufferedReader(new InputStreamReader(m_Socket.getInputStream(),"UTF-8"));
					if( m_BufRead != null ){
					new Thread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
								if (m_Socket!=null && !m_Socket.isClosed()&& m_Socket.isConnected()){
								try {
									Thread.sleep(300);//等待
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}								
								try {
									String in;
									in = m_BufRead.readLine();
									if( in != null && in == CMD_NOTION.TEST ){
										m_myHandler.sendMessage(m_myHandler.obtainMessage(ENUM_MESSAGE.TEST_CONNECTED,1,0));
									}
									else{
										m_myHandler.sendMessage(m_myHandler.obtainMessage(ENUM_MESSAGE.TEST_CONNECTED,0,0));										
									}
								} 
								catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									m_myHandler.sendMessage(m_myHandler.obtainMessage(ENUM_MESSAGE.TEST_CONNECTED,0,0));
								}
								finally{
									DisConnectServer();									
								}
							}
						}		
					}							
					).start();
					}
					}
					else{
						m_myHandler.sendMessage(m_myHandler.obtainMessage(ENUM_MESSAGE.TEST_CONNECTED,0,0));						
					}
				}
				catch(UnknownHostException e){
					e.printStackTrace();
					m_myHandler.sendMessage(m_myHandler.obtainMessage(ENUM_MESSAGE.TEST_CONNECTED,0,0));
				}
				catch(IOException e){			
					e.printStackTrace();
					m_myHandler.sendMessage(m_myHandler.obtainMessage(ENUM_MESSAGE.TEST_CONNECTED,0,0));
				}
				finally{
					DisConnectServer();						
				}
			}			
		}
		).start();		
	}
}
