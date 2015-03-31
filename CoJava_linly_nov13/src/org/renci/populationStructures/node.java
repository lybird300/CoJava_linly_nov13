package org.renci.populationStructures;



public class node {
	
		int name,pop;
		double gen;
		node[] descendent;
		seg segment;
		segWorker segFactory = new segWorker();
		public node(double begin, double end, double aGen, int aPop,
			int aName){
		descendent = new node[2];
		descendent[0] = null;
		descendent[1] = null;
		segment = null;
		segment = segFactory.segAdd(segment, begin, end);
		gen = aGen;
		pop = aPop;
		name = aName;
		
		}
		public node(double aGen,int aPop,int aName){
			descendent = new node[2];
			descendent[0] = null;
			descendent[1] = null;
			segment = null;
			gen = aGen;
			pop = aPop;
			name = aName;
			
			
		}
		public seg getSegment(){
			return segment;
		}
		public void setSegment(seg aSeg){
			segment = aSeg;
		}
		public node[] getDescendents(){
			return descendent;
		}
		public void setDescendents(int desc, node descendent){
			this.descendent[desc] = descendent; 
		}
		public void setGen(double aGen){
			gen = aGen;
		}
		public double getGen(){
			return gen;
		}
		public int getName(){
			return name;
		}
		public void setName(int aName){
			name = aName;
		}
		public int getPop(){
			return pop;
		}
		public void printNode(){
			System.out.println("node: "+ name + " pop:  " + pop + " gen: " + gen + " ");
			//segs out as well
			System.out.println("desc: ");
			for(int i=0;i<2;i++){
				if(descendent[i]!=null)
				System.out.println(descendent[i].name);
			}
		}
	}

