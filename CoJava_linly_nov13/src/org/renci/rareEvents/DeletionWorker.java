package org.renci.rareEvents;

import java.util.ArrayList;

import org.renci.Random.randomNum;
import org.renci.populationStructures.demography;
import org.renci.populationStructures.node;
import org.renci.populationStructures.seg;

public class DeletionWorker extends RareEventWorker {
	
	ArrayList<RareEvent> delArray;
	
	public DeletionWorker(demography aDem, randomNum aRNG, double aBaseRate) {
		super(aDem, aRNG, aBaseRate);
		
		
	}
	public void execute(int popIndex,double gen){
		
		
		int numNodes = dem.getNumNodesInPopByIndex(popIndex);
		int nodeIndex = (int)(numNodes*random.randomDouble());
		node tmpNode = 
				dem.getPopByIndex(popIndex).getNode(nodeIndex);
		double length ;
		//weight regions according to length.... deletions as regions? 
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
		delSegment(tmpNode,tmpNode.getName(),midPoint,length,gen);
		
		
	}
	private void delSegment(node aNode, int nodeName,double aMidPoint,double aLength, double aGen){
		// this is a length of deletions and not a single point deletion.....
		eventHolder.add(new Deletion(aNode, nodeName, aMidPoint, aLength, aGen));
		
	}

}
