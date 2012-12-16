package com.conbit.factbookparser.concept;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import com.conbit.factbookparser.owl.OwlHandler;

public class TableEntry {
	private String name;
	private static OwlHandler handler;
	private String type;
	private HashMap<String,Integer> attackTypes = new HashMap<String, Integer>();
	private HashMap<String,Integer> victimTypes = new HashMap<String, Integer>();
	private Set<String> countries = new HashSet<String>();
	private static HashMap<String, TableEntry> entries = new HashMap<String, TableEntry>();
	private static Logger logger = Logger.getLogger(TableEntry.class.toString());
	
	public static void setHandler(OwlHandler owlHandler){
		handler = owlHandler;
	}
	
	public static TableEntry createEntry(String name){
		TableEntry entry = entries.get(name);
		if(entry != null)
			return entry;
		entry = new TableEntry(name);
		entries.put(name, entry);
		return entry;
	}
	
	public static void save(TableEntry entry){
		entries.put(entry.getName(), entry);
		logger.info(entry.toARFFLine());
	}
	
	private TableEntry(String name){
		this.name = name;
	}
	
	public void addAttackType(String type){
		addToList(type, attackTypes);
	}
	
	public void addVictimType(String type){
		addToList(type, victimTypes);
	}

	private void addToList(String element, HashMap<String, Integer> list) {
		Integer amount = list.get(element);
		if(amount != null){
			amount++;
			list.put(element, amount);
		} else {
			list.put(element, 1);
		}		
	}

	public void addCountry(String country){
		countries.add(country);
	}
	
	public String getMostPopular(HashMap<String, Integer> list){
		int max = 0;
		String maxElement = null;
		for(String currentElem: list.keySet()){
			if(list.get(currentElem) > max){
				max = list.get(currentElem);
				maxElement = currentElem;
			}
		}
		return maxElement;
	}
	
	public String getContinent(){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for(String currentCountry : countries){
			OWLIndividual individual = handler.getIndividual(currentCountry);
			Set<OWLIndividual> individuals = handler.getObjectRelationIndividuals(individual, "liesIn");
			if(individuals == null)
				return null;
			Iterator<OWLIndividual> it = individuals.iterator();
			if(individuals.iterator().hasNext()){
				OWLIndividual continent = it.next();
				Set<OWLLiteral> continents = handler.getDataRelationIndividuals(continent, "name");
				Iterator<OWLLiteral> it2 = continents.iterator();
				if(it2.hasNext()){
					OWLLiteral continentLiteral = it2.next();
					String continentName = continentLiteral.getLiteral();
					addToList(continentName, map);
				}
			}
		}
		return getMostPopular(map);
	}
	
	public String toARFFLine(){
		String continent = getContinent();
		String classification = type;
		String victimType = getMostPopular(victimTypes);
		String attackType = getMostPopular(attackTypes);
		if(continent == null || classification == null || victimType == null || attackType == null)
			return null;
		return safe(name)+","+safe(classification)+","+safe(continent)+","+safe(victimType)+","+safe(attackType);
	}
	
	public static String safe(String input){
		return input.replaceAll(" ", "_").replaceAll("'", "").replaceAll(",","");
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private static String getIndividualNameList(String className){
		Set<OWLIndividual> continents = handler.getIndividuals(className);
		Iterator<OWLIndividual> it = continents.iterator();
		ArrayList<String> continentList = new ArrayList<String>();
		while(it.hasNext()){
			OWLIndividual continent = it.next();
			Set<OWLLiteral> continentNames = handler.getDataRelationIndividuals(continent, "name");
			if(continentNames == null)
				continue;
			continentList.add(continentNames.iterator().next().getLiteral());
		}
		return listToCommaSeperate(continentList);
	}

	private static String listToCommaSeperate(ArrayList<String> continentList) {
		String result = "";
		for(String current: continentList){
			result = result+safe(current)+",";
		}
		return (String) result.subSequence(0, result.length()-1);
	}
	
	public static void writoToArff(String location) throws IOException{
		FileWriter writer = new FileWriter(location);
		BufferedWriter out = new BufferedWriter(writer);
		out.write("@Relation titanic\n");
		out.write("@ATTRIBUTE name string\n");
		out.write("@ATTRIBUTE classification {"+getIndividualNameList("Classification")+"}\n");
		out.flush();
		out.write("@ATTRIBUTE continent {"+getIndividualNameList("Continent")+"}\n");
		out.flush();
		out.write("@ATTRIBUTE victimType {"+getIndividualNameList("VictimType")+"}\n");
		out.flush();
		out.write("@ATTRIBUTE attackType {"+getIndividualNameList("AttackType")+"}\n");
		out.flush();
		out.write("@DATA\n");
		for(TableEntry entry: entries.values()){
			String arffLine = entry.toARFFLine();
			if(arffLine != null)
				out.write(entry.toARFFLine()+"\n");
				out.flush();
		}
		out.close();
		entries.clear();
	}
}
