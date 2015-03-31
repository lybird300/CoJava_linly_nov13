package org.renci.postSim;

public class hap {
	int nPop,nChrom;
	int[] popId,whichPopInd,mutArraySize,nMut;
	int[][] mutIndex;
	public hap(){
		
	}
	public void setnChrom(int data){
		this.nChrom = data;
	}
	public int getNChrom(){
		return nChrom;
	}
	public int getnPop() {
		return nPop;
	}
	public void setnPop(int nPop) {
		this.nPop = nPop;
	}
	public int[] getnMut() {
		return nMut;
	}
	public void setnMut(int[] nMut) {
		this.nMut = nMut;
	}
	public void setnMut(int index,int data){
		this.nMut[index]=data;
	}
	public int[] getMutArraySize() {
		return mutArraySize;
	}
	public void setMutArraySize(int index,int data){
		this.mutArraySize[index] = data;
	}
	public void setMutArraySize(int[] mutArraySize) {
		this.mutArraySize = mutArraySize;
	}
	public int[] getWhichPopInd() {
		return whichPopInd;
	}
	public void setWhichPopInd(int index,int data){
		this.whichPopInd[index] = data;
	}
	public void setWhichPopInd(int[] whichPopInd) {
		this.whichPopInd = whichPopInd;
	}
	public void setPopId(int index,int data){
		this.popId[index] = data;
	}
	public int[] getPopId() {
		return popId;
	}
	public void setPopId(int[] pipId) {
		this.popId = pipId;
	}
	public void setMutIndex(int index1,int index2,int data){
		this.mutIndex[index1][index2] = data;
	}
	public int[][] getMutIndex() {
		return mutIndex;
	}
	public void setMutIndex(int[][] aMutIndex) {
		this.mutIndex = aMutIndex;
	}
	public void setMutIndex(int index, int[] data){
		this.mutIndex[index] = data;
	}
	
}
