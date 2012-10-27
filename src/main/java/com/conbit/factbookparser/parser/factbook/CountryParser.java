package com.conbit.factbookparser.parser.factbook;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.conbit.factbookparser.MyLogger;
import com.conbit.factbookparser.concept.Country;

public class CountryParser {
	
	private Document countryPage;
	private Logger logger = MyLogger.getInstance();
	private Country country;

	public CountryParser(Document countryPage){
		this.countryPage = countryPage;
		country = new Country(parseCountryName());
		ArrayList<FilteredProperty> properties = parse();
		PropertyConvertor convertor = new PropertyConvertor(properties);
		convertor.convertTo(country);
		country.writeToFile();
	}
	
	private ArrayList<FilteredProperty> parse(){
		Elements mainTables = countryPage.select("table[width=638][cellpadding=0][cellspacing=0]");
		Element informationTable = mainTables.get(2);
		Elements contentPanels = informationTable.select("table[class=CollapsiblePanelContent]");
		Elements informationRows = new Elements();
		for(Element e : contentPanels){
			informationRows.addAll(e.select("tr"));
		}
		int i = 0;
		ArrayList<BigPropertyBlock> bigBlocks = new ArrayList<BigPropertyBlock>();
		while(i < informationRows.size()){
			if(informationRows.get(i).attr("class").contains("_light")){
				bigBlocks.add(new BigPropertyBlock(informationRows.get(i),informationRows.get(i+1)));
				i = i+2;
			} else{
				i++;
			}
		}
		return parseBigPropertyBlocks(bigBlocks);
	}
	
	private ArrayList<FilteredProperty> parseBigPropertyBlocks(ArrayList<BigPropertyBlock> bigBlocks) {
		ArrayList<FilteredProperty> filteredProperties = new ArrayList<FilteredProperty>();
		for(BigPropertyBlock bigBlock: bigBlocks ){
			Elements subProperties = bigBlock.getContent().select("td").select("div[class=category]");
			if(subProperties.isEmpty()){
				filteredProperties.add(new FilteredProperty(bigBlock.getTitle().text(), bigBlock.getContent().text()));
				logger.debug("Found property: " + bigBlock.getTitle().text() + " = " + bigBlock.getContent().text());
			}
			else{
				for(Element subProperty : subProperties){
					try{
						filteredProperties.add(new FilteredProperty(subProperty.text().split(":")[0]+bigBlock.getTitle().text(), subProperty.text().split(":")[1]));
						logger.debug("Found property: " + subProperty.text().split(":")[0] + " " +bigBlock.getTitle().text() + " = " + subProperty.text().split(":")[1]);
					} catch (Exception e){
						logger.error("No value found for: " + subProperty.text().split(":")[0]);
					}
				}
			}
		}
		return filteredProperties;
	}

	public void writeToFile(){
		country.writeToFile();
	}
	
	private String parseCountryName() {
		Elements countryElement = countryPage.select("span[class=region_name1]");
		String result = countryElement.text();
		logger.debug("Name: "+result);
		return result;
	}
	
	private String toCamelCase(String s){
		   String[] parts = s.split(" ");
		   String camelCaseString = "";
		   for (String part : parts){
		      camelCaseString = camelCaseString + toProperCase(part);
		   }
		   return camelCaseString.substring(0, 1).toLowerCase();
		}

	private String toProperCase(String s) {
		    return s.substring(0, 1).toUpperCase() +
	               s.substring(1).toLowerCase();
	}

}
