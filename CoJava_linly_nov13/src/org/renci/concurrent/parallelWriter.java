package org.renci.concurrent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.RecursiveAction;

public class parallelWriter extends RecursiveAction {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6142321546560553883L;
	int threshold,hi,lo,length;
	byte[][] mutArray;
	double[][] posSnp;
	int[] ind2Name,popBounds;
	boolean[][] useMut;
	public parallelWriter(int alo,int ahi,byte[][] aMutArray,double[][] aSnpPos,
			int[] aind2Name,int[] aPopBounds,boolean[][] ausemut,int aLength){
		threshold =1;
		mutArray = aMutArray;
		posSnp = aSnpPos;
		useMut = ausemut;
		hi = ahi;
		lo = alo;
		ind2Name = aind2Name;
		popBounds = aPopBounds;
		length = aLength;
	}
	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		if(hi-lo>threshold){
			int mid = lo + (int)(hi -lo)/2;
			parallelWriter lowSide = new parallelWriter(lo,mid,mutArray,posSnp,ind2Name,popBounds,useMut,length);
			lowSide.fork();
			parallelWriter hiSide = new parallelWriter(mid,hi,mutArray,posSnp,ind2Name,popBounds,useMut,length);
			hiSide.compute();
			lowSide.join();
		}
		else{
			int i;
			for(i=lo;i<hi;i++){
				String fileName = "out.hap-";
				fileName = fileName + ind2Name[i];
				File outFile = new File(fileName);
				outFile.delete();
				BufferedWriter writer=null;
				try {
					outFile.createNewFile();
					writer = new BufferedWriter(new FileWriter(fileName),8*1024);
					for(int k=popBounds[i];k<popBounds[i+1];k++){
						writer.write(String.format("%d\t%d\t",k,ind2Name[i]));
						for(int l=0;l<posSnp[k].length;l++){
							if(useMut[k][l]){
								writer.write(String.format("%d ",mutArray[k][l]));
								}
						}
						writer.newLine();
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// now write our position file
				fileName = "out.pos-";
				fileName = fileName + ind2Name[i];
				outFile = new File(fileName);
				outFile.delete();
				try{
					outFile.createNewFile();
					writer = new BufferedWriter(new FileWriter(fileName),8*1024);
					writer.write("Node\tCHROM_POS.....\n");//header
					for(int k=popBounds[i];k<popBounds[i+1];k++){
						writer.write(String.format("%d\t",k));
						for(int l=0;l<posSnp[k].length;l++){
							if(useMut[k][l]){
								writer.write(String.format("%d\t",(int)(posSnp[k][l]*length)));
							}
						}
						writer.newLine();
					}
						writer.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}




