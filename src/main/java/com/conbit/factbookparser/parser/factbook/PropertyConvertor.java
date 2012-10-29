package com.conbit.factbookparser.parser.factbook;

import java.util.ArrayList;

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
		if(htmlProperty.getProperty().equals("naturalResources")){
			String[] resources = htmlProperty.getValue().split(", ");
			for(String resource : resources){
				resource = convertToValid(resource);
				Relation r = new Relation(country.getIndividualName(), RelationType.naturalResource, resource, "NaturalResource", "Country");
				country.addRelation(r);
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("ethnicGroups")){
			String[] ethnics = htmlProperty.getValue().split(" .+?, ");
			for(String ethnic : ethnics){
				ethnic = convertToValid(ethnic);
				if(isValid(ethnic)){
					Relation r = new Relation(country.getIndividualName(), RelationType.ethnicGroup, ethnic, "EthnicGroup", "Country");
					country.addRelation(r);
				}
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
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("administrativeDivisions")){
			String[] divisions = htmlProperty.getValue().split(" ");
			for(String division : divisions){
				division = convertToValid(division);
				if(division.substring(0, 1).matches("[A-Z]") && isValid(division)){
					Relation r = new Relation(country.getIndividualName(), RelationType.administrativeDivision, division, "AdministrativeDivision", "Country");
					country.addRelation(r);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("nameCapital")){
			String name = convertToValid(htmlProperty.getValue());
			if(name.substring(0, 1).matches("[A-Z]") && isValid(name)){
				Relation r = new Relation(country.getIndividualName(), RelationType.capital, "Capital_"+name, "CapitalCity", "Country");
				country.addRelation(r);
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("pipelines")){
			String name = convertToValid(htmlProperty.getValue());
			if(isValid(name)){
				Relation r = new Relation(country.getIndividualName(), RelationType.pipelines, name, "PipelineDistance", "Country");
				country.addRelation(r);
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("agricultureProducts")){
			String[] products = htmlProperty.getValue().split(" ");
			for(String product : products){
				product = convertToValid(product);
				if(isValid(product)){
					Relation r = new Relation(country.getIndividualName(), RelationType.agricultureProduct, product, "AgricultureProduct", "Country");
					country.addRelation(r);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("importsCommodities")){
			String[] commodities = htmlProperty.getValue().split(" ");
			for(String commodity : commodities){
				commodity = convertToValid(commodity);
				if(isValid(commodity)){
					Relation r = new Relation(country.getIndividualName(), RelationType.importsCommodity, commodity, "Commodity", "Country");
					country.addRelation(r);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("exportsCommodities")){
			String[] commodities = htmlProperty.getValue().split(" ");
			for(String commodity : commodities){
				commodity = convertToValid(commodity);
				if(isValid(commodity)){
					Relation r = new Relation(country.getIndividualName(), RelationType.exportsCommodity, commodity, "Commodity", "Country");
					country.addRelation(r);
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
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("importsPartners")){
			String[] partners = htmlProperty.getValue().split(" ");
			for(String partner : partners){
				partner = convertToValid(partner);
				if(partner.substring(0, 1).matches("[A-Z]") && isValid(partner)){
					Relation r = new Relation(country.getIndividualName(), RelationType.importPartner, partner, "Country", "Country");
					country.addRelation(r);
				}
			}
			return true;
		}
		if(htmlProperty.getProperty().equals("exportsPartners")){
			String[] partners = htmlProperty.getValue().split(" ");
			for(String partner : partners){
				partner = convertToValid(partner);
				if(partner.substring(0, 1).matches("[A-Z]") && isValid(partner)){
					Relation r = new Relation(country.getIndividualName(), RelationType.exportPartner, partner, "Country", "Country");
					country.addRelation(r);
				}
			}
			return true;
		}
		
		
		
		
		
		
	return false;
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
	if(htmlProperty.getProperty().equals("highestPointElevationExtremes"))
		htmlProperty.renameProperty("highestPoint");
	if(htmlProperty.getProperty().equals("lowestPointElevationExtremes"))
		htmlProperty.renameProperty("lowestPoint");
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
	if(htmlProperty.getProperty().equals("religions"))
		htmlProperty.renameProperty("religion");
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
	name = name.replace(",","");
	name = name.replace(")","");
	name = name.replace("(","");
	name = name.replace("'","");
	name = name.replace(";","");
	if(name.substring(0, 1).matches(" "))
		name = name.substring(1);
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
