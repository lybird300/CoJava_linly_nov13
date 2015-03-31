package org.renci.populationStructures;


public class nodeWorker {
	int nodeIndex;
	segWorker segFactory;
	
	public nodeWorker(segWorker aSegFactory){
		nodeIndex = 0;
		segFactory = aSegFactory;
	}
	public node makeNewNode(int begin,int end,double gen,int pop){
		
		node aNode = new node(begin, end, gen, pop, nodeIndex);
		nodeIndex++;
		return aNode;
	}
	
	public node makeEmptyNode(double gen,int pop,node des1){
		node aNode = new node( gen, pop,nodeIndex);
		nodeIndex++;
		aNode.descendent[0] = des1;
		return aNode;
	}
	public node makeEmptyNode2(double gen, int pop, node des1,node des2){
		node aNode = new node(gen, pop, nodeIndex);
		nodeIndex++;
		aNode.descendent[0] = des1;
		aNode.descendent[1] = des2;
		return aNode;
	}
	public node nodeCoalesce(node aNode1,node aNode2,double gen){
		node newNode = makeEmptyNode2(gen,aNode1.getPop(),aNode1,aNode2);
		newNode.setSegment(segFactory.segUnion(aNode1.getSegment(), aNode2.getSegment()));
		return newNode;
	}
	//this should probably return the nodes instead?
	public Object[] nodeRecombine(node aNode, node newNode1, node newNode2,
			double gen, double loc){
		seg aSeg,seg1,seg2;
		Object[] returnArray; //container for nodes to return....
		aSeg = new seg(0,loc);
		seg1 = segFactory.segIntersect(aNode.getSegment(), aSeg);
		seg2 = segFactory.segIntersect(aNode.getSegment(),segFactory.segInverse(aSeg));
		aSeg = null;
		if(seg1 == null || seg2 == null){
			if(seg1 != null )
				seg1 = null;
			if(seg2 != null)
				seg2 = null;
			returnArray = new Object[1];
			returnArray[0] = 1;
			return returnArray;
		}
		returnArray = new Object[3];
		newNode1 = makeEmptyNode(gen,aNode.pop, aNode);
		newNode1.setSegment(seg1);
		newNode2 = makeEmptyNode(gen,aNode.pop,aNode);
		newNode2.setSegment(seg2);
		returnArray[0] = 2;
		returnArray[1] = newNode1;
		returnArray[2] = newNode2;
		
		return returnArray;
	}
	// this should probably also return the nodes instead? //also basically same function as above.
	// gene conversion //
	public Object[] nodeGC(node aNode, node newNode1, node newNode2, double gen,
			double loc, double locend){
		seg aSeg,seg1,seg2;
		Object[] returnArray;
		aSeg = new seg(loc,locend);
		seg1 = segFactory.segIntersect(aNode.getSegment(), aSeg);
		seg2 = segFactory.segIntersect(aNode.getSegment(), segFactory.segInverse(aSeg));
		aSeg = null;
		if( seg1 ==null || seg2 == null ){
			if(seg1!=null)
				seg1 =null;
			if(seg2!=null)
				seg2 = null;
			returnArray = new Object[1];
			returnArray[0] = 1;
			return returnArray;
		}
		returnArray = new Object[3];
		newNode1 = makeEmptyNode(gen,aNode.getPop(),aNode);
		newNode1.setSegment(seg1);
		newNode2 = makeEmptyNode(gen,aNode.getPop(),aNode);
		newNode2.setSegment(seg2);
		returnArray[0]=2;
		returnArray[1]=newNode1;
		returnArray[2]=newNode2;
		
		return returnArray;
	}
	
	public Object[] nodeBreakOffSeg(node aNode, node newNode1, node newNode2,
			double begin, double end){
		Object[] returnArray;
		double gen = aNode.getGen();
		seg aSeg, seg1,seg2;
		aSeg = new seg(begin,end);
		seg1 = segFactory.segIntersect(aNode.getSegment(), aSeg);
		seg2 = segFactory.segIntersect(aNode.getSegment(), segFactory.segInverse(aSeg));
		aSeg = null;
		
		if(seg1 == null || seg2 == null ){
			if(seg1 != null)
				seg1 = null;
			if(seg2 != null)
				seg2 = null;
			returnArray = new Object[1];
			returnArray[0] = 1;
			return returnArray;
		}
		returnArray = new Object[3];
		returnArray[0]=2;
		newNode1 = makeEmptyNode(gen,aNode.getPop(),aNode);
		newNode1.setSegment(seg1);
		newNode2 = makeEmptyNode(gen,aNode.getPop(),aNode);
		newNode2.setSegment(seg2);
		returnArray[1] = newNode1;
		returnArray[2] = newNode2;
		return returnArray;
		
	}
	public void nodeDelete(node aNode){
		if(aNode != null){
			nodeDelete(aNode.descendent[0]);
			nodeDelete(aNode.descendent[1]);
			aNode = null;
		}
	}

}
