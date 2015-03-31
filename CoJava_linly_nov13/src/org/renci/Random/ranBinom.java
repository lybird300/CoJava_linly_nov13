package org.renci.Random;


public class ranBinom {
	final int BTHRESH = 50;
	randomNum random;
	public ranBinom(randomNum aRNG){
		random = aRNG;
	}
	public int ranbinom(int n, double p){
		/** 
		 Knuth Vol 2, p 131
		*/
		   int a, b ;
		   double x ; 
		   if (p>=1) return n ;
		   if (p<=0) return 0 ;
		   if (n<=0) return 0 ;

		   if (n<=BTHRESH) {
		    return ranb1(n,p) ;  /** small case */
		   }

		   a  = 1 + n/2 ;  
		   b  = n + 1 - a ;
		   x = ranbeta((double) a, (double) b) ;
		   if (x>=p) return ranbinom(a-1, p/x) ;
		   return (a + ranbinom(b-1, (p-x)/(1.0-x)) ) ;
		}



		
	
		double ranexp()
		{
		  /**
		   exponential mean 1
		  */
		  double          x, t;
		  t = random.randomDouble();
		  x = -Math.log(1.0 - t);
		  return x;
		}


		double randev0(double a)
		{
		  /**
		   algorithm G6: Gamma for a < 1
		  */
		  double          r1, r2, x, w;
		  double t = 1.0 - a;
		  double p = t / (t + a * Math.exp(-t));
		  double s = 1.0 / a;
		  for (;;) {
		    r1 = random.randomDouble();
		    if (r1 <= p) {
		      x = t * Math.pow(r1 / p, s);
		      w = x;
		    } else {
		      x = t + Math.log((1.0 - p) / (1.0 - r1));
		      w = t * Math.log(x / t);
		    }
		    r2 = random.randomDouble();
		    if ((1.0 - r2) <= w) {
		      if ((1.0 / r2 - 1.0) <= w)
		        continue;
		      if (-Math.log(r2) <= w)
		        continue;
		    }
		    return x;

		  }

		}


		double randev1(double a)
		{
		  /**
		   Random gamma deviate:  a>=1
		   GBEST algorithm  (D.J. BEST: Appl. Stat. 29 p 181 1978
		  */
		  double          x, d, e, c, g, f, r1, r2;

		  e = a - 1.0;
		  c = 3.0 * a - 0.75;


		  for (;;) {
		    r1 = random.randomDouble();
		    g = r1 - (r1 * r1);
		    if (g <= 0.0)
		      continue;
		    f = (r1 - 0.5) * Math.sqrt(c / g);
		    x = e + f;
		    if (x <= 0.0)
		      continue;
		    r2 = random.randomDouble();
		    d = 64.0 * r2 * r2 * g * g * g;
		    if ((d >= 1.0 - 2.0 * f * f / x) && (Math.log(d) >= 2.0 * (e * Math.log(x / e) - f)))
		      continue;
		    return (x);
		  }

		}


		double ranbeta(double a, double b) 
		{
		   double xa, xb ;

		   xa = rangam(a) ;
		   xb = rangam(b) ;
		   return xa/(xa+xb) ;
		}

		double
		rangam(double a)
		{
		  /**
		   generate gamma deviate mean a
		  */
		  if (a < 1.0) {
		    return( randev0(a));
		  }
		  if (a == 1.0) {
		    return( ranexp());
		  }
		  return( randev1(a));
		}

		int ranb1 (int n, double p) 
		/** 
		 binomial dis. 
		 Naive routine
		*/
		{ 
		    int cnt = 0, i ;

		    for (i=0 ; i< n ; i++)  {
		     if (random.randomDouble() <= p) ++ cnt ;
		    }

		    return cnt ;

		}
}