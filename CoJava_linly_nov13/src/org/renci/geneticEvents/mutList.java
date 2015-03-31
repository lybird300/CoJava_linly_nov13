package org.renci.geneticEvents;

public class mutList {
	final int INITSIZE = 500;
	int nMut;
	double[] pos;
	int arraySize;

	public mutList(){
		nMut = 0;
		arraySize = INITSIZE;
		pos = new double[INITSIZE];
		
	}
	public void setNumMut(int numMut){
		nMut = numMut;
	}
	public int getNumMut(){
		return nMut;
	}
	public void setPos(double[] aPos){
		pos = aPos;
	}
	public void setPos(int index,double data){
		pos[index] = data;
	}
	public int getArraySize(){
		return arraySize;
	}
	public void setArraySize(int anArraySize){
		arraySize = anArraySize;
	}
	public int getNMut(){
		return nMut;
	}
	public double[] getPos(){
		return pos;
	}
	
}