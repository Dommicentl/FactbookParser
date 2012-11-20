package com.conbit.factbookparser.parser.terroristorg;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.conbit.factbookparser.MyLogger;

public class ListParser {
	
	public static final String BASE = "http://www.start.umd.edu/start/data_collections/tops/";
	public Logger logger = MyLogger.getInstance();
	
	public ArrayList<String> getOrganisationLinks() throws IOException{
		Document page;
		ArrayList<String> result = new ArrayList<String>();
		try {
			page = Jsoup.connect(BASE+"terrorist_organizations_by_alpha.asp?q=All").get();
			Elements elements = page.select("a[href^=terrorist_organization_profile.asp?id=]");
			for(Element current: elements){
				String endString = current.attr("href");
				String resultString = BASE+endString;
				result.add(resultString);
				logger.info("Added link "+resultString+" to the list");
			}
			return result;
		} catch (IOException e) {
			logger.fatal("Failed to load the list page");
			e.printStackTrace();
			throw e;
		}
	}
	
	public static void main(String[] args) {
		ListParser parser = new ListParser();
		try {
			ArrayList<String> list = parser.getOrganisationLinks();
			PageParser pageParser = new PageParser(list);
			pageParser.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
