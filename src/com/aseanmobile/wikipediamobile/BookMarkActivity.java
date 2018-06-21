package com.aseanmobile.wikipediamobile ;

import java.io.File;
import java.util.ArrayList ;

import android.app.Activity ;
import android.app.AlertDialog ;
import android.content.Context ;
import android.content.DialogInterface ;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener ;
import android.os.Bundle ;
import android.os.Environment;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener ;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView.OnEditorActionListener ;

import com.aseanmobile.wikipediamobile.model.WikiData ;
import com.aseanmobile.wikipediamobile.support.Constant ;
import com.aseanmobile.wikipediamobile.support.HeavyWorker ;
import com.aseanmobile.wikipediamobile.support.HeavyWorkerDelegate ;
import com.aseanmobile.wikipediamobile.support.Utils ;

public class BookMarkActivity extends Activity implements OnItemClickListener , OnClickListener , android.view.View.OnClickListener {
    
    private ListView               bookMarkListView       = null ;
    private BookMarkAdapter        bookMarkAdapter        = null ;
    
    private boolean                FINISH_NOW             = false ;
    private boolean                SHOW_EDIT_DAILOG_AGIAN = false ;
    private WikiData               tempData               = null ;
    
    private ArrayList < WikiData > fullBookMarkList ;
    
    TableLayout                    searchBarHolder         = null ;
    EditText                       searchText             = null ;
    ImageButton                    searchButton           = null ;
    
    private boolean                isEnterPressed         = false ;
    private int                    textLength ;
    
    @ Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState ) ;
        setContentView ( R.layout.book_mark_ui ) ;
        Utils.getGTRACKER ( getApplicationContext ( ) ).trackPageViewEvent ( Constant.GoogleAnalytics.CATEGORY_BOOKMARK );
        bookMarkListView = ( ListView ) findViewById ( R.id.book_mark_list_view ) ;
        bookMarkListView.setOnItemClickListener ( this ) ;
        
        searchBarHolder = ( TableLayout ) findViewById ( R.id.audio_sear_holder ) ;
        searchText = ( EditText ) findViewById ( R.id.audio_sear_edit_text ) ;
        searchButton = ( ImageButton ) findViewById ( R.id.audio_sear_btn ) ;
        searchButton.setOnClickListener ( this ) ;
        
        searchText.setOnFocusChangeListener ( new OnFocusChangeListener ( ) {
            
            public void onFocusChange ( View v , boolean hasFocus ) {
                
                InputMethodManager imm = ( InputMethodManager ) getSystemService ( Context.INPUT_METHOD_SERVICE ) ;
                if ( hasFocus ) {
                    imm.showSoftInput ( searchText , 0 ) ;
                } else {
                    imm.hideSoftInputFromWindow ( searchText.getWindowToken ( ) , 0 ) ;
                }
            }
        } ) ;
        
        searchText.setOnEditorActionListener ( new OnEditorActionListener ( ) {
            @ Override
            public boolean onEditorAction ( TextView v , int actionId , KeyEvent event ) {
                if ( actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
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
                try {
                    if ( fullBookMarkList == null || fullBookMarkList.size ( ) <= 0 ) {
                        return ;
                    }
                    
                    String text = searchText.getText ( ).toString ( ) ;
                    if ( text == null || text.length ( ) <= 0 ) {
                        hideKeyboard ( ) ;
                        getBookMarkList ( null ) ;
                    }
                    
                } catch ( Exception e ) {
                    Log.e ( "Exception" , "onTextChanged Message = " + e.toString ( ) ) ;
                    e.printStackTrace ( ) ;
                }
                
            }
        } ) ;
        
    }
    
    public void filterOutSearchResult ( ) {
        try {
            hideKeyboard ( ) ;
            Utils.getGTRACKER ( getApplicationContext ( ) ).trackBookMarkEvent ( Constant.GoogleAnalytics.EVENT_BOOK_MARK_SEARCH );
            textLength = searchText.getText ( ).length ( ) ;
            String text = searchText.getText ( ).toString ( ) ;
            text = text.toLowerCase ( ) ;
            
            if ( textLength > 0 ) {
                getBookMarkList ( text ) ;
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
        if ( bookMarkAdapter == null ) {
            getBookMarkList ( null ) ;
        }
        super.onResume ( ) ;
    }
    
    private void getBookMarkList ( final String textToSearch ) {
        Utils.getGTRACKER ( getApplicationContext ( ) ).trackBookMarkEvent ( Constant.GoogleAnalytics.EVENT_BOOK_MARK_GET_FROM_DB );
        try {
            HeavyWorkerDelegate serviceWork = new HeavyWorkerDelegate ( ) {
                
                @ Override
                public Object performInBackground ( ) {
                    return Utils.database.getBookMarkItems ( textToSearch ) ;
                }
                
                @ Override
                public void callback ( Object result ) {
                    ArrayList < WikiData > bookMarkList = ( ArrayList < WikiData > ) result ;
                    
                    if ( bookMarkList == null || bookMarkList.size ( ) <= 0 ) {
                        showMessage ( "Sorry" , "No bookmark found. Please add a bookmark from menu in the app." ) ;
                        FINISH_NOW = true ;
                    } else {
                        fullBookMarkList = bookMarkList ;
                        bookMarkAdapter = new BookMarkAdapter ( BookMarkActivity.this , R.layout.bookmark_row , bookMarkList ) ;
                        bookMarkListView.setAdapter ( bookMarkAdapter ) ;
                       // bookMarkListView.setOnLongClickListener(l)
                        bookMarkListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                	        public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                	        	  WikiData clickedData = bookMarkAdapter.getmBookMarkList ( ).get ( position ) ;
                	        	  showBookMarkOption(clickedData,position);
                               
                	        	//Toast.makeText(getBaseContext(), clickedData.toString(), 1000).show();
return FINISH_NOW;                	        	 
                	        	
                	                 }
                	             });
                	             
                        
                        
                    }
                }
                
                @ Override
                public void finalResult ( ) {
                    
                }
            } ;
            
            new HeavyWorker ( serviceWork , BookMarkActivity.this ).execute ( serviceWork ) ;
            
        } catch ( Exception e ) {
            Log.e ( "Exception" , "getBookMarkList Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    @ Override
    public void onItemClick ( AdapterView < ? > parent , View view , int position , long id ) {
        hideKeyboard ( ) ;
        try {
            if ( bookMarkAdapter != null && bookMarkAdapter.getmBookMarkList ( ) != null && bookMarkAdapter.getmBookMarkList ( ).size ( ) > 0 ) {
                
                WikiData clickedData = bookMarkAdapter.getmBookMarkList ( ).get ( position ) ;
                if ( clickedData != null ) {
                   
                    CustomeWebViewActivity.book_mark_url_to_open = new String ( clickedData.getUrl ( ) ) ;
                    Utils.getGTRACKER ( getApplicationContext ( ) ).trackBookMarkEvent ( Constant.GoogleAnalytics.EVENT_BOOK_MARK_OPEN );
                    finish ( ) ;
              
                	//showBookMarkOption ( clickedData , position ) ;
                }
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "onItemClick Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
    }
    
    
    private void showBookMarkOption ( final WikiData clickedData , final int rowNo ) {
        try {
            new AlertDialog.Builder ( BookMarkActivity.this ).setTitle ( "Please select" ).setItems ( R.array.book_mark_dailog_options ,
                    new DialogInterface.OnClickListener ( ) {
                        public void onClick ( DialogInterface dialog , int which ) {
                            
                           // if ( which == 0 ) {
                            //    CustomeWebViewActivity.book_mark_url_to_open = new String ( clickedData.getUrl ( ) ) ;
                             //   Utils.getGTRACKER ( getApplicationContext ( ) ).trackBookMarkEvent ( Constant.GoogleAnalytics.EVENT_BOOK_MARK_OPEN );
                              //  finish ( ) ;
                        //    }
                        //else 
                        	if ( which == 0 ) {
                                Utils.getGTRACKER ( getApplicationContext ( ) ).trackBookMarkEvent ( Constant.GoogleAnalytics.EVENT_BOOK_MARK_ADDED );
                                showBookMarkEditDialog ( clickedData ) ;
                            } else if ( which == 1 ) {
                                Utils.getGTRACKER ( getApplicationContext ( ) ).trackBookMarkEvent ( Constant.GoogleAnalytics.EVENT_BOOK_MARK_DELETED );
                                Utils.database.deleteBookMark ( clickedData ) ;
                                bookMarkAdapter.getmBookMarkList ( ).remove ( rowNo ) ;
                                bookMarkAdapter.notifyDataSetChanged ( ) ;
                                if ( bookMarkAdapter.getmBookMarkList ( ).size ( ) <= 0 ) {
                                    finish ( ) ;
                                }
                            }
                        }
                    } ).show ( ) ;
        } catch ( Exception e ) {
            Log.e ( "Exception" , "showBookMarkOption Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    private void showBookMarkEditDialog ( final WikiData clickedData ) {
        
        final EditText input = new EditText ( BookMarkActivity.this ) ;
        
        if ( clickedData.getTitle ( ) != null && clickedData.getTitle ( ).length ( ) > 0 ) {
            input.setText ( "" + clickedData.getTitle ( ) ) ;
        } else {
            input.setText ( "" + Constant.BOOK_MARK_NAME ) ;
        }
        
        new AlertDialog.Builder ( BookMarkActivity.this ).setTitle ( "Edit Bookmark" ).setView ( input ).setPositiveButton ( "Ok" ,
                new DialogInterface.OnClickListener ( ) {
                    public void onClick ( DialogInterface dialog , int whichButton ) {
                        
                        String bookMarkName = input.getText ( ).toString ( ) ;
                        if ( bookMarkName != null && bookMarkName.length ( ) > 0 ) {
                            bookMarkName = bookMarkName.trim ( ) ;
                            clickedData.setTitle ( bookMarkName ) ;
                            Utils.database.updateBookMark ( clickedData ) ;
                            bookMarkAdapter.notifyDataSetChanged ( ) ;
                        } else {
                            showMessage ( "Sorry" , "Please enter bookmark name" ) ;
                            SHOW_EDIT_DAILOG_AGIAN = true ;
                            tempData = clickedData ;
                        }
                    }
                } ).setNegativeButton ( "Cancel" , new DialogInterface.OnClickListener ( ) {
            public void onClick ( DialogInterface dialog , int whichButton ) {
                // Do nothing.
            }
        } ).show ( ) ;
    }
    
    private void showMessage ( String title , String message ) {
        
        AlertDialog alert = new AlertDialog.Builder ( this ).setTitle ( title ).setMessage ( message ).setPositiveButton ( "OK" , this ).create ( ) ;
        alert.show ( ) ;
        
    }
    
    @ Override
    public void onClick ( DialogInterface view , int id ) {

        if ( SHOW_EDIT_DAILOG_AGIAN ) {
            SHOW_EDIT_DAILOG_AGIAN = false ;
            showBookMarkEditDialog ( tempData ) ;
            return ;
        }
        if ( FINISH_NOW == true ) {
            finish ( ) ;
            return ;
        }
        
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