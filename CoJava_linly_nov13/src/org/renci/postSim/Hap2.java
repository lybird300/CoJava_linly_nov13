package org.renci.postSim;

import java.util.ArrayList;

import org.renci.populationStructures.demography;
import org.renci.rareEvents.Inversion;

public class Hap2 {
	
	private class popMap{
		int totPop;
		int totSampleSize;
		int[] ind2name;
		int[] ind2size;
		popMap(int atotPop){
			totPop = atotPop;
			ind2name = new int[totPop];
			ind2size = new int[totPop];
		}
		void setInd2Name(int index, int name){
			ind2name[index]=name;
		}
		void setInd2size(int index, int size){
			ind2size[index]=size;
		}
		int getPopName(int index){
			return ind2name[index];
		}
		int getPopSize(int index){
			return ind2size[index];
		}
	}
	demography dem;
	popMap aPopMap;
	byte[][] hapData;
	double[] snpPos;
	ArrayList<Inversion> inversions;
	public Hap2(demography adem){
		dem = adem;
		aPopMap = new popMap(dem.getNumPops());	
		hapData = null;
		snpPos = null;
		inversions = null;
		setPopMap();
		
	}
	private void setPopMap(){
		for(int i=dem.getNumPops()-1;i>=0;i--){
			int index = dem.getNumPops()-1-i;
			aPopMap.setInd2Name(index,dem.getPopNameByIndex(i));
			aPopMap.setInd2size(index, dem.getNumNodesInPopByIndex(i));
			}
		for(int i=0;i<aPopMap.totPop;i++){
			aPopMap.totSampleSize += aPopMap.getPopSize(i);
		}
	}
	public void setHapData(byte[][] aHapData){
		hapData = aHapData;
	}
	public void setPosSnp(double[] aSnpPos){
		snpPos = aSnpPos;
	}
	public void setInversions(ArrayList<Inversion> allInversions){
		inversions = allInversions;		
	}
	public ArrayList<Inversion> getInversionList(){
		return inversions;
	}
	public byte[][] getHapData(){
		return hapData;
	}
	public double[] getSnpPos(){
		return snpPos;
	}
	public int getTotalSampleSize(){
		return aPopMap.totSampleSize;
	}
	public int getTotPop(){
		return aPopMap.totPop;
	}
	public int getSampleSize4Pop(int index){
		return aPopMap.getPopSize(index);
	}
	public int getPopNamebyInd(int index){
		return aPopMap.getPopName(index);
	}
	public int[] getPopNameArray(){
		return aPopMap.ind2name;
	}
	
}
