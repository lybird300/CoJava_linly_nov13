package org.renci.populationStructures;


public class popList {
	population pop;
	popList next;
	 
	public popList(){
		
	}
	
	public population getPop(){
		return pop;
	}
	public void setPop(population aPop){
		pop = aPop;
	}
	public popList getNext(){
		return next;
	}
	public void setNext(popList aNext){
		next = aNext;
	}

}
