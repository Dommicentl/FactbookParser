package com.conbit.factbookparser.concept;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.conbit.factbookparser.MyLogger;

public class Attack {
	private Logger logger = MyLogger.getInstance();

	private long id;
	private int nb_of_fatalities;
	private String date;
	private List<String> hasVictim = new ArrayList<String>();
	private List<String> ofType = new ArrayList<String>();
	private String perpetrator;

	public Attack(long id, int nb_of_fatalities, String date) {
		this.id = id;
		this.nb_of_fatalities = nb_of_fatalities;
		this.date = date;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getNb_of_fatalities() {
		return nb_of_fatalities;
	}

	public void setNb_of_fatalities(int nb_of_fatalities) {
		this.nb_of_fatalities = nb_of_fatalities;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean addHasVictim(String hasVictim) {
		if (hasVictim.isEmpty()) {
			logger.debug("The given victim is empty");
			return false;
		}
		this.hasVictim.add(hasVictim);
		return true;
	}

	public boolean removeHasVictim(String hasVictim) {
		if (!hasVictim.contains(hasVictim)) {
			logger.error("Can't remove victim because it isn't in the list.");
			return false;
		}
		this.hasVictim.remove(hasVictim);
		return true;
	}

	public boolean addOfType(String ofType) {
		if (ofType.isEmpty()) {
			logger.debug("The given type is empty");
			return false;
		}
		this.ofType.add(ofType);
		return true;
	}

	public boolean removeOfType(String ofType) {
		if (!ofType.contains(ofType)) {
			logger.error("Can't remove type because it isn't in the list.");
			return false;
		}
		this.ofType.remove(ofType);
		return true;
	}

	public String getPerpetrator() {
		return perpetrator;
	}

	public void setPerpetrator(String perpetrator) {
		this.perpetrator = perpetrator;
	}

}
