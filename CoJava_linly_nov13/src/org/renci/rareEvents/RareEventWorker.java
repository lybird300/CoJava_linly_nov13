package org.renci.rareEvents;

import java.util.ArrayList;

import org.renci.Random.randomNum;
import org.renci.populationStructures.demography;
import org.renci.populationStructures.node;
import org.renci.populationStructures.segWorker;
import org.renci.populationStructures.siteList;

public abstract class RareEventWorker {
	demography dem;
	randomNum random;
	double baseRate;
	ArrayList<RareEvent> eventHolder;
	public RareEventWorker(demography aDem, randomNum aRNG, double aBaseRate){
		dem = aDem;
		random = aRNG;
		baseRate = aBaseRate;
		eventHolder = new ArrayList<RareEvent>();
	}
	public double getBaseRate(){
		return baseRate;
	}
	public double getRate(){
		int numPops = dem.getNumPops();
		double rate = 0;
		int numNodes;
		
		if(baseRate==0)return 0;
		
		for(int i = 0;i<numPops;i++){
			numNodes = dem.getNumNodesInPopByIndex(i);
			rate += (numNodes * baseRate);
		}
		
		return rate;
	}
	public int getPopIndex(){
		int popIndex = 0;
		double randCounter = random.randomDouble() * getRate();
		int numPops = dem.getNumPops();
		int numNodes;
		double rate = 0;
		int i;
		//weight pops by numNodes
		if(numPops>1){
			for( i = 0; i<numPops&&rate<randCounter;i++){
				numNodes = dem.getNumNodesInPopByIndex(i);
				rate += ((double)numNodes * baseRate);
				}
			popIndex = i - 1;
		}
		return popIndex;
	}
	void findRegions(){
		for(int i=0;i<eventHolder.size();i++){
			siteList tempList = dem.getRecombSites();
			RareEvent event = eventHolder.get(i);
			int region = 0;
			while (tempList.getNext()!=null){
				if(event.getLocation() > tempList.getSite()){
					if(event.getLocation() <= tempList.getNext().getSite()){
						event.setRegion(region);
						break;
					}
					else{
						region++;
						tempList = tempList.getNext();
					}
				}
				
			}
		}
			
	}
	void findAssocNodes(){
		for(int i=0;i<eventHolder.size();i++){
			RareEvent event = eventHolder.get(i);
			//int nodeName = tempInv.getNodeName();
			node invertNode = event.getEventNode();
			double loc = dem.regCenter(event.getRegion());
			NodeList nodeArray = getNodesAssoc(invertNode,null,loc);
			event.setNodesAssociated(nodeArray);
		}
	}
	NodeList getNodesAssoc(node aNode,NodeList nList ,double loc){
		/*Uses the exact logic as mutations we find out where our inversion is then
		 * we just find all the nodes below it that have the given region we will keep
		 * these and then invert our snps when we write it all out.
		 */
		NodeList newNdeList = null;
		segWorker segFactory = new segWorker();
		//if we are at a bottom node add this node name
		
		if(aNode.getDescendents()[0] == null&&aNode.getDescendents()[1]==null){
			newNdeList = new NodeList();
			newNdeList.setNodeName(aNode.getName());
			newNdeList.setNextNode(null);
			return addNodesAssoc(newNdeList,nList);
			
		}
		else{
			//if the 0 descendent has this loc, traverse it
			if(segFactory.segContains(aNode.getDescendents()[0].getSegment(), loc)){
				newNdeList = getNodesAssoc(aNode.getDescendents()[0],nList,loc);
				nList = newNdeList;
			}
			if(aNode.getDescendents()[1]!= null){
				//if the 1 descendent has this loc, traverse it
				if(segFactory.segContains(aNode.getDescendents()[1].getSegment(), loc)){
					newNdeList = getNodesAssoc(aNode.getDescendents()[1],nList,loc);
					
				}
			}
			return newNdeList;
		}
	}
	
	NodeList addNodesAssoc(NodeList newNL,NodeList nList){
		if(nList==null){
			newNL.setNextNode(nList);
			return newNL;
		}
		else if(nList.getNodeName() > newNL.getNodeName()){
			newNL.setNextNode(nList);
			return newNL;
		}
		else{
			nList.setNextNode(addNodesAssoc(newNL,nList.getNextNode()));
			return nList;
		}
	}
}

