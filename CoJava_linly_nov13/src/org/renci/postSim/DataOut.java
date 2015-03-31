package org.renci.postSim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.renci.concurrent.parallelWriter;
import org.renci.main.CoalescentMain;
import org.renci.rareEvents.Inversion;
import org.renci.rareEvents.RareEventLocker;
import org.renci.rareEvents.RareEventSorter;
import org.renci.rareEvents.NodeList;

public class DataOut {

	boolean infSites = false;
	double[] sortVals;
	public DataOut(){
		
	}
	
	public void setInfiniteSites(boolean aBol){
		infSites = aBol;
	}
	public void printHaps(String fileBase,int length, Hap2 haplos, RareEventLocker theRareEvents) throws IOException{
		
		
		/*Let us go ahead and handle our rare genetic events
		 * This can be moved elsewhere and probably should be 
		 */
		
		//inversions
		ArrayList<Inversion> inversions = haplos.getInversionList();
		BufferedWriter writer = null;
		int invcnt = 0;
		File outFile = null;
		// Sort inversions so that the oldest is actually first in the list
		Collections.sort(inversions,new RareEventSorter());
		double[][] snpsOut = new double[haplos.getTotalSampleSize()][haplos.getSnpPos().length];
		for (int i=0;i<haplos.getTotalSampleSize();i++){
			snpsOut[i]=haplos.getSnpPos().clone();
		}
		
		try{
			
			for(Inversion tmpInv: inversions){
				outFile = new File("inversion-" + invcnt++ + ".dat" );
				writer = new BufferedWriter(new FileWriter(outFile));
				writer.write("Inversion File\n");
				writer.write("Start\tEnd\tAge\tnodes.....\n");
				int index1 = Arrays.binarySearch(haplos.getSnpPos(), tmpInv.getStartPoint());
				int index2 = Arrays.binarySearch(haplos.getSnpPos(), tmpInv.getEndPoint());
				int invStart = (int)(tmpInv.getStartPoint()*length);
				int invEnd = (int)(tmpInv.getEndPoint()*length);
				writer.write(String.format("%d\t%d\t%d\t",invStart,invEnd,(int)tmpInv.getGen()));
				
				//If the start points are not an exact match they will return neg values of 
				//where the index would be
				if(index1 < 0)
					index1 = Math.abs(index1+1);
				if(index2<0)
					index2 = Math.abs(index2+2);
				NodeList nodes = tmpInv.getNodesAssociated();
				while (nodes!=null){
					int tmpInd1 = index1;
					int tmpInd2 = index2;
					int nodeName = nodes.getNodeName();
					writer.write(nodes.getNodeName()+ " ");
					double[] tmpSnp = snpsOut[nodeName];
					byte[] tmpHap = haplos.getHapData()[nodeName];
					while(tmpInd1<=tmpInd2){ // invert haplotype
						byte tmp = tmpHap[tmpInd2];
						tmpHap[tmpInd2] = tmpHap[tmpInd1];
						tmpHap[tmpInd1] = tmp;
						tmpInd1 ++;
						tmpInd2 --;
					}
					tmpInd1 = index1;
					tmpInd2 = index2;
					while(tmpInd1<=tmpInd2){// invert position
						/*formula x = start point, y = end point, a = original SNP position
						 *  given a line like this 
						 *	0------[-----------------------|--------]----1
						 *		   x					   a		y
						 *	
						 * 	Then to get a' we need a' = y - a + x
						 *  so after applying the formula we have 
						 *   0------[-----|--------------------------]----1
						 *		   	x	  a'                         y
						 */
						double tmp = tmpSnp[tmpInd2];
						tmpSnp[tmpInd2] = tmpInv.getEndPoint() - tmpSnp[tmpInd1] + tmpInv.getStartPoint();
						tmpSnp[tmpInd1] = tmpInv.getEndPoint() - tmp + tmpInv.getStartPoint();
						tmpInd1 ++;
						tmpInd2 --;
					}
					//finished inverting that hap and its pos
					//so we need to add it back to our main list					
					haplos.getHapData()[nodeName] = tmpHap;
					snpsOut[nodeName] = tmpSnp;
					nodes = nodes.getNextNode();
				}
				writer.write("\n");
				writer.close();
			}
		}
		catch(Exception e){
			
		}
	
		
		
		boolean[][] usemut = new boolean[haplos.getTotalSampleSize()][haplos.getSnpPos().length];
		for (int m = 0;m< haplos.getTotalSampleSize();m++){
			for(int k=0;k<haplos.getSnpPos().length;k++){
				usemut[m][k]=true;
			}
		}
		
		if(!infSites){
			//int snpCount = 0;
			for(int k=0;k<haplos.getTotalSampleSize();k++){
				double[] tmpSnp = snpsOut[k];//sample
				int j=0;
				for(int i=0;i<tmpSnp.length-1;i++){//so last j<length...
					j = i+1;
			
					if((int)(tmpSnp[i]* length)==(int)(tmpSnp[j]* length)){
						usemut[k][j]=false;
					}
				}
			}
		}
		//split up the io into the different populations and write them at the same time!!!!
		int[] popBounds = new int[haplos.getTotPop()+1];
		popBounds[0]=0;
		for(int i=0;i<haplos.getTotPop();i++){
			popBounds[i+1]= popBounds[i]+haplos.getSampleSize4Pop(i);
		}
		CoalescentMain.pool.invoke(new parallelWriter(0,haplos.getTotPop(),haplos.getHapData(),snpsOut,
				haplos.getPopNameArray(),popBounds,usemut,length));
			
	}
		
		
		
		
		
		
		
		
		
}
	
