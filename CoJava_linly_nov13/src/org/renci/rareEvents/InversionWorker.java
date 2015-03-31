package org.renci.rareEvents;

import java.util.ArrayList;

import org.renci.Random.randomNum;
import org.renci.populationStructures.demography;
import org.renci.populationStructures.node;
import org.renci.populationStructures.seg;

public class InversionWorker extends RareEventWorker {
	
	
	public InversionWorker(demography aDem,randomNum RNG, double invRate){
		super(aDem,RNG,invRate);
		
	}
	public double getRate(){
		/* Logic for the probability rate of this event
		 * starts here. 
		 */
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
	/*Weight the populations so we know which one to use
	 * 
	 */
	public int getPopIndex(){
		//double invRate = 10e-10;
		int popIndex = 0;
		double invRate = getRate();
		double randCounter = random.randomDouble() * invRate;
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
	public void execute(int popIndex,double gen){
		int numNodes = dem.getNumNodesInPopByIndex(popIndex);
		int nodeIndex = (int)(numNodes*random.randomDouble());
		node tmpNode = 
				dem.getPopByIndex(popIndex).getNode(nodeIndex);
		double length ;
		//weight regions according to length.... assuming inversion 
		//will seek out largest non recombinated region bad bad 
		seg aSeg = tmpNode.getSegment();
		double tmp = random.randomDouble();
		double rate = 0;
		double totalLength = 0.0;
		while(aSeg.getNext() != null){
			totalLength += aSeg.getEnd() - aSeg.getBegin();
			aSeg = aSeg.getNext();
		}
		aSeg = tmpNode.getSegment();
		while(aSeg.getNext()!= null && rate<tmp){
			rate += (aSeg.getEnd() - aSeg.getBegin()) / totalLength;
			if(rate<tmp)
				aSeg = aSeg.getNext();
		}
		//This is a bad distribution for the breaks but oh well
		length = aSeg.getEnd() - aSeg.getBegin();
		//could add buffer here if it is needed 
		double midPoint = aSeg.getBegin() + (aSeg.getEnd()- aSeg.getBegin())/2 ;
		invertSegment(tmpNode,tmpNode.getName(),midPoint,length,gen);
		
		
	}
	public ArrayList<RareEvent> getInversionArray(){
		return eventHolder;
	}
	void invertSegment(node aNode,int aNodeName, double midPoint, double alength, double aGen){
		Inversion invert = new Inversion(aNode,aNodeName,midPoint,alength,aGen);
		eventHolder.add(invert);
	}
	

}
