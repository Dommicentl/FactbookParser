package com.conbit.factbookparser.parser.factbook;

import org.jsoup.nodes.Element;

/**
 * This class represents properties that have subproperties
 * Instances of this class are being parsed further.
 * e.g:
 * 
 * Land boundaries:    (==Title)
 *	total: 5,529 km		(==Content)
 *  border countries: China 76 km, Iran 936 km, Pakistan 2,430 km, Tajikistan 1,206 km, Turkmenistan 744 km, Uzbekistan 137 km (==Content)
 * 
 * 
 * @author jorn
 *
 */
public class BigPropertyBlock {
	
	private Element content;
	private Element title;

	BigPropertyBlock(Element title, Element content){
		this.content = content;
		this.title = title;
	}
	
	public Element getContent(){
		return content;
	}
	
	public Element getTitle(){
		return title;
	}
	
}
