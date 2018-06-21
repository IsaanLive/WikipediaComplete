package com.aseanmobile.wikipediamobile.model;

import java.text.DateFormat ;
import java.text.SimpleDateFormat ;
import java.util.Date ;

public class WikiData {

	private int id = -1;
	private String title,url,time;
	
	public WikiData() {
		id = -1;
	}
	
	public WikiData(String title, String url) {
		super();
		this.title = title;
		this.url = url;
		id = -1;
		refreshTime();
	}

	public WikiData(String title, String url, String time) {
		super();
		this.title = title;
		this.url = url;
		this.time = time;
	}

	public WikiData(int id, String title, String url, String time) {
		super();
		this.id = id;
		this.title = title;
		this.url = url;
		this.time = time;
	}

	public void refreshTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        this.time = dateFormat.format(date);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Override
	public String toString() {
		return "WikiData [url=" + url + ", title=" + title + ", time=" + time + ", id=" + id + "]";
	}
}
