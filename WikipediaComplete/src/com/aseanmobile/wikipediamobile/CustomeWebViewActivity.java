/**
 * 
 */
package com.aseanmobile.wikipediamobile ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList ;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity ;
import android.app.AlertDialog ;
import android.content.Context ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.graphics.Bitmap ;
import android.media.MediaPlayer ;
import android.media.MediaPlayer.OnCompletionListener ;
import android.os.Bundle ;
import android.os.Message ;
import android.text.ClipboardManager;
import android.text.Editable ;
import android.text.TextWatcher ;
import android.util.Log ;
import android.view.KeyEvent ;
import android.view.Menu ;
import android.view.MenuInflater ;
import android.view.MenuItem ;
import android.view.View ;
import android.view.View.OnClickListener ;
import android.view.View.OnFocusChangeListener ;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo ;
import android.view.inputmethod.InputMethodManager ;
import android.webkit.HttpAuthHandler ;
import android.webkit.JsPromptResult ;
import android.webkit.JsResult ;
import android.webkit.WebChromeClient ;
import android.webkit.WebView ;
import android.webkit.WebViewClient ;
import android.widget.EditText ;
import android.widget.ImageButton ;
import android.widget.ProgressBar ;
import android.widget.TextView ;
import android.widget.TextView.OnEditorActionListener ;
import android.widget.Toast;

import com.aseanmobile.wikipediamobile.model.WikiData ;
import com.aseanmobile.wikipediamobile.support.Constant ;
import com.aseanmobile.wikipediamobile.support.Utils ;

/**
 *
 */
public class CustomeWebViewActivity extends Activity implements OnClickListener {
    
    
	WebView                  webView ;
    ProgressBar              top_progress_bar ;
    ImageButton              top_Favroite_button ;
    ImageButton              top_search_button ;
    EditText                 top_search_text ;
    int check=0;
    String                   current_url , current_tittle , cur_bookmark_title ;
    ArrayList < WikiData >   historyList ;
    String html=null;
    
    
    public static String     book_mark_url_to_open ;
    protected static boolean CHANGE_LANGUAGE_URL ;
    private WikiData         isFavorite ;
    
    private boolean          isEnterPressed = false ;
    
    
    public static int state=0;
    public static String filename=null;
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @ Override
    protected void onCreate ( Bundle savedInstanceState ) {
        
        super.onCreate ( savedInstanceState ) ;
        try {
            setContentView ( R.layout.custom_web_view ) ;
            
            top_progress_bar = ( ProgressBar ) findViewById ( R.id.top_progress_bar ) ;
            top_progress_bar.setProgress ( 0 ) ;
            
            top_search_text = (EditText) findViewById ( R.id.top_header_search_txt );
            top_search_text.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {
					// TODO Auto-generated method stub
					ClipboardManager clipboard=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					top_search_text.setText(clipboard.getText());
					return false;
				}
			});
            
            top_search_text.setOnFocusChangeListener ( new OnFocusChangeListener ( ) {
                
                public void onFocusChange ( View v , boolean hasFocus ) {
                    
                    InputMethodManager imm = ( InputMethodManager ) getSystemService ( Context.INPUT_METHOD_SERVICE ) ;
                    if ( hasFocus ) {
                        imm.showSoftInput ( top_search_text , 0 ) ;
                        String text = top_search_text.getText ( ).toString ( ) ;
                        if ( isEnterPressed ) {
                            isEnterPressed = false ;
                            if ( text != null && text.length ( ) > 0 ) {
                                top_search_text.setText ( text.substring ( 0 , text.length ( ) - 1 ) ) ;
                                top_search_text.setSelection ( top_search_text.getText ( ).length ( ) ) ;
                            }
                        }else{
                            if ( text != null && text.length ( ) > 0 ) {
                                top_search_text.setSelection ( top_search_text.getText ( ).length ( ) ) ;
                            }
                        }
                    } else {
                        imm.hideSoftInputFromWindow ( top_search_text.getWindowToken ( ) , 0 ) ;
                    }
                }
            } ) ;
            top_search_text.setOnEditorActionListener ( new OnEditorActionListener ( ) {
                @ Override
                public boolean onEditorAction ( TextView v , int actionId , KeyEvent event ) {
                    if ( actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                            || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO
                            || event.getAction ( ) == KeyEvent.ACTION_DOWN && event.getKeyCode ( ) == KeyEvent.KEYCODE_ENTER ) {
                        startSearchIntent ( );
                        isEnterPressed = true ;
                    }
                    return false ;
                }
            } ) ;
            
            top_search_text.addTextChangedListener ( new TextWatcher ( ) {
                
                public void afterTextChanged ( Editable s ) {
                    
                    if ( isEnterPressed ) {
                        isEnterPressed = false ;
                        String text = top_search_text.getText ( ).toString ( ) ;
                        if ( text != null && text.length ( ) > 0 ) {
                            top_search_text.setText ( text.substring ( 0 , text.length ( ) - 1 ) ) ;
                            top_search_text.setSelection ( top_search_text.getText ( ).length ( ) ) ;
                        }
                    }
                }
                
                public void beforeTextChanged ( CharSequence s , int start , int count , int after ) {
                }
                
                public void onTextChanged ( CharSequence s , int start , int before , int count ) {
                    
                }
            } ) ;
            
            top_Favroite_button = ( ImageButton ) findViewById ( R.id.top_header_btn_favorite ) ;
            top_search_button = ( ImageButton ) findViewById ( R.id.top_header_btn_search ) ;
            
            top_Favroite_button.setOnClickListener ( this ) ;
            top_search_button.setOnClickListener ( this ) ;
            
            String uri = getIntent ( ).getExtras ( ).getString ( "uri" ) ;
            current_url = new String ( uri ) ;
            webView = ( WebView ) findViewById ( R.id.cus_webview ) ;
            webView.getSettings ( ).setJavaScriptEnabled ( true ) ;
            webView.getSettings ( ).setJavaScriptCanOpenWindowsAutomatically ( true ) ;
            webView.getSettings ( ).setPluginsEnabled ( false ) ;
            webView.getSettings ( ).setSupportMultipleWindows ( false ) ;
            webView.getSettings ( ).setSupportZoom ( true ) ;
            webView.getSettings ( ).setJavaScriptEnabled ( true ) ;
            webView.getSettings ( ).setBuiltInZoomControls ( true ) ;
            webView.getSettings ( ).setSaveFormData ( true ) ;
            webView.getSettings ( ).setSavePassword ( true ) ;
            webView.setVerticalScrollBarEnabled ( false ) ;
            webView.setHorizontalScrollBarEnabled ( false ) ;
            webView.setWebChromeClient ( new MyWebChromeClient ( ) ) ;
            webView.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {
				 	Toast.makeText(CustomeWebViewActivity.this, "Tap to select text", Toast.LENGTH_SHORT);
					SelectText();
					return false;
				}
			});
            
            webView.clearCache ( true ) ;
            webView.clearHistory ( ) ;
            webView.loadUrl ( uri ) ;
            webView.setWebViewClient ( new MyWebView ( ) ) ;
            webView.requestFocus ( );
            
            
            
            showProgressBar ( ) ;
            
            WikiData isAlreadyBookMarked = Utils.database.getBookMarkForURL ( uri ) ;
            if ( isAlreadyBookMarked != null ) {
                isFavorite = isAlreadyBookMarked ;
                top_Favroite_button.setSelected ( true ) ;
            } else {
                isFavorite = null ;
                top_Favroite_button.setSelected ( false ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "onCreate Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        hideKeyboard ( );
    }
    
    @ Override
    protected void onResume ( ) {
        super.onResume ( ) ;
        if(state==1){
        	
//        	Log.i("zacharia", "onResume  "+filename);
//        	webView.loadUrl("file://"+filename);
        	String savep="file://"+filename;
        	webView.loadUrl(savep);
        	
        	
        	
        	state=0;
//        	Log.i("zacharia", "onResume  "+state);
        	
        	
        }else{
        Utils.getGTRACKER ( getApplicationContext ( ) ).trackPageViewEvent ( Constant.GoogleAnalytics.PAGE_VIEW_MAIN );
        if ( CHANGE_LANGUAGE_URL == true ) {
            CHANGE_LANGUAGE_URL = false ;
            changeLanguageURL ( ) ;
        }
        
        if ( book_mark_url_to_open == null || book_mark_url_to_open.length ( ) <= 0 ) {
            return ;
        }
        String uri = new String ( book_mark_url_to_open ) ;
        book_mark_url_to_open = null ;
        current_url = new String ( uri ) ;
        webView.loadUrl ( uri ) ;
//        Log.i("zacharia", "on resume state 0  "+uri);
        hideProgressDailog ( ) ;
        }
        
    }
    
    @ Override
    protected void onDestroy ( ) {
        Utils.getGTRACKER ( getApplicationContext ( ) ).endTrackingSession ( );
        super.onDestroy ( ) ;
    }
    
    private void changeLanguageURL ( ) {
        try {
            String cur = new String ( current_url ) ;
            String nlan = new String ( Constant.SELECTED_LANGUAGE.getUrl ( ) ) ;
            
            String curSub[] = cur.split ( "\\." ) ;
            String nlanSub[] = nlan.split ( "\\." ) ;
            
            String uri = cur.replace ( curSub [ 0 ] , nlanSub [ 0 ] ) ;
            Utils.getGTRACKER ( getApplicationContext ( ) ).trackGeneralEvent ( Constant.GoogleAnalytics.CATEGORY_GANERAL_LANGUAGE_CHANGE );
            current_url = new String ( uri ) ;
            webView.loadUrl ( uri ) ;
            showProgressBar ( ) ;
        } catch ( Exception e ) {
            Log.e ( "Exception" , "changeLanguageURL Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
    }
    
    private void hideProgressDailog ( ) {
        if ( top_progress_bar != null ) {
            top_progress_bar.setProgress ( 0 ) ;
            top_progress_bar.setVisibility ( View.GONE ) ;
            
        }
    }
    
    private void showProgressBar ( ) {
        try {
            if ( top_progress_bar != null ) {
                top_progress_bar.setProgress ( 0 ) ;
                top_progress_bar.setVisibility ( View.VISIBLE ) ;
                
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "showProgressBar Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
    }
    
    private class MyWebView extends WebViewClient {
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit
         * .WebView, java.lang.String)
         */
        @ Override
        public boolean shouldOverrideUrlLoading ( WebView view , String url ) {
//           Log.i("zacharia", "inside override"+url);
           if(url.startsWith("file:///")){
        	   if(url.startsWith("file:///sdcard/")){
        		   url=url;
        		  }
        	   else{
        		   String serv=Constant.SELECTED_LANGUAGE.getUrl();
        		   url=url.replace("file:///", serv);
        	   }
           }
//           Log.i("zacharia", "after modification:"+url);
        	
        	current_url = url ;
            view.loadUrl ( url ) ;
            return true ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#doUpdateVisitedHistory(android.webkit
         * .WebView, java.lang.String, boolean)
         */
        @ Override
        public void doUpdateVisitedHistory ( WebView view , String url , boolean isReload ) {
            
            super.doUpdateVisitedHistory ( view , url , isReload ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#onFormResubmission(android.webkit.WebView
         * , android.os.Message, android.os.Message)
         */
        @ Override
        public void onFormResubmission ( WebView view , Message dontResend , Message resend ) {
            
            super.onFormResubmission ( view , dontResend , resend ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#onLoadResource(android.webkit.WebView,
         * java.lang.String)
         */
        @ Override
        public void onLoadResource ( WebView view , String url ) {
            
            super.onLoadResource ( view , url ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#onPageFinished(android.webkit.WebView,
         * java.lang.String)
         */
        @ Override
        public void onPageFinished ( WebView view , String url ) {
            hideProgressDailog ( ) ;
//            Log.i("zacharia", "in page finish"+html);
            
            super.onPageFinished ( view , url ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#onPageStarted(android.webkit.WebView,
         * java.lang.String, android.graphics.Bitmap)
         */
        @ Override
        public void onPageStarted ( WebView view , String url , Bitmap favicon ) {
            hideKeyboard ( ) ;
            showProgressBar ( ) ;
            super.onPageStarted ( view , url , favicon ) ;
            if(state==0){
            WikiData isAlreadyBookMarked = Utils.database.getBookMarkForURL ( url ) ;
//            Log.i("zacharia", "on page stated state0  "+url);
//            String html=getHtml(url);
            html=null;
            html=getHtml(url);
            if(html!=null){
//            	Log.i("zacharia", "in page start"+html);
            	view.loadDataWithBaseURL(url, html, null, "utf-8", url);
            }
            if ( isAlreadyBookMarked != null ) {
                isFavorite = isAlreadyBookMarked ;
                top_Favroite_button.setSelected ( true ) ;
            } else {
                isFavorite = null ;
                top_Favroite_button.setSelected ( false ) ;
            }
            }
            else{
            	
//            	view.loadUrl("file://"+filename);
//
//            	state=0;
            }
           
            
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#onReceivedError(android.webkit.WebView,
         * int, java.lang.String, java.lang.String)
         */
        @ Override
        public void onReceivedError ( WebView view , int errorCode , String description , String failingUrl ) {
            hideProgressDailog ( ) ;
            super.onReceivedError ( view , errorCode , description , failingUrl ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#onReceivedHttpAuthRequest(android.webkit
         * .WebView, android.webkit.HttpAuthHandler, java.lang.String,
         * java.lang.String)
         */
        @ Override
        public void onReceivedHttpAuthRequest ( WebView view , HttpAuthHandler handler , String host , String realm ) {
            
            super.onReceivedHttpAuthRequest ( view , handler , host , realm ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#onScaleChanged(android.webkit.WebView,
         * float, float)
         */
        @ Override
        public void onScaleChanged ( WebView view , float oldScale , float newScale ) {
            
            super.onScaleChanged ( view , oldScale , newScale ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#onTooManyRedirects(android.webkit.WebView
         * , android.os.Message, android.os.Message)
         */
        @ Override
        public void onTooManyRedirects ( WebView view , Message cancelMsg , Message continueMsg ) {
            
            super.onTooManyRedirects ( view , cancelMsg , continueMsg ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#onUnhandledKeyEvent(android.webkit.WebView
         * , android.view.KeyEvent)
         */
        @ Override
        public void onUnhandledKeyEvent ( WebView view , KeyEvent event ) {
            
            super.onUnhandledKeyEvent ( view , event ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebViewClient#shouldOverrideKeyEvent(android.webkit
         * .WebView, android.view.KeyEvent)
         */
        @ Override
        public boolean shouldOverrideKeyEvent ( WebView view , KeyEvent event ) {
            
            return super.shouldOverrideKeyEvent ( view , event ) ;
        }
        
    public String getHtml(String url){
    	String pageHtml=null;
    	String data=
    			"<script type=\"text/javascript\">" +
    			"function hello(){" +
    			"document.getElementById(\"mw-mf-header\").style.display='none';" +
    			"document.getElementById(\"content_footer\").style.display='none';" +
//    			"document.getElementById(\"mp-itn\").style.display='none';" +
    			"}" +
    			
    			"window.onload=hello;</script>" ;
    			
    	DefaultHttpClient client=new DefaultHttpClient();
    	HttpGet pageGet=new HttpGet(url);
    	pageHtml=null;
    	try{ 
    		HttpResponse response=client.execute(pageGet);
    		StatusLine status=response.getStatusLine();
    		int statuscode=status.getStatusCode();
    		if(statuscode==200){
    			HttpEntity entity=response.getEntity();
    			InputStream content=entity.getContent();
    			BufferedReader reader=new BufferedReader(new InputStreamReader(content));
    			StringBuilder builder=new StringBuilder();
    			String line;
    			while((line=reader.readLine())!=null){
    				builder.append(line);
    			}
    			pageHtml=builder.toString();
    		}
    	}catch(Exception e){
    		
    	}
    	/*
    	ResponseHandler< String> handler=new ResponseHandler<String>() {
    		@Override
    		public String handleResponse(HttpResponse response)
    				throws ClientProtocolException, IOException {
    			// TODO Auto-generated method stub
    			HttpEntity entity=response.getEntity();
    			String html;
    			if(entity!=null){
    				html=EntityUtils.toString(entity);
    				return html;
    			}else{
    			return null;
    			}
    		}
		};*/
		
		try{
//			while(pageHtml==null){
//				pageHtml=client.execute(pageGet,handler);
//			}
			
			if(pageHtml!=null){
				pageHtml+=data;
			}
		}catch(Exception e){
			
		}
    	return pageHtml;
    }
    }
    
    /**
     * Provides a hook for calling "alert" from javascript. Useful for debugging
     * your javascript.
     */
    class MyWebChromeClient extends WebChromeClient implements OnCompletionListener {
        
        @ Override
        public boolean onJsAlert ( WebView view , String url , String message , JsResult result ) {
            
            result.confirm ( ) ;
            return true ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.media.MediaPlayer.OnCompletionListener#onCompletion(android
         * .media.MediaPlayer)
         */
        @ Override
        public void onCompletion ( MediaPlayer mp ) {
            
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebChromeClient#onCloseWindow(android.webkit.WebView)
         */
        @ Override
        public void onCloseWindow ( WebView window ) {
            
            super.onCloseWindow ( window ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebChromeClient#onCreateWindow(android.webkit.WebView,
         * boolean, boolean, android.os.Message)
         */
        @ Override
        public boolean onCreateWindow ( WebView view , boolean dialog , boolean userGesture , Message resultMsg ) {
            
            return super.onCreateWindow ( view , dialog , userGesture , resultMsg ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebChromeClient#onJsBeforeUnload(android.webkit.WebView
         * , java.lang.String, java.lang.String, android.webkit.JsResult)
         */
        @ Override
        public boolean onJsBeforeUnload ( WebView view , String url , String message , JsResult result ) {
            
            return super.onJsBeforeUnload ( view , url , message , result ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebChromeClient#onJsConfirm(android.webkit.WebView,
         * java.lang.String, java.lang.String, android.webkit.JsResult)
         */
        @ Override
        public boolean onJsConfirm ( WebView view , String url , String message , JsResult result ) {
            
            return super.onJsConfirm ( view , url , message , result ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebChromeClient#onJsPrompt(android.webkit.WebView,
         * java.lang.String, java.lang.String, java.lang.String,
         * android.webkit.JsPromptResult)
         */
        @ Override
        public boolean onJsPrompt ( WebView view , String url , String message , String defaultValue , JsPromptResult result ) {
            
            return super.onJsPrompt ( view , url , message , defaultValue , result ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebChromeClient#onProgressChanged(android.webkit.WebView
         * , int)
         */
        @ Override
        public void onProgressChanged ( WebView view , int newProgress ) {
            
            try {
                if ( top_progress_bar != null && top_progress_bar.getVisibility ( ) == View.VISIBLE ) {
                    top_progress_bar.setProgress ( newProgress ) ;
                }
            } catch ( Exception e ) {
                Log.e ( "Exception" , "onProgressChanged Message = " + e.toString ( ) ) ;
                e.printStackTrace ( ) ;
            }
            super.onProgressChanged ( view , newProgress ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebChromeClient#onReceivedIcon(android.webkit.WebView,
         * android.graphics.Bitmap)
         */
        @ Override
        public void onReceivedIcon ( WebView view , Bitmap icon ) {
            
            super.onReceivedIcon ( view , icon ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebChromeClient#onReceivedTitle(android.webkit.WebView
         * , java.lang.String)
         */
        @ Override
        public void onReceivedTitle ( WebView view , String title ) {
            current_tittle = title ;
           
            // if ( current_tittle != null && !current_tittle.contains (
            // "page not" ) ) {
            // top_search_text.setText ( ""+title );
            // }
            super.onReceivedTitle ( view , title ) ;
        }
        
        /*
         * (non-Javadoc)
         * @see
         * android.webkit.WebChromeClient#onRequestFocus(android.webkit.WebView)
         */
        @ Override
        public void onRequestFocus ( WebView view ) {
            
            super.onRequestFocus ( view ) ;
        }
    }
    
    @ Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        MenuInflater inflater = getMenuInflater ( ) ;
        inflater.inflate ( R.menu.main_activity_menu , menu ) ;
        if ( isFavorite != null ) {
            MenuItem menu_item = menu.getItem ( 1 ) ;
            menu_item.setIcon ( R.drawable.bok_yes ) ;
            menu_item.setTitle ( Constant.UN_MARK ) ;
        } else {
            MenuItem menu_item = menu.getItem ( 1 ) ;
            menu_item.setIcon ( R.drawable.bok_no ) ;
            menu_item.setTitle ( Constant.MARK ) ;
        }
        return true ;
    }
    
    @ Override
    public boolean onPrepareOptionsMenu ( Menu menu ) {
        super.onPrepareOptionsMenu ( menu ) ;
        if ( isFavorite != null ) {
            MenuItem menu_item = menu.getItem ( 1 ) ;
            menu_item.setIcon ( R.drawable.bok_yes ) ;
            menu_item.setTitle ( Constant.UN_MARK ) ;
            return true ;
        } else {
            MenuItem menu_item = menu.getItem ( 1 ) ;
            menu_item.setIcon ( R.drawable.bok_no ) ;
            menu_item.setTitle ( Constant.MARK ) ;
            return true ;
        }
        
    }
    
    private void doFavoriteOrUnFavoriteOperation ( ) {
        try {
            if ( isFavorite != null ) {
                Utils.getGTRACKER ( getApplicationContext ( ) ).trackBookMarkEvent ( Constant.GoogleAnalytics.EVENT_BOOK_MARK_DELETED );
                Utils.database.deleteBookMark ( isFavorite ) ;
                isFavorite = null ;
                top_Favroite_button.setSelected ( false ) ;
            } else {
                showBookMarkDailog ( ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "doFavoriteOrUnFavoriteOperation Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    @ Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        
        switch ( item.getItemId ( ) ) {
            case R.id.menu_more:
                Intent intent = new Intent ( CustomeWebViewActivity.this , MoreActivity.class ) ;
                startActivity ( intent ) ;
                return true ;
                
            case R.id.menu_add_to_book_mark:
                doFavoriteOrUnFavoriteOperation ( ) ;
                return true ;
                
            case R.id.menu_book_marks:

                intent = new Intent ( CustomeWebViewActivity.this , BookMarkActivity.class ) ;
                startActivity ( intent ) ;
                return true ;
                
            case R.id.menu_exit:

                showExitDailog ( ) ;
                return true ;
                
            case R.id.menu_home:
//                Utils.getGTRACKER ( getApplicationContext ( ) ).trackGeneralEvent ( Constant.GoogleAnalytics.CATEGORY_GANERAL_HOME );
//                showHomePage ( ) ;
            	if(html!=null && !current_url.equals(Constant.SELECTED_LANGUAGE.getUrl())){
            		intent=new Intent(CustomeWebViewActivity.this,SavePage.class);
            		intent.putExtra("title", current_tittle);
            		intent.putExtra("htmlcode", html);
            		startActivity(intent);
            	}
            	else{
            		intent=new Intent(CustomeWebViewActivity.this,SavePage.class);
            		intent.putExtra("title", "null");
            		intent.putExtra("htmlcode", "null");
            		startActivity(intent);
            	}
            	
                return true ;
            case R.id.menu_refresh:
                Utils.getGTRACKER ( getApplicationContext ( ) ).trackGeneralEvent ( Constant.GoogleAnalytics.CATEGORY_GANERAL_REFRESH );
                refreshPage ( current_url ) ;
                return true ;
                
            default:
                return super.onOptionsItemSelected ( item ) ;
        }
    }
    
    private void showHomePage ( ) {
        try {
            refreshPage ( null ) ;
        } catch ( Exception e ) {
            Log.e ( "Exception" , "showHomePage Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    private void refreshPage ( String pageURL ) {
        try {
            String urlToLoad = null ;
            if ( pageURL == null ) {
                urlToLoad = Constant.SELECTED_LANGUAGE.getUrl ( ) ;
            } else {
                urlToLoad = pageURL ;
            }
            if ( urlToLoad != null ) {
                current_url = new String ( urlToLoad ) ;
                webView.loadUrl ( urlToLoad ) ;
                showProgressBar ( ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "refreshPage Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
    }
    
    private void showBookMarkDailog ( ) {
        // Set an EditText view to get user input
        try {
            if ( current_tittle != null && current_tittle.contains ( "page not" ) ) {
                showMessage ( "Error" , "No page found for bookmark. Please check you internet connection and refresh this page" ) ;
                return ;
            }
            if ( current_tittle != null && current_tittle.length ( ) > 0 ) {
                if ( current_tittle.contains ( " - Wikipedia, the free encyclopedia" ) ) {
                    cur_bookmark_title = current_tittle.replace ( " - Wikipedia, the free encyclopedia" , "" ) ;
                } else {
                    cur_bookmark_title = new String ( current_tittle ) ;
                }
                
                final EditText input = new EditText ( CustomeWebViewActivity.this ) ;
                
                if ( cur_bookmark_title != null && cur_bookmark_title.length ( ) > 0 ) {
                    input.setHint ( "" + cur_bookmark_title ) ;
                } else {
                    input.setHint ( "" + Constant.BOOK_MARK_NAME ) ;
                }
                
                new AlertDialog.Builder ( CustomeWebViewActivity.this ).setTitle ( "Add New BookMark" ).setView ( input ).setPositiveButton ( "Ok" ,
                        new DialogInterface.OnClickListener ( ) {
                            public void onClick ( DialogInterface dialog , int whichButton ) {
                                
                                String bookMarkName = input.getText ( ).toString ( ) ;
                                if ( bookMarkName != null && bookMarkName.length ( ) > 0 ) {
                                    bookMarkName = bookMarkName.trim ( ) ;
                                } else {
                                    bookMarkName = "" + cur_bookmark_title ;
                                }
                                
                                WikiData newBookMark = new WikiData ( bookMarkName , current_url ) ;
                                isFavorite = newBookMark ;
                                top_Favroite_button.setSelected ( true ) ;
                                Utils.getGTRACKER ( getApplicationContext ( ) ).trackBookMarkEvent ( Constant.GoogleAnalytics.EVENT_BOOK_MARK_ADDED );
                                Utils.database.addNewBookMark ( newBookMark ) ;
                                
                            }
                        } ).setNegativeButton ( "Cancel" , new DialogInterface.OnClickListener ( ) {
                    public void onClick ( DialogInterface dialog , int whichButton ) {
                        // Do nothing.
                    }
                } ).show ( ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "showBookMarkDailog Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    @ Override
    public boolean onKeyDown ( int keyCode , KeyEvent event ) {
        try {
            if ( event.getKeyCode ( ) == KeyEvent.KEYCODE_BACK && event.getRepeatCount ( ) == 0 ) {
                if ( webView != null && webView.canGoBack ( ) ) {
                    webView.goBack ( ) ;
                    return true ;
                } else {
                    showExitDailog ( ) ;
                    return true ;
                }
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "onKeyDown Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        return super.onKeyDown ( keyCode , event ) ;
    }
    
    public void showExitDailog ( ) {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this ) ;
        
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener ( ) {
            @ Override
            public void onClick ( DialogInterface dialog , int which ) {
                switch ( which ) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Utils.getGTRACKER ( getApplicationContext ( ) ).trackGeneralEvent ( Constant.GoogleAnalytics.CATEGORY_GANERAL_FINISHED );
                        finish ( ) ;
                        break ;
                    
                    case DialogInterface.BUTTON_NEGATIVE:

                        break ;
                }
            }
        } ;
        
        builder.setMessage ( "Are you sure you want to exit?" ).setPositiveButton ( "Yes" , dialogClickListener ).setNegativeButton ( "No" ,
                dialogClickListener ).show ( ) ;
    }
    
    private void showMessage ( String title , String message ) {
        
        AlertDialog alert = new AlertDialog.Builder ( CustomeWebViewActivity.this ).setTitle ( title ).setMessage ( message ).setPositiveButton (
                "OK" , null ).create ( ) ;
        alert.show ( ) ;
        
    }
    
    @ Override
    public void onClick ( View view ) {
        if ( view == top_Favroite_button ) {
            doFavoriteOrUnFavoriteOperation ( ) ;
        } else if ( view == top_search_button ) {
            startSearchIntent ( ) ;
        }
        
    }
    
    private void startSearchIntent ( ) {
        try {
            hideKeyboard ( ) ;
            String textForSearch = top_search_text.getText ( ).toString ( ) ;
            if ( textForSearch != null && textForSearch.length ( ) > 0 ) {
                textForSearch = textForSearch.trim ( ) ;
                if ( textForSearch.length ( ) > 0 ) {
                    Intent searchIntent = new Intent ( CustomeWebViewActivity.this , SearchResultActivity.class ) ;
                    searchIntent.putExtra ( "text" , textForSearch ) ;
                    startActivity ( searchIntent ) ;
                    top_search_text.setText ( "" ) ;
                } else {
                    showMessage ( "Inavlid Text" , "Please enter valid text for search" ) ;
                }
            } else {
                showMessage ( "Error" , "Please enter text for search" ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "startSearchIntent Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    private void hideKeyboard ( ) {
        try {
            if(top_search_text != null){
                InputMethodManager imm = ( InputMethodManager ) getSystemService ( Context.INPUT_METHOD_SERVICE ) ;
                imm.hideSoftInputFromWindow ( top_search_text.getWindowToken ( ) , 0 ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "hideKeyboard Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    public void SelectText(){  
        try{ 
//       
          KeyEvent shiftPressEvent =   
                   new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,       
                   KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);  
          shiftPressEvent.dispatch(webView);  
      }catch(Exception e){  
          throw new AssertionError(e);  
      }  
    }
}
