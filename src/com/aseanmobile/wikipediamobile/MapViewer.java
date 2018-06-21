package com.aseanmobile.wikipediamobile ;

import android.os.Bundle ;
import android.view.Menu ;
import android.view.MenuItem ;
import android.widget.LinearLayout ;

import com.aseanmobile.wikipediamobile.model.MyMenuItem ;
import com.aseanmobile.wikipediamobile.support.Constant ;
import com.aseanmobile.wikipediamobile.support.Utils ;
import com.google.android.maps.GeoPoint ;
import com.google.android.maps.MapActivity ;
import com.google.android.maps.MapController ;
import com.google.android.maps.MapView ;

public class MapViewer extends MapActivity {
    
    final static int  MENU_MAP = 0 ;
    final static int  MENU_SAT = 1 ;
    
    public static MyMenuItem transferItem ;
    
    LinearLayout      linearLayout ;
    MapView           mapView ;
    
    @ Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState ) ;
        setContentView ( R.layout.map_view ) ;
        Utils.getGTRACKER ( getApplicationContext ( ) ).trackPageViewEvent ( Constant.GoogleAnalytics.PAGE_VIEW_MAP);
        linearLayout = ( LinearLayout ) findViewById ( R.id.zoomview ) ;
        mapView = ( MapView ) findViewById ( R.id.mapview ) ;
        mapView.setBuiltInZoomControls ( true ) ;
        
        if ( transferItem != null ) {
            loadMap ( transferItem ) ;
        }
    }
    
    private void loadMap ( MyMenuItem item ) {
        String [ ] coords = item.getMap ( ).split ( "," ) ;
        if ( coords.length == 2 ) {
            coords [ 0 ] = coords [ 0 ].trim ( ) ;
            coords [ 1 ] = coords [ 1 ].trim ( ) ;
            
            int lat ;
            int lon ;
            if ( item.getMapfloat ( ).equals ( "1" ) ) {
                lat = ( int ) ( new Float ( coords [ 0 ] ).floatValue ( ) * 1E6 ) ;
                lon = ( int ) ( new Float ( coords [ 1 ] ).floatValue ( ) * 1E6 ) ;
            } else {
                lat = new Integer ( coords [ 0 ] ).intValue ( ) ;
                lon = new Integer ( coords [ 1 ] ).intValue ( ) ;
            }
            
            GeoPoint pos = new GeoPoint ( lat , lon ) ;
            
            MapView mapView = ( MapView ) findViewById ( R.id.mapview ) ;
            
            // set center and zoom
            MapController mapController = mapView.getController ( ) ;
            mapController.setCenter ( pos ) ;
            int mapzoom = 12 ;
            if ( ! item.getMapzoom ( ).equals ( "" ) )
                mapzoom = new Integer ( item.getMapzoom ( ) ).intValue ( ) ;
            
            mapController.setZoom ( mapzoom ) ;
        }
        
        setTitle ( item.getText ( ) ) ;
    }
    
    @ Override
    protected boolean isRouteDisplayed ( ) {
        return false ;
    }
    
    public boolean onCreateOptionsMenu ( Menu menu ) {
        super.onCreateOptionsMenu ( menu ) ;
        
        menu.add ( 0 , MENU_MAP , 0 , "Mapa de calles" ) ;
        menu.add ( 0 , MENU_SAT , 1 , "Mapa satelital" ) ;
        
        return true ;
    }
    
    @ Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId ( ) ) {
            case MENU_MAP:
                mapView.setSatellite ( false ) ;
                return true ;
            case MENU_SAT:
                mapView.setSatellite ( true ) ;
                return true ;
        }
        return false ;
    }
    
}
