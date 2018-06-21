package com.aseanmobile.wikipediamobile.support ;

import android.app.ProgressDialog ;
import android.content.Context ;
import android.os.AsyncTask ;

import com.aseanmobile.wikipediamobile.R ;

/* Heavy Work */
public class HeavyWorker extends AsyncTask < HeavyWorkerDelegate , Context , Void > {
    private HeavyWorkerDelegate delegate ;
    private Object              result ;
    private ProgressDialog      progressDialog ;
    private Context             targetCtx ;
    
    public HeavyWorker ( HeavyWorkerDelegate delegate , Context context ) {
        this.delegate = delegate ;
        this.targetCtx = context ;
        progressDialog = new ProgressDialog ( targetCtx ) ;
        progressDialog.setCancelable ( false ) ;
        progressDialog.setMessage ( "Retrieving data..." ) ;
        progressDialog.setTitle ( "Please wait" ) ;
        progressDialog.setIcon ( R.drawable.icon ) ;
        progressDialog.setIndeterminate ( true ) ;
    }
    
    public HeavyWorker ( HeavyWorkerDelegate delegate , Context context , String message) {
        this.delegate = delegate ;
        this.targetCtx = context ;
        progressDialog = new ProgressDialog ( targetCtx ) ;
        progressDialog.setCancelable ( false ) ;
        progressDialog.setMessage ( message ) ;
        progressDialog.setTitle ( "Please wait" ) ;
        progressDialog.setIcon ( R.drawable.icon ) ;
        progressDialog.setIndeterminate ( true ) ;
    }
    
    @ Override
    protected void onPreExecute ( ) {
        progressDialog.show ( ) ;
    }
    
    @ Override
    protected Void doInBackground ( HeavyWorkerDelegate ... params ) {
        result = delegate.performInBackground ( ) ;
        return null ;
    }
    
    @ Override
    protected void onPostExecute ( Void result ) {
    	try {
    		delegate.callback ( this.result ) ;
    		if(progressDialog != null && progressDialog.isShowing()){
    			progressDialog.dismiss ( ) ;
    		}
            delegate.finalResult ( ) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }
    
}
