package org.renci.rareEvents;

import org.renci.populationStructures.node;

public class Insertion extends RareEvent {
	boolean singlePoint;
	double length,startPoint,endPoint;
	public Insertion(node aNode, int aNodeName , double loc , double aGen) {
		/*This constructor is for a single point insertion. We do not pass
		 *  a length so the length is a single point insertion
		 */
		super(aNode, aNodeName, loc, aGen);
		singlePoint = true;
		length = 0;
		startPoint = location;
		endPoint = location;
	}
	public Insertion(node aNode, int aNodeName, double loc, double aLength, double aGen){
		/* This constructor is for an insertion that spans some length. It will have 
		 * some certain amount of bases that are inserted.
		 */
		super(aNode,aNodeName,loc,aGen);
		singlePoint = false;
		length = aLength;
		/* set start and end points if we are not doing a single point insertion
		 * but make sure we are in the bounds of [0,1]. If not make it so
		 */
		startPoint = (location - length/2 > 0.0)?location - length/2 : 0.0;
		endPoint = (location + length/2<=1.0) ?location + length/2 : 1.0;
	}
	public double getStartPoint(){
		return startPoint;
	}
	public double getEndPoint(){
		return endPoint;
	}
	public boolean isSinglePoint(){
		return singlePoint;
	}
	

}
