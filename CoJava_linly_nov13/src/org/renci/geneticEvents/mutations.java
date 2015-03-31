package org.renci.geneticEvents;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.renci.pointers.doublePointer;
import org.renci.populationStructures.demography;
import org.renci.populationStructures.node;
import org.renci.populationStructures.segWorker;
import org.renci.postSim.hap;

public class mutations {
	double location;
	node ancNode1,ancNode2;
	mutNodeList mutNodes;
	boolean MUTATE_DEBUG = false;
	demography dem;
	segWorker segFactory;
	public mutations(demography adem,segWorker aSegWorker){
		segFactory = aSegWorker;
		dem = adem;
	}
	public void setLocation(double aLoc){
		location = aLoc;
		
	}
	public double getLocation(){
		return location;
	}
	public void setAncNode1(node aNode){
		ancNode1 = aNode;
	}
	public node getAncNode1(){
		return ancNode1;
	}
	public void setAncNode2(node aNode){
		ancNode2 = aNode;
	}
	public mutNodeList getMutNodes(){
		return mutNodes;
	}
	public void setMutNodes(mutNodeList aMutNodes){
		mutNodes = aMutNodes;
	}
	public node getAncNode2(){
		return ancNode2;
	}
	public void mutateFindAndPrint(File aFile,int regionNum,double loc,double randMark,double treeTime,mutList aMutList,hap aHap){
		mutations aMut;
		node aNode;
		aNode = dem.getHeadNode(regionNum);
		aMut = mutateRegion(aNode,loc,treeTime,randMark);
		if(MUTATE_DEBUG){
			System.out.println("mark: " + randMark);
		}
		mutatePrint(aFile,aMut,aMutList,aHap);
		
	}
	public void mutatePrintHeaders(File aFile){
		String out = " pos \t anc1 anc2 \tfreq \tnodes...\n";
		//out to out file 
		
	}
	
	
	public mutations mutateRegion(node headNode,double loc,double treeAge,double randMark){
		doublePointer ratio = new doublePointer();//already set to 0...
		mutations newMuts;
		node mutateNode,tempNode;
		tempNode = headNode;
		newMuts = new mutations(dem,segFactory);
		newMuts.setLocation(loc);
		mutateNode = mutateRecurse(tempNode,ratio,newMuts,treeAge,randMark,loc);
		newMuts.setMutNodes(mutateDescendents(mutateNode,null,loc));
		return newMuts;
	}
	/* HOW MUTATION WORKS RECURSIVELY */
	/*
	 * 1. Check the left descendent. If the left exists, continue.
	 *    If not, return NULL.
	 * 2. If it contains a relevant node
	 *    (i.e. the node is active at the point of mutation)
	 *    then continue. Else go to Step 4.
	 * 3. Is this the randomly-selected, weighted-by-time branch?
	 *    If so, put the mutation there. If not, recurse on this
	 *    descendent.
	 * 4. If the RIGHT descendent contains a relevant node then continue.
	 *    Else return NULL.
	 * 5. Is this the randomly-selected, weighted-by-time branch?
	 *    If so, put the mutation there. If not, recurse on this
	 *    descendent.
	 */
	node mutateRecurse(node  nodeptr, doublePointer ratioptr , mutations newmuts, 
			double tree_age, double threshold, double loc)
	{
	  node mutatenode = null;
		
	  if (nodeptr.getDescendents()[0] != null) {
	    if (segFactory.segContains(nodeptr.getDescendents()[0].getSegment(), loc)) {
				
	      double temp = (double)(nodeptr.getGen() - nodeptr.getDescendents()[0].getGen())/ tree_age;
	      ratioptr.setDouble(ratioptr.getDouble()+temp);
				
	      if (ratioptr.getDouble() > threshold) {
	    	  newmuts.setAncNode1(nodeptr);
	    	  newmuts.setAncNode2(nodeptr.getDescendents()[0]);
	    	  return nodeptr.getDescendents()[0];
	      }
				
	      mutatenode = mutateRecurse (nodeptr.getDescendents()[0], 
					   ratioptr, 
					   newmuts, tree_age, 
					   threshold, loc);
	      if (mutatenode != null) return mutatenode;
	      /* this happens if we place a mutation inside this recursion. */
	    }
	  }	
	  else return null;


	  if ((nodeptr.getDescendents()[1]!= null) &&
	      (segFactory.segContains(nodeptr.getDescendents()[1].getSegment(), loc))) {
		  
	    double temp = (double) (nodeptr.getGen() 
				   - nodeptr.getDescendents()[1].getGen())
	      / tree_age;
	    ratioptr.setDouble(ratioptr.getDouble() + temp);
			
	    if (ratioptr.getDouble() > threshold) {
	      newmuts.setAncNode1(nodeptr);
	      newmuts.setAncNode2(nodeptr.getDescendents()[1]);
	      return nodeptr.getDescendents()[1];
	    }
	    mutatenode = mutateRecurse (nodeptr.getDescendents()[1], 
					 ratioptr,
					 newmuts, tree_age, 
					 threshold, loc);
	    if (mutatenode != null) return mutatenode;
	    /* this happens if we place a mutation inside this recursion. */
			
	  }
	  return null;
	}
	/* mutate_descendents
	 * also inserts them in numerical order.
	 */
	public mutNodeList mutateDescendents(node tempNode,mutNodeList mnList,double loc){
		mutNodeList newMNList = null;
		segWorker segFactory = new segWorker();
		//if we are at a bottom node add this node name
		if(tempNode.getDescendents()[0] == null){
			newMNList = new mutNodeList();
			newMNList.setNodeName(tempNode.getName());
			newMNList.setNextNode(null);
			return mutateAddDesc(newMNList,mnList);
			
		}
		else{
			//if the 0 descendent has this loc, traverse it
			if(segFactory.segContains(tempNode.getDescendents()[0].getSegment(), loc)){
				newMNList = mutateDescendents(tempNode.getDescendents()[0],mnList,loc);
				mnList = newMNList;
			}
			if(tempNode.getDescendents()[1]!= null){
				//if the 1 descendent has this loc, traverse it
				if(segFactory.segContains(tempNode.getDescendents()[1].getSegment(), loc)){
					newMNList = mutateDescendents(tempNode.getDescendents()[1],mnList,loc);
					
				}
			}
			return newMNList;
		}
	}
	mutNodeList mutateAddDesc(mutNodeList newMNL,mutNodeList mnList){
		if(mnList==null){
			newMNL.setNextNode(mnList);
			return newMNL;
		}
		else if(mnList.getNodeName() > newMNL.getNodeName()){
			newMNL.setNextNode(mnList);
			return newMNL;
		}
		else{
			mnList.setNextNode(mutateAddDesc(newMNL,mnList.getNextNode()));
			return mnList;
		}
	}
	public void mutatePrint(File aFile,mutations aMut,mutList aMutList,hap aHap){
		try{
		int i = 0,ichr;
		String out;
		mutNodeList tempMNList;
		
		out = String.format("M %.8f \t%d %d \t", aMut.getLocation(),aMut.getAncNode1().getName(),aMut.getAncNode2().getName());
		
		tempMNList = aMut.getMutNodes();
		while(tempMNList!=null){ // gross 
			tempMNList = tempMNList.getNextNode();
			i++;
		}
		out = out + String.format("%d  \t", i);
		tempMNList = aMut.getMutNodes();
		while(tempMNList != null){
			out = out + String.format("%d ", tempMNList.getNodeName());
			ichr = tempMNList.getNodeName();
			if(aHap.getnMut()[ichr] == aHap.getMutArraySize()[ichr]){
				int oldsize = aHap.getMutArraySize()[ichr];
				aHap.setMutArraySize(ichr, aHap.getMutArraySize()[ichr]*2);
				int[] temp = new int[aHap.getMutArraySize()[ichr]];
				
				//aHap.setMutIndex(ichr, new int[aHap.getMutArraySize()[ichr]]);
				for(i=0;i<oldsize;i++){
					temp[i] = aHap.getMutIndex()[ichr][i];
				}
				aHap.setMutIndex(ichr, temp );
			}
			aHap.setMutIndex(ichr,aHap.getnMut()[ichr],aMutList.getNumMut());
			aHap.setnMut(ichr, aHap.getnMut()[ichr]+1);
			tempMNList = tempMNList.getNextNode();
			
		}
		if(aMutList.getNumMut() == aMutList.getArraySize()){
			aMutList.setArraySize(aMutList.getArraySize()*2);
			double[] temp = new double[aMutList.arraySize];
			for(i=0;i<aMutList.getPos().length;i++){
				temp[i] = aMutList.getPos()[i];
			}
			aMutList.setPos(temp);
		}
		aMutList.setPos(aMutList.getNumMut(), aMut.getLocation());
		aMutList.setNumMut(aMutList.getNumMut() + 1);
		out = out + "\n";
		
		if(aFile!=null){
		FileWriter fileOut = new FileWriter(aFile.getName(),true);
		BufferedWriter outwriter = new BufferedWriter(fileOut);
		outwriter.write(out);
		outwriter.close();
		}
		
	
	}
	catch(Exception e){
		e.printStackTrace();
	}
}
}
