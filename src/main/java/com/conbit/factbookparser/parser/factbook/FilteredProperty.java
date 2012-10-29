package com.conbit.factbookparser.parser.factbook;

/**
 * Instances of this class contain the property and value found in the factbook
 * @author jorn
 *
 */
public class FilteredProperty {

	private String property;
	private String value;

	public FilteredProperty(String property, String value){
		this.property = property;
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public String getProperty() {
		return property;
	}
	
	public void renameProperty(String newName){
		property = newName;
	}
	
}
