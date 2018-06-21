package com.aseanmobile.wikipediamobile ;

import java.io.IOException ;
import java.io.InputStream ;

import javax.xml.parsers.DocumentBuilderFactory ;
import javax.xml.parsers.ParserConfigurationException ;

import org.w3c.dom.Document ;
import org.w3c.dom.Element ;
import org.w3c.dom.NamedNodeMap ;
import org.w3c.dom.Node ;
import org.w3c.dom.NodeList ;
import org.xml.sax.SAXException ;

import android.app.Activity ;
import android.content.Intent ;
import android.content.SharedPreferences ;
import android.os.Bundle ;
import android.os.Handler ;
import android.view.animation.Animation ;
import android.view.animation.ScaleAnimation ;
import android.view.animation.Animation.AnimationListener ;
import android.widget.LinearLayout ;

import com.aseanmobile.wikipediamobile.model.MyMenuItem ;
import com.aseanmobile.wikipediamobile.support.Constant ;
import com.aseanmobile.wikipediamobile.support.DataBase ;
import com.aseanmobile.wikipediamobile.support.Utils ;

public class SplashScreen extends Activity {
    LinearLayout       dataLayout ;
    SharedPreferences  prefrence             = null ;
    private MyMenuItem activityLocalRoot ;
    int                pos                   = 0 ;
    
    private final int  SPLASH_DISPLAY_LENGHT = 400 ;
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @ Override
    public void onCreate ( Bundle icicle ) {
        
        super.onCreate ( icicle ) ;
        setContentView ( R.layout.splash_ui ) ;
        DataBase db = new DataBase ( getApplicationContext ( ) ) ;
        
        
        // Set database and Google Aanalytics to globaly
        Utils.database = db ;
        Utils.setGTRACKER ( new GTracker(SplashScreen.this) );
        
        dataLayout = ( LinearLayout ) findViewById ( R.id.splash_main_linear_layout ) ;
        
        prefrence = getSharedPreferences ( Constant.PREF_NAME , MODE_WORLD_WRITEABLE ) ;
        pos = prefrence.getInt ( Constant.LANG_KEY , 0 ) ;
        
        ScaleAnimation scale = new ScaleAnimation ( 0.0f , 1f , 0.0f , 1f , Animation.RELATIVE_TO_SELF , 0.5f , Animation.RELATIVE_TO_SELF , 0.5f ) ;
        scale.setDuration ( 1200 ) ;
        scale.setAnimationListener ( new AnimationListener ( ) {
            @ Override
            public void onAnimationStart ( Animation animation ) {
                
            }
            
            @ Override
            public void onAnimationRepeat ( Animation animation ) {
                
            }
            
            @ Override
            public void onAnimationEnd ( Animation animation ) {
                activityLocalRoot = new MyMenuItem ( ) ;
                loadXML ( activityLocalRoot ) ;
                Constant.SELECTED_LANGUAGE = activityLocalRoot.getChilds ( ).get ( pos ) ;
                Constant.ALL_LANGUAGES = activityLocalRoot ;
                
                Utils.getGTRACKER ( getApplicationContext ( ) ).tackAppStartEvent ( );
                
                new Handler ( ).postDelayed ( new Runnable ( ) {
                    @ Override
                    public void run ( ) {
                        
                        Intent intent = new Intent ( getApplicationContext ( ) , CustomeWebViewActivity.class ) ;
                        intent.putExtra ( "uri" , Constant.SELECTED_LANGUAGE.getUrl ( ) ) ;
                        startActivity ( intent ) ;
                        // Intent mainIntent = new Intent ( SplashScreen.this ,
                        // Main.class ) ;
                        
                        // startActivity ( mainIntent ) ;
                        finish ( ) ;
                    }
                } , SPLASH_DISPLAY_LENGHT ) ;
            }
        } ) ;
        
        findViewById ( R.id.splash_image ).startAnimation ( scale ) ;
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
        ;
        
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
}
