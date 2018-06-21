package com.aseanmobile.wikipediamobile.support ;

import java.io.ByteArrayInputStream ;
import java.io.InputStream ;
import java.util.ArrayList ;

import javax.xml.parsers.DocumentBuilder ;
import javax.xml.parsers.DocumentBuilderFactory ;

import org.w3c.dom.Document ;
import org.w3c.dom.Element ;
import org.w3c.dom.Node ;
import org.w3c.dom.NodeList ;

import android.util.Log ;

import com.aseanmobile.wikipediamobile.model.WikiData ;

public class DataParser {
    
    public ArrayList < WikiData > parseDataStories ( String xmlResponse ) {
        ArrayList < WikiData > itemList = new ArrayList < WikiData > ( ) ;
        try {
            InputStream inputStream = new ByteArrayInputStream ( xmlResponse.getBytes ( "UTF-8" ) ) ;
            
            DocumentBuilderFactory fectory = DocumentBuilderFactory.newInstance ( ) ;
            DocumentBuilder builder = fectory.newDocumentBuilder ( ) ;
            Document document = builder.parse ( inputStream ) ;
            
            NodeList nodeList ;
            Node childNode ;
            
            nodeList = document.getElementsByTagName ( "Item" ) ;
            
            for ( int i = 0 ; i < nodeList.getLength ( ) ; i++ ) {
                childNode = nodeList.item ( i ) ;
                WikiData item = this.parseStoryItem ( ( Element ) childNode ) ;
                if ( item != null ) {
                    // System.out.println(item);
                    itemList.add ( item ) ;
                }
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "Message = " + e.toString ( ) ) ;
        } catch ( Error e ) {
            Log.e ( "Error" , "Message = " + e.toString ( ) ) ;
        }
        
        return itemList ;
    }
    
    private WikiData parseStoryItem ( Element theElement ) {
        WikiData currentData = new WikiData ( ) ;
        NodeList allChildern ;
        
        try {
            String title = "" ;
            String description = "" ;
            String url = "" ;
            
            // Get text if any
            if ( ( theElement.getElementsByTagName ( "Text" ) ).getLength ( ) > 0 ) {
                
                allChildern = theElement.getElementsByTagName ( "Text" ).item ( 0 ).getChildNodes ( ) ; 
                for ( int index = 0 ; index < allChildern.getLength ( ) ; index++ ) {
                    title += allChildern.item ( index ).getNodeValue ( ) ;
                }
                currentData.setTitle ( title ) ;
            }

            // Read the summary of Story
            if ( ( theElement.getElementsByTagName ( "Description" ) ).getLength ( ) > 0 ) {
                
                allChildern = theElement.getElementsByTagName ( "Description" ).item ( 0 ).getChildNodes ( ) ;
                
                for ( int index = 0 ; index < allChildern.getLength ( ) ; index++ ) {
                    description += allChildern.item ( index ).getNodeValue ( ) ;
                }
                currentData.setTime ( description ) ;
            }
            
            // Get pub date if any
            if ( ( theElement.getElementsByTagName ( "Url" ) ).getLength ( ) > 0 ) {
                
                allChildern = theElement.getElementsByTagName ( "Url" ).item ( 0 ).getChildNodes ( ) ;
                for ( int index = 0 ; index < allChildern.getLength ( ) ; index++ ) {
                    url += allChildern.item ( index ).getNodeValue ( ) ;
                }
                currentData.setUrl ( url ) ;
            }
            
        } catch ( Exception e ) {
            Log.e ( "Exception" , "parseStoryItem Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        } catch ( Error e ) {
            Log.e ( "Error" , "parseStoryItem Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
        return currentData ;
    }
    
    public static String stringFromDateString ( String string ) {
        
        String datePart = string.substring ( 0 , 25 ) ;
        
        return datePart ;
    }
}
