package com.conbit.factbookparser.concept;

public class Property {
	
	private String individual;
	private Enum property;
	private String value;
	
	public Property(String individualName, Enum property, String value){
		this.individual = individualName;
		this.property = property;
		this.value = value;
	}
	
	public String getPropery(){
		return property.toString();
	}
	
	public String getValue(){
		return value;
	}

	public String getIndividual() {
		return individual;
	}
	
	

}
