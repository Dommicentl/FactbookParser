package com.conbit.factbookparser.convertors;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.conbit.factbookparser.concept.TableEntry;
import com.conbit.factbookparser.owl.OwlHandler;
import com.conbit.factbookparser.parser.factbook.FactbookParser;

public class FactbooktoARFF {
	
	public static void main(String[] args) throws OWLOntologyCreationException, IOException {
		PropertyConfigurator.configure("log4j.conf");
		FactbooktoARFF convertor = new FactbooktoARFF("/home/leendert/Total.owl");
		convertor.convert("/home/leendert/homework6.arff");
	}

	private OwlHandler handler;
	private Logger logger = Logger.getLogger(this.toString());
	
	public FactbooktoARFF(String location) throws OWLOntologyCreationException{
		logger.debug("Start parsing ontology");
		handler = new OwlHandler("/home/leendert/Total.owl");
		TableEntry.setHandler(handler);
		logger.debug("Finished parsing ontology");
	}
	
	public void convert(String outputFileLocation) throws IOException{
		logger.debug("Start converting...");
		logger.debug("Searching attacks...");
		Set<OWLIndividual> attacks = handler.getIndividuals("Attack");
		Iterator<OWLIndividual> attackIterator = attacks.iterator();
		while(attackIterator.hasNext()){
			OWLIndividual attack = attackIterator.next();
			logger.debug("Handling attack "+attack.toStringID());
			OWLIndividual perpetrator = getPerpetrator(attack);
			String perpetratorName = getPerpetratorName(perpetrator);
			if(perpetratorName == null)
				continue;
			logger.debug("From perpetrator "+perpetratorName);
			TableEntry entry = TableEntry.createEntry(perpetratorName);
			if(!addCountry(attack,entry))
				continue;
			logger.debug("Countries added");
			if(!addAttackType(attack,entry))
				continue;
			logger.debug("Attacks added");
			if(!addVictimType(attack,entry))
				continue;
			logger.debug("Victims added");
			if(!addClassification(perpetrator,entry))
				continue;
			logger.debug("Classification added");
			TableEntry.save(entry);
			logger.debug("Entry saved");
		}
		TableEntry.writoToArff(outputFileLocation);
	}
	
	private String getPerpetratorName(OWLIndividual perpetrator) {
		Set<OWLLiteral> names = handler.getDataRelationIndividuals(perpetrator, "name");
		if(names == null){
			logger.debug("Perpetrator name failed, ignoring...");
			return null;
		}
		return names.iterator().next().getLiteral();
	}

	private boolean addClassification(OWLIndividual perpetrator, TableEntry entry) {
		Set<OWLIndividual> classifications = handler.getObjectRelationIndividuals(perpetrator, "hasClassification");
		boolean isSet = false;
		if(classifications == null)
			return false;
		Iterator<OWLIndividual> typeIterator = classifications.iterator();
		while(typeIterator.hasNext()){
			OWLIndividual currentype = typeIterator.next();
			Set<OWLLiteral> typeNames = handler.getDataRelationIndividuals(currentype, "name");
			if(typeNames == null)
				continue;
			String typeString = typeNames.iterator().next().getLiteral();
			entry.setType(typeString);
			isSet = true;
		}
		return isSet;
	}

	private boolean addVictimType(OWLIndividual attack, TableEntry entry) {
		Set<OWLIndividual> types = handler.getObjectRelationIndividuals(attack, "hasVictim");
		boolean oneAdded = false;
		if(types == null)
			return false;
		Iterator<OWLIndividual> typeIterator = types.iterator();
		while(typeIterator.hasNext()){
			OWLIndividual type = typeIterator.next();
			Set<OWLLiteral> typeNames = handler.getDataRelationIndividuals(type, "name");
			if(typeNames == null)
				continue;
			entry.addVictimType(typeNames.iterator().next().getLiteral());
			oneAdded = true;
		}
		return oneAdded;
	}

	private boolean addAttackType(OWLIndividual attack, TableEntry entry) {
		Set<OWLIndividual> types = handler.getObjectRelationIndividuals(attack, "ofType");
		boolean oneAdded = false;
		if(types == null)
			return false;
		Iterator<OWLIndividual> typeIterator = types.iterator();
		while(typeIterator.hasNext()){
			OWLIndividual type = typeIterator.next();
			Set<OWLLiteral> typeNames = handler.getDataRelationIndividuals(type, "name");
			if(typeNames == null)
				continue;
			entry.addAttackType(typeNames.iterator().next().getLiteral());
			oneAdded = true;
		}
		return oneAdded;
	}

	private boolean addCountry(OWLIndividual attack, TableEntry entry) {
		Set<OWLIndividual> countries = handler.getObjectRelationIndividuals(attack, "occured");
		if(countries == null)
			return false;
		OWLIndividual country = countries.iterator().next();
		Set<OWLLiteral> countryName = handler.getDataRelationIndividuals(country, "name");
		if(countryName == null)
			return false;
		entry.addCountry(countryName.iterator().next().getLiteral());
		return true;
	}

	public OWLIndividual getPerpetrator(OWLIndividual individual){
		Set<OWLIndividual> perpetrators = handler.getObjectRelationIndividuals(individual, "executedBy");
		if(perpetrators == null)
			return null;
		return perpetrators.iterator().next();
	}
}