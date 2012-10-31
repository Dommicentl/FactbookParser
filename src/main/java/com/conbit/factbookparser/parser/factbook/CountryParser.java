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
		properties.add(new FilteredProperty("continent", getContinent()));
		PropertyConvertor convertor = new PropertyConvertor(properties, country);
		convertor.convertToCountry();
		country.writeToFile();
	}

	private String getContinent() {
		Elements continentElement = countryPage.select("div[class=region1]");
		continentElement = continentElement.select("a");
		String result = continentElement.text();
		logger.debug("Continent: "+result);
		return result;
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
			subProperties = removeNoteSubs(subProperties);
			if(subProperties.isEmpty()){
				filteredProperties.add(new FilteredProperty(toCamelCase(bigBlock.getTitle().text()), bigBlock.getContent().text()));
				logger.debug("Found property: " + toCamelCase(bigBlock.getTitle().text()) + " = " + bigBlock.getContent().text());
			}
			else{
				for(Element subProperty : subProperties){
					try{
						filteredProperties.add(new FilteredProperty(toCamelCase(subProperty.text().split(":")[0] + " " +bigBlock.getTitle().text()), subProperty.text().split(":")[1]));
						logger.debug("Found property: " + toCamelCase(subProperty.text().split(":")[0] + " " +bigBlock.getTitle().text()) + " = " + subProperty.text().split(":")[1]);
					} catch (Exception e){
						logger.error("No value found for: " + subProperty.text().split(":")[0]);
					}
				}
			}
		}
		return filteredProperties;
	}

	private Elements removeNoteSubs(Elements subProperties) {
		Elements removedNoteBlockList = new Elements();
		for(Element e : subProperties){
			if (e.text().contains("note")){}
			else{
				removedNoteBlockList.add(e);
			}
		}
		return removedNoteBlockList;
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
		if (camelCaseString.contains(":"))
			camelCaseString = camelCaseString.replace(":",	"");
		if (camelCaseString.contains("-"))
			camelCaseString = camelCaseString.replace("-",	"");
		return camelCaseString.substring(0, 1).toLowerCase() + camelCaseString.substring(1);
	}

	private String toProperCase(String s) {
		return s.substring(0, 1).toUpperCase() +
				s.substring(1).toLowerCase();
	}

}
