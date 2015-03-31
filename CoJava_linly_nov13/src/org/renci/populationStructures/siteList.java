package org.renci.populationStructures;

public class siteList {
	double site;
	int nnode;
	siteList next;
	public siteList(){
		
	}
	public void setSite(double aSite){
		site = aSite;
	}
	public double getSite(){
		return site;
	}
	public void setNNode(int aNNode){
		nnode = aNNode;
	}
	public int getNNode(){
		return nnode;
	}
	public void setNext(siteList aNext){
		next = aNext;
	}
	public siteList getNext(){
		return next;
	}
}
