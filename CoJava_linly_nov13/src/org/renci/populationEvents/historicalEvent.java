package org.renci.populationEvents;

public class historicalEvent {
	double gen;
	double[] params;
	int type;
	int[] popIndex;
	char[] label;
	historicalEvent next;
	public historicalEvent(){
		
	}
	public double getGen() {
		return gen;
	}
	public void setGen(double gen) {
		this.gen = gen;
	}
	public double[] getParams() {
		return params;
	}
	public void setParams(double[] params) {
		this.params = params;
	}
	public void setParams(int index,double data){
		params[index] = data;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int[] getPopIndex() {
		return popIndex;
	}
	public void setPopIndex(int[] popIndex) {
		this.popIndex = popIndex;
	}
	public void setPopIndex(int index,int data){
		popIndex[index] = data;
	}
	public char[] getLabel() {
		return label;
	}
	public void setLabel(char[] label) {
		this.label = label;
	}
	public historicalEvent getNext() {
		return next;
	}
	public void setNext(historicalEvent next) {
		this.next = next;
	}
}