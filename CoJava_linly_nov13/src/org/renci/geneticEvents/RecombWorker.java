package org.renci.geneticEvents;


import org.renci.Random.randomNum;
import org.renci.pointers.doublePointer;
import org.renci.populationStructures.demography;
import org.renci.populationStructures.node;

public class RecombWorker {
	
	private class recombStructure{
		int startBase;
		double rate;
		private double cumulative;
		recombStructure next;
		public recombStructure(int aStartBase,double aRate,
				double aCumulative,recombStructure aNext){
			startBase = aStartBase;
			rate = aRate;
			next = aNext;
		}
		public double getCumulative() {
			return cumulative;
		}
		public void setCumulative(double cumulative) {
			this.cumulative = cumulative;
		}
	}
	demography dem;
	int length,count;
	double recombRate,lastRate;
	recombStructure recombs;
	randomNum random;
	//ArrayList<recombStructure> recList;
	
	public RecombWorker(demography adem, randomNum aRNG){
		random = aRNG;
	    dem = adem;
	}
	public void setLength(int i){
		length = i;
	}
	public int getLength(){
		return length;
	}
	
	public void addRecombSiteLL(int aStart,double aRate){
		recombStructure  newRecomb = new recombStructure(aStart,aRate,0.0,null);
		recombStructure tempRecomb = recombs;
		if(recombs == null){
			recombs = newRecomb;
			return;
		}
		else if (recombs.startBase>aStart){
			newRecomb.next = recombs;
			recombs = newRecomb;
			return;			
		}
		else{
		while (tempRecomb.next !=null){
			if(tempRecomb.next.startBase > aStart){
				newRecomb.next = tempRecomb.next;
				tempRecomb.next = newRecomb; 
				return;
			}
			tempRecomb = tempRecomb.next;
		}
		newRecomb.next = null;
		tempRecomb.next = newRecomb;
		}
		
	}
	
	public recombStructure getRecomb(){
		return recombs;
	}
	
	public void recomb_calc_r(){
		 double rr = 0;
		  recombStructure temprecomb = recombs;

		  while (temprecomb != null && temprecomb.startBase < length) {
		    if (temprecomb.next == null || 
			temprecomb.next.startBase > length) {
		      rr += (length - temprecomb.startBase) * 
			temprecomb.rate;
		    }
		    else
		      rr += (temprecomb.next.startBase - 
			     temprecomb.startBase) * temprecomb.rate;
				
		    temprecomb.setCumulative(rr);
		    temprecomb = temprecomb.next;

		  }
		  recombRate = rr;
	}
	public double recombGetR(){
		return recombRate;
	}
	public node[] recombExecute(double gen,int popIndex, doublePointer location){
		/*  pick location */  
		  double loc;
		  double temp;
		  double temp1;
		  recombStructure temprecomb = recombs;
		  double rr = 0;
		  double end;
				       
		  /* choosing location.. */
		  temp1 = random.randomDouble();
		  temp = (double) (temp1 * (recombRate));

		  while (temprecomb != null && temprecomb.startBase < length && rr < temp) {
		    if (temprecomb.next == null || 
			temprecomb.next.startBase > length) {
		      rr += (length - temprecomb.startBase) * temprecomb.rate;
		    }
		    else {
		      rr += (temprecomb.next.startBase - temprecomb.startBase) * 
			temprecomb.rate;			
		    }

		    if (rr < temp) {
		      temprecomb = temprecomb.next;
		    }
		  }

		  if (temprecomb.next == null || temprecomb.next.startBase > length)
		    end = length;
		  else end = temprecomb.next.startBase;

		  loc = (double)((int) (end - (rr - temp) / temprecomb.rate)) / length;
		  location.setDouble(loc);

		  return dem.recombineByIndex(popIndex, gen, loc);
	}
	public double recombGetRate(){
		int numPops = dem.getNumPops();
		int i;
		double rate = 0;
		int numNodes;
		if(recombRate == 0) return 0;
		for(i=0;i<numPops;i++){
			numNodes = dem.getNumNodesInPopByIndex(i);
			rate += (numNodes* recombRate);
		}
		lastRate = rate;
		return rate;
	}
	public int recombPickPopIndex(){
		/* figure out which pop to recombine */
		  /* Note: the use of lastrate looks fragile to me, with little time savings.
		     It assumes that get_rate has been called immediately before this 
		     function, with no change in the number of chromosomes since. sfs */
		int popIndex =0,i,numNodes;
		double randCounter = random.randomDouble() * recombGetRate();
		double rate = 0;
		int numPops = dem.getNumPops();
		
		//weigh pops by numNodes
		if(numPops>1){
			for(i=0;i<numPops && rate<randCounter; i++){
				numNodes = dem.getNumNodesInPopByIndex(i);
				rate+=(numNodes*recombRate);
			}
			popIndex = i -1;
		}
		return popIndex;
	}
}
