package com.conbit.factbookparser.parser.terroristorg;

import java.util.ArrayList;

import org.jsoup.nodes.Element;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.conbit.factbookparser.owl.OwlHandler;

public class TerroristOrg {
	
	private String name;
	private ArrayList<String> classifications = new ArrayList<String>();
	private ArrayList<String> bases = new ArrayList<String>();

	public void setName(String name) {
		this.name = name;
	}
	
	public void setClassificiation(ArrayList<String> classifications){
		this.classifications = classifications;
	}
	
	public String getName(){
		return name;
	}
	
	public ArrayList<String> getClassifications(){
		return classifications;
	}
	
	public ArrayList<String> getBases() {
		return bases;
	}

	public void setBases(ArrayList<String> bases) {
		this.bases = bases;
	}

	public void writeToFile(OwlHandler owl){

		owl.addIndividual("Perpetrator", name);	
		owl.addDataProperty("name", name, name);
		
		//adding classificationrelation
		for(String classification : classifications){
			if(! owl.addObjectRelation(name, "hasClassification", classification)){
				owl.addIndividual("Classification", classification);
				owl.addDataProperty("name", classification, classification);
				owl.addObjectRelation(name, "hasClassification", classification);
			}
		}
		
		//adding bases
		for(String base : bases){
			if(! owl.addObjectRelation(name, "hasBaseOfOperation", base)){
				owl.addIndividual("Country", base);
				owl.addDataProperty("name", base, base);
				owl.addObjectRelation(name, "hasBaseOfOperation", base);
			}
		}
		
		//adding name property
		owl.addDataProperty("name", name, name);
		
		
		
	}

}
