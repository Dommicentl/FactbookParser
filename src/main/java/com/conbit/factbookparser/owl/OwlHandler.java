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

	private void addIndividual(String className, String individualName) {
		PrefixManager pm = getCorrectPrefixManager(className);
		OWLClass owlClass = factory.getOWLClass(":" + className, pm);
		OWLNamedIndividual individual = factory.getOWLNamedIndividual(":"
				+ individualName, pm);
		OWLClassAssertionAxiom classAssertion = factory
				.getOWLClassAssertionAxiom(owlClass, individual);
		ontManager.addAxiom(ontology, classAssertion);
		logger.debug("added individual "+individualName+" of class "+className);
		//TODO:Save only at the end or at regular intervals
		save();
	}

	private PrefixManager getCorrectPrefixManager(String className) {
		if (classIriMapper.keySet().contains(className))
			return classIriMapper.get(className);
		else
			return defaultPm;
	}

	private void save() {
		try {
			ontManager.saveOntology(ontology);
		} catch (OWLOntologyStorageException e) {
			logger.error("Could not save the ontology!");
			e.printStackTrace();
		}
	}
}
