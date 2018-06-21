package com.aseanmobile.wikipediamobile.support ;

import java.io.BufferedInputStream ;
import java.io.InputStream ;
import java.util.ArrayList ;

import org.apache.http.HttpResponse ;
import org.apache.http.client.methods.HttpGet ;
import org.apache.http.impl.client.DefaultHttpClient ;
import org.apache.http.util.ByteArrayBuffer ;

import com.aseanmobile.wikipediamobile.model.WikiData ;

import android.net.Uri ;
import android.util.Log ;

public class DataFetcher {
    
    private static DataFetcher instance ;
    
    private final String WIKI_QUERY_START = "http://en.wikipedia.org/w/api.php?action=opensearch&search=";
    private final String WIKI_QUERY_END = "&limit=15&namespace=0&format=xml";
    
    /**
     * Default constructor
     */
    private DataFetcher ( ) {
        
    }
    
    /*
     * Returns the Singleton object of this class
     */

    /**
     * @return {@link DataFetcher} Object
     */
    public static DataFetcher getInstance ( ) {
        
        if ( instance == null ) {
            instance = new DataFetcher ( ) ;
            
        }
        return instance ;
        
    }
    
    public ArrayList < WikiData > getDataListForSearch ( String textForSearch ) {
        
        String textAfterEncode = Uri.encode ( textForSearch );
        String URL = WIKI_QUERY_START + textAfterEncode + WIKI_QUERY_END;
        Log.e("Query",""+URL);
        
        
        ArrayList < WikiData > items = null ;
        
        try {
            HttpGet http = new HttpGet ( URL ) ;
            
            String response = this.getSendRequest ( http ) ;
            
            if ( response != null && response.length ( ) > 0 ) {
                DataParser parser = new DataParser ( ) ;
                items = parser.parseDataStories ( response );
            }
            
        } catch ( Exception e ) {
            Log.e ( "Exception" , "Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
        return items ;
    }
    
    private String getSendRequest ( HttpGet http ) {
        
        try {
            DefaultHttpClient client = new DefaultHttpClient ( ) ;
            
            HttpResponse response ;
            String stringResponse = "" ;
            
            response = client.execute ( http ) ;
            
            InputStream inputStream = response.getEntity ( ).getContent ( ) ;
            BufferedInputStream bufferedInputStream = new BufferedInputStream ( inputStream , 2048 ) ;
            ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer ( 2048 ) ;
            
            int currentBuf = 0 ;
            while ( ( currentBuf = bufferedInputStream.read ( ) ) != - 1 ) {
                byteArrayBuffer.append ( ( byte ) currentBuf ) ;
            }
            
            stringResponse = new String ( byteArrayBuffer.toByteArray ( ) ) ;
            return stringResponse ;
        } catch ( Exception e ) {
            Log.e ( "Exception" , "Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        return null ;
    }
}
