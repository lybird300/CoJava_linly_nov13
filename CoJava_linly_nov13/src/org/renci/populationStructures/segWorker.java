package org.renci.populationStructures;

public class segWorker {
		
	public segWorker(){
		
	}
	public seg segInverse (seg segptr) 
	{
		seg newseg, newseg2;
		  seg oldseg, headseg;
		  boolean setend = true;

		  if (segptr == null) 
		    return new seg(0,1);

		  headseg = new seg(0,0);
		  newseg = headseg;

		  oldseg = segptr;

		  if (oldseg.begin > 0)
		    newseg.begin = 0;
		  else {
		    newseg.begin = oldseg.end;
		    oldseg = oldseg.next;
		  }

		  while (oldseg != null) {
		    newseg.end = oldseg.begin;
		    if (oldseg.end != 1) {
					
		      newseg2 = new seg(0,0);
		      newseg.next = newseg2;
		      newseg = newseg2;
		      newseg.begin = oldseg.end;
		      oldseg = oldseg.next;
		    }
		    else {
		      setend = false;
		      oldseg = oldseg.next;
		    }
		  }
		       
		  if (setend)
		    newseg.end = 1;


		  newseg.next = null;
		  return headseg;
	}
	public seg segUnion(seg segptr1, seg segptr2) 
	{
		  seg activesegptr = null, nextsegptr = null, tempptr;
		  double begin = 0, end = 0;
		  seg newptr;
			
		  newptr = null;
		
		  if (segptr1 != null && segptr2 != null) {
				
		    if (segptr1.begin < segptr2.begin) {
		      begin = segptr1.begin;
		      end = segptr1.end;
		      nextsegptr = segptr1.next;
		      activesegptr = segptr2;
		    }
		    else { 
		      begin = segptr2.begin;
		      end = segptr2.end;
		      nextsegptr = segptr2.next;
		      activesegptr = segptr1;
		    }
				
		    if (nextsegptr != null && (activesegptr.begin > nextsegptr.begin)) {
		      tempptr = nextsegptr;
		      nextsegptr = activesegptr;
		      activesegptr = tempptr;
		    }
		  }
		  else {
		    System.out.println("segment_union: you did something wrong - null segment.\n");
		    System.exit(0);
		    /*    if one or the other is null 
			  this has got to be impossible. */
		  }
		  
		  /* updating begin/end using activesegptr */
		  if (activesegptr.begin > end) {
		    newptr = segAdd(newptr, begin, end);
		    begin = activesegptr.begin;
		    end = activesegptr.end;    
		  } 
		  else if (activesegptr.end > end) {
		    end = activesegptr.end;
		  }
			
			
		  /* choosing between activesegptr->next and nextsegptr */
		  while (activesegptr.next != null && nextsegptr != null) {
				
		    if (activesegptr.next.begin > nextsegptr.begin) {
		      tempptr = nextsegptr;
		      nextsegptr = activesegptr.next;
		      activesegptr = tempptr;
		    }
		    else {
		      activesegptr = activesegptr.next;
		    }
				
		    /* if next possible segment is disjoint, add this segment, and startover. */
		    if (activesegptr.begin > end) {
		      newptr = segAdd(newptr, begin, end);
		      begin = activesegptr.begin;
		      end = activesegptr.end;    
		    } 
		    else if (activesegptr.end > end) {
		      end = activesegptr.end;
		    }
		  }
			
		  /* when we reach this point, either activesegptr->next is null or nextsegptr is null. */
			
		  if (nextsegptr != null) {
		    activesegptr = nextsegptr;
				
		  }
		  
		  /* end */
		  else {
		    activesegptr = activesegptr.next;
		  }  
		  
		  while (activesegptr != null) {
		    /* first check if activesegptr->next overlaps the current begin/end
		       then copy all of activesegptr to new segment */
		    
		    if (activesegptr.begin > end) {
		      newptr = 
			segAdd(newptr, begin, end);
		      begin = activesegptr.begin;
		      end = activesegptr.end;
		    }
			  
		    else if (activesegptr.end > end) {
		      end = activesegptr.end;
		    }

		    activesegptr = activesegptr.next;
		  
		    /*end */
		  }
		  newptr = segAdd(newptr, begin, end);

		  
		  return newptr;
	}

	public seg segIntersect(seg segptr1,seg segptr2){
		seg newsegptr = null;
		  double begin, end;

		  seg firstseg, secondseg, tempseg;

		  if (segptr1 == null || segptr2 == null) {
		    System.out.println("stop! you did something wrong. null seg in seg intersect\n");
		  }

		  if (segptr1.begin < segptr2.begin) {
		    firstseg = segptr1;
		    secondseg = segptr2;
		  }
		  else {
		    firstseg = segptr2;
		    secondseg = segptr1;
		  }

		  while (firstseg != null && secondseg != null) {
		    /* which is first, first or 2nd? */
		    if (firstseg.begin > secondseg.begin) {
		      tempseg = firstseg;
		      firstseg = secondseg;
		      secondseg = tempseg;
		    }
				

		    /* if they overlap: */
		    if (firstseg.end > secondseg.begin) {
		      begin = secondseg.begin;
					
		      /* if second is wholly contained in first */
		      if (firstseg.end > secondseg.end) {
		    	  end = secondseg.end;
		    	  secondseg = secondseg.next;
		      }
					
		      else {
		    	  end = firstseg.end;
		    	  firstseg = firstseg.next;
		      }
					
		      newsegptr = segAdd(newsegptr, begin, end);
		    }
		    else {
		      firstseg = firstseg.next;
		    }
		  }

		  return newsegptr;
	}
	public seg segAdd(seg ptr, double begin, double end) {
		  seg newptr = new seg(0,0);
			
			
		  if (ptr == null) {
		    	
		    newptr.begin = begin;
		    newptr.end = end;
		    newptr.next = ptr;
		    return newptr;
		  }
			
		  else if (ptr.begin > begin) {
		    	
		    newptr.begin = begin;
		    newptr.end = end;
		    newptr.next = ptr;
		    return newptr;
		  }
			
		  else {
		    ptr.next = segAdd(ptr.next, begin, end);
		    return ptr;
		  }
	}
	public boolean segContains(seg aSeg,double loc){
		while (aSeg != null){
			if(loc>= aSeg.begin){
				if(loc<=aSeg.end) return true;
				
				aSeg = aSeg.next;
			}
			else return false;
		}
		return false;
	}
	public void printSeg(seg aSeg){
		if(aSeg != null){
			System.out.println(aSeg.begin + "  " + aSeg.end);
			printSeg(aSeg.next);
			
		}
	}
}

