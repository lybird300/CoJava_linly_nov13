package org.renci.populationEvents;

import java.util.StringTokenizer;

import org.renci.geneticEvents.bottleNeck;
import org.renci.geneticEvents.sweep;
import org.renci.populationStructures.demography;

public class histWorker {
	///constants for history 
		final String HE_LABEL_TOKENS = "\"";

		final int HE_POP_SIZE = 1;
		final String HE_POP_SIZE_STRING = "change_size";

		final int HE_POP_SIZE_EXP =2;
		final String HE_POP_SIZE_EXP_STRING = "exp_change_size";

		final int HE_BOTTLENECK =3;
		final String HE_BOTTLENECK_STRING = "bottleneck";

		final int HE_ADMIX =4;
		final String HE_ADMIX_STRING = "admix";

		final int HE_MIGRATION = 6;
		final String HE_MIGRATION_STRING = "migration_rate";

		final int HE_SPLIT= 7;
		final String HE_SPLIT_STRING = "split";

		final int HE_SWEEP =8;
		final String HE_SWEEP_STRING = "sweep";
		historicalEvent currentEvent;
		final int EXP_INT = 10;
		final boolean DEBUG = true;
		demography dem;
		migrationWorker migFactory;
		sweep sweeper;
		bottleNeck bottleneck;
		public histWorker(demography adem,migrationWorker aMigFactory,sweep aSweeper,bottleNeck aBottleNeck ){
			migFactory = aMigFactory;
			dem = adem;
			currentEvent = null;
			sweeper = aSweeper;
			bottleneck = aBottleNeck;
		}
	public int historicalNumPops (int type ){
		switch (type) {
		
		case HE_POP_SIZE:
			return 1;
		case HE_POP_SIZE_EXP:
			return 1;
		case HE_BOTTLENECK:
			return 1;
		case HE_ADMIX:
			return 2;
		case HE_MIGRATION:
			return 2;
		case HE_SPLIT:
			return 2;
		case HE_SWEEP:
			return 1;
		}

		return 0;
	}
	public int	historicalNumParams(int type){ 
	
		switch (type) {
		
		case HE_POP_SIZE:
			return 1;
		case HE_POP_SIZE_EXP:
		  return 3; /* end_gen, start_size, end_size */
		case HE_BOTTLENECK:
			return 1;
		case HE_ADMIX:
			return 1;
		case HE_MIGRATION:
			return 1;
		case HE_SPLIT:
			return 0;
		case HE_SWEEP:
			return 3;
		}
		 
		return 0;
	}
	public int historicalGetType (String  typestr) 
	{

		if (typestr.equalsIgnoreCase(HE_POP_SIZE_STRING)) {
			return HE_POP_SIZE;
		}

		else if (typestr.equalsIgnoreCase(HE_POP_SIZE_EXP_STRING)) {
			return HE_POP_SIZE_EXP;
		}

		else if (typestr.equalsIgnoreCase(HE_BOTTLENECK_STRING)) {
			return HE_BOTTLENECK;
		}

		else if (typestr.equalsIgnoreCase(HE_ADMIX_STRING)) {
			return HE_ADMIX;
		}

		else if (typestr.equalsIgnoreCase( HE_MIGRATION_STRING)) {
			return HE_MIGRATION;
		}

		else if (typestr.equalsIgnoreCase( HE_SPLIT_STRING)) {
			return HE_SPLIT;
		}

		else if (typestr.equalsIgnoreCase(HE_SWEEP_STRING)) {
			return HE_SWEEP;
		}

		return -1;
	}
	public double historicalEventExecute( double historicalEventTime){
		historicalEvent tempEvent;
		double gen = historicalEventTime;
		if(currentEvent == null){
			System.out.println("Sorry no historical event to execute");
			return 0;
		}
		else{
			dem.dgLog(10 , currentEvent.getGen(), currentEvent.getLabel());
			switch(currentEvent.getType()){
			case HE_POP_SIZE:
				dem.setPopSizeByName(currentEvent.getGen(), currentEvent.getPopIndex()[0], (int) currentEvent.getParams()[0]);
				break;
			
			case HE_POP_SIZE_EXP:
				dem.setPopSizeByName(currentEvent.getGen(), currentEvent.getPopIndex()[0], (int) (currentEvent.getParams()[1]+.5));
				historicalNextExp(currentEvent);
				break;
			case HE_BOTTLENECK:
				bottleneck.bottleNeckExecute(currentEvent.getPopIndex()[0],currentEvent.getParams()[0], (int) currentEvent.getGen());
				break;
			case HE_ADMIX:
				dem.moveNodesByName(currentEvent.getPopIndex()[0], currentEvent.getPopIndex()[1], currentEvent.getParams()[0], currentEvent.getGen());
				break;
			case HE_MIGRATION:
				migFactory.migrateDelete(currentEvent.getPopIndex()[1], currentEvent.getPopIndex()[0]);
				migFactory.migrateAdd(currentEvent.getPopIndex()[1],currentEvent.getPopIndex()[0], currentEvent.getParams()[0]);
				break;
			case HE_SPLIT:
				dem.moveNodesByName(currentEvent.getPopIndex()[1], currentEvent.getPopIndex()[0], 1, currentEvent.getGen());
				dem.endPopByName(currentEvent.getPopIndex()[1]);
				break;
			case HE_SWEEP:
				gen = sweeper.sweepExecute(currentEvent.getPopIndex()[0], currentEvent.getParams()[0], currentEvent.getGen(), currentEvent.getParams()[1], currentEvent.getParams()[2]);
				break;
		}
			tempEvent = currentEvent;
			currentEvent = currentEvent.getNext();
			this.historicalFree(tempEvent);
			return gen;
		}
		
	}
	public void historicalProcessPopEvent(String buffer){
		double[] newParams;
		int[] newPops;
		int i =0;
		int eventType;
		String labelStr,newLabel,typeStr;
		double gen;
		double k;
		//String[] results = this.parsePopEventString(buffer);
		StringTokenizer str = new StringTokenizer(buffer," ");
		str.nextToken();
		typeStr = str.nextToken();
		
		
		if(DEBUG){
			
			String out = "type: " + typeStr.toString();
			//System.out.println(typeStr.toString());
			System.out.println(out);
		}
		eventType = this.historicalGetType(typeStr);
		//results[1] = results[1].trim();
		
		//get the label of the event and take off the ""...
		//labelStr = results[2].substring(1, results[2].length()).toCharArray();
		str.nextToken("\"");
		labelStr = str.nextToken("\"");
		newLabel = labelStr;//why?
		if(DEBUG){System.out.println(String.format("event label: %s\n", newLabel));}
		//get pops
		newPops = new int[this.historicalNumPops(eventType)];
		String params = str.nextToken();
		params = params.trim();
		str = new StringTokenizer(params," ");
		for( i=0;i<this.historicalNumPops(eventType);i++){
			newPops[i] = Integer.parseInt(str.nextToken());//we need to start at the 3rd index...
		}
		//extract the gen now.....
		gen = Double.parseDouble(str.nextToken());
		if(this.historicalNumParams(eventType)>0){
			newParams = new double[this.historicalNumParams(eventType)];
			for(i =0 ; i<this.historicalNumParams(eventType);i++){
				newParams[i] = Double.parseDouble(str.nextToken());//probably wrong
			}
		}
		else newParams = null;
		if(eventType == HE_POP_SIZE_EXP){
			k = Math.log(newParams[2]/newParams[1])/(newParams[0]-gen);
			newParams[2] = k;
			
		}
		
		
		
	this.historicalNewEvent(eventType, newLabel.toCharArray(), gen, newPops, newParams);	
	}
	public void historicalFree(historicalEvent event){
		event.setLabel(null);
		event.setPopIndex(null);
		if(event.getParams()!= null){
			event.setParams(null);
		}
		event = null;
	}
	public historicalEvent historicalAdd(historicalEvent newEvent, historicalEvent eventList){
		if(eventList == null){
			newEvent.setNext(eventList);
			return newEvent;
		}
		else if(newEvent.getGen() < eventList.getGen()){
			newEvent.setNext(eventList);
			return newEvent;
		}
		else{
			eventList.setNext(historicalAdd(newEvent,eventList.getNext()));
			return eventList;
		}
	}
	public void historicalNewEvent(int eventType,char[] label, double gen, int[] newPops,double[] newParams){
		historicalEvent newEvent;
		newEvent = new historicalEvent();
		newEvent.setGen(gen);
		newEvent.setType(eventType);
		newEvent.setPopIndex(newPops);
		newEvent.setParams(newParams);
		newEvent.setLabel(label);
		newEvent.setNext(null);
		currentEvent = historicalAdd(newEvent,currentEvent);
	}
	public void historicalNextExp(historicalEvent event){
		double gen;
		char[] label;
		int[] newPops;
		double[] newParams;
		if(event.getGen() == event.getParams()[0])return;
		
		assert(event.getGen()< event.getParams()[0]);
		
		//label = new char[event.getLabel().length+1];
		newPops = new int[1];
		newParams = new double[3];
		label = event.getLabel();
		newPops[0] = event.getPopIndex()[0];
		gen = event.getGen() + EXP_INT;
		if(gen>event.getParams()[0])
			gen = event.getParams()[0];
		
		newParams[0] = event.getParams()[0];
		newParams[1] = event.getParams()[1] * Math.exp(event.getParams()[2] * (gen -event.getGen()));
		newParams[2] = event.getParams()[2]; //expansion rate
		historicalNewEvent(event.getType(),label,gen,newPops,newParams);
		
		
	}
	public double historicalGetNext(double gen){
		if(currentEvent == null) return -1;
		else return currentEvent.getGen() - gen;
	}
}
