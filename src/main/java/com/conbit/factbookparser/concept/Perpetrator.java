package com.conbit.factbookparser.concept;

import java.util.List;

import org.apache.log4j.Logger;

import com.conbit.factbookparser.MyLogger;

public class Perpetrator {
	
	private Logger logger = MyLogger.getInstance();
	private String name;
	private List<String> believesIn;
	private String country;
	
	public Perpetrator(String name, String country){
		this.name = name;
		this.country = country;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void addReligion(String religion){
		believesIn.add(religion);
	}
	
	public boolean removeReligion(String religion){
		if(believesIn.contains(religion)){
			logger.error("Can't delete religion because it isn't added. Ignoring...");
			return false;
		}
		believesIn.remove(religion);
		return true;
	}
}
