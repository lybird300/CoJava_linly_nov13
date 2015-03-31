package org.renci.rareEvents;

import org.renci.populationStructures.node;

public abstract class RareEvent {
		int nodeName;
		double location,gen;
		int region;
		NodeList nodesAssociated;
		node eventNode;
		public RareEvent(node aNode,int aNodeName,double loc,double aGen){
			eventNode = aNode;
			nodeName = aNodeName;
			location = loc;
			gen = aGen;
			
		}
		
		
		public node getEventNode() {
			return eventNode;
		}


		public void setEventNode(node eventNode) {
			this.eventNode = eventNode;
		}


		public int getRegion() {
			return region;
		}



		public void setRegion(int region) {
			this.region = region;
		}



		public NodeList getNodesAssociated() {
			return nodesAssociated;
		}



		public void setNodesAssociated(NodeList nodesAssociated) {
			this.nodesAssociated = nodesAssociated;
		}
		
		public int getNodeName() {
			return nodeName;
		}



		public void setNodeName(int nodeName) {
			this.nodeName = nodeName;
		}



		public double getLocation() {
			return location;
		}



		public void setLocation(double location) {
			this.location = location;
		}



		public double getGen() {
			return gen;
		}



		public void setGen(double gen) {
			this.gen = gen;
		}
		
}
