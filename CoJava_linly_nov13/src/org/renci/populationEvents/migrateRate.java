package org.renci.populationEvents;

public class migrateRate {
	int fromPop,toPop;
	double rate;
	migrateRate next;
	public migrateRate(){
		
	}
	public int getFromPop(){
		return fromPop;
	}
	public void setFromPop(int aFromPop){
		fromPop = aFromPop;
	}
	public int getToPop(){
		return toPop;
	}
	public void setToPop(int aToPop){
		toPop = aToPop;
	}
	public void setRate(double aRate){
		rate = aRate;
	}
	public double getRate(){
		return rate;
	}
	public void setNext(migrateRate aNext){
		next = aNext;
	}
	public migrateRate getNext(){
		return next;
	}
	
	
}
