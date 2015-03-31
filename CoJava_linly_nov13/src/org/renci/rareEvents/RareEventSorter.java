package org.renci.rareEvents;

import java.util.Comparator;

public class RareEventSorter implements Comparator<RareEvent> {

	@Override
	public int compare(RareEvent event1, RareEvent event2) {
		if(event1.getGen()<event2.getGen())
			return 1;
		else if(event1.getGen()>event2.getGen())
			return -1;
		else
			return 0;
	}

}
