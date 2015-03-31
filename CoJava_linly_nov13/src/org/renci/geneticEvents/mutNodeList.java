package org.renci.geneticEvents;

public class mutNodeList {
	int node;
	mutNodeList next;
	
	public mutNodeList(){
		
	}
	public void setNodeName(int aName){
		node = aName;
	}
	public int getNodeName(){
		return node;
	}
	public void setNextNode(mutNodeList aNext){
		next = aNext;
	}
	public mutNodeList getNextNode(){
		return next;
	}
}
