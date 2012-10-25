package com.conbit.factbookparser.parser.factbook.country;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.conbit.factbookparser.MyLogger;
import com.conbit.factbookparser.concept.Country;

public class CountryParser {
	
	private Document countryPage;
	private Logger logger = MyLogger.getInstance();
	private Country country = new Country();

	public CountryParser(Document countryPage){
		this.countryPage = countryPage;
		parse();
	}
	
	private void parse(){
		parseContinent();
		parseCountryName();
	}
	
	public void writeToFile(){
		country.writeToFile();
	}
	
	private void parseContinent() {
		Elements continentElement = countryPage.select("div[class=region1]");
		continentElement = continentElement.select("a");
		String result = continentElement.text();
		logger.debug("Continent: "+result);
		country.setContinent(result);
	}

	private void parseCountryName() {
		Elements countryElement = countryPage.select("span[class=region_name1]");
		String result = countryElement.text();
		logger.debug("Name: "+result);
		country.setCountry(result);
	}

}
