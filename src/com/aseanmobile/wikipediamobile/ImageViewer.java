package com.aseanmobile.wikipediamobile;

import android.app.Activity ;

import com.aseanmobile.wikipediamobile.model.MyMenuItem ;

public class ImageViewer extends Activity {
	
	static MyMenuItem transferItem;
	static int showSplashResId;
//	
//	final int STOPSPLASH = 0;
//	
//	@Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        
//        if (showSplashResId != 0)
//        	requestWindowFeature(Window.FEATURE_NO_TITLE);
//        	
//        setContentView(R.layout.image_view);
//        
//
//        if (transferItem != null)
//        {
//	        loadImage(transferItem);
//	        //transferItem = null;
//        }
//        
//        if (showSplashResId != 0)
//        {        	
//        	loadImageFromRes(showSplashResId);
//        	setTitle(getString(R.string.app_name));
//        	showSplashResId = 0;
//        	
//            Message msg = new Message(); 
//            msg.what = STOPSPLASH; 
//            splashHandler.sendMessageDelayed(msg, 3000); 
//        }
//	}	
//	
//	private void loadImage(MyMenuItem item)
//	{
//		InputStream input = null;//Main.activity.getFileFromResource(item.imageResName, "drawable");
//		Bitmap bm = BitmapFactory.decodeStream(input);
//
//		ImageView imageView = (ImageView)findViewById(R.id.image01);
//		imageView.setImageBitmap(bm);
//		
//		setTitle(item.text);
//	}
//	
//	private void loadImageFromRes(int resId)
//	{
//		ImageView imageView = (ImageView)findViewById(R.id.image01);
//		imageView.setImageResource(resId);		
//	}
//	
//    private Handler splashHandler = new Handler() { 
//        @Override 
//        public void handleMessage(Message msg) { 
//             switch (msg.what) { 
//             case STOPSPLASH: 
//                 finish(); 
//            	 break; 
//             } 
//             super.handleMessage(msg); 
//        } 
//   }; 	

}
