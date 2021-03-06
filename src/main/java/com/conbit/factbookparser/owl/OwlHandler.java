package com.conbit.factbookparser.owl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.conbit.factbookparser.MyLogger;

public class OwlHandler {

	private Logger logger = MyLogger.getInstance();
	private OWLOntology ontology;
	private HashMap<String, PrefixManager> classIriMapper = new HashMap<String, PrefixManager>();
	private PrefixManager defaultPm;
	private OWLOntologyManager ontManager;
	private OWLDataFactory factory;

	/**
	 * Initiates the OwlHandler by reading the ontology from file
	 * 
	 * @param owlLocation
	 *            | The location of the owl ontology that has to be loaded
	 * @throws OWLOntologyCreationException 
	 */
	public OwlHandler(String owlLocation) throws OWLOntologyCreationException {
		initIriMapper();
		ontManager = OWLManager.createOWLOntologyManager();
		File owlFile = new File(owlLocation);
		try {
			ontology = ontManager.loadOntologyFromOntologyDocument(owlFile);
			defaultPm = new DefaultPrefixManager(ontology.getOntologyID()
					.getOntologyIRI() + "#");
			factory = ontManager.getOWLDataFactory();
		} catch (OWLOntologyCreationException e) {
			throw e;
		}
	}

	private void initIriMapper() {
		PrefixManager pm = new DefaultPrefixManager(
				"http://www.daml.org/2001/09/countries/fips-10-4-ont#");
		classIriMapper.put("Country", pm);
		classIriMapper.put("code", pm);
//		classIriMapper.put("name",pm);
	}

	/**
	 * Adds an individual to the ontology
	 * 
	 * @param className
	 *            | The class name of the class for which you want to add an
	 *            individual
	 * @param individualName
	 *            | The name of the individual
	 * @return True of the individual is added successfully, False if not
	 */
	public boolean addIndividual(String className, String individualName)  {
		if(! isValidClassName(className)){
//			logger.error("The classname '" + className + "' is not valid!");
//			return false;
		}
		individualName = makeSafe(individualName);
		PrefixManager pm = getCorrectPrefixManager(className);
		OWLClass owlClass = factory.getOWLClass(":" + className, pm);
		OWLNamedIndividual individual = factory.getOWLNamedIndividual(":"
				+ individualName, defaultPm);
		OWLClassAssertionAxiom classAssertion = factory
				.getOWLClassAssertionAxiom(owlClass, individual);
		ontManager.addAxiom(ontology, classAssertion);
		logger.debug("added individual " + individualName + " of class "
				+ className);
		// TODO:Save only at the end or at regular intervals

		return true;
	}
	
	public Set<OWLIndividual> getIndividuals(String className){
		PrefixManager pm = getCorrectPrefixManager(className);
		OWLClass owlClass = factory.getOWLClass(className, pm);
		return owlClass.getIndividuals(ontology);
	}
	
	public OWLIndividual getIndividual(String name){
		return factory.getOWLNamedIndividual(name, defaultPm);
	}
	
	public Set<OWLIndividual> getObjectRelationIndividuals(OWLIndividual individual, String relationName){
		 Map<OWLObjectPropertyExpression, Set<OWLIndividual>> map = individual.getObjectPropertyValues(ontology);
		 PrefixManager pm = getCorrectPrefixManager(relationName);
		 IRI relationIRI = pm.getIRI(relationName);
		 for(OWLObjectPropertyExpression current: map.keySet()){
			 IRI foundRelationIri = current.getNamedProperty().getIRI();
			 if(relationIRI.equals(foundRelationIri)){
				 return map.get(current);
			 }
		 }
		 return null;
	}
	
	public Set<OWLLiteral> getDataRelationIndividuals(OWLIndividual individual, String relationName){
		PrefixManager pm = getCorrectPrefixManager(relationName);
		IRI relationIRI = pm.getIRI(relationName);
		Map<OWLDataPropertyExpression, Set<OWLLiteral>> properties = individual.getDataPropertyValues(ontology);
		for(OWLDataPropertyExpression current: properties.keySet()){
			IRI foundRelationIRI = current.asOWLDataProperty().getIRI();
			if(relationIRI.equals(foundRelationIRI)){
				 return properties.get(current);
			 }
		}
		return null;
	}
	
	public boolean checkAmountOfObjectRelations(String className, int amount){
		Set<OWLIndividual> individuals = getIndividuals(className);
		for(OWLIndividual individual : individuals){
			Map<OWLObjectPropertyExpression, Set<OWLIndividual>> relations = individual.getObjectPropertyValues(ontology);
			if(relations.keySet().size() != amount)
				return false;
		}
		return true;
	}

	/**
	 * Adds an object relation to the ontology
	 * 
	 * @param relation
	 *            | The name of the relation
	 * @param class1
	 *            | The name of the class of the first individual
	 * @param individual1
	 *            | The name of the first individual
	 * @param class2
	 *            | The name of the class of the second individual
	 * @param individual2
	 *            | The name of the second individual
	 * @return True if the relation is added, False if not
	 */
	public boolean addObjectRelation(String individual1, String relation, String individual2) {
		individual1 = makeSafe(individual1);
		individual2 = makeSafe(individual2);
		if (!isValidIndividual(individual1) || !isValidIndividual(individual2)){
			//	|| !isValidObjectRelation(relation)) {
			logger.error("The given arguments are not valid");
			return false;
		}
		OWLNamedIndividual owlIndividual1 = factory.getOWLNamedIndividual(":"
				+ individual1, defaultPm);
		OWLNamedIndividual owlIndividual2 = factory.getOWLNamedIndividual(":"
				+ individual2, defaultPm);
		OWLObjectProperty owlObjectProperty = factory.getOWLObjectProperty(":"
				+ relation, getCorrectPrefixManager(relation));
		OWLObjectPropertyAssertionAxiom axiom = factory
				.getOWLObjectPropertyAssertionAxiom(owlObjectProperty,
						owlIndividual1, owlIndividual2);
		ontManager.addAxiom(ontology, axiom);
		logger.debug("Added relation \"" + individual1 + " " + relation + " "
				+ individual2 + "\"");
		return true;
	}

	/**
	 * Adding a data property
	 * @param property
	 * @param individual
	 * @param value
	 * @return | true if added
	 */
	public boolean addDataProperty(String property, String individual, String value){
		individual = makeSafe(individual);
		if(!isValidIndividual(individual) || !isValidDataProperty(property)){
			logger.error("The given argument is not valid --> individual=" + individual + " and  property=" + property );
			return false;
		}
		OWLNamedIndividual owlIndividual = factory.getOWLNamedIndividual(":"
				+ individual, defaultPm);
		OWLDataProperty owlDataProperty = factory.getOWLDataProperty(":"
				+ property, defaultPm);
		OWLDataPropertyAssertionAxiom axiom = factory
				.getOWLDataPropertyAssertionAxiom(owlDataProperty,
						owlIndividual, value);
		ontManager.addAxiom(ontology, axiom);
		logger.debug("Added property \"" + property + " = " + value + " to " + individual + "\"");
		
		return true;
	}
	
	private boolean isValidClassName(String className){
		IRI iri = IRI.create(defaultPm.getDefaultPrefix() + className);
		return ontology.containsDataPropertyInSignature(iri);
	}


	private boolean isValidDataProperty(String dataProperty){
		IRI iri = IRI.create(getCorrectPrefixManager(dataProperty).getDefaultPrefix() + dataProperty);
		return ontology.containsDataPropertyInSignature(iri);
	}

	private boolean isValidObjectRelation(String objectRelation) {
		IRI iri = IRI.create(getCorrectPrefixManager(objectRelation).getDefaultPrefix() + objectRelation);
		return ontology.containsObjectPropertyInSignature(iri);
	}

	private boolean isValidIndividual(String individual1) {
		IRI iri = IRI.create(defaultPm.getDefaultPrefix() + individual1);
		return ontology.containsIndividualInSignature(iri);
	}

	private PrefixManager getCorrectPrefixManager(String className) {
		if (classIriMapper.keySet().contains(className))
			return classIriMapper.get(className);
		else
			return defaultPm;
	}

	public boolean save() {
		try {
			ontManager.saveOntology(ontology);
			return true;
		} catch (Exception e) {
			logger.debug("Could not save the ontology!");
			e.printStackTrace();
			return false;
		}
	}
	
	private String makeSafe(String string){
		string = string.replaceAll(" ", "_");
		string = string.replaceAll("\"", "");
		string = string.replaceAll("`", "");
		return string;
	}
	
}
