package org.renci.rareEvents;

public class NodeList {
	int node;
	NodeList next;
	
	public NodeList(){
		
	}
	public void setNodeName(int aName){
		node = aName;
	}
	public int getNodeName(){
		return node;
	}
	public void setNextNode(NodeList aNext){
		next = aNext;
	}
	public NodeList getNextNode(){
		return next;
	}
}

