package org.renci.geneticEvents;

import org.renci.Random.poisson;
import org.renci.Random.randomNum;
import org.renci.pointers.doublePointer;
import org.renci.populationStructures.demography;
import org.renci.populationStructures.node;
import org.renci.populationStructures.nodeWorker;
import org.renci.populationStructures.population;
import org.renci.populationStructures.seg;
import org.renci.populationStructures.segWorker;
import org.renci.populationStructures.siteList;
import org.renci.postSim.hap;

public class sweep {
	mutList aMutList;
	hap haplos;
	double coalesceRate,migrateRate,recombRate,geneConvRate,poissonRate;
	demography dem;
	RecombWorker recomb;
	gc geneConversion;
	randomNum random;
	poisson poissoner;
	nodeWorker nodeFactory;
	segWorker segFactory;
	public sweep(demography adem,RecombWorker aRecomb,gc aGeneConverter,randomNum aRNG,poisson aPois,nodeWorker aNodeFactory,segWorker aSegFactory){
		random = aRNG;
		geneConversion = aGeneConverter;
		dem = adem;
		recomb = aRecomb;
		poissoner = aPois;
		nodeFactory = aNodeFactory;
		segFactory = aSegFactory;
	}
	public void sweepInitMut(mutList ml,hap aHap){
		aMutList = ml;
		haplos = aHap;

	}
	public double sweepExecute(int popName,double selCoeff,double gen,double selPos,double finalSelFreq){
		population aPop;
		int nsel,nunsel,newn;
		boolean foundIt;
		node[] selNodes, unSelNodes,recNodes,gcNodes;
		node oldAlleleNode,newAlleleNode;
		double deltaT, selFreq, epsilon, t, x, tEnd, alpha, rate;
		double probCoalUnsel, probCoalSel, probRecomb, allProb;
		double tsave,  endShift=0., tNonsweep, poissonEventTime;
		double probGc; 
		int selSize, unselSize; 
		doublePointer loc = new doublePointer();
		doublePointer loc1,loc2;
		loc1 = new doublePointer();
		loc2 = new doublePointer();
		boolean dorandom=true;
		mutations aMut;
		
		aPop = dem.getPopByName(popName);
		nunsel = nsel = 0;
		selSize = unselSize = aPop.getMembers().getNumMembers();
		selNodes = new node[selSize];
		unSelNodes = new node[unselSize];
		
		epsilon = 1/(2 * aPop.getPopSize());
		if(finalSelFreq > 1-epsilon){
			endShift = 0;
			dorandom = false;
		}
		else {
			endShift = Math.log((1 - finalSelFreq) / (finalSelFreq * epsilon * (1- epsilon)))/selCoeff ;
			
		}
		deltaT = .01 / selCoeff;
		tEnd = gen - 2* Math.log(epsilon)/ selCoeff;
		alpha = 2 * aPop.getPopSize() * selCoeff;
		
		for( int inode = 0; inode<aPop.getMembers().getNumMembers();inode++){
			if(!dorandom || random.randomDouble() < finalSelFreq){
				selNodes[nsel] = aPop.getMembers().getNode(inode);
				nsel++;
			}
			else{
				unSelNodes[nunsel]= aPop.getMembers().getNode(inode);
				nunsel++;
			}
		}
		selFreq = 1;
		tsave = gen;
		rate = swGetPoissonRate(popName);
		poissonEventTime = 1e50;
		if(rate>0){
			poissonEventTime = poissoner.poissonGetNext(rate);
			
		}
		tNonsweep = gen + poissonEventTime;
		
		for(t = gen;selFreq>= epsilon; t+=deltaT){
			while(t>tNonsweep){
			    /* Process event in a nonsweep population, and update time for next nonsweep event */
				swDoPoisson(popName,tNonsweep);
				poissonEventTime = poissoner.poissonGetNext(swGetPoissonRate(popName));
				tNonsweep += poissonEventTime;
			}
			selFreq = epsilon / (epsilon + (1-epsilon)* Math.exp(selCoeff * (t + endShift - tEnd)));
			probCoalSel = deltaT * nsel * (nsel -1) /4 / aPop.getPopSize() / selFreq;
			probCoalUnsel = deltaT * nunsel * (nunsel -1) / 4 / aPop.getPopSize() / (1-selFreq);
			probRecomb = deltaT * (nsel + nunsel) * recomb.recombGetR();
			probGc = deltaT * (nsel + nunsel) * geneConversion.getR();
			allProb = 0;
			allProb += probCoalUnsel + probCoalSel + probRecomb + probGc;
			
			x = random.randomDouble();
			
			if(allProb >= x ){
				if(x < probRecomb/allProb){
				//Recombination. Pic sel/unsel for new chrom piece	
				
					int selPopIndex = dem.getPopIndexByName(popName);
					recNodes = recomb.recombExecute(t, selPopIndex, loc);
					if(recNodes != null){
						//which segment carries old allele?
						if(loc.getDouble() < selPos){
							oldAlleleNode = recNodes[2];
							newAlleleNode = recNodes[1];
							
						}
						else{
							oldAlleleNode = recNodes[1];
							newAlleleNode = recNodes[2];
						}
						
						foundIt = false;
						for(int inode =0; inode<nsel;inode++){
							if(selNodes[inode].getName() == recNodes[0].getName()){
								selNodes[inode] = oldAlleleNode;
								foundIt = true;
								break;
							}
						}
						if(!foundIt){
							for(int inode = 0 ; inode < nunsel; inode ++){
								if(unSelNodes[inode].getName() == recNodes[0].getName()){
									unSelNodes[inode] = oldAlleleNode;
									foundIt = true;
									break;
								}
							}
						}
						if(!foundIt)System.out.println(String.format("Could not find %d\n", recNodes[0].getName()));
						assert(foundIt);
				/* Decide whether the chrom that we've recombined onto is selected or unselected */
						if(random.randomDouble() < selFreq){
							if(nsel ==selSize){
							selSize *=2;// new sized node array of this size 
							node[] tempArr = new node [selSize];
							for(int i=0;i<selNodes.length;i++){
								tempArr[i] = selNodes[i];
							}
							selNodes = null;
							selNodes = tempArr;
							}
							selNodes[nsel] = newAlleleNode;
							nsel++;
							
						}
						else{
							if(nunsel == unselSize){
								unselSize *= 2;
								node[] tempArr = new node[unselSize];
								for(int i=0;i<unSelNodes.length;i++){
									tempArr[i] = unSelNodes[i];
								}
								unSelNodes = null;
								unSelNodes = tempArr;
							}
								unSelNodes[nunsel] = newAlleleNode;
								nunsel++;
								
							
						}
					}
				}
				else if (x<(probRecomb + probCoalUnsel)/allProb){
					newn = this.swCoalesce(nunsel, unSelNodes, aPop, t);
					assert(newn ==nunsel -1);
					nunsel = newn;
				}
				else if(x < (probRecomb + probCoalUnsel + probCoalSel)/allProb){
					//coalescene among selected chroms
					newn = this.swCoalesce(nsel, selNodes, aPop, t);
					assert(newn == nsel -1);
					nsel = newn;
				}
				else{
					//gene conversion pick sel/unsel for new chrom piece
					int selPopIndex = dem.getPopIndexByName(popName);
					gcNodes = geneConversion.execute(t, selPopIndex, loc1, loc2);
					if(gcNodes != null){
						//which segment carries old allele?
						if(loc1.getDouble()<= selPos && loc2.getDouble()>=selPos){
							oldAlleleNode = gcNodes[1];
							newAlleleNode = gcNodes[2];
						}
						else{
							oldAlleleNode = gcNodes[2];
							newAlleleNode = gcNodes[1];
						}
						foundIt = false;
						for(int inode=0;inode<nsel;inode++){
							if(selNodes[inode].getName() == gcNodes[0].getName()){
								selNodes[inode] = oldAlleleNode;
								foundIt = true;
								break;
							}
						}
						if(!foundIt){
							for(int inode =0;inode<nunsel;inode++){
								if(unSelNodes[inode].getName()== gcNodes[0].getName()){
									unSelNodes[inode] = oldAlleleNode;
									foundIt = true;
									break;
								}
							}
						}
						if(!foundIt){System.out.println(String.format("Could not find %d\n", gcNodes[0].getName()));}
						assert(foundIt);
						
						
						
						/* Decide whether the chrom that we've recombined onto is selected or unselected */
						if(random.randomDouble() < selFreq){
							if(nsel == selSize){
								selSize *=2;// new sized node array of this size 
								node[] tempArr = new node [selSize];
								for(int i=0;i<selNodes.length;i++){
									tempArr[i] = selNodes[i];
								}
								selNodes = null;
								selNodes = tempArr;
							}
								selNodes[nsel] = newAlleleNode;
								nsel++;
							
						}
						else{
							if(nunsel == unselSize){
								unselSize *= 2;
								node[] tempArr = new node[unselSize];
								for(int i=0;i<unSelNodes.length;i++){
									tempArr[i] = unSelNodes[i];
								}
								unSelNodes = null;
								unSelNodes = tempArr;
							}
								unSelNodes[nunsel] = newAlleleNode;
								nunsel++;
								
							
						}
					}
				}
				if(aPop.getMembers().getNumMembers() != nsel + nunsel){
					System.out.println(String.format("mismatch: pop(n): %d here: %d\n",aPop.getMembers().getNumMembers(),nsel + nunsel));
				}
			}
			
		}
		aMut = new mutations(dem,segFactory);
		aMut.setLocation(selPos);
		aMut.setMutNodes(null);
		aMut.setAncNode1(null);
		aMut.setAncNode2(null);
		for(int inode=0; inode<nsel;inode++){
			aMut.setMutNodes(aMut.mutateDescendents(selNodes[inode], aMut.getMutNodes(), selPos));
		}
		aMut.mutatePrint(null, aMut, aMutList,haplos );
		System.out.println(String.format("selective sweep ends at %f generations\n",t));
		return t;
	}
	public int swCoalesce(int nnode,node[] nodes, population aPop, double t){
		
		/* The difference between this routine and the one in demography.c is that here 
	     I have to update both the main population node list and the local list that I'm using. */
		int node1Index,node2Index,inode,nodesAtLoc;
		boolean contains;
		node aNode1,aNode2,newNode;
		siteList siteTemp = dem.getRecombSites();
		double loc;
		seg tSeg;
		//step 1
		node1Index = (int)(random.randomDouble() * nnode);
		node2Index = (int)(random.randomDouble() * (nnode -1));
		if(node2Index >= node1Index)node2Index ++;
		aNode1 = nodes[node1Index];
		aNode2 = nodes[node2Index];
		
		//step 2
		newNode = nodeFactory.nodeCoalesce(aNode1, aNode2, t);
		//step 2a
		while(siteTemp.getNext() != null){
			loc = (siteTemp.getSite()+siteTemp.getNext().getSite())/2;
			nodesAtLoc = 0;
			tSeg = aNode1.getSegment();
			contains = false;
			while(tSeg != null){
				if(loc >= tSeg.getBegin()){
					if(loc<= tSeg.getEnd()){
						contains = true;
						break;
					}
					tSeg = tSeg.getNext();
				}
				else {
					contains = false;
					break;
				}
			}
			if(contains){
				nodesAtLoc ++;
			}
			if(nodesAtLoc > 1){
				
				siteTemp.setNNode(siteTemp.getNNode()-1);
			}
			siteTemp = siteTemp.getNext();
		}
		//step 3
		aPop.removeNode(aNode1);
		aPop.removeNode(aNode2);
		if(node1Index < node2Index){
			for(inode=node2Index; inode<nnode -1; inode++){nodes[inode] = nodes[inode +1];}
			nnode --;
			for(inode = node1Index; inode<nnode-1;inode++){nodes[inode] = nodes[inode+1];}
		}
		else{
			for (inode = node1Index; inode < nnode-1; inode++) {nodes[inode] = nodes[inode+1];}
		    nnode--;
		    for (inode = node2Index; inode < nnode-1; inode++) {nodes[inode] = nodes[inode+1];}
		}
		nnode --;
		//step4 
		aPop.addNode(newNode);
		nodes[nnode] = newNode;
		nnode ++;
		
		//step 5
		dem.dgLog(2, t, aNode1,aNode2,newNode,aPop);
		return nnode;
	}
	/* coalesce_get_rate, modified to ignore coalescence in target pop */

	public double swCoalesceGetRate(int selPopName){
		int numPops = dem.getNumPops();
		int i;
		double rate = 0;
		int numNodes;
		int popSize;
		for (i=0;i<numPops;i++){
			int thisPop = dem.getPopNameByIndex(i);
			if(thisPop == selPopName){continue;}
			numNodes = dem.getNumNodesInPopByIndex(i);
			popSize = dem.getPopSizeByIndex(i);
			if(numNodes > 1){
				rate += (double) (numNodes * (numNodes -1))/4*popSize;
			}
		}
		return rate;
	}
	public double swRecombGetRate(int selPopName){
		int numPops = dem.getNumPops();
		int i;
		double rate = 0;
		double r = recomb.recombGetR();
		int numNodes;
		for(i = 0;i<numPops;i++){
			int thisPop = dem.getPopNameByIndex(i);
			if(thisPop == selPopName){continue;}
			numNodes = dem.getNumNodesInPopByIndex(i);
			rate += (numNodes * r);
		}
		return rate;
		
	}
	public double swGCGetRate(int selPopName){
		int numPops = dem.getNumPops();
		int i;
		double rate = 0,r=geneConversion.getR();
		int numNodes;
		for(i=0;i<numPops;i++){
			int thisPop = dem.getPopNameByIndex(i);
			if(thisPop == selPopName){continue;}
			numNodes = dem.getNumNodesInPopByIndex(i);
			rate += (numNodes*r);
			
		}
		return rate;
	}
	public int swCoalescePickPopIndex(int selPopName){
		double randCounter,sumProb,totProb;
		int popIndex = -1,
		numPops = dem.getNumPops(),
		numNodes,popSize,i,thisPop;
		
		totProb = 0;
		for(i=0;i<numPops;i++){
			thisPop = dem.getPopNameByIndex(i);
			if(thisPop == selPopName){continue;}
			numNodes = dem.getNumNodesInPopByIndex(i);
			popSize = dem.getPopSizeByIndex(i);
			totProb += (double)(numNodes * (numNodes =1))/(4*popSize);
			
		}
		randCounter = random.randomDouble() * totProb;
		
		sumProb = 0;
		for(i = 0;i<numPops;i++){
			thisPop = dem.getPopNameByIndex(i);
			if(thisPop == selPopName){continue;}
			numNodes = dem.getNumNodesInPopByIndex(i);
			popSize = dem.getPopSizeByIndex(i);
			sumProb += (double)(numNodes * (numNodes-1))/(4*popSize);
			if(randCounter <= sumProb){
				popIndex = i;
				break;
			}
		}
		assert(popIndex>=0);
		return popIndex;
	}
	
	public int swRecombPickPopIndex(int selPopName){
		int popIndex=-1,i,sumNodes=0,totNodes,numPops = dem.getNumPops();
		double randCounter;
		
		totNodes = 0;
		for (i = 0;i<numPops;i++){
			int thisPop = dem.getPopNameByIndex(i);
			if(thisPop == selPopName){continue;}
			totNodes += dem.getNumNodesInPopByIndex(i);
		}
		randCounter = totNodes * random.randomDouble();
		assert(randCounter <= totNodes);
		
		//weight pops by numNodes
		for(i=0;i<numPops;i++){
			int thisPop = dem.getPopNameByIndex(i);
			if(thisPop == selPopName){continue;}
			sumNodes += dem.getNumNodesInPopByIndex(i);
			if(randCounter <= sumNodes){
				popIndex = i;
				break;
			}
		}
		assert(popIndex>=0);
		return popIndex;
	}
	public double swGetPoissonRate(int popName){
		coalesceRate = this.swCoalesceGetRate(popName);
		geneConvRate = this.swGCGetRate(popName);
		poissonRate = (double)(coalesceRate + migrateRate + recombRate + geneConvRate);
		return poissonRate;
	}
	public void swDoPoisson(int selPopName,double gen){
		double randDouble = random.randomDouble();
		doublePointer dum,dum2;
		dum = new doublePointer();
		dum2 = new doublePointer();
		int recombPop;
		if(randDouble<(recombRate/poissonRate)){
			recombPop = swRecombPickPopIndex(selPopName);
			recomb.recombExecute(gen,recombPop, dum);
			
		}
		else if(randDouble < (recombRate + migrateRate)/poissonRate){
			//migrate execution //
		}
		else if (randDouble <(recombRate + migrateRate + coalesceRate)/poissonRate){
			int popIndex = this.swCoalescePickPopIndex(selPopName);
			dem.coalesceByIndex(popIndex, gen);
		}
		else{
			int popIndex = this.swRecombPickPopIndex(selPopName);
			geneConversion.execute(gen, popIndex, dum2, dum2);
		}
		return;
	}
	
}
