package org.renci.geneticEvents;

import org.renci.Random.poisson;
import org.renci.populationStructures.demography;

public class bottleNeck {
	poisson poissoner;
	demography dem;
	public bottleNeck(demography aDem,poisson aPois){
		poissoner = aPois;
		dem = aDem;
	}
	public void bottleNeckExecute(int popName,double coeff,int gen){
		int numNodes = dem.getNumNodesInPopByName(popName);
		double t = 0,effectiveN,rate,temp;
		if(numNodes<2)return;
		effectiveN = -1.0 / (2.0 * Math.log(1.0 - coeff));
		long den = numNodes * (numNodes-1);
		rate = (double) (4* effectiveN)/ (double) den;
		temp = poissoner.poissonGetNext(1/rate);
		t+=temp;
		while(t<=1.0){
			dem.coalesceByName(popName, gen + t);
			numNodes --;
			if(numNodes>1){
				den = numNodes * (numNodes-1);
				rate = (4*effectiveN)/((double)den);
				t+=poissoner.poissonGetNext(1/rate);
			}
			else t++;
		}
	}
}
