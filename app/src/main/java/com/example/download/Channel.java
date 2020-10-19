package com.example.download;

import java.io.Serializable;

public class Channel implements Serializable {
	/**
	 * This class is used to store the information of each task in the to do
	 * list. The information are consist of default id number, the name of
	 * title, the time of entered by the user and the task content with all
	 * there corresponding getters and setters. The interface of Serializable is
	 * implements to transmit between activities.
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String time;
	private String content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
