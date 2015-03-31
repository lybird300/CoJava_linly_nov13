package org.renci.populationStructures;


public class population {
	int name;
	int popSize;
	char[] label;
	nodeList members;
	public population(int aName,int aPopSize, char[] aLabel){
		name = aName;
		popSize = aPopSize;
		label = aLabel;
		members = new nodeList();
		
	}
	public void removeNode(node aNode){
		members.removeNode(aNode);
		
	}
	public void addNode(node aNode){
		members.addNode(aNode);
	}
	public void printNodes(){
		members.printNodeNames();
	}
	public void setPopSize(int aSize){
		popSize = aSize;
	}
	public int getPopSize(){
		return popSize;
	}
	public int getNumNodes(){
		return members.getNumMembers();
	}
	public nodeList getMembers(){
		return members;
	}
	public node getNode(int index){
		//index = members.getNumMembers() - index -1;
		return members.getNode(index);
	}
	public int getPopName(){
		return name;
	}
	public void setPopName(int aName){
		name = aName;
	}
	public char[] getPopLabel(){
		return label;
	}
	
}
