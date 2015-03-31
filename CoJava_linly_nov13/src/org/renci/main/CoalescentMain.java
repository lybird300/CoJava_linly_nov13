package org.renci.main;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import org.renci.Random.poisson;
import org.renci.Random.randomNum;
import org.renci.coalSimulator.sim;
import org.renci.geneticEvents.RecombWorker;
import org.renci.geneticEvents.bottleNeck;
import org.renci.geneticEvents.coalesce;
import org.renci.geneticEvents.gc;
import org.renci.geneticEvents.mutList;
import org.renci.geneticEvents.mutations;
import org.renci.geneticEvents.sweep;
import org.renci.populationEvents.histWorker;
import org.renci.populationEvents.migrationWorker;
import org.renci.populationStructures.demography;
import org.renci.populationStructures.nodeWorker;
import org.renci.populationStructures.segWorker;
import org.renci.postSim.DataOut;
import org.renci.postSim.Hap2;
import org.renci.postSim.hap;
import org.renci.postSim.hapWorker;
import org.renci.rareEvents.InversionWorker;
import org.renci.rareEvents.RareEventLocker;

public class CoalescentMain {
	static ArgHandler argHolder;
	static ParamFileParser params;
	static demography dem;
	static mutList aMutList;
	static gc geneConversion;
	//public static mutWorker muts;
	static RecombWorker recomb;
	static sweep sweeper;
	static sim simulator;
	static hap haps;
	static hapWorker hapFactory;
	static histWorker histFactory;
	static coalesce coalesce;
	static migrationWorker migFactory;
	static nodeWorker nodeFactory;
	static mutations mutate;
	static randomNum random;
	static segWorker segFactory;
	static bottleNeck bottleneck;
	static poisson poissoner;
	static Hap2 hapalt;
	static InversionWorker inverter;
	public static ForkJoinPool pool;
	static RareEventLocker rareEvents;
	public static void main(String[] args) throws InterruptedException {
		// get and process arguments
		argHolder = new ArgHandler(args);
		argHolder.setArguments();
		// fork join pool for parallel stuff
		pool = new ForkJoinPool(argHolder.getNumProcs());
		
		// instantiate all the objects that build on each other
		segFactory = new segWorker();
		random = new randomNum();
		haps = new hap();
		poissoner = new poisson(random);
		nodeFactory = new nodeWorker(segFactory);
		dem = new demography(nodeFactory,segFactory,random);
		if(argHolder.logFileSet)dem.setLogFile(argHolder.getLogFile());
		rareEvents = new RareEventLocker(dem,random);
		mutate = new mutations(dem,segFactory);
		recomb = new RecombWorker(dem, random);
		geneConversion = new gc(dem, random);

		migFactory = new migrationWorker(dem,random);
		bottleneck = new bottleNeck(dem,poissoner);
		sweeper = new sweep(dem, recomb, geneConversion, random, poissoner, nodeFactory, segFactory);

		histFactory = new histWorker(dem, migFactory, sweeper, bottleneck);
		coalesce = new coalesce(dem, random);
		simulator = new sim(dem, poissoner, geneConversion, recomb, random, 
				histFactory, coalesce, migFactory, mutate,rareEvents);
		
		//now assign all the  info in our paramater file....
		params = new ParamFileParser(argHolder, dem, recomb, simulator, 
				geneConversion, histFactory, random ,rareEvents);
		params.paramFileProcess();
		rareEvents.composeRareEventWorkers();
		hapalt = new Hap2(dem);
		//sweeper = new sweep(dem, recomb, geneConversion, random, poissoner, nodeFactory, segFactory);
		aMutList = new mutList();//mutlist init
		hapFactory = new hapWorker(haps, dem);
		hapFactory.hapAssignChroms();
		dem.initRecomb();
		sweeper.sweepInitMut(aMutList, haps);
		simulator.simExecute();
		try {
			simulator.simMutate(argHolder.getSegFile(), aMutList, hapalt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataOut outwriter = new DataOut();
		if(argHolder.outFileSet){
			try {
				outwriter.printHaps("out", recomb.getLength(), hapalt, rareEvents);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		}
		
	

}
