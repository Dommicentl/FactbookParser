package com.conbit.factbookparser.convertors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.conbit.factbookparser.concept.Person;

public class XMLtoARFF {
	private static Logger logger = Logger.getLogger(XMLtoARFF.class.getName());

	public static void convert(String xmlLocation, String arffLocation)
			throws SAXException, IOException, ParserConfigurationException {
		PropertyConfigurator.configure("log4j.conf");
		ArrayList<Person> persons = new ArrayList<Person>();
		File fXmlFile = new File(xmlLocation);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("CONTACT");
		for (int itemNr = 0; itemNr < nList.getLength(); itemNr++) {
			String gender = "";
			String age = "";
			String boatNr = "";
			Element item = (Element)nList.item(itemNr);
			NodeList aData = ((Element)item.getElementsByTagName("ADDITIONAL-DATA").item(0)).getElementsByTagName("data");
			for(int counter = 0; counter < aData.getLength(); counter++){
				Element currentDataField = (Element)aData.item(counter);
				if(currentDataField.getAttribute("type").equals("Gender"))
					gender = currentDataField.getTextContent();
				else if(currentDataField.getAttribute("type").equals("Age"))
					age = currentDataField.getTextContent();
				else if(currentDataField.getAttribute("type").equals("Lifeboat"))
					boatNr = currentDataField.getTextContent();
			}			
			persons.add(new Person(gender, age, boatNr));
		}
		writoToArff(persons, "/home/leendert/Bureaublad/out.arff");
	}
	
	private static void writoToArff(ArrayList<Person> elements, String location) throws IOException{
		FileWriter writer = new FileWriter(location);
		BufferedWriter out = new BufferedWriter(writer);
		out.write("@Relation titanic\n");
		out.write("@ATTRIBUTE type {Male,Female,Child}\n");
		out.write("@ATTRIBUTE survived {true,false}\n");
		out.write("@DATA\n");
		for(Person current: elements){
			out.write(current.toArffString()+"\n");
		}
		out.close();
	}
	
	
	public static void main(String[] args) {
		try {
			XMLtoARFF.convert("/home/leendert/Bureaublad/tab.xml", null);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
