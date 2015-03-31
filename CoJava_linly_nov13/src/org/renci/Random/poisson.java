package org.renci.Random;

public class poisson {
	randomNum random;
	double sq,alxm,g,oldm=-1;
	
	
	public poisson(randomNum aRNG){
		 random = aRNG;
	}
	public  int poission(double xm){
		double em,t,y;
		oldm = -1.0;
		if(xm<12.0){
			if(xm!=oldm){
				oldm = xm;
				g = Math.exp(-xm);
			}
			em = -1.0;
			t = 1.0;
			do{
				em += 1.0;
				t *= random.randomDouble();
			}while(t>g);
		}
		else{
			if(xm != oldm){
				oldm = xm;
				sq = Math.sqrt(2.0*xm);
				alxm = Math.log(xm);
				g = xm*alxm - gammaLn.gammLn(xm + 1.0);
			}
			do{
				do{
					y = Math.tan(Math.PI * random.randomDouble());
					em = sq*y + xm;
					
				}while(em < 0);
				em = Math.floor(em);
				t = .9 * (1.0 + y*y)* Math.exp(em*alxm - gammaLn.gammLn(em + 1.0)-g);
			}while (random.randomDouble() > t);
		}
		return (int) (em+.5);
		
	}
	public  double poissonGetNext(double rate){
		 
		if(rate ==0) return -1;
		double ed = expDev();
		return (ed/rate);
		
	}
	private  double expDev(){
		double dum = 0;
		double rand = 	random.randomDouble();
		dum = (double) 1 - rand;
		double returner = - Math.log(dum);
		return returner;
	}
	
}
