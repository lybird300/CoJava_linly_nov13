package org.renci.populationEvents;

import org.renci.Random.randomNum;
import org.renci.populationStructures.demography;

public class migrationWorker {
	migrateRate migrations;
	double lastRate;
	demography dem;
	randomNum random;
	
	public migrationWorker(demography adem, randomNum aRNG){
		lastRate = 0 ;
		migrations = null;
		dem = adem;
		random = aRNG;
	}
	public void migrateAdd(int from, int to, double rate) 
	{
		  migrateRate newmigrate = new migrateRate();
		  
		  
		  newmigrate.fromPop = from;
		  newmigrate.toPop = to;
		  newmigrate.rate = rate;
		  newmigrate.next = migrations;
		  
		  migrations = newmigrate;
	}
	public migrateRate getMigrations(){
		return migrations;
	}
	public void migrateDelete(int from, int to){
		migrateRate tempmigrate = migrations;
		migrateRate delmemigrate = null;
		boolean done = false;
		
		if (tempmigrate == null) return;
		  
		if (tempmigrate.getFromPop() == from && tempmigrate.getToPop() == to) {
			migrations = migrations.next;
		    done = true;
		}
		while (tempmigrate != null && tempmigrate.next != null && !done) {
		
			if (tempmigrate.next.fromPop == from && tempmigrate.next.toPop == to) {
				delmemigrate = tempmigrate.next;
				tempmigrate.next = tempmigrate.next.next;
				delmemigrate = null;
				done = true;
		    }
		    tempmigrate = tempmigrate.next;
		}
	}
	public double migrateGetRate(){
		int numnodes, numnodes1;
		  migrateRate tempmigrate = migrations;
		  double rate = 0;
		  
		  if (migrations == null)
		    lastRate = 0;
		  
		  else {
			  
			  while (tempmigrate != null) {
				  /* check if the pops exist. */
				  numnodes = dem.getNumNodesInPopByName(tempmigrate.fromPop);
				  /* change this into an existance check. */
				  numnodes1 = dem.getNumNodesInPopByName(tempmigrate.toPop);
				  /* numnodes = -1 when the pop does not exist. */
				  if (numnodes < 0 || numnodes1 < 0) {
					  migrateDelete (tempmigrate.fromPop,tempmigrate.toPop);
					  return migrateGetRate();
				  }
				  else {
					  rate += numnodes * tempmigrate.rate;
					  tempmigrate = tempmigrate.next;
				  }
			  }
		    
		    lastRate = rate;
		  }
		  
		  return lastRate;
	}
	public void migrateExecute(double gen){
		 int numnodes;
		  migrateRate tempmigrate = migrations;
		  double rate = 0;
		  double randcounter = random.randomDouble() * lastRate;
		  
		  
		  if (migrations == null)
		    System.out.print("ERROR in migrate.\n");
		  
		  else {
		    
		    while (tempmigrate != null && rate < randcounter) {
		      numnodes = dem.getNumNodesInPopByName(tempmigrate.fromPop);
		      rate += numnodes * tempmigrate.rate;
		      if (rate < randcounter)
			tempmigrate = tempmigrate.next;
		      else
			dem.migrateOneChrom(tempmigrate.fromPop,
					      tempmigrate.toPop, gen);
		      
		    }
		  }
	}
}
