package com.conbit.factbookparser.concept;

import org.apache.log4j.Logger;

public class Person {
	private String type;
	private boolean alive;
	private static Logger logger = Logger.getLogger(Person.class.getName());
	
	public Person(String gender, String age, String boatNr){
		Double ageInt = new Double(age);
		if(ageInt < 18)
			type = "Child";
		else
			type = gender;
		if(boatNr.equals("Lost"))
			alive = false;
		else
			alive = true;
		toArffString();
	}
	
	public String toArffString(){
		logger.debug(type+","+alive);
		return type+","+alive;
	}
	
}
