package com.aseanmobile.wikipediamobile ;

import java.util.HashMap ;

import android.content.Context ;
import android.content.pm.PackageManager.NameNotFoundException ;
import android.util.Log ;

import com.aseanmobile.wikipediamobile.support.Constant ;
import com.google.android.apps.analytics.GoogleAnalyticsTracker ;

public class GTracker {
    
    private static GoogleAnalyticsTracker tracker ;
    private static Context                context ;
    
    private String                        appVersion                      = "2.6" ;
    
    public HashMap < String , String >    COMMON_DATA_FOLDER_NAME_HASHMAP = new HashMap < String , String > ( ) ;
    
    public GTracker ( Context cont ) {
        try {
            context = cont ;
            tracker = GoogleAnalyticsTracker.getInstance ( ) ;
            tracker.startNewSession ( Constant.GoogleAnalytics.VAR_USER_ID , 20 , context ) ;
            
            appVersion = context.getPackageManager ( ).getPackageInfo ( context.getPackageName ( ) , 0 ).versionName ;
        } catch ( Exception e ) {
            Log.e ( "Exception" , "GTracker Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
    }
    
    public void trackAppStartedEvent ( ) {
        try {
            if ( trackerStarted ( ) ) {
                tracker.trackEvent ( Constant.GoogleAnalytics.CATEGORY_GANERAL , Constant.GoogleAnalytics.CATEGORY_GANERAL_STARTED , "" + appVersion ,
                        1 ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "trackAppStartedEvent Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    private boolean trackerStarted ( ) {
        try {
            if ( tracker == null ) {
                if ( context != null ) {
                    tracker = GoogleAnalyticsTracker.getInstance ( ) ;
                    tracker.startNewSession ( Constant.GoogleAnalytics.VAR_USER_ID , 20 , context ) ;
                    return true ;
                }
                Log.e ( "Wait" , "Google Analytics tracker is not initialized" ) ;
                return false ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "trackerStarted Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
        return true ;
    }
    
    public void tarckExitEvent ( ) {
        try {
            if ( trackerStarted ( ) ) {
                tracker.trackEvent ( Constant.GoogleAnalytics.CATEGORY_GANERAL , Constant.GoogleAnalytics.CATEGORY_GANERAL_FINISHED ,
                        "" + appVersion , 1 ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "tarckExitEvent Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    public void trackPageViewEvent ( String pageView ) {
        try {
            if ( trackerStarted ( ) ) {
                tracker.trackPageView ( pageView ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "trackPageViewEvent Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    public void tackAppStartEvent ( ) {
        try {
            if ( trackerStarted ( ) ) {
                trackerStarted ( ) ;
                tracker.trackEvent ( Constant.GoogleAnalytics.CATEGORY_GANERAL , Constant.GoogleAnalytics.CATEGORY_GANERAL_APPLICATION , ""
                        + appVersion , 1 ) ;
                
                String appVersion = context.getPackageManager ( ).getPackageInfo ( context.getPackageName ( ) , 0 ).versionName ;
                String versionOfOS = android.os.Build.VERSION.RELEASE ;
                
                String phoneName = android.os.Build.MODEL ;
                
                tracker.setCustomVar ( 1 , Constant.GoogleAnalytics.VAR_OS , "" + versionOfOS ) ;
                tracker.setCustomVar ( 2 , Constant.GoogleAnalytics.VAR_APPLICATION , "" + appVersion ) ;
                tracker.setCustomVar ( 3 , Constant.GoogleAnalytics.VAR_HARDWARE , "" + phoneName ) ;
                
            }
        } catch ( NameNotFoundException e ) {
            Log.v ( "VersionName" , e.getMessage ( ) ) ;
            e.printStackTrace ( ) ;
        }
    }
    
    public void trackSearchEvents ( String event ) {
        try {
            if ( trackerStarted ( ) ) {
                tracker.trackEvent ( Constant.GoogleAnalytics.CATEGORY_SEARCH , event , "" + appVersion , 1 ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "trackSearchEvents Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    public void trackBookMarkEvent ( String event ) {
        try {
            if ( trackerStarted ( ) ) {
                tracker.trackEvent ( Constant.GoogleAnalytics.CATEGORY_BOOKMARK , event , "" + appVersion , 1 ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "trackBookMarkEvent Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    public void trackSettingsEvents ( String event ) {
        try {
            if ( trackerStarted ( ) ) {
                tracker.trackEvent ( Constant.GoogleAnalytics.CATEGORY_SETTING , event , "" + appVersion , 1 ) ;
                
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "trackSettingsEvents Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    public void trackGeneralEvent ( String event ) {
        try {
            if ( trackerStarted ( ) ) {
                tracker.trackEvent ( Constant.GoogleAnalytics.CATEGORY_GANERAL , event , "" + appVersion , 1 ) ;
            }
        } catch ( Exception e ) {
            Log.e ( "Exception" , "trackGeneralEvent Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
        
    }
    
    public void endTrackingSession ( ) {
        try {
            tracker.stopSession ( ) ;
        } catch ( Exception e ) {
            Log.e ( "Exception" , "endTrackingSession Message = " + e.toString ( ) ) ;
            e.printStackTrace ( ) ;
        }
    }
}
