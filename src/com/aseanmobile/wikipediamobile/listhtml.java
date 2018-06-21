package com.aseanmobile.wikipediamobile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.aseanmobile.wikipediamobile.support.Constant;
import com.aseanmobile.wikipediamobile.support.Utils;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;


	public class listhtml extends ListActivity 
	{
	private File file;
	private List<String> myList;

	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    try{
	    final AlertDialog.Builder builder = new AlertDialog.Builder ( this ) ;
        
	    myList = new ArrayList<String>();   
	    this.getListView().setLongClickable(true);
	    String root_sd = Environment.getExternalStorageDirectory().toString();
	   // try
	   // {
	    	file = new File( root_sd + "/wikimobilehtml" ) ;   
	    if(!file.isDirectory())
	    {
	   	Toast.makeText(getBaseContext(), "No saved files", 100).show();
	    	finish();
	    }
	    
	    File list[] = file.listFiles();
	    if(list.length==0)
	    {	Toast.makeText(getBaseContext(), "No saved files", 100).show();
    	finish();
	    }
	    	
	    for( int i=0; i< list.length; i++)
	    {
	        myList.add( list[i].getName() );
	    }
	   

	    setListAdapter(new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, myList ));
	    this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
	        public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
	        	 
	        	final String root_sd = Environment.getExternalStorageDirectory().getPath();
	        	   
	             DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener ( ) {
	                 @ Override
	                 public void onClick ( DialogInterface dialog , int which ) {
	                     switch ( which ) {
	                         case DialogInterface.BUTTON_POSITIVE:
	                             Utils.getGTRACKER ( getApplicationContext ( ) ).trackGeneralEvent ( Constant.GoogleAnalytics.CATEGORY_GANERAL_FINISHED );
	                         	File temp_file = new File(root_sd + "/wikimobilehtml/"+myList.get( position ) );
	                 	       boolean succ= temp_file.delete();
	                 	      if(succ)
	              	        {
	              	        	
	              	        	Toast.makeText(getBaseContext(), "File deleted succesfully!!!", 100).show();
	              	        Intent alertIntent=new Intent(listhtml.this,listhtml.class);
	                      	finish();
	                      	//alertIntent.putExtra("geo", adddr);
	                      	startActivity(alertIntent);
	              	        }
	              	        else
	              	        	Toast.makeText(getBaseContext(), root_sd + "/wikimobilehtml/"+myList.get( position ), 100).show();
	              	     
	                             //finish ( ) ;
	                             break ;
	                         
	                         case DialogInterface.BUTTON_NEGATIVE:

	                             break ;
	                     }
	                 }
	             } ;
	             
	             builder.setMessage ( "Are you sure you want to delete this file?" ).setPositiveButton ( "Yes" , dialogClickListener ).setNegativeButton ( "No" ,
	                     dialogClickListener ).show ( ) ;
	        	
	          //Do some
	            return true;
	            
	        }
	    });
	}
	   catch(Exception e) 
	   {
		   Toast.makeText(getBaseContext(), "No saved files", 100).show();
	    	finish();  
	   }
	}

	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
	    super.onListItemClick(l, v, position, id);
	    File lFile = new File(Environment.getExternalStorageDirectory() + "/wikimobilehtml/"+myList.get( position ));
        //    mWebView.loadUrl("file:///" + lFile.getAbsolutePath());
           
           Intent intent = new Intent ( getApplicationContext ( ) , CustomeWebViewActivity.class ) ;
           intent.putExtra ( "uri" , "file:///" + lFile.getAbsolutePath() ) ;
              finish();
           startActivity ( intent ) ;
	   /* File temp_file = new File( file, myList.get( position ) );  

	    if( !temp_file.isFile())        
	    {
	        file = new File( file, myList.get( position ));
	        File list[] = file.listFiles();

	        myList.clear();

	        for( int i=0; i< list.length; i++)
	        {
	            myList.add( list[i].getName() );
	        }
	        Toast.makeText(getApplicationContext(), file.toString(), Toast.LENGTH_LONG).show(); 
	        setListAdapter(new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_1, myList ));

	  */  }
	
	}