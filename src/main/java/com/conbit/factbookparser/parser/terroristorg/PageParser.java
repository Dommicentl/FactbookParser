package com.conbit.factbookparser.parser.terroristorg;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.conbit.factbookparser.MyLogger;
import com.conbit.factbookparser.owl.OwlHandler;

public class PageParser {

	private Logger logger = MyLogger.getInstance();
	ArrayList<String> pageList = new ArrayList<String>();
	
	public PageParser(ArrayList<String> pageList){
		this.pageList = pageList;
	}
	
	public void parse() throws IOException{
		String owlLocation = "/home/leendert/Total.owl";
		OwlHandler owl = null;
		try {
			owl = new OwlHandler(owlLocation);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		for(String url : pageList){
			Document countryPage = Jsoup.connect(url).get();
			TerroristOrg org = getTerroristOrg(countryPage);
			org.writeToFile(owl);
		}
		owl.save();
	}

	private TerroristOrg getTerroristOrg(Document countryPage) {
		TerroristOrg org = new TerroristOrg();
		Elements titles = countryPage.select("span[style=font-size: 130%; color: navy;]");
		String name = titles.get(0).text();
		org.setName(name);
		logger.debug("Created: " + org.getName());
		ArrayList<String> classificationList = getClassification(countryPage);
		org.setClassificiation(classificationList);
		ArrayList<String> basesList = getBases(countryPage);
		org.setBases(basesList);
		for(String classif : org.getClassifications()){
			logger.debug("with classification: " + classif);
		}
		return org;
	}

	private ArrayList<String> getClassification(Document countryPage) {
		ArrayList<String> classificationList = new ArrayList<String>();
		Elements mainTables = countryPage.select("table[style=margin-right: 15px;]");
		Element mainTable = mainTables.get(0);
		Elements rows = mainTable.select("tr[valign=top]");
		for(Element row : rows){
			String rowText = row.text();
			if(row.text().contains("Classifications:")){
				String classificiationsString = rowText.split("Classifications: ")[1];
				String[] individualClass = classificiationsString.split(", ");
				for(String classification : individualClass){
					classificationList.add(classification);
				}
			}
		}
		return classificationList;
	}
	
	private ArrayList<String> getBases(Document countryPage){
		ArrayList<String> result = new ArrayList<String>();
		Elements elements = countryPage.select("a[href^=terrorist_organizations_by_country.asp?id=]");
		for(Element current: elements){
			String country = current.text();
			logger.debug("With base: "+country);
			result.add(country);
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("http://www.start.umd.edu/start/data_collections/tops/terrorist_organization_profile.asp?id=4703");
		PageParser parser = new PageParser(list);
		parser.parse();
	}
	

}
