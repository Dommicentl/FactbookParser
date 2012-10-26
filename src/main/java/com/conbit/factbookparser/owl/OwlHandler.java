package com.conbit.factbookparser.owl;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.omg.CORBA.IRObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
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

	public OwlHandler(String owlLocation) {
		initIriMapper();
		ontManager = OWLManager.createOWLOntologyManager();
		File owlFile = new File(owlLocation);
		try {
			ontology = ontManager.loadOntologyFromOntologyDocument(owlFile);
			defaultPm = new DefaultPrefixManager(ontology.getOntologyID()
					.getOntologyIRI() + "#");
			factory = ontManager.getOWLDataFactory();
		} catch (OWLOntologyCreationException e) {
			logger.error("Couldn't load owl file " + owlLocation);
			e.printStackTrace();
		}
	}

	private void initIriMapper() {
		PrefixManager pm = new DefaultPrefixManager(
				"http://www.daml.org/2001/09/countries/fips-10-4-ont#");
		classIriMapper.put("Country", pm);
	}

	private boolean addIndividual(String className, String individualName) {
		// TODO:check if the class name is valid!
		PrefixManager pm = getCorrectPrefixManager(className);
		OWLClass owlClass = factory.getOWLClass(":" + className, pm);
		OWLNamedIndividual individual = factory.getOWLNamedIndividual(":"
				+ individualName, pm);
		OWLClassAssertionAxiom classAssertion = factory
				.getOWLClassAssertionAxiom(owlClass, individual);
		ontManager.addAxiom(ontology, classAssertion);
		logger.debug("added individual " + individualName + " of class "
				+ className);
		// TODO:Save only at the end or at regular intervals
		return save();
	}

	public boolean addObjectRelation(String relation, String class1,
			String individual1, String class2, String individual2) {
		if (!isValidIndividual(class1, individual1)
				|| !isValidIndividual(class2, individual2)
				|| !isValidObjectRelation(relation)) {
			logger.error("The given arguments are not valid");
			return false;
		}
		OWLNamedIndividual owlIndividual1 = factory.getOWLNamedIndividual(":"
				+ individual1, defaultPm);
		OWLNamedIndividual owlIndividual2 = factory.getOWLNamedIndividual(":"
				+ individual2, defaultPm);
		OWLObjectProperty owlObjectPropertie = factory.getOWLObjectProperty(":"
				+ relation, defaultPm);
		OWLObjectPropertyAssertionAxiom axiom = factory
				.getOWLObjectPropertyAssertionAxiom(owlObjectPropertie,
						owlIndividual1, owlIndividual2);
		ontManager.addAxiom(ontology, axiom);
		logger.debug("Added raletion \"" + individual1 + " " + relation + " "
				+ individual2 + "\"");
		return save();
	}

	private boolean isValidObjectRelation(String objectRelation) {
		IRI iri = IRI.create(defaultPm.getDefaultPrefix() + objectRelation);
		return ontology.containsObjectPropertyInSignature(iri);
	}

	private boolean isValidIndividual(String class1, String individual1) {
		PrefixManager pm = getCorrectPrefixManager(class1);
		IRI iri = IRI.create(pm.getDefaultPrefix() + individual1);
		return ontology.containsIndividualInSignature(iri);
	}

	private PrefixManager getCorrectPrefixManager(String className) {
		if (classIriMapper.keySet().contains(className))
			return classIriMapper.get(className);
		else
			return defaultPm;
	}

	private boolean save() {
		try {
			ontManager.saveOntology(ontology);
			return true;
		} catch (OWLOntologyStorageException e) {
			logger.error("Could not save the ontology!");
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) {
		OwlHandler handler = new OwlHandler("/home/leendert/factbook-ont.owl");
		handler.addIndividual("Country", "Belgium");
		handler.addIndividual("Continent", "Europe");
		handler.addObjectRelation("contains", "Continent", "Europe", "Country",
				"Belgium");
	}
}
