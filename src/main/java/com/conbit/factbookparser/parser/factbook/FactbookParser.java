package com.conbit.factbookparser.parser.factbook;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.conbit.factbookparser.MyLogger;
import com.conbit.factbookparser.parser.factbook.country.CountryParser;

public class FactbookParser {
	private Logger logger = MyLogger.getInstance();
	private static final String COUNTRY_PAGE_URL = "https://www.cia.gov/library/publications/the-world-factbook/geos/countrytemplate_[?].html";
	
	public void parse(){
		ArrayList<String> countries = getCountries();
		parseCountries(countries);
	}

	private ArrayList<String> getCountries(){
		ArrayList<String> result = new ArrayList<String>();
		try {
			Document countryPage = Jsoup.connect("https://www.cia.gov/library/publications/the-world-factbook/print/textversion.html").get();
			Elements countries = countryPage.select("option[value]");
			logger.info(+countries.size()+"countries found.");
			for(Element current: countries){
				String code = current.attr("value");
				if(!code.isEmpty()){
					result.add(code);
					logger.debug("Country code "+code+" found.");
				}
			}
		} catch (IOException e) {
			logger.error("Failed to get the countries!");
			e.printStackTrace();
		}
		return result;
	}
	
	private void parseCountries(ArrayList<String> countryCodes) {
		for(String currentCode: countryCodes){
			parseCountry(currentCode);
		}		
	}

	private void parseCountry(String countryCode) {
		try {
			Document countryPage = Jsoup.connect(COUNTRY_PAGE_URL.replaceAll("\\[\\?\\]", countryCode)).get();
			CountryParser countryParser = new CountryParser(countryPage);
			countryParser.writeToFile();
		} catch (IOException e) {
			logger.error("Can't find the page of country "+countryCode+". Ignoring...");
			e.printStackTrace();
		}		
	}
	
	public static void main(String[] args) {
		FactbookParser parser = new FactbookParser();
		parser.parse();
	}
	

}
