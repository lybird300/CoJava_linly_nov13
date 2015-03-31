package org.renci.populationStructures;

public class seg {
	double begin,end;
	seg next;
	public seg(double aBegin, double aEnd){
		begin = aBegin;
		end = aEnd;
		next = null;
	}
	public double getBegin(){
		return begin;
	}
	public void setBegin(double aBegin){
		begin = aBegin;
	}
	public double getEnd() {
		return end;
	}
	public void setEnd(double aEnd) {
		end = aEnd;
	}
	public seg getNext() {
		return next;
	}
	public void setNext(seg aNext) {
		next = aNext;
	}
	 
	
}
