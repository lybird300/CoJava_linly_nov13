package org.renci.rareEvents;

import java.util.ArrayList;
import java.util.Collections;

import org.renci.Random.randomNum;
import org.renci.populationStructures.demography;

public class RareEventLocker {
	
	InversionWorker inverter ;
	DeletionWorker deleter;
	InsertionWorker inserter;
	demography dem;
	randomNum rng;
	double invRate,delRate,insRate;
	ArrayList<RareEvent> eventList ;
	ArrayList<RareEventWorker> workerList;
	public RareEventLocker(demography aDem,randomNum aRng){
		dem = aDem;
		rng = aRng;
		
	}
	public void composeRareEventWorkers(){
		workerList = new ArrayList<RareEventWorker>();
		inverter = new InversionWorker(dem,rng,invRate);
		workerList.add(inverter);
		deleter = new DeletionWorker(dem,rng,delRate);
		workerList.add(deleter);
		inserter = new InsertionWorker(dem,rng,insRate);
		workerList.add(inserter);
	}
	public void inversionEvent(double gen){
		
		inverter.execute(inverter.getPopIndex(), gen);
	}
	public void deletionEvent( double gen){
		deleter.execute(deleter.getPopIndex(), gen);
		
	}
	public void insertionEvent(double gen){
		inserter.execute(inserter.getPopIndex() ,gen);
		
	}
	public void buildRareEventList(){
		eventList = new ArrayList<RareEvent>();
		for(RareEventWorker worker : workerList){
			processEvents(worker);
		}
		Collections.sort(eventList,new RareEventSorter());
		
		
	}
	private void processEvents(RareEventWorker worker){
		for(RareEvent event : worker.eventHolder){
			eventList.add(event);
		}
		worker.findRegions();
		worker.findAssocNodes();
	}
	public InversionWorker getInverter() {
		return inverter;
	}
	public void setInverter(InversionWorker inverter) {
		this.inverter = inverter;
	}
	public DeletionWorker getDeleter() {
		return deleter;
	}
	public void setDeleter(DeletionWorker deleter) {
		this.deleter = deleter;
	}
	public InsertionWorker getInserter() {
		return inserter;
	}
	public void setInserter(InsertionWorker inserter) {
		this.inserter = inserter;
	}
	public double getInvRate() {
		return invRate;
	}
	public void setInvRate(double invRate) {
		this.invRate = invRate;
	}
	public double getDelRate() {
		return delRate;
	}
	public void setDelRate(double delRate) {
		this.delRate = delRate;
	}
	public double getInsRate() {
		return insRate;
	}
	public void setInsRate(double insRate) {
		this.insRate = insRate;
	}
	
}
