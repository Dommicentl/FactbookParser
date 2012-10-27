package com.conbit.factbookparser.concept;


/**
 * Represents a relation
 * 
 * @author jorn
 *
 */
public class Relation {
	
	private String individual1;
	private String individual2;
	private Enum relation;
	private String rangeClass; //the className of the range = second individual
	private String domainClass; //the className of the domain = first individual
	
	public Relation(String individual1, Enum relation, String individual2, String rangeClass, String domainClass){
		this.individual1 = individual1;
		this.individual2 = individual2;
		this.relation = relation;
		this.rangeClass = rangeClass;
		this.domainClass = domainClass;
	}
	
	public String getFirst(){
		return individual1;
	}
	
	public String getSecond(){
		return individual2;
	}
	
	public String getRelation(){
		return relation.toString();
	}
	
	public String getRangeClass(){
		return rangeClass;
	}
	
	public String getDomainClass(){
		return domainClass;
	}
	

}
