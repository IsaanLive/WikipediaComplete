package com.aseanmobile.wikipediamobile ;

import android.app.Activity ;
import android.app.AlertDialog ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.os.Bundle ;
import android.util.Log ;
import android.view.Menu ;
import android.view.MenuInflater ;
import android.view.MenuItem ;
import android.widget.EditText ;

import com.aseanmobile.wikipediamobile.model.MyMenuItem ;
import com.aseanmobile.wikipediamobile.support.Constant ;

public class Main extends Activity {
    
    @ Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState ) ;
        this.setRequestedOrientation ( 1 ) ;
        
        setContentView ( R.layout.ui_for_main ) ;
        
        MyMenuItem clicked = Constant.SELECTED_LANGUAGE ;
        if ( clicked.getChilds ( ).size ( ) == 0 ) {
            if ( ! clicked.getImageResName ( ).equals ( "" ) ) {
                ImageViewer.transferItem = clicked ;
                Intent i = new Intent ( Main.this , ImageViewer.class ) ;
                startActivity ( i ) ;
            } else if ( ! clicked.getMap ( ).equals ( "" ) ) {
                MapViewer.transferItem = clicked ;
                Intent i = new Intent ( Main.this , MapViewer.class ) ;
                startActivity ( i ) ;
            } else if ( ! clicked.getUrl ( ).equals ( "" ) ) {
                Intent intent = new Intent ( getApplicationContext ( ) , CustomeWebViewActivity.class ) ;
                intent.putExtra ( "uri" , clicked.getUrl ( ) ) ;
                startActivity ( intent ) ;
                // WebViewer.transferItem = clicked;
                // Intent i = new Intent(Main.this, WebViewer.class);
                // startActivity(i);
            } else {
                Intent i = new Intent ( Main.this , AboutUsActivity.class ) ;
                startActivity ( i ) ;
            }
        }
        if ( clicked.getChilds ( ).size ( ) > 0 ) {
            Log.e ( "NOT" , "Not now" ) ;
            // transferRoot = clicked;
            // Intent i = new Intent(Main.this, Main.class);
            // startActivity(i);
        }
    }
    
    @ Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        MenuInflater inflater = getMenuInflater ( ) ;
        inflater.inflate ( R.menu.main_activity_menu , menu ) ;
        return true ;
    }
    
    @ Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        
        switch ( item.getItemId ( ) ) {
            case R.id.menu_more:
                Intent intent = new Intent ( Main.this , MoreActivity.class ) ;
                startActivity ( intent ) ;
                return true ;
                
            case R.id.menu_add_to_book_mark:

                showBookMarkDailog ( ) ;
                
                return true ;
                
            case R.id.menu_book_marks:

                intent = new Intent ( Main.this , AboutUsActivity.class ) ;
                startActivity ( intent ) ;
                return true ;
                
            default:
                return super.onOptionsItemSelected ( item ) ;
        }
    }
    
    private void showBookMarkDailog ( ) {
        // Set an EditText view to get user input
        final EditText input = new EditText ( Main.this ) ;
        input.setHint ( "Edit text 10" ) ;
        
        new AlertDialog.Builder ( Main.this ).setTitle ( "Update Status" ).setMessage ( "Please enter the bookmark name" ).setView ( input )
                .setPositiveButton ( "Ok" , new DialogInterface.OnClickListener ( ) {
                    public void onClick ( DialogInterface dialog , int whichButton ) {
                        // String editable = input.getText().toString();
                        // deal with the editable
                    }
                } ).setNegativeButton ( "Cancel" , new DialogInterface.OnClickListener ( ) {
                    public void onClick ( DialogInterface dialog , int whichButton ) {
                        // Do nothing.
                    }
                } ).show ( ) ;
    }
}

// package com.aseanmobile.wikipediamobile;
//
// import java.io.IOException;
// import java.io.InputStream;
//
// import javax.xml.parsers.DocumentBuilderFactory;
// import javax.xml.parsers.ParserConfigurationException;
//
// import org.w3c.dom.Document;
// import org.w3c.dom.Element;
// import org.w3c.dom.NamedNodeMap;
// import org.w3c.dom.Node;
// import org.w3c.dom.NodeList;
// import org.xml.sax.SAXException;
//
// import android.app.Activity;
// import android.app.AlertDialog;
// import android.content.DialogInterface;
// import android.content.Intent;
// import android.content.SharedPreferences;
// import android.os.Bundle;
// import android.view.Menu;
// import android.view.MenuInflater;
// import android.view.MenuItem;
// import android.widget.EditText;
// import android.widget.ListView;
// import android.widget.RadioGroup;
//
// import com.aseanmobile.wikipediamobile.LanguageChooseAtivity.MyAdapter;
// import com.aseanmobile.wikipediamobile.model.MyMenuItem;
// import com.aseanmobile.wikipediamobile.support.Constant;
//
// public class Main extends Activity {
//
// private static MyMenuItem transferRoot;
// SharedPreferences prefrence = null;
// private MyMenuItem activityLocalRoot;
// int pos = 0;
//
// @Override
// public void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// this.setRequestedOrientation(1);
//
// setContentView(R.layout.ui_for_main);
//
// activityLocalRoot = new MyMenuItem();
// loadXML(activityLocalRoot);
// Constant.ALL_LANGUAGES = activityLocalRoot;
//
// prefrence = getSharedPreferences(Constant.PREF_NAME,
// this.MODE_WORLD_WRITEABLE);
// pos = prefrence.getInt(Constant.LANG_KEY, 0);
//
// Constant.SELECTED_LANGUAGE = activityLocalRoot.getChilds().get(pos);
//
// MyMenuItem clicked = Constant.SELECTED_LANGUAGE;
// if (clicked.getChilds().size() == 0) {
// if (!clicked.getImageResName().equals("")) {
// ImageViewer.transferItem = clicked;
// Intent i = new Intent(Main.this, ImageViewer.class);
// startActivity(i);
// } else if (!clicked.getMap().equals("")) {
// MapViewer.transferItem = clicked;
// Intent i = new Intent(Main.this, MapViewer.class);
// startActivity(i);
// } else if (!clicked.getUrl().equals("")) {
// WebViewer.transferItem = clicked;
// Intent i = new Intent(Main.this, WebViewer.class);
// startActivity(i);
// } else {
// AboutUsActivity.transferItem = clicked;
// Intent i = new Intent(Main.this, AboutUsActivity.class);
// startActivity(i);
// }
// }
// if (clicked.getChilds().size() > 0) {
// Main.transferRoot = clicked;
// Intent i = new Intent(Main.this, Main.class);
// startActivity(i);
// }
// }
//
// @Override
// public boolean onCreateOptionsMenu(Menu menu) {
// MenuInflater inflater = getMenuInflater();
// inflater.inflate(R.menu.main_activity_menu, menu);
// return true;
// }
//
// @Override
// public boolean onOptionsItemSelected(MenuItem item) {
//
// switch (item.getItemId()) {
// case R.id.menu_more:
// Intent intent = new Intent(Main.this, MoreActivity.class);
// startActivity(intent);
// return true;
//
// case R.id.menu_add_to_book_mark:
//
// showBookMarkDailog();
//
// return true;
//
// case R.id.menu_book_marks:
//
// intent = new Intent(Main.this, AboutUsActivity.class);
// startActivity(intent);
// return true;
//
// default:
// return super.onOptionsItemSelected(item);
// }
// }
//
// private void showBookMarkDailog() {
// // Set an EditText view to get user input
// final EditText input = new EditText(Main.this);
// input.setHint("Edit text 10");
//
// new
// AlertDialog.Builder(Main.this).setTitle("Update Status").setMessage("Please enter the bookmark name").setView(input)
// .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
// public void onClick(DialogInterface dialog, int whichButton) {
// // String editable = input.getText().toString();
// // deal with the editable
// }
// }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
// public void onClick(DialogInterface dialog, int whichButton) {
// // Do nothing.
// }
// }).show();
// }
//
// void loadXML(MyMenuItem loadIn) {
// javax.xml.parsers.DocumentBuilderFactory dbf =
// DocumentBuilderFactory.newInstance();
// dbf.setValidating(false);
// try {
// javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();
// Document doc = parser.parse(getFileFromResource("struct", "raw"));
//
// Element mainMenu = doc.getDocumentElement();
// parseSubElements(mainMenu, loadIn);
//
// } catch (ParserConfigurationException e) {
// } catch (IOException e) {
// } catch (SAXException e) {
// }
// ;
//
// }
//
// public InputStream getFileFromResource(String fileName, String folder) {
// return getApplicationContext().getResources().openRawResource(
// getApplicationContext().getResources().getIdentifier(fileName, folder,
// getPackageName()));
// }
//
// private void parseSubElements(Element menu, MyMenuItem parent) {
// NodeList items = menu.getElementsByTagName("*");
// for (int i = 0; i < items.getLength(); i++) {
// if (items.item(i).getParentNode() != menu)
// continue;
//
// MyMenuItem newItem = new MyMenuItem();
// newItem.setParent(parent);
// newItem.setLevel(parent.getLevel() + 1);
//
// NamedNodeMap attributes = items.item(i).getAttributes();
//
// if (attributes != null)
// for (int a = 0; a < attributes.getLength(); a++) {
// Node attribute = attributes.item(a);
// if (attribute.getNodeName().equals("icon"))
// newItem.setIcon(attribute.getNodeValue());
// if (attribute.getNodeName().equals("text"))
// newItem.setText(attribute.getNodeValue());
// if (attribute.getNodeName().equals("comment"))
// newItem.setComment(attribute.getNodeValue());
// if (attribute.getNodeName().equals("content"))
// newItem.setContent(attribute.getNodeValue());
// if (attribute.getNodeName().equals("image_resource_name"))
// newItem.setImageResName(attribute.getNodeValue());
// if (attribute.getNodeName().equals("map"))
// newItem.setMap(attribute.getNodeValue());
// if (attribute.getNodeName().equals("mapfloat"))
// newItem.setMapfloat(attribute.getNodeValue());
// if (attribute.getNodeName().equals("mapzoom"))
// newItem.setMapzoom(attribute.getNodeValue());
// if (attribute.getNodeName().equals("url"))
// newItem.setUrl(attribute.getNodeValue());
// }
//
// if (items.item(i).getNodeName().equals("menu")) {
// String text = newItem.getText();
// text += "";
// newItem.setText(text);
// if ((items.item(i).getNodeType() & Node.ELEMENT_NODE) != 0)
// parseSubElements((Element) items.item(i), newItem);
// }
//
// parent.getChilds().add(newItem);
// }
// }
// }