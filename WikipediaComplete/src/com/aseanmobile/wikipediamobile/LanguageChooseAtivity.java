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
import android.content.SharedPreferences ;
import android.content.res.Configuration ;
import android.os.Bundle ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.AdapterView ;
import android.widget.ArrayAdapter ;
import android.widget.ImageView ;
import android.widget.ListView ;
import android.widget.RadioGroup ;
import android.widget.TextView ;
import android.widget.AdapterView.OnItemClickListener ;

import com.aseanmobile.wikipediamobile.model.MyMenuItem ;
import com.aseanmobile.wikipediamobile.model.ViewHolder ;
import com.aseanmobile.wikipediamobile.support.Constant ;
import com.aseanmobile.wikipediamobile.support.Utils ;

public class LanguageChooseAtivity extends Activity {
    
    SharedPreferences  prefrence    = null ;
    ListView           lv ;
    private MyMenuItem activityLocalRoot ;
    static MyMenuItem  transferRoot = null ;
    RadioGroup         radioGroup   = null ;
    MyAdapter          listAdapter  = null ;
    int                pos          = 0 ;
    
    @ Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState ) ;
        this.setRequestedOrientation ( 1 ) ;
        Utils.getGTRACKER ( getApplicationContext ( ) ).trackPageViewEvent ( Constant.GoogleAnalytics.PAGE_VIEW_CHOSE_LANGUAGE );
        setContentView ( R.layout.language_chose_ui ) ;
        if(Constant.ALL_LANGUAGES == null || Constant.ALL_LANGUAGES.getChilds ( ).size ( ) <= 0){
            activityLocalRoot = new MyMenuItem ( ) ;
            loadXML ( activityLocalRoot ) ;
            Constant.ALL_LANGUAGES = activityLocalRoot ;
        }else{
            activityLocalRoot = Constant.ALL_LANGUAGES;
        }
        
        prefrence = getSharedPreferences ( Constant.PREF_NAME , this.MODE_WORLD_WRITEABLE ) ;
        pos = prefrence.getInt ( Constant.LANG_KEY , 0 ) ;
        if ( pos != 0 ) {
            activityLocalRoot.getChilds ( ).get ( pos ).setSelected ( true ) ;
        } else {
            activityLocalRoot.getChilds ( ).get ( pos ).setSelected ( true ) ;
        }
        Constant.SELECTED_LANGUAGE = activityLocalRoot.getChilds ( ).get ( pos ) ;
        
        lv = ( ListView ) findViewById ( R.id.main_list_views ) ;
        listAdapter = new MyAdapter ( this , R.layout.list_item , activityLocalRoot.getChilds ( ) ) ;
        lv.setAdapter ( listAdapter ) ;
        lv.setOnItemClickListener ( new OnItemClickListener ( ) {
            
            @ Override
            public void onItemClick ( AdapterView < ? > parent , View view , int position , long id ) {
                
                if(position == pos){
                    return;
                }
                prefrence.edit ( ).putInt ( Constant.LANG_KEY , position ).commit ( ) ;
                
                activityLocalRoot.getChilds ( ).get ( pos ).setSelected ( false ) ;
                activityLocalRoot.getChilds ( ).get ( position ).setSelected ( true ) ;
                Constant.SELECTED_LANGUAGE = activityLocalRoot.getChilds ( ).get ( position ) ;
                CustomeWebViewActivity.CHANGE_LANGUAGE_URL = true;
                listAdapter.notifyDataSetChanged ( ) ;
                finish ( ) ;
            }
        } ) ;
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
            Document doc = parser.parse ( getFileFromResource ( "struct" , "raw" ) ) ;
            
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
            
            if ( attributes != null )
                for ( int a = 0 ; a < attributes.getLength ( ) ; a++ ) {
                    Node attribute = attributes.item ( a ) ;
                    if ( attribute.getNodeName ( ).equals ( "icon" ) )
                        newItem.setIcon ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "text" ) )
                        newItem.setText ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "comment" ) )
                        newItem.setComment ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "content" ) )
                        newItem.setContent ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "image_resource_name" ) )
                        newItem.setImageResName ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "map" ) )
                        newItem.setMap ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "mapfloat" ) )
                        newItem.setMapfloat ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "mapzoom" ) )
                        newItem.setMapzoom ( attribute.getNodeValue ( ) ) ;
                    if ( attribute.getNodeName ( ).equals ( "url" ) )
                        newItem.setUrl ( attribute.getNodeValue ( ) ) ;
                }
            
            if ( items.item ( i ).getNodeName ( ).equals ( "menu" ) ) {
                String text = newItem.getText ( ) ;
                text += "" ;
                newItem.setText ( text ) ;
                if ( ( items.item ( i ).getNodeType ( ) & Node.ELEMENT_NODE ) != 0 )
                    parseSubElements ( ( Element ) items.item ( i ) , newItem ) ;
            }
            
            parent.getChilds ( ).add ( newItem ) ;
        }
    }
    
    @ Override
    public void onConfigurationChanged ( Configuration newConfig ) {
        super.onConfigurationChanged ( newConfig ) ;
    }
    
    public class MyAdapter extends ArrayAdapter < MyMenuItem > {
        
        private LayoutInflater mInflater ;
        
        int                    mResource ;
        List < MyMenuItem >    mData ;
        Context                context ;
        MyMenuItem             currentItem = null ;
        int                    index       = 0 ;
        ViewHolder             holder      = null ;
        
        public MyAdapter ( Context context , int resource , List < MyMenuItem > data ) {
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
            ImageView icon = ( ImageView ) convertView.findViewById ( R.id.icon ) ;
            TextView text = ( TextView ) convertView.findViewById ( R.id.text ) ;
            TextView comment = ( TextView ) convertView.findViewById ( R.id.comment ) ;
            
            text.setText ( mData.get ( position ).getText ( ) ) ;
            comment.setText ( mData.get ( position ).getComment ( ) ) ;
            icon.setImageResource ( getApplicationContext ( ).getResources ( ).getIdentifier ( mData.get ( position ).getIcon ( ) , "drawable" ,
                    getPackageName ( ) ) ) ;
            
            if ( mData.get ( position ).isSelected ( ) ) {
                convertView.setBackgroundResource ( R.drawable.list_selector ) ;
            } else {
                convertView.setBackgroundResource ( R.drawable.list_not_selector ) ;
            }
            return convertView ;
        }
        
    }
}