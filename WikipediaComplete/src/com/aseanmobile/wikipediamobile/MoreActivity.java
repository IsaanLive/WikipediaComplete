package com.aseanmobile.wikipediamobile ;

import java.io.IOException ;
import java.io.InputStream ;
import java.util.List ;

import javax.xml.parsers.DocumentBuilderFactory ;
import javax.xml.parsers.ParserConfigurationException ;

import org.w3c.dom.Document ;
import org.w3c.dom.Element ;
import org.w3c.dom.NamedNodeMap ;
import org.w3c.dom.Node ;
import org.w3c.dom.NodeList ;
import org.xml.sax.SAXException ;

import android.app.Activity ;
import android.content.Context ;
import android.content.Intent ;
import android.content.res.Configuration ;
import android.net.Uri ;
import android.os.Bundle ;
import android.util.Log ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.AdapterView ;
import android.widget.ArrayAdapter ;
import android.widget.Button ;
import android.widget.ImageView ;
import android.widget.ListView ;
import android.widget.TextView ;
import android.widget.AdapterView.OnItemClickListener ;

import com.aseanmobile.wikipediamobile.model.MyMenuItem ;
import com.aseanmobile.wikipediamobile.model.ViewHolder ;
import com.aseanmobile.wikipediamobile.support.Constant ;
import com.aseanmobile.wikipediamobile.support.Utils ;

public class MoreActivity extends Activity implements OnItemClickListener {
    
    Button            btn_chose_language , btn_about_us ;
    MoreOptionAdapter optionAdapter = null ;
    ListView          lv ;
    
    MyMenuItem        optionData    = null ;
    
    @ Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState ) ;
        setContentView ( R.layout.more_ui ) ;
        lv = ( ListView ) findViewById ( R.id.more_option_list ) ;
        
        Utils.getGTRACKER ( getApplicationContext ( ) ).trackPageViewEvent ( Constant.GoogleAnalytics.CATEGORY_SETTING );
        
        optionData = new MyMenuItem ( ) ;
        loadXML ( optionData ) ;
        optionAdapter = new MoreOptionAdapter ( MoreActivity.this , R.layout.more_option_list_items , optionData.getChilds ( ) ) ;
        lv.setAdapter ( optionAdapter ) ;
        lv.setOnItemClickListener ( this ) ;
    }
    
    @ Override
    protected void onResume ( ) {
        try {
            if(optionAdapter != null){
                optionAdapter.notifyDataSetChanged ( );
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "onResume Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        super.onResume ( ) ;
    }
    
    public InputStream getFileFromResource ( String fileName , String folder ) {
        return getApplicationContext ( ).getResources ( ).openRawResource (
                getApplicationContext ( ).getResources ( ).getIdentifier ( fileName , folder , getPackageName ( ) ) ) ;
    }
    
    void loadXML ( MyMenuItem loadIn ) {
        javax.xml.parsers.DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ( ) ;
        dbf.setValidating ( false ) ;
        try {
            javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder ( ) ;
            Document doc = parser.parse ( getFileFromResource ( "fb_about_icons" , "raw" ) ) ;
            
            Element mainMenu = doc.getDocumentElement ( ) ;
            parseSubElements ( mainMenu , loadIn ) ;
            
        } catch ( ParserConfigurationException e ) {
        } catch ( IOException e ) {
        } catch ( SAXException e ) {
        }
    }
    
    private void parseSubElements ( Element menu , MyMenuItem parent ) {
        NodeList items = menu.getElementsByTagName ( "*" ) ;
        for ( int i = 0 ; i < items.getLength ( ) ; i++ ) {
            if ( items.item ( i ).getParentNode ( ) != menu )
                continue ;
            
            MyMenuItem newItem = new MyMenuItem ( ) ;
            newItem.setParent ( parent ) ;
            newItem.setLevel ( parent.getLevel ( ) + 1 ) ;
            
            NamedNodeMap attributes = items.item ( i ).getAttributes ( ) ;
            
            if ( attributes != null ) {
                for ( int a = 0 ; a < attributes.getLength ( ) ; a++ ) {
                    Node attribute = attributes.item ( a ) ;
                    if ( attribute.getNodeName ( ).equals ( "icon" ) )
                        newItem.setIcon ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "text" ) )
                        newItem.setText ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "comment" ) )
                        newItem.setComment ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "url" ) )
                        newItem.setUrl ( attribute.getNodeValue ( ) ) ;
                }
            }
            
            parent.getChilds ( ).add ( newItem ) ;
        }
    }
    
    @ Override
    public void onConfigurationChanged ( Configuration newConfig ) {
        super.onConfigurationChanged ( newConfig ) ;
    }
    
    public class MoreOptionAdapter extends ArrayAdapter < MyMenuItem > {
        
        private LayoutInflater mInflater ;
        
        int                    mResource ;
        List < MyMenuItem >    mData ;
        Context                context ;
        MyMenuItem             currentItem = null ;
        int                    index       = 0 ;
        ViewHolder             holder      = null ;
        
        public MoreOptionAdapter ( Context context , int resource , List < MyMenuItem > data ) {
            super ( context , resource , data ) ;
            this.context = context ;
            mData = data ;
            mResource = resource ;
            mInflater = ( LayoutInflater ) getSystemService ( Context.LAYOUT_INFLATER_SERVICE ) ;
        }
        
        @ Override
        public View getView ( int position , View convertView , ViewGroup parent ) {
            
            if ( convertView == null ) {
                convertView = mInflater.inflate ( mResource , null ) ;
            }
            try {
                ImageView icon = ( ImageView ) convertView.findViewById ( R.id.more_list_row_icon ) ;
                TextView text = ( TextView ) convertView.findViewById ( R.id.more_list_row_title ) ;
                TextView comment = ( TextView ) convertView.findViewById ( R.id.more_list_row_details ) ;
                
                MyMenuItem current = mData.get ( position ) ;
                String tempComments = current.getComment ( ) ;
                
                if ( tempComments.startsWith ( "Current : " ) ) {
                    if(Constant.SELECTED_LANGUAGE != null){
                        tempComments = tempComments + Constant.SELECTED_LANGUAGE.getText ( ) ;
                    }
                }
                
                text.setText ( mData.get ( position ).getText ( ) ) ;
                comment.setText ( tempComments ) ;
                
                icon.setImageResource ( getApplicationContext ( ).getResources ( ).getIdentifier ( mData.get ( position ).getIcon ( ) , "drawable" ,
                        getPackageName ( ) ) ) ;
            } catch ( Exception e ) {
                Log.e ( "Exception" , "getView Message = " + e.toString ( ) ) ;
                e.printStackTrace ( ) ;
            }
            
            return convertView ;
        }
    }
    
    @ Override
    public void onItemClick ( AdapterView < ? > parent , View view , int position , long id ) {
        try {
            Intent startIntent = null ;
            switch ( position ) {
                case 0:
                    Utils.getGTRACKER ( getApplicationContext ( ) ).trackSettingsEvents ( Constant.GoogleAnalytics.EVENT_SETTING_CHOSE_LANG );
                    startIntent = new Intent ( MoreActivity.this , LanguageChooseAtivity.class ) ;
                    startActivity ( startIntent ) ;
                    break ;
                case 1:
                    Utils.getGTRACKER ( getApplicationContext ( ) ).trackSettingsEvents ( Constant.GoogleAnalytics.EVENT_SETTING_ABOUT_US );
                    
                    startIntent = new Intent ( MoreActivity.this , AboutUsActivity.class ) ;
                    startActivity ( startIntent ) ;
                    break ;
                case 2:
                    Utils.getGTRACKER ( getApplicationContext ( ) ).trackSettingsEvents ( Constant.GoogleAnalytics.EVENT_SETTING_FACEBOOK );
                    Utils.getGTRACKER ( getApplicationContext ( ) ).trackPageViewEvent ( Constant.GoogleAnalytics.PAGE_VIEW_FACEBOOK );
                    startIntent = new Intent ( Intent.ACTION_VIEW ) ;
                    startIntent.setData ( Uri.parse ( optionAdapter.mData.get ( position ).getUrl ( ) ) ) ;
                    startActivity ( startIntent ) ;
                    break ;
                case 3:
                    Utils.getGTRACKER ( getApplicationContext ( ) ).trackPageViewEvent ( Constant.GoogleAnalytics.PAGE_VIEW_FACEBOOK );
                    Utils.getGTRACKER ( getApplicationContext ( ) ).trackSettingsEvents ( Constant.GoogleAnalytics.EVENT_SETTING_MAP );
                    MapViewer.transferItem = optionAdapter.mData.get ( position ) ;
                    startIntent = new Intent ( MoreActivity.this , MapViewer.class ) ;
                    startActivity ( startIntent ) ;
                    break ;
                default:
                    break ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "onItemClick Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
    }
}