package org.renci.concurrent;

import java.util.concurrent.RecursiveAction;

import org.renci.populationStructures.demography;
import org.renci.populationStructures.node;

public class TreeTime2 extends RecursiveAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5352210528678563400L;
	int hi,lo,threshold;
	double[] treeTime;
	demography dem;
	public TreeTime2(int alo,int ahi, double[] treeTimes, demography aDem){
		treeTime = treeTimes;
		lo = alo;
		hi = ahi;
		dem = aDem;
		threshold = 1;//may need to be higher
	}
	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		if(hi-lo>threshold){
			int mid = lo + (int) (hi - lo)/2;
			TreeTime2 lowTask = new TreeTime2(lo,mid,treeTime,dem);
			lowTask.fork();
			TreeTime2 hiTask = new TreeTime2(mid,hi,treeTime,dem);
			hiTask.compute();
			lowTask.join();
		}
		else{
			for(int reg=lo;reg<hi;reg++){
			node headNode = dem.getHeadNode(reg);
			double point = dem.regCenter(reg);
			double totalTime = 0;
			totalTime += dem.calcTimeInBranch(point,headNode.getGen(),headNode.getDescendents()[0]);
			totalTime += dem.calcTimeInBranch(point,headNode.getGen(),headNode.getDescendents()[1]);
			treeTime[reg] = totalTime;
			}
		}

	}
	
}
