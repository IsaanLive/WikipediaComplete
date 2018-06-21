package com.aseanmobile.wikipediamobile ;

import java.io.BufferedReader ;
import java.io.InputStream ;
import java.io.InputStreamReader ;

import com.aseanmobile.wikipediamobile.support.Constant ;
import com.aseanmobile.wikipediamobile.support.Utils ;

import android.app.Activity ;
import android.os.Bundle ;
import android.text.Html ;
import android.text.method.LinkMovementMethod ;
import android.widget.TextView ;

public class AboutUsActivity extends Activity {
    
    TextView txtAboutUs , txtFooter ;
    
    @ Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState ) ;
        setContentView ( R.layout.text_view ) ;
        Utils.getGTRACKER ( getApplicationContext ( ) ).trackPageViewEvent ( Constant.GoogleAnalytics.PAGE_VIEW_ABOUTUS );
        txtAboutUs = ( TextView ) findViewById ( R.id.about_text ) ;
        txtAboutUs.setText ( Html.fromHtml ( loadText ( ) ) ) ;
        txtAboutUs.setMovementMethod ( LinkMovementMethod.getInstance ( ) ) ;
        
        txtFooter = ( TextView ) findViewById ( R.id.footer ) ;
        txtFooter.setText ( Html.fromHtml ( "<br>_______________________________________________<br>"
                + "© 2010 <a href=\"http://aseanmobile.mobi/\">Adaptive Wave LLC</a><br>" + "All Rights reserved" ) ) ;
        txtFooter.setMovementMethod ( LinkMovementMethod.getInstance ( ) ) ;
    }
    
    private String loadText ( ) {
        InputStream input = getFileFromResource ( "text_data" , "raw" ) ;
        BufferedReader in ;
        String text = "" ;
        try {
            in = new BufferedReader ( new InputStreamReader ( input ) ) ;
            
            while ( in.ready ( ) ) {
                String line = in.readLine ( ) ;
                text = text + line ;
            }
        } catch ( Exception e ) {
            e.printStackTrace ( ) ;
        }
        return text ;
    }
    
    public InputStream getFileFromResource ( String fileName , String folder ) {
        return getApplicationContext ( ).getResources ( ).openRawResource (
                getApplicationContext ( ).getResources ( ).getIdentifier ( fileName , folder , getPackageName ( ) ) ) ;
    }
}
