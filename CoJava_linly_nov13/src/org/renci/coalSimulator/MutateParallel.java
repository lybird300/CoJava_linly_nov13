package org.renci.coalSimulator;

import java.util.concurrent.RecursiveAction;

import org.renci.Random.randomNum;
import org.renci.geneticEvents.mutNodeList;
import org.renci.geneticEvents.mutations;
import org.renci.populationStructures.demography;
import org.renci.populationStructures.node;

public class MutateParallel extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1323525112131649744L;
	double[] pos,treeTime;
	int[][] snpMap;
	mutations mutate;
	int numRegions,lo,hi,threshold;
	byte[][] mutMat;
	randomNum random;
	demography dem;
	
	
	
	public MutateParallel(int alo, int ahi, double[] apos, double[] atreeTime, int[][] asnpMap,
			mutations aMut, int aNumRegions , byte[][] aMatrix,
			randomNum aRng, demography adem){
		pos = apos;
		treeTime = atreeTime;
		snpMap = asnpMap;
		mutate = aMut;
		numRegions = aNumRegions;
		mutMat = aMatrix;
		lo = alo;
		hi = ahi;
		random = aRng;
		dem=adem;
		threshold = 1;
	}
	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		if(hi-lo>threshold){
			int mid = lo + (int) (hi - lo)/2;
			MutateParallel lowTask = new MutateParallel(lo,mid,pos,treeTime,snpMap,mutate,
					numRegions,mutMat,random,dem);
			lowTask.fork();
			MutateParallel hiTask = new MutateParallel(mid,hi,pos,treeTime,snpMap,mutate,
					numRegions,mutMat,random,dem);
			hiTask.compute();
			lowTask.join();
		}
		else{
			mutations muts4reg;
			mutNodeList mutNodes;
			for(int reg=lo;reg<hi;reg++){
				node headNode = dem.getHeadNode(reg);
				for(int k=0;k<snpMap[reg].length;k++){
					muts4reg = mutate.mutateRegion(headNode, pos[snpMap[reg][k]], treeTime[reg], 
							random.randomDouble());
					mutNodes = muts4reg.getMutNodes();
					while(mutNodes!=null){
						mutMat[mutNodes.getNodeName()][snpMap[reg][k]]=1;
						mutNodes = mutNodes.getNextNode();
						
					}
				}
			}

		}
		

	}

}
