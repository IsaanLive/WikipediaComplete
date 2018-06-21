package com.aseanmobile.wikipediamobile;

import android.app.Activity ;
import android.content.Intent ;
import android.os.Bundle ;
import android.webkit.WebView ;
import android.webkit.WebViewClient ;

import com.aseanmobile.wikipediamobile.model.MyMenuItem ;

public class WebViewer extends Activity {
	
	static MyMenuItem transferItem;
	WebView webView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        webView = (WebView)findViewById(R.id.web01);
        webView.setWebViewClient ( new MyWebView ( ) );
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setPluginsEnabled(false);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSavePassword(false);
        webView.setWebViewClient(new MyWebView());

        if (transferItem != null)
        {
	        loadPage(transferItem);
	        //transferItem = null;
        }
	}	
	
	private void loadPage(MyMenuItem item)
	{
		webView.loadUrl(item.getUrl());
		setTitle(item.getText());
	}
	
	private class MyWebView extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //view.loadUrl(url);
            Intent intent = new Intent ( getApplicationContext ( ) , CustomeWebViewActivity.class );
            intent.putExtra ( "uri" , url );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NO_HISTORY );
            startActivity ( intent );
            return true;
        }
	}

}
