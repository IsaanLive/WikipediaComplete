/**
 * 
 */
package com.aseanmobile.wikipediamobile ;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList ;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity ;
import android.app.AlertDialog ;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap ;
import android.graphics.Color;
import android.media.MediaPlayer ;
import android.media.MediaPlayer.OnCompletionListener ;
import android.os.AsyncTask;
import android.os.Bundle ;
import android.os.Environment;
import android.os.Message ;
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
import android.view.inputmethod.EditorInfo ;
import android.view.inputmethod.InputMethodManager ;
import android.webkit.HttpAuthHandler ;
import android.webkit.JsPromptResult ;
import android.webkit.JsResult ;
import android.webkit.WebChromeClient ;
import android.webkit.WebView ;
import android.webkit.WebViewClient ;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText ;
import android.widget.ImageButton ;
import android.widget.ListView;
import android.widget.ProgressBar ;
import android.widget.TextView ;
import android.widget.TextView.OnEditorActionListener ;

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
    
   protected  String                   current_url , current_tittle , cur_bookmark_title ;
    ArrayList < WikiData >   historyList ;
    
    public static String     book_mark_url_to_open ;
    protected static boolean CHANGE_LANGUAGE_URL ;
    private WikiData         isFavorite ;
    
    private boolean          isEnterPressed = false ;
    protected ProgressDialog progressDialog; 
//private Context ct; 
protected asynhttpp asht;
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @ Override
    protected void onCreate ( Bundle savedInstanceState ) {
       
        
    	super.onCreate ( savedInstanceState ) ;
    	
    //	ct=CustomeWebViewActivity.this;
    	 
        try {
            setContentView ( R.layout.custom_web_view ) ;
            
            top_progress_bar = ( ProgressBar ) findViewById ( R.id.top_progress_bar ) ;
            top_progress_bar.setProgress ( 0 ) ;
            
            top_search_text = (EditText) findViewById ( R.id.top_header_search_txt );
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
            showProgressBar ( ) ;
            
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
            webView.setBackgroundColor(Color.WHITE);
          //  webView.setClickable(true);
           webView.setWebChromeClient ( new MyWebChromeClient ( ) ) ;
            webView.setWebViewClient(new WebViewClient() {  
            	 // public void onPageFinished ( WebView view , String url ) {
                  //    hideProgressDailog ( ) ;
                   //   super.onPageFinished ( view , url ) ;
                 // } 
            	
            	@Override  
                public void onPageFinished(WebView view, String url)  
                {   hideProgressDailog ( ) ;//////ajays codeyyy
                    webView.loadUrl("javascript:(function() { " +  
                            "document.getElementById('mw-mf-header').style.display='none'; " +  
                            "})()");  
                }
            	
            }); 
           
            webView.loadUrl ( uri ) ;
          webView.clearCache ( true ) ;
            webView.clearHistory ( ) ;
             
            
         //   webView.setWebViewClient ( new MyWebView ( ) ) ;
            
            ///
            
           
            
            ////
            webView.requestFocus ( );
          //  Toast.makeText(getBaseContext(),uri,1000).show();
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
        hideProgressDailog ( ) ;
        
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
            WikiData isAlreadyBookMarked = Utils.database.getBookMarkForURL ( url ) ;
            if ( isAlreadyBookMarked != null ) {
                isFavorite = isAlreadyBookMarked ;
                top_Favroite_button.setSelected ( true ) ;
            } else {
                isFavorite = null ;
                top_Favroite_button.setSelected ( false ) ;
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
        public void onSelectionStart(WebView view) {
            // By default we cancel the selection again, thus disabling
            // text selection unless the chrome client supports it.
            // view.notifySelectDialogDismissed();
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
            current_url=view.getUrl();
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
                Utils.getGTRACKER ( getApplicationContext ( ) ).trackGeneralEvent ( Constant.GoogleAnalytics.CATEGORY_GANERAL_HOME );
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                asht=new asynhttpp(this,current_url,current_tittle);
        	    
                builder.setTitle("Options");

                final ListView modeList = new ListView(this);
                String[] stringArray = new String[] { "Save current page", "View saved page" };
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);

                //modeList.setOnItemSelectedListener(listener)
                modeList.setClickable(true);
                builder.setView(modeList);
                final Dialog dialog = builder.create();

                dialog.show();
                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                  @Override
                  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                	  if(position==0)
                	  {
                		  ///////////
                		//  Toast.makeText(getBaseContext(),current_tittle,100).show();
                		  File folder = new File(Environment.getExternalStorageDirectory() + "/wikimobilehtml");
                		 //   webView.loadData(data, "text/html", HTTP.UTF_8);
                		  boolean success = false;
                		  if (!folder.exists()) {
                		      success = folder.mkdir();
                		  }
                			  Object[] params = new Object[1];
                	        params[0] = "ss";
                	       	
                		// asht.execute(params);
                		  dialog.dismiss();
                		  
                		 // asht.settval(current_url,current_tittle);
                		  //////////////
                		  asht.execute(params);
                		  ///////////////////
                		  
                		  
                		  
                		  //ProgressDialog.show(CustomeWebViewActivity.this, "Saving in progress", "Please wait", true, false);

                		  if (!success) {
                		      // Do something on success
                		  } else {
                		      // Do something else on failure 
                		  }
                		  ///////////
                		  
                		  
                	  }
            		 

                		 // Toast.makeText(getBaseContext(), "nijin", 1000).show();
                 //   Object o = modeList.getItemAtPosition(position);
                    /* write you handling code like...
                    String st = "sdcard/";
                    File f = new File(st+o.toString());
                    // do whatever u want to do with 'f' File object
                    */  
                	  else
                      { Intent alertIntent=new Intent(CustomeWebViewActivity.this,listhtml.class);
                       // finish();
                	//alertIntent.putExtra("geo", adddr);
                      dialog.dismiss();
                	startActivity(alertIntent);
        	     
                    	  
                      }
                  }
                  
               
                  
                });
               
                
                
                // showHomePage ( ) ;
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
                	String uri = getIntent ( ).getExtras ( ).getString ( "uri" ) ;
                    if(uri.startsWith("file:///"))    
                    	finish();
                    else
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
}





class asynhttpp extends AsyncTask<Object, Void, String> {

    private final static String TAG = "LoginActivity.EfetuaLogin";
private Context ttt;
private CustomeWebViewActivity activity;
private Context context;
private String curl;
private String cbok;

    public asynhttpp(Context c, String currentUrl, String curBookmarkTitle) {
        context=c;    	//ttt=tt;
        curl=currentUrl;
        cbok=curBookmarkTitle;
	}


  


	private asynhttpp updateTask = null;
	private ProgressDialog pp;
	@Override
    protected void onPreExecute()
    {
       // super.onPreExecute();
		   updateTask = this;

        Log.d(TAG, "Executando onPreExecute de EfetuaLogin");
     
        //inicia diálogo de progresso, mostranto processamento com servidor.
         pp=new ProgressDialog(context);   
        pp.setMessage(cbok);
        pp.setOnDismissListener(new OnDismissListener() {               
            @Override
            public void onDismiss(DialogInterface dialog) {
            	updateTask.cancel(true);
            }
        });
        pp.show();
    }



    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        pp.dismiss();
       
        
     
     //   progressDialog.dismiss();
    }


	@Override
	protected String doInBackground(Object... arg0) {
		DefaultHttpClient client = new DefaultHttpClient();
    	HttpGet get = new HttpGet(curl);

    	//get.setHeader("Content-Type", "application/x-www-form-urlencoded");
    	//get.setHeader("User-Agent","Mozilla/5.0 (Linux; U; Android 2.1-update1; de-de; HTC Desire 1.19.161.5 Build/ERE27) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");

    	HttpResponse response = null;
		try {
			response = client.execute(get);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	String data = null;
		try {
			data = new BasicResponseHandler().handleResponse(response);
		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//data=data.replace("href=\"//", "href=\"");
	//	   protected  String                   current_url , current_tittle , cur_bookmark_title ;
		data=data.replace("href=\"", "href=\"http://en.m.wikipedia.org");
    	data=data.replace("src=\"//", "src=\"http://");
    	cbok=	cbok.replace(",", " ");
    	cbok=	cbok.replace(".", " ");
    	cbok=	cbok.replace(" ", "_");
     	
     	//cbok= 	cbok.replace("/", " ");
    	
        	File file = new File(Environment.getExternalStorageDirectory().toString(), "wikimobilehtml/"+cbok+".html");
        OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			outStream.write(data.getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			outStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return null;
		
	}

} 
