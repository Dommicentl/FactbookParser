package com.conbit.factbookparser.concept;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.conbit.factbookparser.owl.OwlHandler;

public class Country {
	
	private ArrayList<Relation> relations;
	private ArrayList<Property> properties;
	private String individualName;
	
	public Country(String name){
		relations = new ArrayList<Relation>();
		properties = new ArrayList<Property>();
		this.individualName = name;
	}
	
	public void writeToFile(){
		//TODO: uncomment
		String owlLocation = "/media/Data/Documenten/KUL/Master/2ejaar/1e sem/advanced databases/homework2/factbook-ont.owl";
		OwlHandler owl = null;
		try {
			owl = new OwlHandler(owlLocation);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		owl.addIndividual("Country", individualName);	
		for(Relation r : relations){
			if(! owl.addObjectRelation(r.getFirst(), r.getRelation(), r.getSecond())){
				owl.addIndividual(r.getDomainClass(), r.getFirst());
				owl.addIndividual(r.getRangeClass(), r.getSecond());
				owl.addObjectRelation(r.getFirst(), r.getRelation(), r.getSecond());
			}
		}
		for(Property p : properties){
			owl.addDataProperty(p.getPropery(), p.getIndividual(), p.getValue());
		}
		
	}
	
	public void addRelation(Relation r){
		relations.add(r);
	}
	
	public void addProperty(Property p){
		properties.add(p);
	}

	public String getIndividualName(){
		return individualName;
	}
	
}
