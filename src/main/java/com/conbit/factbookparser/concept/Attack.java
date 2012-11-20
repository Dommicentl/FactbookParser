package com.conbit.factbookparser.concept;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.conbit.factbookparser.MyLogger;
import com.conbit.factbookparser.owl.OwlHandler;

public class Attack {
	private Logger logger = MyLogger.getInstance();

	private String id;
	private String nb_of_fatalities;
	private String date;
	private String country;
	private List<String> hasVictim = new ArrayList<String>();
	private List<String> ofType = new ArrayList<String>();
	private List<String> perpetrators = new ArrayList<String>();

	public Attack(String id, String nb_of_fatalities, String date) {
		this.id = id;
		this.nb_of_fatalities = nb_of_fatalities;
		this.date = date;
		logger.debug("Attack created with id "+id+" and "+nb_of_fatalities+" fatalities on "+date);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNb_of_fatalities() {
		return nb_of_fatalities;
	}

	public void setNb_of_fatalities(String nb_of_fatalities) {
		this.nb_of_fatalities = nb_of_fatalities;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean addHasVictim(String hasVictim) {
		if (hasVictim == null || hasVictim.isEmpty() || hasVictim.equals(".")) {
			logger.debug("The given victim is empty");
			return false;
		}
		this.hasVictim.add(hasVictim);
		logger.debug("Victim "+hasVictim+" added.");
		return true;
	}

	public boolean removeHasVictim(String hasVictim) {
		if (!hasVictim.contains(hasVictim)) {
			logger.error("Can't remove victim because it isn't in the list.");
			return false;
		}
		this.hasVictim.remove(hasVictim);
		logger.debug("Victim "+hasVictim+" removed.");
		return true;
	}

	public boolean addOfType(String ofType) {
		if (ofType == null | ofType.isEmpty() || ofType.equals(".")) {
			logger.debug("The given type is empty");
			return false;
		}
		this.ofType.add(ofType);
		logger.debug("Type "+ofType+" added.");
		return true;
	}

	public boolean removeOfType(String ofType) {
		if (!ofType.contains(ofType)) {
			logger.error("Can't remove type because it isn't in the list.");
			return false;
		}
		this.ofType.remove(ofType);
		logger.debug("Type "+ofType+" removed.");
		return true;
	}

	public boolean addPerpetrator(String perpetrator) {
		if (perpetrator == null || perpetrator.isEmpty() || perpetrator.equals(".")) {
			logger.debug("The given perpetrator is empty");
			return false;
		}
		this.perpetrators.add(perpetrator);
		logger.debug("Perpetrator "+perpetrator+" added.");
		return true;
	}

	public boolean removePerpetrator(String perpetrator) {
		if (!perpetrator.contains(perpetrator)) {
			logger.error("Can't remove perpetrator because it isn't in the list.");
			return false;
		}
		this.perpetrators.remove(perpetrator);
		logger.debug("Perpetrator "+perpetrator+" removed.");
		return true;
	}
	
	public boolean writeToOwl(OwlHandler handler){
		handler.addIndividual("Attack", ""+id);
		handler.addIndividual("Country", country);
		handler.addDataProperty("name", country, country);
		handler.addDataProperty("id", id, id);
		handler.addDataProperty("numberOfFatalities", id, nb_of_fatalities);
		handler.addDataProperty("date", id, date);
		writeObjectRelations(hasVictim, "VictimType", "hasVictim", handler);
		writeObjectRelations(ofType, "AttackType", "ofType", handler);
		writeObjectRelations(perpetrators, "Perpetrator", "executedBy", handler);
		handler.addObjectRelation(""+id, "occured", country);
		logger.debug("Attack "+id+" written to owl");
		return true;
	}
	
	public void writeObjectRelations(List<String> properties, String objectName, String relation, OwlHandler handler){
		for(String property: properties){
			handler.addIndividual(objectName, property);
			handler.addDataProperty("name", property, property);
			handler.addObjectRelation(id, relation, property);
		}
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	
}
