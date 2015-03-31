package org.renci.rareEvents;

import org.renci.populationStructures.node;

public class Deletion extends Insertion {
	
	public Deletion(node aNode, int aNodeName , double loc , double aGen) {
		/*Single point deletion.
		 * 
		 */
		super(aNode,aNodeName,loc,aGen);
	}
	public Deletion(node aNode, int aNodeName, double loc, double aLength, double aGen){
		/* Deletion of some length of base pairs
		 * 
		 */
		super(aNode,aNodeName,loc,aLength,aGen);
		
	}
}
