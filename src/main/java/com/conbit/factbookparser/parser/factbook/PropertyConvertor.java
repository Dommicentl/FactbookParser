package com.conbit.factbookparser.parser.factbook;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.conbit.factbookparser.MyLogger;
import com.conbit.factbookparser.concept.Country;
import com.conbit.factbookparser.concept.Property;
import com.conbit.factbookparser.concept.PropertyType;
import com.conbit.factbookparser.concept.Relation;
import com.conbit.factbookparser.concept.RelationType;

/**
 * This class should convert each found html property to a relation or a property inside the country
 * @author jorn
 *
 */
public class PropertyConvertor {

	private ArrayList<FilteredProperty> htmlProperties;
	private Country country;
	private Logger logger = MyLogger.getInstance();

	public PropertyConvertor(ArrayList<FilteredProperty> htmlProperties, Country country){
		this.htmlProperties = htmlProperties;
		this.country = country;
	}

	/**
	 * Convert the created object to the properties of given country at construction
	 */
	public void convertToCountry(){
		for(FilteredProperty p : htmlProperties){
			if( !setProperty(p) && !setRelation(p))
				logger.error("No property or relation found for " + p.getProperty());
		}
	}

	private boolean setRelation(FilteredProperty htmlProperty) {
		htmlProperty.renameValue(takeRightValuePart(htmlProperty.getValue()));
		if(htmlProperty.getProperty().equals("naturalResources")){
			String[] resources = htmlProperty.getValue().split(", ");
			for(String resource : resources){
				resource = convertToValid(resource);
				Relation r = new Relation(country.getIndividualName(), RelationType.naturalResource, resource, "NaturalResource", "Country");
				country.addRelation(r);
				Property p = new Property(resource, PropertyType.name, resource);
				country.addProperty(p);
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("ethnicGroups")){
			String value = htmlProperty.getValue().replaceAll("\\((.*?)\\)", "");
			String[] ethnics = value.split(", ");
			for(String ethnic : ethnics){
				String[] parts = ethnic.split(" ");
				String name = convertToValid(parts[0]);
				String percent = "";
				try{
					percent = convertToValid(parts[1]);
				} catch (Exception e){
					break;
				}
				String individualName = convertToValid(ethnic);
				Relation r = new Relation(individualName, RelationType.ethnicGroup, name, "EthnicGroup", "EthnicGroupPercent");
				country.addRelation(r);
				Property p = new Property(individualName, PropertyType.percent, percent);
				country.addProperty(p);
				p = new Property(name, PropertyType.name, name);
				country.addProperty(p);
				r = new Relation(country.getIndividualName(), RelationType.ethnicGroup, individualName, "EthnicGroupPercent", "Country");
				country.addRelation(r);

			}
			return true;
		}
		if(htmlProperty.getProperty().equals("languages")){
			String[] languages = htmlProperty.getValue().split(" ");
			for(String language : languages){
				language = convertToValid(language);
				if(language.substring(0, 1).matches("[A-Z]") && isValid(language)){
					Relation r = new Relation(country.getIndividualName(), RelationType.language, language, "Language", "Country");
					country.addRelation(r);
					Property p = new Property(language, PropertyType.name, language);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("administrativeDivisions")){
			String[] divisions = htmlProperty.getValue().split(", ");
			for(String division : divisions){
				division = convertToValid(division);
				if(division.substring(0, 1).matches("[A-Z]") && isValid(division)){
					Relation r = new Relation(country.getIndividualName(), RelationType.administrativeDivision, division, "AdministrativeDivision", "Country");
					country.addRelation(r);
					Property p = new Property(division, PropertyType.name, division);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("nameCapital")){
			String name = convertToValid(htmlProperty.getValue());
			if(name.substring(0, 1).matches("[A-Z]") && isValid(name)){
				Relation r = new Relation(country.getIndividualName(), RelationType.capital, "Capital_"+name, "CapitalCity", "Country");
				country.addRelation(r);
				Property p = new Property("Capital_"+name, PropertyType.name, name);
				country.addProperty(p);
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("pipelines")){
			String[] pipelines = htmlProperty.getValue().split(", ");
			for(String pipeline : pipelines){
				//gas 466 km (2010)
				String[] parts = pipeline.split(" ");
				String pipelineInd = parts[0]+parts[1];
				String type = convertToValid(parts[0]);
				Relation r = new Relation(pipelineInd, RelationType.type, type, "PipelineType", "PipelineDistance");
				country.addRelation(r);
				Property p = new Property(pipelineInd, PropertyType.distance, parts[1]);
				country.addProperty(p);
				p = new Property(type, PropertyType.name, type);
				country.addProperty(p);
				r = new Relation(country.getIndividualName(), RelationType.pipelines, pipelineInd, "PipelineDistance", "Country");
				country.addRelation(r);
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("agricultureProducts")){
			String[] products = htmlProperty.getValue().split(", ");
			for(String product : products){
				product = convertToValid(product);
				if(isValid(product)){
					Relation r = new Relation(country.getIndividualName(), RelationType.agricultureProduct, product, "AgricultureProduct", "Country");
					country.addRelation(r);
					Property p = new Property(product, PropertyType.name, product);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("importsCommodities")){
			String[] commodities = htmlProperty.getValue().split(", ");
			for(String commodity : commodities){
				commodity = convertToValid(commodity);
				if(isValid(commodity)){
					Relation r = new Relation(country.getIndividualName(), RelationType.importsCommodity, commodity, "Commodity", "Country");
					country.addRelation(r);
					Property p = new Property(commodity, PropertyType.name, commodity);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("exportsCommodities")){
			String[] commodities = htmlProperty.getValue().split(", ");
			for(String commodity : commodities){
				commodity = convertToValid(commodity);
				if(isValid(commodity)){
					Relation r = new Relation(country.getIndividualName(), RelationType.exportsCommodity, commodity, "Commodity", "Country");
					country.addRelation(r);
					Property p = new Property(commodity, PropertyType.name, commodity);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("partyToEnvironmentInternationalAgreements")){
			String[] agreements = htmlProperty.getValue().split(", ");
			for(String agreement : agreements){
				agreement = convertToValid(agreement);
				if(isValid(agreement)){
					Relation r = new Relation(country.getIndividualName(), RelationType.environmentalAgreementPartyTo, agreement, "EnvironmentalAgreement", "Country");
					country.addRelation(r);
					Property p = new Property(agreement, PropertyType.name, agreement);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("signed,ButNotRatifiedEnvironmentInternationalAgreements")){
			String[] agreements = htmlProperty.getValue().split(", ");
			for(String agreement : agreements){
				agreement = convertToValid(agreement);
				if(isValid(agreement)){
					Relation r = new Relation(country.getIndividualName(), RelationType.environmentalAgreementSigned, agreement, "EnvironmentalAgreement", "Country");
					country.addRelation(r);
					Property p = new Property(agreement, PropertyType.name, agreement);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("industries")){
			String[] industries = htmlProperty.getValue().split(", ");
			for(String industry : industries){
				industry = convertToValid(industry);
				if(isValid(industry)){
					Relation r = new Relation(country.getIndividualName(), RelationType.industry, industry, "Industry", "Country");
					country.addRelation(r);
					Property p = new Property(industry, PropertyType.name, industry);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("governmentType")){
			String[] governments = htmlProperty.getValue().split(", ");
			for(String government : governments){
				government = convertToValid(government);
				if(isValid(government)){
					Relation r = new Relation(country.getIndividualName(), RelationType.hasGovernment, government, "GovernmentType", "Country");
					country.addRelation(r);
					Property p = new Property(government, PropertyType.name, government);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("importsPartners")){
			String[] partners = htmlProperty.getValue().split(", ");
			for(String parner : partners){
				String[] parts = parner.split(" ");
				String partnerName = convertToValid(parts[0]);
				String percent = convertToValid(parts[1]);
				String individualName = convertToValid(parner);
				Relation r = new Relation(individualName, RelationType.country, partnerName, "Country", "CountryPercent");
				country.addRelation(r);
				Property p = new Property(individualName, PropertyType.percent, percent);
				country.addProperty(p);
				r = new Relation(country.getIndividualName(), RelationType.importPartner, individualName, "CountryPercent", "Country");
				country.addRelation(r);

			}
			return true;
		}
		if(htmlProperty.getProperty().equals("exportsPartners")){
			String[] partners = htmlProperty.getValue().split(", ");
			for(String partner : partners){
				String[] parts = partner.split(" ");
				String partnerName = convertToValid(parts[0]);
				String percent = convertToValid(parts[1]);
				String individualName = convertToValid(partner);
				Relation r = new Relation(individualName, RelationType.country, partnerName, "Country", "CountryPercent");
				country.addRelation(r);
				Property p = new Property(individualName, PropertyType.percent, percent);
				country.addProperty(p);
				r = new Relation(country.getIndividualName(), RelationType.exportPartner, individualName, "CountryPercent", "Country");
				country.addRelation(r);

			}
			return true;
		}	
		if(htmlProperty.getProperty().equals("religions")){
			String value = takeRightValuePart(htmlProperty.getValue());
			String[] religions = value.split(", ");
			for(String religion : religions){
				religion = convertToValid(religion);
				Pattern namePattern = Pattern.compile("[^0-9]*");
				Matcher m = namePattern.matcher(religion);
				String religionName = "";
				if(m.find()) {
					religionName = m.group(0);
				}
				religionName = convertToValid(religionName);
				String[] percentages = religion.split(religionName);
				String percentage = "";
				try{
					percentage = percentages[1];
				} catch (Exception e){
					return true;
				}
				String individualName = convertToValid(religion);
				Relation r = new Relation(individualName, RelationType.religion, religionName, "Religion", "ReligionPercent");
				country.addRelation(r);
				Property p = new Property(individualName, PropertyType.percent, percentage);
				country.addProperty(p);
				r = new Relation(country.getIndividualName(), RelationType.religion, individualName, "ReligionPercent", "Country");
				country.addRelation(r);
				p = new Property(religionName, PropertyType.name, religionName);
				country.addProperty(p);

			}
			return true;
		}	
		if(htmlProperty.getProperty().equals("highestPointElevationExtremes")){
			String point = htmlProperty.getValue();
			point = convertToValid(point);
			Pattern namePattern = Pattern.compile("[^0-9]*");
			Matcher m = namePattern.matcher(point);
			String pointName = "";
			if(m.find()) {
				pointName = m.group(0);
			}
			pointName = convertToValid(pointName);
			String[] altitudes = point.split(pointName);
			String altitude = altitudes[1];
			altitude = convertToValid(altitude);
			Relation r = new Relation(country.getIndividualName(), RelationType.highestPoint, pointName, "ElevationExtreme", "Country");
			country.addRelation(r);
			Property p = new Property(pointName, PropertyType.name, pointName);
			country.addProperty(p);
			p = new Property(pointName, PropertyType.elevation, altitude);
			country.addProperty(p);
			return true;
		}	
		if(htmlProperty.getProperty().equals("lowestPointElevationExtremes")){
			String point = htmlProperty.getValue();
			point = convertToValid(point);
			Pattern namePattern = Pattern.compile("[^0-9]*");
			Matcher m = namePattern.matcher(point);
			String pointName = "";
			if(m.find()) {
				pointName = m.group(0);
			}
			pointName = convertToValid(pointName);
			String[] altitudes = point.split(pointName);
			String altitude = altitudes[1];
			altitude = convertToValid(altitude);
			Relation r = new Relation(country.getIndividualName(), RelationType.lowestPoint, pointName, "ElevationExtreme", "Country");
			country.addRelation(r);
			Property p = new Property(pointName, PropertyType.name, pointName);
			country.addProperty(p);
			p = new Property(pointName, PropertyType.elevation, altitude);
			country.addProperty(p);
			return true;
		}	
		if(htmlProperty.getProperty().equals("atBirthSexRatio")){
			String[] parts = convertToValid(htmlProperty.getValue()).split(" male(s)/female");
			String ratio = parts[0];
			Relation r = new Relation(country.getIndividualName(), RelationType.sexRatio, "atBirthSexRatio_"+ratio, "SexRatioBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property("atBirthSexRatio_"+ratio, PropertyType.ratio, ratio);
			country.addProperty(p);
			p = new Property("atBirthSexRatio_"+ratio, PropertyType.maxAge, "0");
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("under15YearsSexRatio")){
			String[] parts = convertToValid(htmlProperty.getValue()).split(" male(s)/female");
			String ratio = parts[0];
			Relation r = new Relation(country.getIndividualName(), RelationType.sexRatio, "under15YearsSexRatio_"+ratio, "SexRatioBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property("under15YearsSexRatio_"+ratio, PropertyType.ratio, ratio);
			country.addProperty(p);
			p = new Property("under15YearsSexRatio_"+ratio, PropertyType.maxAge, "15");
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("1564YearsSexRatio")){
			String[] parts = convertToValid(htmlProperty.getValue()).split(" male(s)/female");
			String ratio = parts[0];
			Relation r = new Relation(country.getIndividualName(), RelationType.sexRatio, "1564YearsSexRatio_"+ratio, "SexRatioBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property("1564YearsSexRatio_"+ratio, PropertyType.ratio, ratio);
			country.addProperty(p);
			p = new Property("1564YearsSexRatio_"+ratio, PropertyType.maxAge, "64");
			country.addProperty(p);
			p = new Property("1564YearsSexRatio_"+ratio, PropertyType.minAge, "15");
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("65YearsAndOverSexRatio")){
			String[] parts = convertToValid(htmlProperty.getValue()).split(" male(s)/female");
			String ratio = parts[0];
			Relation r = new Relation(country.getIndividualName(), RelationType.sexRatio, "65YearsAndOverSexRatio_"+ratio, "SexRatioBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property("65YearsAndOverSexRatio_"+ratio, PropertyType.ratio, ratio);
			country.addProperty(p);
			p = new Property("65YearsAndOverSexRatio_"+ratio, PropertyType.minAge, "65");
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("totalPopulationSexRatio")){
			String[] parts = convertToValid(htmlProperty.getValue()).split(" male(s)/female");
			String ratio = parts[0];
			Relation r = new Relation(country.getIndividualName(), RelationType.sexRatio, "totalPopulationSexRatio_"+ratio, "SexRatioBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property("totalPopulationSexRatio_"+ratio, PropertyType.ratio, ratio);
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("totalAirportsWithPavedRunways") || htmlProperty.getProperty().equals("totalAirportsWithUnpavedRunways")){
			String type = htmlProperty.getProperty();
			String value = convertToValid(htmlProperty.getValue());
			Relation r = new Relation(country.getIndividualName(), RelationType.airportBreakdown, type+value, "AirportBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property(type+value, PropertyType.count, value);
			country.addProperty(p);
			p = new Property(type+value, PropertyType.paved, ""+type.equals("totalAirportsWithPavedRunways"));
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("over3,047MAirportsWithPavedRunways") || htmlProperty.getProperty().equals("over3,047MAirportsWithUnpavedRunways")){
			String type = htmlProperty.getProperty();
			String value = convertToValid(htmlProperty.getValue());
			Relation r = new Relation(country.getIndividualName(), RelationType.airportBreakdown, type+country.getIndividualName(), "AirportBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property(type+country.getIndividualName(), PropertyType.count, value);
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.paved, ""+type.equals("over3,047MAirportsWithPavedRunways"));
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.minRunway, "3047m");
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("2,438To3,047MAirportsWithPavedRunways") || htmlProperty.getProperty().equals("2,438To3,047MAirportsWithUnpavedRunways")){
			String type = htmlProperty.getProperty();
			String value = convertToValid(htmlProperty.getValue());
			Relation r = new Relation(country.getIndividualName(), RelationType.airportBreakdown, type+country.getIndividualName(), "AirportBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property(type+country.getIndividualName(), PropertyType.count, value);
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.paved, ""+type.equals("2,438To3,047MAirportsWithPavedRunways"));
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.minRunway, "2438m");
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.maxRunway, "3047m");
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("1,524To2,437MAirportsWithPavedRunways") || htmlProperty.getProperty().equals("1,524To2,437MAirportsWithUnpavedRunways")){
			String type = htmlProperty.getProperty();
			String value = convertToValid(htmlProperty.getValue());
			Relation r = new Relation(country.getIndividualName(), RelationType.airportBreakdown, type+country.getIndividualName(), "AirportBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property(type+country.getIndividualName(), PropertyType.count, value);
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.paved, ""+type.equals("1,524To2,437MAirportsWithPavedRunways"));
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.minRunway, "1524m");
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.maxRunway, "2437m");
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("914To1,523MAirportsWithPavedRunways") || htmlProperty.getProperty().equals("914To1,523MAirportsWithUnpavedRunways") ){
			String type = htmlProperty.getProperty();
			String value = convertToValid(htmlProperty.getValue());
			Relation r = new Relation(country.getIndividualName(), RelationType.airportBreakdown, type+country.getIndividualName(), "AirportBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property(type+country.getIndividualName(), PropertyType.count, value);
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.paved, ""+type.equals("914To1,523MAirportsWithPavedRunways"));
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.minRunway, "914m");
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.maxRunway, "1523m");
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("under914MAirportsWithPavedRunways") || htmlProperty.getProperty().equals("under914MAirportsWithUnpavedRunways")){
			String type = htmlProperty.getProperty();
			String value = convertToValid(htmlProperty.getValue());
			Relation r = new Relation(country.getIndividualName(), RelationType.airportBreakdown, type+country.getIndividualName(), "AirportBreakdown", "Country");
			country.addRelation(r);
			Property p = new Property(type+country.getIndividualName(), PropertyType.count, value);
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.paved, ""+type.equals("under914MAirportsWithPavedRunways"));
			country.addProperty(p);
			p = new Property(type+country.getIndividualName(), PropertyType.maxRunway, "914m");
			country.addProperty(p);
			return true;
		}
		if(htmlProperty.getProperty().equals("agricultureLaborForceByOccupation")){
			//agricultureLaborForceByOccupation =  78.6%
			String value = takeRightValuePart(htmlProperty.getValue());
			value = convertToValid(value);
			Relation r = new Relation(country.getIndividualName(), RelationType.occupation, "agriculture_"+value, "OccupationPercent", "Country");
			country.addRelation(r);
			r = new Relation("agriculture_"+value, RelationType.occupation, "Agriculture", "Occupation", "OccupationPercent");
			country.addRelation(r);
			Property p = new Property("agriculture_"+value, PropertyType.percent, value);
			country.addProperty(p);
			p = new Property("Agriculture", PropertyType.name, "Agriculture");
			country.addProperty(p);
			return true;
		}	
		if(htmlProperty.getProperty().equals("industryLaborForceByOccupation")){
			String value = takeRightValuePart(htmlProperty.getValue());
			value = convertToValid(value);
			Relation r = new Relation(country.getIndividualName(), RelationType.occupation, "industry"+value, "OccupationPercent", "Country");
			country.addRelation(r);
			r = new Relation("industry"+value, RelationType.occupation, "Industry", "Occupation", "OccupationPercent");
			country.addRelation(r);
			Property p = new Property("industry"+value, PropertyType.percent, value);
			country.addProperty(p);
			p = new Property("Industry", PropertyType.name, "Industry");
			country.addProperty(p);
			return true;
		}	
		if(htmlProperty.getProperty().equals("servicesLaborForceByOccupation")){
			String value = takeRightValuePart(htmlProperty.getValue());
			value = convertToValid(value);
			Relation r = new Relation(country.getIndividualName(), RelationType.occupation, "services"+value, "OccupationPercent", "Country");
			country.addRelation(r);
			r = new Relation("services"+value, RelationType.occupation, "Services", "Occupation", "OccupationPercent");
			country.addRelation(r);
			Property p = new Property("services"+value, PropertyType.percent, value);
			country.addProperty(p);
			p = new Property("Services", PropertyType.name, "Services");
			country.addProperty(p);
			return true;
		}	
		if(htmlProperty.getProperty().equals("internationalOrganizationParticipation")){
			String value = takeRightValuePart(htmlProperty.getValue());
			String[] organizations = value.split(", ");
			for(String organization : organizations){
				organization = convertToValid(organization);
				if(isValid(organization)){
					Relation r = new Relation(country.getIndividualName(), RelationType.participatesIn, organization, "InternationalOrganization", "Country");
					country.addRelation(r);
					Property p = new Property(organization, PropertyType.name, organization);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("portsAndTerminals")){
			String value = takeRightValuePart(htmlProperty.getValue());
			String[] ports = value.split(", ");
			for(String port : ports){
				port = convertToValid(port);
				if(isValid(port)){
					Relation r = new Relation(country.getIndividualName(), RelationType.port, port, "Port", "Country");
					country.addRelation(r);
					Property p = new Property(port, PropertyType.name, port);
					country.addProperty(p);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("continent")){
			String continentName = takeRightValuePart(htmlProperty.getValue());
			continentName = convertToValid(continentName);
			if(isValid(continentName)){
					Relation r = new Relation(country.getIndividualName(), RelationType.liesIn, continentName, "Continent", "Country");
					country.addRelation(r);
					Property p = new Property(continentName, PropertyType.name, continentName);
					country.addProperty(p);
					r = new Relation(continentName, RelationType.contains, country.getIndividualName(), "Country", "Continent");
					country.addRelation(r);
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("borderCountriesLandBoundaries")){
			String value = takeRightValuePart(htmlProperty.getValue());
			//borderCountriesLandBoundaries =  China 76 km, Iran 936 km, Pakistan 2,430 km, Tajikistan 1,206 km, Turkmenistan 744 km, Uzbekistan 137 km
			String[] countryDistances = value.split(", ");
			for(String countryDistance : countryDistances){
				countryDistance = countryDistance.replaceAll("\\(.*?\\)", "");
				Pattern namePattern = Pattern.compile("[^0-9]*");
				Matcher m = namePattern.matcher(countryDistance);
				String countryName = "";
				if(m.find()) {
					countryName = m.group(0);
				}
				countryName = convertToValid(countryName);
				String[] distances = countryDistance.split(countryName);
				String distance = distances[1];
				distance = convertToValid(distance);
				Relation r = new Relation(country.getIndividualName(), RelationType.border, country.getIndividualName()+distance+countryName, "Border", "Country");
				country.addRelation(r);
				Property p = new Property(country.getIndividualName()+distance+countryName, PropertyType.distance, distance);
				country.addProperty(p);
				r = new Relation(country.getIndividualName()+distance+countryName, RelationType.country, countryName, "Country", "Border");
				country.addRelation(r);
			}
			return true;
		}
		
		
		return false;
		
		
	}

	public String takeRightValuePart(String value) {
		String[] parts = value.split("note");
		value = parts[0];
		return value;
	}

	private boolean setProperty(FilteredProperty htmlProperty) {
		if(htmlProperty.getProperty().equals("totalLandBoundaries"))
			htmlProperty.renameProperty("landBoundaries");
		if(htmlProperty.getProperty().equals("maritimeClaims"))
			htmlProperty.renameProperty("maritimeClaim");
		if(htmlProperty.getProperty().equals("arableLandLandUse"))
			htmlProperty.renameProperty("arableLand");
		if(htmlProperty.getProperty().equals("expendituresBudget"))
			htmlProperty.renameProperty("budgetExpenditures");
		if(htmlProperty.getProperty().equals("revenuesBudget"))
			htmlProperty.renameProperty("budgetRevenues");
		if(htmlProperty.getProperty().equals("areaComparative"))
			htmlProperty.renameProperty("comparativeArea");
		if(htmlProperty.getProperty().equals("localShortFormCountryName"))
			htmlProperty.renameProperty("localShortCountryName");
		if(htmlProperty.getProperty().equals("localLongFormCountryName"))
			htmlProperty.renameProperty("localLongCountryName");
		if(htmlProperty.getProperty().equals("conventionalLongFormCountryName"))
			htmlProperty.renameProperty("conventionalLongCountryName");
		if(htmlProperty.getProperty().equals("conventionalShortFormCountryName"))
			htmlProperty.renameProperty("conventionalShortCountryName");
		if(htmlProperty.getProperty().equals("electricityFromFossilFuels"))
			htmlProperty.renameProperty("electricityProductionFossilFuel");
		if(htmlProperty.getProperty().equals("electricityFromHydroelectricPlants"))
			htmlProperty.renameProperty("electricityProductionHydro");
		if(htmlProperty.getProperty().equals("electricityFromNuclearFuels"))
			htmlProperty.renameProperty("electricityProductionNuclear");
		if(htmlProperty.getProperty().equals("electricityFromOtherRenewableSources"))
			htmlProperty.renameProperty("electricityProductionOther");
		if(htmlProperty.getProperty().equals("environmentCurrentIssues"))
			htmlProperty.renameProperty("environmentalIssue");
		if(htmlProperty.getProperty().equals("debtExternal"))
			htmlProperty.renameProperty("externalDebt");
		if(htmlProperty.getProperty().equals("femalesAge1649ManpowerAvailableForMilitaryService"))
			htmlProperty.renameProperty("femalesOfMilitaryAge");
		if(htmlProperty.getProperty().equals("femalesAge1649ManpowerFitForMilitaryService"))
			htmlProperty.renameProperty("femalesFitForMilitaryService");
		if(htmlProperty.getProperty().equals("femaleManpowerReachingMilitarilySignificantAgeAnnually"))
			htmlProperty.renameProperty("femalesReachingMilitaryAgeAnnually");
		if(htmlProperty.getProperty().equals("agricultureGdpCompositionBySector"))
			htmlProperty.renameProperty("grossDomesticProductAgriculture");
		if(htmlProperty.getProperty().equals("industryGdpCompositionBySector"))
			htmlProperty.renameProperty("grossDomesticProductIndustry");
		if(htmlProperty.getProperty().equals("servicesGdpCompositionBySector"))
			htmlProperty.renameProperty("grossDomesticProductServices");
		if(htmlProperty.getProperty().equals("gdpRealGrowthRate"))
			htmlProperty.renameProperty("grossDomesticProductRealGrowth");
		if(htmlProperty.getProperty().equals("totalRoadways"))
			htmlProperty.renameProperty("highwaysTotal");
		if(htmlProperty.getProperty().equals("pavedRoadways"))
			htmlProperty.renameProperty("highwaysPaved");
		if(htmlProperty.getProperty().equals("unpavedRoadways"))
			htmlProperty.renameProperty("highwaysUnpaved");
		if(htmlProperty.getProperty().equals("lowest10%HouseholdIncomeOrConsumptionByPercentageShare"))
			htmlProperty.renameProperty("householdIncomeLowest10Percent");
		if(htmlProperty.getProperty().equals("highest10%HouseholdIncomeOrConsumptionByPercentageShare"))
			htmlProperty.renameProperty("householdIncomeHighest10Percent");
		if(htmlProperty.getProperty().equals("totalInfantMortalityRate"))
			htmlProperty.renameProperty("infantMortalityRateTotal");
		if(htmlProperty.getProperty().equals("femaleInfantMortalityRate"))
			htmlProperty.renameProperty("infantMortalityRateFemale");
		if(htmlProperty.getProperty().equals("maleInfantMortalityRate"))
			htmlProperty.renameProperty("infantMortalityRateMale");
		if(htmlProperty.getProperty().equals("inflationRate(consumerPrices)"))
			htmlProperty.renameProperty("inflationRate");
		if(htmlProperty.getProperty().equals("disputesInternational"))
			htmlProperty.renameProperty("internationalDispute");
		if(htmlProperty.getProperty().equals("definitionLiteracy"))
			htmlProperty.renameProperty("literacyDefinition");
		if(htmlProperty.getProperty().equals("totalPopulationLiteracy"))
			htmlProperty.renameProperty("literacyTotal");
		if(htmlProperty.getProperty().equals("maleLiteracy"))
			htmlProperty.renameProperty("literacyMale");
		if(htmlProperty.getProperty().equals("femaleLiteracy"))
			htmlProperty.renameProperty("literacyFemale");
		if(htmlProperty.getProperty().equals("telephonesMainLinesInUse"))
			htmlProperty.renameProperty("mainTelephoneLines");
		if(htmlProperty.getProperty().equals("malesAge1649ManpowerAvailableForMilitaryService"))
			htmlProperty.renameProperty("malesOfMilitaryAge");
		if(htmlProperty.getProperty().equals("malesAge1649ManpowerFitForMilitaryService"))
			htmlProperty.renameProperty("malesFitForMilitaryService");
		if(htmlProperty.getProperty().equals("maleManpowerReachingMilitarilySignificantAgeAnnually"))
			htmlProperty.renameProperty("malesReachingMilitaryAgeAnnually");
		if(htmlProperty.getProperty().equals("totalMedianAge"))
			htmlProperty.renameProperty("medianAgeTotal");
		if(htmlProperty.getProperty().equals("maleMedianAge"))
			htmlProperty.renameProperty("medianAgeMale");
		if(htmlProperty.getProperty().equals("femaleMedianAge"))
			htmlProperty.renameProperty("medianAgeFemale");
		if(htmlProperty.getProperty().equals("telephonesMobileCellular"))
			htmlProperty.renameProperty("mobileTelephoneLines");
		if(htmlProperty.getProperty().equals("adjectiveNationality"))
			htmlProperty.renameProperty("nationalityAdjective");
		if(htmlProperty.getProperty().equals("nounNationality"))
			htmlProperty.renameProperty("nationalityNoun");
		if(htmlProperty.getProperty().equals("naturalHazards"))
			htmlProperty.renameProperty("naturalHazard");
		if(htmlProperty.getProperty().equals("crudeOilProduction"))
			htmlProperty.renameProperty("oilProduction");
		if(htmlProperty.getProperty().equals("crudeOilExports"))
			htmlProperty.renameProperty("oilExports");
		if(htmlProperty.getProperty().equals("crudeOilImports"))
			htmlProperty.renameProperty("oilImports");
		if(htmlProperty.getProperty().equals("crudeOilProvedReserves"))
			htmlProperty.renameProperty("oilProvedReserves");
		if(htmlProperty.getProperty().equals("domesticTelephoneSystem"))
			htmlProperty.renameProperty("telephoneSystemDomestic");
		if(htmlProperty.getProperty().equals("generalAssessmentTelephoneSystem"))
			htmlProperty.renameProperty("telephoneSystemGeneralAssessment");
		if(htmlProperty.getProperty().equals("internationalTelephoneSystem"))
			htmlProperty.renameProperty("telephoneSystemInternational");
		if(htmlProperty.getProperty().equals("permanentCropsLandUse"))
			htmlProperty.renameProperty("permanentCrops");
		if(htmlProperty.getProperty().equals("internetHosts"))
			htmlProperty.renameProperty("internetServiceProviders");
		if(htmlProperty.getProperty().equals("gdp(officialExchangeRate)"))
			htmlProperty.renameProperty("grossDomesticProduct");
		if(htmlProperty.getProperty().equals("gdpPerCapita(ppp)"))
			htmlProperty.renameProperty("grossDomesticProductPerCapita");
		for (PropertyType owlProperty : PropertyType.values()) {
			if(owlProperty.toString().equals(htmlProperty.getProperty())){
				Property countryProperty = new Property(country.getIndividualName(), owlProperty, htmlProperty.getValue());
				country.addProperty(countryProperty);
				return true;
			}
		}
		return false;
	}

	private String convertToValid(String name){
		if(name.equals(" ") || name.equals(""))
			return "";
		name = name.replace(",","");
		name = name.replace(")","");
		name = name.replace("(","");
		name = name.replace("'","");
		name = name.replace(";","");
		name = name.replace("%","");
		if(name.substring(0, 1).matches(" "))
			name = name.substring(1);
		String lastCharacter = name.substring(name.length()-1, name.length());
		if(lastCharacter.matches(" "))
			name = name.substring(0,name.length()-1);
		return name;
	}

	private boolean isValid(String name){
		if(name.equals(""))
			return false;
		if(name.contains("%"))
			return false;
		if(name.equals("and"))
			return false;
		if(name.equals("or"))
			return false;
		return true;
	}

}
