package com.aseanmobile.wikipediamobile.support ;

import java.util.Hashtable ;

import android.content.Context ;

import com.aseanmobile.wikipediamobile.GTracker ;

public class Utils {
    
    public static Hashtable < String , Integer >  hashListFavorites ;
    
    public static Hashtable < Integer , Boolean > hashListImages     = null ;
    
    public static Hashtable < String , Integer >  hashListStoriesIds = null ;
    
    public static DataBase                        database ;
    
    private static GTracker                       GTRACKER ;
    
    public static GTracker getGTRACKER ( Context context ) {
        if ( GTRACKER == null ) {
            GTRACKER = new GTracker ( context ) ;
        }
        return GTRACKER ;
    }
    
    public static void setGTRACKER ( GTracker gTRACKER ) {
        GTRACKER = gTRACKER ;
    }
    
    /**
     * Convert the date to display in application
     * 
     * @param pubDate
     *            date to be convert
     * @return String date
     */
    public static String convertStringToListDateFormat ( String date ) {
        
        String res = null ;
        if ( date != null && date.length ( ) > 21 ) {
            String partsOfDate[] = date.split ( " " ) ;
            res = "" + partsOfDate [ 0 ] + " " + partsOfDate [ 1 ] + " " + partsOfDate [ 2 ] + " " + partsOfDate [ 3 ] ;
        }
        return res ;
    }
}
