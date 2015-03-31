package org.renci.geneticEvents;

import org.renci.Random.randomNum;
import org.renci.populationStructures.demography;

public class coalesce {
	demography dem;
	double lastRate;
	randomNum random;
	public coalesce(demography adem,randomNum aRNG){
		random = aRNG;
		lastRate = 0;
		dem = adem;
	}
	public double coalesceGetRate(){
		int numpops = dem.getNumPops();
		int i;
		double rate = 0;
		double numnodes;
		double popsize;
		
		for (i = 0; i < numpops; i++) {
			numnodes = dem.getNumNodesInPopByIndex(i);
			popsize = dem.getPopSizeByIndex(i);
			if (numnodes > 1){
				double num = (numnodes * (numnodes -1 ));
				double den = 4 * popsize;
				rate += num/den;
			}
		}
		
		lastRate = rate;
		return rate;
	}
	public void setLastRate(double rate) {
		lastRate = rate;
		
	}
	public double getLastRate(){
		return lastRate;
	}
	public int coalescePickPopIndex(){

		double  rate = 0,
			randcounter = random.randomDouble() * coalesceGetRate();
		int     popindex = 0,
			numpops = dem.getNumPops(),i;
		double numnodes;
		double popsize;
		
		
		if (numpops > 1) {
			for (i = 0; i < numpops && randcounter > rate; i++) {
				numnodes = dem.getNumNodesInPopByIndex(i);
				popsize = dem.getPopSizeByIndex(i);
				if (numnodes > 1){
					double num = (numnodes*(numnodes-1));
					double den = 4*popsize;
					rate+= num/den;
					
				}
					//rate += (double) ((numnodes * (numnodes - 1))/(4 * popsize));
				
			}		
			popindex = i - 1;
		}
		return popindex;
		
	}
	
}
