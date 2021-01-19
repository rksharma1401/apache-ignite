package com.learn.ignitedemo.objects;

public class StringObjects {

	private String name;
	private String type;
	private String location;

	public StringObjects() {
		super();
	}

	public StringObjects(String name, String type, String location) {
		super();
		this.name = name;
		this.type = type;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "StringObjects [name=" + name + ", type=" + type + ", location=" + location + "]";
	}
	
	

}
