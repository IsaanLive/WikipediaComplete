package com.aseanmobile.wikipediamobile.model;

import java.util.ArrayList ;

public class MyMenuItem {
    
    String text = new String();
    String comment = new String();
    String icon = new String();
    
    String content = new String();
    String imageResName = new String();
    String map = new String();
    String mapfloat = new String();
    String mapzoom = new String();
    String url = new String();
    
    ArrayList<MyMenuItem> childs = new ArrayList<MyMenuItem>();
    MyMenuItem parent = null;
    int level = 0;
    
    private boolean selected = false;
    
    @Override
    public String toString() {
        if (childs.size() == 0)
            return text;
        else
            return text;
    }

    public boolean isSelected() {
		return selected;
	}


	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	public String getText ( ) {
        return text ;
    }

    public void setText ( String text ) {
        this.text = text ;
    }

    public String getComment ( ) {
        return comment ;
    }

    public void setComment ( String comment ) {
        this.comment = comment ;
    }

    public String getIcon ( ) {
        return icon ;
    }

    public void setIcon ( String icon ) {
        this.icon = icon ;
    }

    public String getContent ( ) {
        return content ;
    }

    public void setContent ( String content ) {
        this.content = content ;
    }

    public String getImageResName ( ) {
        return imageResName ;
    }

    public void setImageResName ( String imageResName ) {
        this.imageResName = imageResName ;
    }

    public String getMap ( ) {
        return map ;
    }

    public void setMap ( String map ) {
        this.map = map ;
    }

    public String getMapfloat ( ) {
        return mapfloat ;
    }

    public void setMapfloat ( String mapfloat ) {
        this.mapfloat = mapfloat ;
    }

    public String getMapzoom ( ) {
        return mapzoom ;
    }

    public void setMapzoom ( String mapzoom ) {
        this.mapzoom = mapzoom ;
    }

    public String getUrl ( ) {
        return url ;
    }

    public void setUrl ( String url ) {
        this.url = url ;
    }

    public ArrayList < MyMenuItem > getChilds ( ) {
        return childs ;
    }

    public void setChilds ( ArrayList < MyMenuItem > childs ) {
        this.childs = childs ;
    }

    public MyMenuItem getParent ( ) {
        return parent ;
    }

    public void setParent ( MyMenuItem parent ) {
        this.parent = parent ;
    }

    public int getLevel ( ) {
        return level ;
    }

    public void setLevel ( int level ) {
        this.level = level ;
    }
    
    
}
