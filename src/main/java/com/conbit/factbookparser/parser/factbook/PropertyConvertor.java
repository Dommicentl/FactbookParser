package com.conbit.factbookparser.parser.factbook;

import java.util.ArrayList;

import com.conbit.factbookparser.concept.Country;

/**
 * This class should convert each found html property to a relation or a property inside the country
 * @author jorn
 *
 */
public class PropertyConvertor {
	
	private ArrayList<FilteredProperty> htmlProperties;

	public PropertyConvertor(ArrayList<FilteredProperty> htmlProperties){
		this.htmlProperties = htmlProperties;
	}
	
	public void convertTo(Country country){
		for(FilteredProperty p : htmlProperties){
			//TODO: country.setProperty(...)
			//TODO: country.setRelation(...)
		}
	}
	
}
