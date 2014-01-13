package com.example.ygapp_2013;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class WebViewDemo extends Activity {
	private WebView m_webview=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view_demo);
		
		m_webview = (WebView)findViewById(R.id.webView1);
		m_webview.loadUrl("file:///android_asset/webviewdemo.htm");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view_demo, menu);
		return true;
	}

}
