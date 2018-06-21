package com.aseanmobile.wikipediamobile ;

import java.util.ArrayList ;

import android.app.Activity ;
import android.app.AlertDialog ;
import android.content.Context ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.content.DialogInterface.OnClickListener ;
import android.os.Bundle ;
import android.text.Editable ;
import android.text.TextWatcher ;
import android.util.Log ;
import android.view.KeyEvent ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnFocusChangeListener ;
import android.view.inputmethod.EditorInfo ;
import android.view.inputmethod.InputMethodManager ;
import android.widget.AdapterView ;
import android.widget.ArrayAdapter ;
import android.widget.EditText ;
import android.widget.ImageButton ;
import android.widget.ListView ;
import android.widget.TableLayout ;
import android.widget.TextView ;
import android.widget.AdapterView.OnItemClickListener ;
import android.widget.TextView.OnEditorActionListener ;

import com.aseanmobile.wikipediamobile.model.WikiData ;
import com.aseanmobile.wikipediamobile.support.Constant ;
import com.aseanmobile.wikipediamobile.support.DataFetcher ;
import com.aseanmobile.wikipediamobile.support.HeavyWorker ;
import com.aseanmobile.wikipediamobile.support.HeavyWorkerDelegate ;
import com.aseanmobile.wikipediamobile.support.Utils ;

public class SearchResultActivity extends Activity implements OnItemClickListener , OnClickListener , android.view.View.OnClickListener {
    
    private ListView               searchItemListView = null ;
    private BookMarkAdapter        searchItemAdapter  = null ;
    
    private WikiData               tempData           = null ;
    
    private ArrayList < WikiData > fullBookMarkList ;
    
    TableLayout                    searchBarHolder    = null ;
    EditText                       searchText         = null ;
    ImageButton                    searchButton       = null ;
    
    private boolean                isEnterPressed     = false ;
    private int                    textLength ;
    
    String                         textToSearch       = null ;
    
    @ Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState ) ;
        setContentView ( R.layout.book_mark_ui ) ;
        Utils.getGTRACKER ( getApplicationContext ( ) ).trackPageViewEvent ( Constant.GoogleAnalytics.CATEGORY_SEARCH );
        searchItemListView = ( ListView ) findViewById ( R.id.book_mark_list_view ) ;
        searchItemListView.setOnItemClickListener ( this ) ;
        
        textToSearch = getIntent ( ).getExtras ( ).getString ( "text" ) ;
        
        if ( textToSearch == null || textToSearch.length ( ) <= 0 ) {
            showMessage ( "Error" , "No data found for search" ) ;
        }
        
        searchBarHolder = ( TableLayout ) findViewById ( R.id.audio_sear_holder ) ;
        searchText = ( EditText ) findViewById ( R.id.audio_sear_edit_text ) ;
        searchButton = ( ImageButton ) findViewById ( R.id.audio_sear_btn ) ;
        searchButton.setOnClickListener ( this ) ;
        
        searchText.setText ( textToSearch ) ;
        searchText.setSelection ( textToSearch.length ( ) ) ;
        
        searchText.setOnFocusChangeListener ( new OnFocusChangeListener ( ) {
            
            public void onFocusChange ( View v , boolean hasFocus ) {
                
                InputMethodManager imm = ( InputMethodManager ) getSystemService ( Context.INPUT_METHOD_SERVICE ) ;
                if ( hasFocus ) {
                    imm.showSoftInput ( searchText , 0 ) ;
                    String text = searchText.getText ( ).toString ( ) ;
                    if ( isEnterPressed ) {
                        isEnterPressed = false ;
                        if ( text != null && text.length ( ) > 0 ) {
                            searchText.setText ( text.substring ( 0 , text.length ( ) - 1 ) ) ;
                            searchText.setSelection ( searchText.getText ( ).length ( ) ) ;
                        }
                    }else{
                        if ( text != null && text.length ( ) > 0 ) {
                           searchText.setSelection ( searchText.getText ( ).length ( ) ) ;
                        }
                    }
                } else {
                    imm.hideSoftInputFromWindow ( searchText.getWindowToken ( ) , 0 ) ;
                }
            }
        } ) ;
        
        searchText.setOnEditorActionListener ( new OnEditorActionListener ( ) {
            @ Override
            public boolean onEditorAction ( TextView v , int actionId , KeyEvent event ) {
                if ( actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO
                        || event.getAction ( ) == KeyEvent.ACTION_DOWN && event.getKeyCode ( ) == KeyEvent.KEYCODE_ENTER ) {
                    filterOutSearchResult ( ) ;
                    isEnterPressed = true ;
                }
                return false ;
            }
        } ) ;
        
        searchText.addTextChangedListener ( new TextWatcher ( ) {
            
            public void afterTextChanged ( Editable s ) {
                
                if ( isEnterPressed ) {
                    isEnterPressed = false ;
                    String text = searchText.getText ( ).toString ( ) ;
                    if ( text != null && text.length ( ) > 0 ) {
                        searchText.setText ( text.substring ( 0 , text.length ( ) - 1 ) ) ;
                        searchText.setSelection ( searchText.getText ( ).length ( ) ) ;
                    }
                }
            }
            
            public void beforeTextChanged ( CharSequence s , int start , int count , int after ) {
            }
            
            public void onTextChanged ( CharSequence s , int start , int before , int count ) {
                
            }
        } ) ;
        hideKeyboard ( );
    }
    
    public void filterOutSearchResult ( ) {
        try {
            hideKeyboard ( ) ;
            textLength = searchText.getText ( ).length ( ) ;
            String text = searchText.getText ( ).toString ( ) ;
            text = text.toLowerCase ( ) ;
            String textForSearch = searchText.getText ( ).toString ( ) ;
            if ( textForSearch != null && textForSearch.length ( ) > 0 ) {
                textForSearch = textForSearch.trim ( ) ;
                if ( textForSearch.length ( ) > 0 ) {
                    textToSearch = text ;
                    getSearchList ( text ) ;
                } else {
                    showMessage ( "Inavlid Text" , "Please enter valid text for search" ) ;
                }
            } else {
                showMessage ( "Error" , "Please enter text for search" ) ;
            }
            if ( textLength > 0 ) {
                
            }
        } catch ( Exception e ) {
            e.printStackTrace ( ) ;
        }
    }
    
    @ Override
    public void onClick ( View view ) {
        if ( view == searchButton ) {
            filterOutSearchResult ( ) ;
        }
        
    }
    
    @ Override
    protected void onResume ( ) {
        if ( searchItemAdapter == null ) {
            getSearchList ( textToSearch ) ;
        }
        super.onResume ( ) ;
    }
    
    private void getSearchList ( final String textToSearch ) {
        Utils.getGTRACKER ( getApplicationContext ( ) ).trackSearchEvents ( Constant.GoogleAnalytics.EVENT_SEARCH_DONE );
        
        try {
            HeavyWorkerDelegate serviceWork = new HeavyWorkerDelegate ( ) {
                
                @ Override
                public Object performInBackground ( ) {
                    return DataFetcher.getInstance ( ).getDataListForSearch ( textToSearch ) ;
                }
                
                @ Override
                public void callback ( Object result ) {
                    ArrayList < WikiData > bookMarkList = ( ArrayList < WikiData > ) result ;
                    
                    if ( bookMarkList == null || bookMarkList.size ( ) <= 0 ) {
                        showMessage ( "Error" , "No result found" ) ;
                        hideKeyboard ( );
                    } else {
                        fullBookMarkList = bookMarkList ;
                        searchItemAdapter = new BookMarkAdapter ( SearchResultActivity.this , R.layout.bookmark_row , bookMarkList ) ;
                        searchItemListView.setAdapter ( searchItemAdapter ) ;
                        hideKeyboard ( );
                    }
                }
                
                @ Override
                public void finalResult ( ) {
                    
                }
            } ;
            
            new HeavyWorker ( serviceWork , SearchResultActivity.this ).execute ( serviceWork ) ;
            
        } catch ( Exception e ) {
            Log.e ( "Exception" , "getBookMarkList Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    @ Override
    public void onItemClick ( AdapterView < ? > parent , View view , int position , long id ) {
        hideKeyboard ( ) ;
        try {
            if ( searchItemAdapter != null && searchItemAdapter.getmBookMarkList ( ) != null && searchItemAdapter.getmBookMarkList ( ).size ( ) > 0 ) {
                
                WikiData clickedData = searchItemAdapter.getmBookMarkList ( ).get ( position ) ;
                if ( clickedData != null ) {
                    clickedData.refreshTime ( ) ;
                    CustomeWebViewActivity.book_mark_url_to_open = new String ( clickedData.getUrl ( ) ) ;
                    Utils.getGTRACKER ( getApplicationContext ( ) ).trackSearchEvents ( Constant.GoogleAnalytics.EVENT_SEARCH_RESULT );
                    finish ( ) ;
                }
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "onItemClick Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
    }
    
    private void showMessage ( String title , String message ) {
        
        AlertDialog alert = new AlertDialog.Builder ( this ).setTitle ( title ).setMessage ( message ).setPositiveButton ( "OK" , this ).create ( ) ;
        alert.show ( ) ;
        
    }
    
    @ Override
    public void onClick ( DialogInterface view , int id ) {
    }
    
    public class BookMarkAdapter extends ArrayAdapter < WikiData > {
        
        private LayoutInflater         mInflater ;
        
        private final int              mResource ;
        private ArrayList < WikiData > mBookMarkList ;
        Context                        context ;
        
        public BookMarkAdapter ( Context context , int resource , ArrayList < WikiData > data ) {
            super ( context , resource , data ) ;
            this.context = context ;
            mBookMarkList = data ;
            mResource = resource ;
            mInflater = ( LayoutInflater ) getSystemService ( Context.LAYOUT_INFLATER_SERVICE ) ;
        }
        
        public ArrayList < WikiData > getmBookMarkList ( ) {
            return mBookMarkList ;
        }
        
        @ Override
        public View getView ( int position , View convertView , ViewGroup parent ) {
            
            if ( convertView == null ) {
                convertView = mInflater.inflate ( mResource , null ) ;
            }
            try {
                TextView txtTitle = ( TextView ) convertView.findViewById ( R.id.book_mark_row_title ) ;
                TextView txtURL = ( TextView ) convertView.findViewById ( R.id.book_mark_row_url ) ;
                TextView txtTime = ( TextView ) convertView.findViewById ( R.id.book_mark_row_time ) ;
                
                WikiData mark = mBookMarkList.get ( position ) ;
                txtTitle.setText ( "" + mark.getTitle ( ) ) ;
                txtURL.setText ( "" + mark.getUrl ( ) ) ;
                txtTime.setText ( "" + mark.getTime ( ) ) ;
            } catch ( Exception e ) {
                Log.e ( "Exception" , "getView Message = " + e.toString ( ) ) ;
                e.printStackTrace ( ) ;
            }
            
            return convertView ;
        }
        
    }
    
    private void hideKeyboard ( ) {
        
        InputMethodManager imm = ( InputMethodManager ) getSystemService ( Context.INPUT_METHOD_SERVICE ) ;
        imm.hideSoftInputFromWindow ( searchText.getWindowToken ( ) , 0 ) ;
        
    }
    
}