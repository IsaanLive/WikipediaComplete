package com.aseanmobile.wikipediamobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import com.aseanmobile.wikipediamobile.model.WikiData;
import com.aseanmobile.wikipediamobile.support.Constant;
import com.aseanmobile.wikipediamobile.support.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SavePage extends Activity{
	ListView pagelist;
	ImageButton addbutton;
	TextView tv1;
	File sdcard;
	File appFolder;
	String htmlcode;
	String[] list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_pages);
		pagelist=(ListView) findViewById(R.id.listView1);
		addbutton=(ImageButton) findViewById(R.id.imageButton1);
		tv1=(TextView) findViewById(R.id.textView1);
		sdcard=Environment.getExternalStorageDirectory();
		appFolder=new File(sdcard.getAbsolutePath()+"/"+getPackageName()+"/savedPages");
		
		Log.i("zacharia",appFolder.getAbsolutePath());
		if(appFolder.exists()){
			int size=appFolder.listFiles().length;
			if(size==0){
				tv1.setText("No saved pages");
			}
			else{
				createlist();
			}
		}
		else{
			boolean form=appFolder.mkdirs();
			Log.i("zacharia", "inside app folder check"+form);
			tv1.setText("No saved pages");
		}
		final String title=this.getIntent().getStringExtra("title");
		Log.i("zacharia", "title:"+title);
		if(title.equals("null")){
			addbutton.setVisibility(View.GONE);
		}
		else{
			htmlcode=this.getIntent().getStringExtra("htmlcode");
			
		}
		addbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!appFolder.exists()){
					Log.i("zacharia", "inside app folder check");
					
				}
			final EditText input = new EditText ( SavePage.this ) ;
			input.setText(title);
			 new AlertDialog.Builder ( SavePage.this ).setTitle ( "Save Page" ).setView ( input )
			 			.setPositiveButton ( "Ok" ,
	                       new DialogInterface.OnClickListener ( ) {
	                          public void onClick ( DialogInterface dialog , int whichButton ) {
	                                
	                            String bookMarkName = input.getText ( ).toString ( ) ;
	                             if ( bookMarkName != null && bookMarkName.length ( ) > 0 ) {
	                                  bookMarkName = bookMarkName.trim ( ) ;
	                               } else {
	                                    bookMarkName = "" + title ;
	                                }
	                                
	                              File savedfile=new File(appFolder,bookMarkName+".html");
	                              try {
	                              	PrintStream out = null;
	                                	try {
//	                                		String toadd=Constant.SELECTED_LANGUAGE.getUrl();
//	                                		htmlcode=htmlcode.replaceAll("href=\"/", "href=\""+toadd+"");
//	                                		Log.i("zacharia", htmlcode);
	                               	    out = new PrintStream(new FileOutputStream(savedfile.getAbsolutePath()));
	                               	    out.print(htmlcode);
	                               	    Toast.makeText(getApplicationContext(), "Page Saved", Toast.LENGTH_SHORT).show();
	                                	}catch(Exception e){
	                               	    Toast.makeText(getApplicationContext(), "Not Saved", Toast.LENGTH_SHORT).show();

	                                	}
	                                	finally {
	                                	    if (out != null) out.close();
	                                	    
	                                	    SavePage.this.finish();
	                                	}
	                                	
										
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	                                
	                                
	                            }
	                        } ).setNegativeButton ( "Cancel" , new DialogInterface.OnClickListener ( ) {
	                    public void onClick ( DialogInterface dialog , int whichButton ) {
	                        // Do nothing.
	                    }
	                } ).show ( ) ;
				
				
			}
		});
		pagelist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				try {
		            new AlertDialog.Builder ( SavePage.this ).setTitle ( "Please select" ).setItems ( R.array.book_mark_dailog_options ,
		                    new DialogInterface.OnClickListener ( ) {
		                        public void onClick ( DialogInterface dialog , int which ) {
		                            
		                            if ( which == 0 ) {
		                            	CustomeWebViewActivity.filename=appFolder.getAbsolutePath()+"/"+list[arg2];
		                				CustomeWebViewActivity.state=1;
		                				SavePage.this.finish();
		                            } else if ( which == 1 ) {
		                            	
		                            	final EditText input = new EditText ( SavePage.this ) ;
		                    			input.setText(list[arg2]);
		                    			 new AlertDialog.Builder ( SavePage.this ).setTitle ( "Save Page" ).setView ( input )
		                    			 		.setPositiveButton ( "Ok" ,
		                    	                   new DialogInterface.OnClickListener ( ) {
		                    	                     public void onClick ( DialogInterface dialog , int whichButton ) {
		                    	                                
		                    	                       String bookMarkName = input.getText ( ).toString ( ) ;
		                    	                         if ( bookMarkName != null && bookMarkName.length ( ) > 0 ) {
		                    	                               bookMarkName = bookMarkName.trim ( ) ;
		                    	                             } else {
		                    	                                bookMarkName = "" + list[arg2] ;
		                    	                             }
		                    	                               if(bookMarkName.endsWith(".html")){
		                    	                            	   Log.i("zacharia", "with . html");
		                    	                            	   bookMarkName=bookMarkName.replace(".html", "");
		                    	                               }
		                    	                      File renamefile=new File(appFolder.getAbsoluteFile()+"/"+bookMarkName+".html");
		                    	                      File originalfile=new File(appFolder.getAbsoluteFile()+"/"+list[arg2]);
		                  
		                    	                      Log.i("zacharia","edit re"+renamefile.getAbsolutePath());
		                    	                      Log.i("zacharia","edit ori"+originalfile.getAbsolutePath());
		                    	                      boolean check=originalfile.renameTo(renamefile);
		                    	                      Log.i("zacharia","inside edit"+check);
		                    	                      createlist();
		                    	                                
		                    	                                
		                    	                            }
		                    	                        } ).setNegativeButton ( "Cancel" , new DialogInterface.OnClickListener ( ) {
		                    	                    public void onClick ( DialogInterface dialog , int whichButton ) {
		                    	                        // Do nothing.
		                    	                    }
		                    	                } ).show ( ) ;
		                    				
		                                
		                            } else if ( which == 2 ) {
		                            	File delete=new File(appFolder.getAbsolutePath()+"/"+list[arg2]);
		                            	delete.delete();
		                            	
		                            	createlist();
		                                
		                            }
		                        	
//		            
		                        }
		                    } ).show ( ) ;
		        } catch ( Exception e ) {
		            Log.e ( "Exception" , "showBookMarkOption Message = " + e.toString ( ) ) ;
		            e.printStackTrace ( ) ;
		        }
				return false;
			}
		});
	}
	public void createlist(){
		list=appFolder.list();
		pagelist.setAdapter(new ArrayAdapter<String>(SavePage.this, android.R.layout.simple_list_item_1, list));
		pagelist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				CustomeWebViewActivity.filename=appFolder.getAbsolutePath()+"/"+list[arg2];
				CustomeWebViewActivity.state=1;
				SavePage.this.finish();
				
			}
		});
		
	}
	

}
