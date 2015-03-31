package org.renci.rareEvents;

import org.renci.populationStructures.node;

public class Inversion extends RareEvent {
	double length;
	double start,end;
	public Inversion(node aNode,int aNodeName, double midPoint, double alength, double aGen ) {
		/* Inversion is a rare event with a location
		 * that represents the center of our inversion
		 * the added field length is then used to calculate 
		 * the start and end of our inversion event
		 */
		super( aNode,aNodeName, midPoint, aGen);
		length = alength;
		setStartPoint();
		setEndPoint();
	}
	void setStartPoint(){
		
		start = location - length/2;
		
	}
	public double getStartPoint(){
		return start;
	}
	void setEndPoint(){
		
		end = location + length/2;
		
	}
	public double getEndPoint(){
		return end;
	}
	
	

}
