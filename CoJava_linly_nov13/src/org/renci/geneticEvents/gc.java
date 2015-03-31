package org.renci.geneticEvents;

import org.renci.Random.randomNum;
import org.renci.pointers.doublePointer;
import org.renci.populationStructures.demography;
import org.renci.populationStructures.node;

public class gc {
	double geneConversionRate,gcRate,lastGCRate;
	int length,gcLength;
	final boolean  DEBUG = true;
	randomNum random;
	demography dem;
	public gc(demography adem, randomNum aRNG){
		geneConversionRate = 0;
		gcRate = 0;
		lastGCRate = 0;
		gcLength = 500; //in bp
		dem = adem;
		random = aRNG;
	}
	public void setGCRate(double gcr){
		geneConversionRate = gcr;
		gcRate = geneConversionRate * (length + gcLength);
	}
	public void setLength(int l){
		length = l;
		gcRate = geneConversionRate * ( length + gcLength);
		if(DEBUG){
			System.out.println("rate: "+ geneConversionRate + " length: "+length + " ");
			
		}
	}
	public double getR(){
		return gcRate;
	}
	public double getRate(){
		int numPops = dem.getNumPops();
		double rate = 0;
		int numNodes;
		if(gcRate==0)return 0;
		
		for(int i = 0;i<numPops;i++){
			numNodes = dem.getNumNodesInPopByIndex(i);
			rate += (numNodes * gcRate);
		}
		lastGCRate = rate;
		return rate;
	}
	public int pickPopIndex(){
		//figure out which pop to recombine
		int popIndex = 0;
		double randCounter = random.randomDouble() * lastGCRate;
		int numPops = dem.getNumPops();
		int numNodes;
		double rate = 0;
		int i;
		//weight pops by numNodes
		if(numPops>1){
			for( i = 0; i<numPops&&rate<randCounter;i++){
				numNodes = dem.getNumNodesInPopByIndex(i);
				rate += ((double)numNodes * gcRate);
				//don't need this multiplication // comment in original code....
			}
			popIndex = i - 1;
		}
		return popIndex;
	}
	public node[] execute(double gen , int popIndex,doublePointer location1,doublePointer location2){
		double loc, loc2;
		  double temp;
		  double temp1;
		  /* choosing location.. */
		  temp1 = random.randomDouble();
		  temp = (double) (temp1 * (gcRate));
		  
		  loc = (double)((int) (temp / gcRate * 
					(length + gcLength) - gcLength)) / length;
		  loc2 = (double)((int) (temp / gcRate * (length + gcLength))) / length;
		  
		  if (loc2 > length) loc2 = length;
		  location1.setDouble(loc);
		  location2.setDouble(loc2);
		  
		  return dem.gcByIndex(popIndex, gen, loc, loc2);
	}
}
