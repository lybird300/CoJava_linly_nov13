package org.renci.populationStructures;

import java.io.File;
import java.io.FileWriter;

import org.renci.Random.ranBinom;
import org.renci.Random.randomNum;
import org.renci.concurrent.TreeTime;
import org.renci.main.CoalescentMain;

public class demography {
	File logFile;
	siteList recombSites;
	popList pops;
	population completePop;
	int numPops,totMembers,numSites;
	segWorker segFactory;
	nodeWorker nodeFactory;
	//constants for switch for logging
	final int ADD_NODE = 1;
	final int COALESCE = 2;
	final int CREATE_POP = 3;
	final int RECOMBINE = 4;
	final int DONE =5;
	final int MOVE = 7;
	final int CHANGE_SIZE = 8;
	final int MIG_RATE = 9; 
	final int HISTORICAL = 10;
	final int GENE_CONVERSION = 12;
	final boolean DEMOG_DEBUG = false;
	boolean logFileSet = false;
	ranBinom randBinom;
	randomNum random;
	double[] recombArray;
	public demography(nodeWorker aNodeFactory,segWorker aSegFactory,randomNum aRNG){//dg_initialize()
		nodeFactory = aNodeFactory;
		pops = null;
		numPops = 0;
		totMembers = 0;
		completePop = new population(-1,0,"complete_nodes".toCharArray());
		segFactory = aSegFactory;
		random = aRNG;
		randBinom = new ranBinom(aRNG);

	}
	public void initRecomb(){//dg_init_recomb()
		recombSites = addRecombSite2(0.0,null);
		recombSites = addRecombSite2(1.0,recombSites);
		numSites = 1; 
		
	}
	public void setLogFile(File aFile){
		logFile = aFile;
		logFileSet = true;
	}
	public File getLogFile(){
		return logFile;
	}
	public siteList getRecombSites(){//dg_recombsites()
		return recombSites;
	}
	public siteList addRecombSite2(double site, siteList sitelistptr) 
		{
		  siteList newsitelist, thislistptr, lastlistptr;

		  /* 1. check if site belongs at the top of the list.
		   * 2. loop through list.
		   * 3. check if site already exists in the list.
		   * 4. add new site.
		   */
		  /* Modify: eliminate recursion (which blows up if the stack gets too deep). sfs */

		  if (sitelistptr == null) {
		    newsitelist = new siteList();
		    

		    newsitelist.setSite(site);
		    newsitelist.setNext(sitelistptr);
		    newsitelist.setNNode(totMembers);
		    numSites++;
		    return newsitelist;
		  }

		  thislistptr = sitelistptr.getNext();
		  lastlistptr = sitelistptr;

		  while (thislistptr != null) {
		    if (thislistptr.getSite() == site) {return sitelistptr;}
		    if (thislistptr.getSite() > site) {
		      newsitelist = new siteList();
		      newsitelist.setSite(site);
		      newsitelist.setNext( thislistptr);
		      newsitelist.setNNode(lastlistptr.getNNode());
		      lastlistptr.setNext(newsitelist);
		      numSites++;
		      return sitelistptr;
		    }
		    lastlistptr = thislistptr;
		    thislistptr = thislistptr.getNext();
		  }
		  newsitelist = new siteList();
		  newsitelist.setSite(site);
		  newsitelist.setNext(null);
		  newsitelist.setNNode(0);
		  lastlistptr.setNext(newsitelist);
		  numSites++;
		  return sitelistptr;
		}
	
	public siteList addRecombSite(double site, siteList aSiteList ) {
		siteList newSiteList, thisList, lastList;
			/* 1. check if site belongs at the top of the list.
		   * 2. loop through list.
		   * 3. check if site already exists in the list.
		   * 4. add new site.
		   */
		  /* Modify: eliminate recursion (which blows up if the stack gets too deep). sfs */
		if(aSiteList == null){
			newSiteList = new siteList();
			newSiteList.setSite(site);
			newSiteList.setNext(aSiteList);
			newSiteList.setNNode(totMembers);
			numSites++;
			return newSiteList;
		}
		
		thisList = aSiteList.getNext();
		lastList = aSiteList;
		
		while (thisList != null){
			if(thisList.getSite() == site)
				return aSiteList;
			if(thisList.getSite() > site){
				newSiteList = new siteList();
				newSiteList.setSite(site);
				newSiteList.setNext(thisList);
				newSiteList.setNNode(lastList.getNNode());
				lastList.setNext(newSiteList);
				numSites ++;
				return aSiteList;
			}
			lastList = lastList.getNext();
			thisList = thisList.getNext();
		
		}
		
		newSiteList = new siteList();
		newSiteList.setSite(site);
		newSiteList.setNext(null);
		newSiteList.setNNode(0);
		lastList.setNext(newSiteList);
		numSites++;
		return aSiteList;
	}
	public popList deletePopByName(int popName,popList tempPopList){
		
		if(tempPopList == null){
			System.out.println("dg_deletePopByName, couldn't delete specified pop");
			return null;
		}
		if(tempPopList.getPop().getPopName() == popName){
			if(DEMOG_DEBUG) System.out.println(String.format("pop %s removed\n", tempPopList.getPop().getPopLabel()));
			numPops--;
			return tempPopList.getNext();
		}
		
		tempPopList.setNext(this.deletePopByName(popName , tempPopList.getNext()));
		return tempPopList;
	}
	public int addToCompletePopulation(node aNode,population aPop,double begin,double end){
		node doneNode = null;
		node contNode = null;
		int numberOfNodes;
		Object[] results = nodeFactory.nodeBreakOffSeg(aNode, doneNode, contNode , begin, end);
		numberOfNodes = (int) results[0];
		if(numberOfNodes == 2){
			doneNode = (node) results[1];
			contNode = (node) results[2];
			completePop.addNode(doneNode);
			aPop.removeNode(aNode);
			aPop.addNode(contNode);
			dgLog(DONE,aNode.getGen(),aNode,doneNode,contNode);
			if(DEMOG_DEBUG)System.out.println(completePop.getNumNodes());
			return 1;
		}
		else if(numberOfNodes == 1){
			completePop.addNode(aNode);
			aPop.removeNode(aNode);
			dgLog(DONE,aNode.getGen(),aNode,doneNode,contNode);
			if(DEMOG_DEBUG)System.out.println(completePop.getNumNodes());
		}
		return 0;
				
	}
	/* POP FUNCTIONS 
	 * "index" refers to the location in the population list.
	 * "name" refers to the numerical name of the population, as
	 *        defined in the param file.
	 * 
	 * How to create a population:
	 * 1. call dg_create_pop to create an empty population
	 * 2. call dg_set_pop.. to set the population size
	 * 3. (opt) call dg_populate_by_name to fill in the nodes
	 */
	public void createPop(int popName, char[] label,double gen){//dg_create_pop()
		population newPop, tempPop;
		/* check to make sure the popname is not taken and
		 is non negative
		 if it is, throw a fatal exception.  */
		if(popName<0){
			System.out.println("createPop: popName must not be Negative");
			
		}
		//tempPop = getPopByNameInt(popName,pops);
		//if(tempPop!=null){
		//	System.out.println("duplicate population name used");
		//}
		newPop = new population(popName, 0, label);
		this.addPop(newPop,gen,pops);
		
		
	}
	public void populateByName(int popName,int members, double gen){
		population aPop = this.getPopByNameInt(popName,pops);
		node tempNode;
		for(int i=0;i<members;i++){
			tempNode = nodeFactory.makeNewNode(0, 1, gen, popName);
			aPop.addNode(tempNode);
			dgLog(ADD_NODE,gen,tempNode,aPop);
			
		}
		totMembers += members;
	}
	public int getNumPops(){//dg_get_num_pops()
		return numPops;
	}
	public char[] getPopLabelByName(int popName){
		return getPopByNameInt(popName,pops).getPopLabel();
	}
	//return -1 if something goes wrong
	public int getPopNameByLabel(char[] label){
		popList  tempPop = pops;
		while(tempPop !=null){
			if(tempPop.getPop().getPopLabel().toString().equalsIgnoreCase(label.toString()))
				return tempPop.getPop().getPopName();
			tempPop = tempPop.getNext();
		}
		return -1;
	}
	// popsize functions
	public int getPopSizeByIndex(int popIndex){
		population aPop = this.getPopByIndex(popIndex);
		return aPop.getPopSize();
	}
	//return 0 if population does not exist....
	public int setPopSizeByName(double gen, int popName,int newSize){
		population aPop = this.getPopByNameInt(popName,pops);
				if(aPop == null){
					System.out.println("setPopSizeByName: Pop does not exist");
					return 0;
				}
		aPop.setPopSize(newSize);
		dgLog(CHANGE_SIZE,gen,aPop);
		return 1;
	}
	//ending pops
	public void endPopByName(int popName){
		pops = deletePopByName(popName,pops);
	}
	//coalesce
	public void coalesceByIndex(int popIndex,double gen){
		population aPop = this.getPopByIndex(popIndex);
		assert(popIndex>=0);
		this.coalesceByName(aPop.getPopName(), gen);
	}
	public void coalesceByName(int popName,double gen){
		population popptr;
		int node1index, node2index, nodesatloc; 
		boolean contains;
		node node1, node2, newnode;
		siteList  sitetemp = recombSites;
		double loc;
		seg tseg;
	  
		/* 
		 * 1. Choose two unique nodes.
		 * 2. Coalesce them, creating newnode.
		 * 2a. Update the tally of nodes associated with segments
		 * 3. Remove old nodes from population.
		 * 4. Add new node to population.
		 * 5. Log it.
		 */ 

		popptr = getPopByNameInt (popName,pops);

		/* STEP 1 */
		node1index = (int) (random.randomDouble() * popptr.getMembers().getNumMembers());
		node2index = (int) (random.randomDouble() * (popptr.getMembers().getNumMembers() - 1));

		if (node2index >= node1index) node2index++;

		node1 = popptr.getNode(node1index);
		node2 = popptr.getNode(node2index);

		/* STEP 2 */
		newnode = nodeFactory.nodeCoalesce(node1, node2, gen);

		/* STEP 2a */
		while (sitetemp.next != null) {
		  if (sitetemp.nnode >= 2) {
		    loc = (sitetemp.site + sitetemp.next.site) / 2;
		    nodesatloc = 0;
		    tseg = node1.getSegment();
		    contains = false;
		    while (tseg != null) {
		      if (loc >= tseg.getBegin()) {
		    	  if (loc <= tseg.getEnd()) {
		    		  contains = true;
		    		  break;
		    	  }
		    	  
		    	  tseg = tseg.getNext();
		      }
		      else { 
		    	  contains = false;
		    	  break;
		      }
		    }
		    
		    if (contains) {
		      nodesatloc++;
		    }
		    tseg = node2.getSegment();
		    contains = false;
		    while (tseg != null) {
		    	if (loc >= tseg.getBegin()) {
		    		if (loc <= tseg.getEnd()) {
		    			contains = true;
		    			break;
		    		}
		    		
		    		tseg = tseg.getNext();
		      }
		      else { 
		    	  contains = false;
		    	  break;
		      }
		    }
		    if (contains) {
		      nodesatloc++;
		    }
		    if (nodesatloc > 1) {
		      sitetemp.nnode--;
		    }
		  }
		  sitetemp = sitetemp.next;
		}

		/* STEP 3 */
		popptr.removeNode(node1);
		popptr.removeNode(node2);
		
		/* STEP 4 */
		popptr.addNode(newnode);

		/* STEP 5 */
		dgLog (COALESCE, gen, node1, node2, newnode, popptr);
	}
	//recombine
	public node[] recombineByIndex(int popIndex,double gen, double loc){
		node newNode1,newNode2,aNode;
		newNode1 = null;
		newNode2 = null;
		node[] returnNodes = null;
		population aPop;
		int nodeIndex,nr;
		/*
		 * 1. Pick a random node to recombine.
		 * 2. Execute recombination.
		 * 3. If recombination produces two nodes (i.e.
		 *    recombination occurs in one of these locations:
		 *        a. in the middle of an "active segment", or
		 *        b. between two "active segments",
		 *    then do following steps, otherwise exit.
		 * 4. Remove old node.
		 * 5. Add two new nodes.
		 * 6. Add new recombination site.
		 * 7. Log it.
		 */
		aPop = this.getPopByIndex(popIndex);
		//step 1
		nodeIndex = (int) (random.randomDouble() * aPop.getMembers().getNumMembers());
		aNode = aPop.getNode(nodeIndex);
		//step 2
		Object[] returnArray= nodeFactory.nodeRecombine(aNode, newNode1, newNode2, gen, loc);
		nr = (int) returnArray[0];//playing fast and loose with casting
		
		//step 3
		if(nr ==2){
			newNode1 = (node) returnArray[1];
			newNode2 = (node) returnArray[2];
			//step 4
			aPop.removeNode(aNode);
			//step 5
			aPop.addNode(newNode1);
			aPop.addNode(newNode2);
			//step 6
			recombSites = addRecombSite2(loc,recombSites);
			//step 7 
			dgLog(RECOMBINE,gen,aNode,newNode1,newNode2,aPop,loc);
			returnNodes = new node[3];
			returnNodes[0] = aNode;
			returnNodes[1] = newNode1;
			returnNodes[2] = newNode2;
			
			return returnNodes;
		}
		else return null;
	}
	// gene conversion
	public  node[] gcByIndex(int popIndex,double gen, double loc, double locend){
		node newNode1,newNode2,aNode;
		newNode1 = null;
		newNode2 = null;
		node[] returnNodes = null;
		population aPop;
		int nodeIndex,nr;
		/*
		 * 1. Pick a random node to geneconvert.
		 * 2. Execute recombination.
		 * 3. If recombination produces two nodes (i.e.
		 *    the location of gene conversion overlaps an
		 *    active region)
		 *    then do following steps, otherwise exit.
		 * 4. Remove old node.
		 * 5. Add two new nodes.
		 * 6. Add two new recombination sites.
		 * 7. Log it.
		 */
		aPop = this.getPopByIndex(popIndex);
		//step 1
		nodeIndex = (int) random.randomDouble() * aPop.getMembers().getNumMembers();
		aNode = aPop.getNode(nodeIndex);
		//step 2
		Object[] result = nodeFactory.nodeGC(aNode, newNode1, newNode2, gen, loc, locend);
		nr = (int) result[0];
		//step 3 
		if(nr ==2){
			newNode1 = (node) result[1];
			newNode2 = (node) result[2];
			//step 4
			aPop.removeNode(aNode);
			//step 5
			aPop.addNode(newNode1);
			aPop.addNode(newNode2);
			
			//step 6
			if (segFactory.segContains(aNode.getSegment(), loc))
				recombSites = addRecombSite2(loc,recombSites);
			if(segFactory.segContains(aNode.getSegment(), locend))
				recombSites = addRecombSite2(locend,recombSites);
			//step 7 
			dgLog(GENE_CONVERSION,gen,aNode,newNode1,newNode2,aPop,loc,locend);
			returnNodes = new node[3];
			returnNodes[0] = aNode;
			returnNodes[1] = newNode1;
			returnNodes[2] = newNode2;
			
			return returnNodes;
		}
		else return null;
	}
	//migrate
	public void migrateOneChrom(int fromPop,int toPop, double gen){
		population popFrom, popTo;
		node tempNode;
		int nodeIndex;
		popFrom = this.getPopByNameInt(fromPop,pops);
		popTo = this.getPopByNameInt(toPop,pops);
		nodeIndex = (int) (random.randomDouble()*popFrom.getNumNodes());
		tempNode = popFrom.getNode(nodeIndex);
		popFrom.removeNode(tempNode);
		popTo.addNode(tempNode);
		dgLog(MOVE,gen,tempNode,popFrom,popTo);
	}
	/* MOVING NODES */
	/* dg_move_nodes_by_name is called from functions that implement 
	 * admixing and spliting, to move a bunch of nodes from one 
	 * population to another. The number moved is binomially distributed.
	 */
	public void moveNodesByName(int fromPop,int toPop,double members,double gen){
		population popFrom,popTo;
		node tempNode;
		int numToMove,nodeIndex;
		popFrom = getPopByNameInt(fromPop,pops);
		popTo = getPopByNameInt(toPop,pops);
		numToMove = randBinom.ranbinom(popFrom.getNumNodes(), members);
		for(int i=0;i<numToMove;i++){
			nodeIndex = (int) (random.randomDouble() * popFrom.getNumNodes());
			tempNode = popFrom.getNode(nodeIndex);
			popFrom.removeNode(tempNode);
			popTo.addNode(tempNode);
			dgLog(MOVE,gen,tempNode,popFrom,popTo);
		}
	}
	// node functions
	public int getNumNodes(){//dg_get_num_nodes
		int total = 0;
		for(int i = 0; i<numPops; i++)
			{total = getNumNodesInPopByIndex(i);}//should be plus = ?
		return total;
	}
	public int getNumNodesInPopByIndex(int popIndex){
		population aPop = getPopByIndex(popIndex);
		if(aPop == null)return -1;
		
		return aPop.getMembers().getNumMembers();
	}
	public int getNumNodesInPopByName(int popName){
		population aPop = getPopByNameInt(popName,pops);
			if(aPop == null) return -1;
			
			return aPop.getMembers().getNumMembers();
		
	}
	public boolean doneCoalescent2(){
		
		   siteList  sitetemp = recombSites;
		  int     nonemptypopcount = 0, i;
		  boolean contains;
		  population     popptr=null, temppopptr;
		  node    nodeptr, tempnodeptr=null;
		  double loc;
		  seg tseg;

			/*
			 * so i don't want to delete the pops right away, but i don't
			 * want to count empty pops either. so we count non-empty pops
			 * instead of deleting them. If there is more than one non-empty
			 * pop, we know we're not done.
			*/

		 	for (i = this.getNumPops() - 1; i >= 0; i--) {
				temppopptr = this.getPopByIndex(i);
				if ( temppopptr.getNumNodes() > 0) {  
					nonemptypopcount++;
					popptr = temppopptr;
				}
			}

			if (nonemptypopcount > 1) return false;

			/* 
			 * Now check each member of the last population and move
			 * out all the regions that have coalesced. 
			 * The regions moved out go into a "completepop" where they
			 * do not participate in further simulation and await mutation.
			 * If the entire chromosome is coalesced, we're done.
			 */

			if (popptr.getMembers().getNumMembers() > 1) {     
			    //printf("done_coal num in pop left: %d \n",popptr->members.nummembers);
			  while (sitetemp.getNext() != null) {
			    if (sitetemp.getNNode() == 1) {
			      loc = (sitetemp.getSite() + sitetemp.getNext().getSite()) / 2;
			      for (i = 0; i < popptr.getMembers().getNumMembers(); i++) {
			    	  /* Unoptimized:*/
			    	  nodeptr = popptr.getNode(i);
			    	  /* Optimized, but fragile: */
			    	  //nodeptr = popptr.getMembers().getNode(popptr.getMembers().getNumMembers() - i - 1); 
			    	  /* Start hardwired optimization (from seg_contains(), segment.c) */
			    	  tseg = nodeptr.getSegment();
			    	  contains = false;
			    	  while (tseg != null) {
			    		  if (loc >= tseg.getBegin()) {
			    			  if (loc <= tseg.getEnd()) {
			    				  contains = true;
			    				  tempnodeptr = nodeptr;
			    				  break;
			    			  }
			    			  tseg = tseg.getNext();
			    		  }
			    		  else { 
			    			  contains = false;
			    			  break;
			    		  }
			    	  }
			    	  /* end optimization */
			    	  assert(contains = true);
			      }
			      this.addToCompletePopulation(tempnodeptr, 
						     popptr, 
						     sitetemp.getSite(), 
						     sitetemp.getNext().getSite());
			      sitetemp.setNNode(0);
			    }
			    sitetemp = sitetemp.getNext();
			  }
			}

			if (popptr.getMembers().getNumMembers() < 2) {
				tempnodeptr = popptr.getMembers().getNode(0);
				//fprintf(stdout,"did %d recombsites", demography_data.numsites);
				
				completePop.addNode(tempnodeptr);
				popptr.removeNode(tempnodeptr);
				return true;
			}

			return false;
		}


	
	public boolean doneCoalescent(){
		siteList siteTemp = recombSites;
		int nonEmptyPopCount =0;
		boolean contains;
		population aPop, tempPop;
		aPop = null;
		node aNode, tempNode;
		tempNode = null;
		double loc;
		seg tSeg;
		/*
		 * so i don't want to delete the pops right away, but i don't
		 * want to count empty pops either. so we count non-empty pops
		 * instead of deleting them. If there is more than one non-empty
		 * pop, we know we're not done.
		*/
		for(int i=numPops-1;i>=0;i--){
			tempPop = getPopByIndex(i);
			if(tempPop.getNumNodes() > 0){
				nonEmptyPopCount++;
				aPop=tempPop;
			}
		}
		//System.out.println(aPop.getNumNodes());
		if(nonEmptyPopCount>1)return false;
		/* 
		 * Now check each member of the last population and move
		 * out all the regions that have coalesced. 
		 * The regions moved out go into a "completepop" where they
		 * do not participate in further simulation and await mutation.
		 * If the entire chromosome is coalesced, we're done.
		 */
		
		if(aPop.getMembers().getNumMembers() > 1){
			while (siteTemp.getNext() != null){
				if(siteTemp.getNNode() == 1){
					loc = (siteTemp.getSite() + siteTemp.getNext().getSite()) / 2;
					for(int i = 0; i<aPop.getMembers().getNumMembers(); i++){
						/* Unoptimized:*/
						/* nodeptr = pop_get_node (i, &(popptr->members)); */
						aNode = aPop.getNode(i);
						/* Optimized, but fragile: */
						//aNode = aPop.getMembers().getNode(aPop.getMembers().getNumMembers()-i-1);
						/*//Start hardwired optimization (from seg_contains(), segment.c) */
						tSeg = aNode.getSegment();
						contains = false;
						while(tSeg!=null){
							if(loc>=tSeg.getBegin()){
								if(loc<=tSeg.getEnd()){
									contains = true;
									tempNode = aNode;
									break;
								}
								tSeg = tSeg.getNext();
							}
							else{
								contains = false;
								break;
							}
						}
						assert(contains);
						
						
							
						
					}
					this.addToCompletePopulation(tempNode,aPop,siteTemp.getSite(),siteTemp.getNext().getSite());
					siteTemp.setNNode(0);
				}
				siteTemp = siteTemp.getNext();
			}
		}
		if(aPop.getMembers().getNumMembers() <2){
			tempNode = aPop.getMembers().getNode(0);
			completePop.addNode(tempNode);
			aPop.removeNode(tempNode);
			if(DEMOG_DEBUG){
				System.out.println("the following "+ completePop.getMembers().getNumMembers() + " are left: ");
				for(int i = 0; i<completePop.getMembers().getNumMembers();i++){
					completePop.getNode(i).printNode();
				}
			}
			return true;
		}
		return false;
		
	}
	/////LOGGING 
	/* The dg_log function can take a variable number of arguments,
	 * depending on what we are logging. The only external call of this
	 * function occurs in historical.c, where it is needed to log the
	 * historical events that occur. All other calls are from within
	 * demography.c.
	 */
	public void dgLog (int type,double gen, Object ... args){
		if(!logFileSet) return;
		File outPutFile = logFile;
		population aPop,aPop2;
		node aNode,aNode2,aNode3;
		double double1,loc,loc2;
		String string1,out;
		try{
		FileWriter outFile = new FileWriter(logFile,true);
		
		switch(type){
		case ADD_NODE:
			aNode = (node) args[0];
			aPop = (population) args[1];
			if(outPutFile != null){
				 
				out = "" + gen +  "\t Add \t node:" + aNode.getName() + " pop: " + aPop.getPopName() + "\n";
				//write to file
				outFile.write(out);
				outFile.close();
			}
			break;
		case CHANGE_SIZE:
			// CHANGE_SIZE population
			aPop = (population) args[0];
			
			if(outPutFile != null){
				out = "" + gen + "\t" + "CHANGE_SIZE" + " \t pop: " + aPop.getPopName() + "\t size: "+ aPop.getPopSize() + "\n";
				outFile.write(out);
				outFile.close();
			}
			break;
		
		case COALESCE:
			// COALESCE oldNode oldNode2 newNode pop 
			aNode = (node) args[0];
			aNode2 = (node) args[1];
			aNode3 = (node) args[2];
			aPop = (population) args[3];
			if(outPutFile != null){
				out = "" + gen + "\t C \t" + aNode.getName() + " " + aNode2.getName() + " -> " + aNode3.getName() + " pop: " + aPop.getPopName() + "\n";
				//write to file 
				outFile.write(out);
				outFile.close();
			}
			break;
		
		case CREATE_POP:
			//CREATE_POP population
			aPop = (population) args[0];
			if(outPutFile != null){
				out = "" + gen + "\tCreate_pop\tpop:" + aPop.getPopName() + "size: " + aPop.getPopSize() + "\n";
				// write to file
				outFile.write(out);
				outFile.close();
				
			}
			break;
		case DONE:
			// DONE oldNode doneNode newNode 
			aNode = (node) args[0];
			aNode2 = (node) args[1];
			aNode3 = (node) args[2];
			if(aNode3 !=null){			
				if(outPutFile !=null){
				out = "" + gen + "\tD\t" + aNode.getName() + " -> " + aNode3.getName() + " | " + aNode2.getName() + " " + aNode2.getSegment().getBegin() + " " + aNode2.getSegment().getEnd() + "\n";
				//write to file
				//System.out.println(out);
				outFile.write(out);
				outFile.close();
				break;
				}
			}
			else{
				if (outPutFile!=null){
					out = "" + gen + "\tD\t"+aNode.getName() + "\n";
					outFile.write(out);
					outFile.close();
				}
			}
			break;
		case GENE_CONVERSION:
			//GENE_CONVERSION gen node newNode1 newNode2 aPop loc locend
			aNode = (node) args[0];
			aNode2 = (node) args[1];
			aNode3 = (node) args[2];
			aPop = (population) args[3];
			loc = (Double) args[4];
			loc2 = (Double) args[5];
			if(aNode3 != null){
				out = "" + gen + "\tG\t" + aNode.getName() + " -> " + aNode2.getName() + " " +aNode3.getName() + " " + aPop.getPopName() + " " + loc + " " + loc2 + "\n";
				//write out
				outFile.write(out);
				outFile.close();
			}
			else{
				out = "" + gen + "\tG\t" + aNode.getName() + " -> " + aNode2.getName() + " - " + aPop.getPopName() + " " + loc + " " + loc2 + " " ;
				outFile.write(out);
				outFile.close();
				//write out
			}
			break;
		case HISTORICAL:
			// HISTORICAL string_descriptions
			string1 = "";
			char[] anArr = (char[])args[0];
			for(int i = 0;i<anArr.length;i++){
				string1 = string1 + anArr[i];
			}
			
			out = " " + gen + "\tH\t" + string1 + "\n";
			outFile.write(out);
			outFile.close();
			break;
		case MIG_RATE:
			/* MIG_RATE from-pop to-pop new_rate */
			/* note that "from" and "to" are in real time.
			 * chromosomes will move in the opposite direction */
			aPop = (population) args[0];
			aPop2 = (population) args[1];
			double1 = (Double) args[2];
			
			out = "" + gen + "\tmig_rate\t" + aPop.getPopName() + " " + aPop2.getPopName() + " " + double1 + "\n";
			//write out 
			outFile.write(out);
			outFile.close();
			break;
		case MOVE:
			aNode = (node) args[0];
			aPop = (population) args[1];
			aPop2 = (population) args[2];
			outFile.write(String.format("%f\tM\t%d %d %d\n",gen,aNode.getName(),aPop.getPopName(),aPop2.getPopName()));
			outFile.close();
			break;
		case RECOMBINE:
			//Recombine oldNode1 oldNode2 newNode pop 
			aNode = (node) args[0];
			aNode2 = (node) args[1];
			aNode3 = (node) args[2];
			aPop = (population) args[3];
			loc = (Double) args[4];
			if(aNode3 != null){
				out = "" + gen + "\tR\t" + aNode.getName() + " -> " + aNode2.getName() + " " + aNode3.getName() + " " + aPop.getPopName() + " " + loc + "\n";
				//write out
				outFile.write(out);
				outFile.close();
			}
			else{
				out = "" + gen + "\tR\t" + aNode.getName() + " -> " + aNode2.getName() + " - " + aPop.getPopName() + " " + loc + "\n";
				//write out
				outFile.write(out);
				outFile.close();
			}
			break;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	///////////////// functions for mutations
	/////////////////////////////////////////
	public int getNumRegs(){
		return numSites;
	}
	/*  returns the length of the region indicated by rindex.
	 *  does the exterior program need to know the position of the region?
	 *  perhaps this can be used later to modify mutation rates on different
	 *  parts of the chromosome.
	 */
	public double getRegLength(int rindex1){//regLength should possible be an array.
		int i = 0;
		siteList tempSites = recombSites;
		if(rindex1> numSites){
			System.out.println("rindex too long");
			return 0;
			
		}
		while(i<rindex1 && tempSites !=null){
			tempSites = tempSites.getNext();
			i++;
		}
		if(tempSites != null && tempSites.getNext() != null)
			return tempSites.getNext().getSite() - tempSites.getSite();
		else{
			System.out.println("getRegLength: something went wrong");
			return 0;
		}
	}
	
	public double regBegin(int rindex1){
		int i = 0;
		siteList tempSites = recombSites;
		if(rindex1 > numSites){
			System.out.println("regBegin: rindex too long");
			return 0;
		}
		while( i< rindex1 && tempSites != null){
			tempSites = tempSites.getNext();
			i++;
		}
		if(tempSites != null)
			return tempSites.getSite();
		else{
			System.out.println("regBegin: something went wrong");
			return 0;
		}
	}
	public void addPop( population newPop,double gen ,popList aPopList){
		
		popList tempPopList;
		tempPopList = new popList();
		tempPopList.setPop(newPop);
		tempPopList.setNext(aPopList);
		aPopList = tempPopList;//not sure this may need to return aPopList....
		
		pops = aPopList;
		numPops ++;
		dgLog(CREATE_POP , gen , newPop );
	}
	
	
	
	/////Population stuff/////
	
	public population getPopByName(int popName){
		return this.getPopByNameInt(popName,pops);
	}
	public population getPopByNameInt(int index1, popList aPopList){
		popList temp = aPopList;
		while(temp!=null){
			if(temp.getPop().getPopName() == index1)
				return temp.getPop();
			temp = temp.getNext();
		}
		return null;
	}
	public int getPopNameByIndex(int popIndex){
		return this.getPopByIndex(popIndex).getPopName();
	}
	
	public int getPopIndexByName(int popName){
		popList temp = pops;
		int i =0;
		while (temp!=null){
			if (temp.getPop().getPopName() == popName){
				return i;
			}
			i++;
			temp = temp.getNext();
		}
		return -1;
	}
	public population getPopByIndex(int index1){
		return this.getPopFromList(index1,pops);
	}
	
	public population getPopFromList(int index1 , popList aPopList){
		if(aPopList == null){
			return null;
		}
		else if (index1 == 0){
			return aPopList.getPop();
		}
		else return this.getPopFromList(index1-1,aPopList.getNext());
	}
	
		
	
	public node getHeadNode(int regionNum){
		population pop;
		//segWorker segFactory = new segWorker();
		node aNode;
		double loc;
		
		//since there is only one population;
		pop = completePop;
		loc = this.regCenter(regionNum);
		for(int i=0;i<pop.getMembers().getNumMembers();i++){
			aNode = pop.getNode(i);
				if(segFactory.segContains(aNode.getSegment(), loc)){
					return this.reCurseHeadNode(aNode,loc);
				}
		}
		
		System.out.println("head node not found");
		return null;
	}
	public node reCurseHeadNode(node aNode, double loc) {
		
		boolean test0,test1;
		if(aNode.getDescendents()[0]==null)
			return aNode;
		else if (aNode.getDescendents()[1] == null)
			return reCurseHeadNode(aNode.getDescendents()[0],loc);
		
		else{
			test0 = segFactory.segContains(aNode.getDescendents()[0].getSegment(),loc);
			test1 = segFactory.segContains(aNode.getDescendents()[1].getSegment(), loc);
			if(test0 == true && test1 == false)
				return reCurseHeadNode(aNode.getDescendents()[0],loc);
			else if (test0 == false && test1 == true)
				return reCurseHeadNode(aNode.getDescendents()[1],loc);
			else
				return aNode;
		}
		
	}
	public double regCenter(int rindex1) {
		int i = 0;
		siteList tempSites = recombSites;
		if(rindex1 > numSites){
			System.out.println("regCenter: rindex too long");
			return 0;
		}
		while(i<rindex1 && tempSites != null){
			tempSites = tempSites.getNext();
			i++;
		}
		if(tempSites != null && tempSites.getNext()!=null)
			return (tempSites.getNext().getSite() + tempSites.getSite())/2;
		else{
			System.out.println("regCenter: something went wrong");
			return 0;
		}
		
	}
	public double  totalTreeTime(int rindex1){
		double point = this.regCenter(rindex1);
		node tempNode = this.getHeadNode(rindex1);
		//double totalTime = 0;
		/*totalTime += this.calcTimeInBranch(point,tempNode.getGen(),tempNode.getDescendents()[0]);
		totalTime += this.calcTimeInBranch(point,tempNode.getGen(),tempNode.getDescendents()[1]);
		*/
		TreeTime treeL = new TreeTime(point,tempNode.getGen(),tempNode.getDescendents()[0],segFactory);
		TreeTime treeR = new TreeTime(point,tempNode.getGen(),tempNode.getDescendents()[1],segFactory);
		return CoalescentMain.pool.invoke(treeL) + CoalescentMain.pool.invoke(treeR);
		
		
	}
	public double[] getRegLengthArray(){
		double[] regArray = new double[getNumRegs()];
		siteList tempList = recombSites;
		int i = 0;
		while(tempList.getNext()!=null){
			regArray[i++]=tempList.getNext().getSite() - tempList.getSite();
			tempList = tempList.getNext();
		}
		return regArray;
	}
	public double calcTimeInBranch(double point, double parentTime , node aNode){ 
		double time;
		if(aNode == null)
			return 0;
		if(!segFactory.segContains(aNode.getSegment(), point))
			return 0;
		
		time = parentTime - aNode.getGen();
		time += calcTimeInBranch(point,aNode.getGen(),aNode.getDescendents()[0]);
		time += calcTimeInBranch(point,aNode.getGen(),aNode.getDescendents()[1]);
		return time;
	}
	public void setRecombArray(){
		//only call after the simulation is finished......
		double[] temp = new double[numSites+1];
		siteList tempList = recombSites;
		int i = 0;
		while(tempList!=null){
			temp[i++]=tempList.getSite();
			tempList = tempList.getNext();
		}
		recombArray = temp;
		
	}
	public double[] getRecombArray(){
		return recombArray;
	}
	public popList getPopList(){
		return pops;
	}
}
