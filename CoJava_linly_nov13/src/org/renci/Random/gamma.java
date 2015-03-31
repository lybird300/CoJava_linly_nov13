package org.renci.Random;

import java.util.Random;

public class gamma {
	
	static Random generator = new Random();
	/* gamma function routine #1 from Yang's PAML package  */
	/* Assumes random seed already initialized */
	/* s = shape param = k = mean**2/Var  */
	/* multiply output by mean/k to get correct mean and Var */
	
	public static double rndGamma(double s){
		double r = 0;
		if(s<0.0)
			return 0;
		else if (s<1.0)
			r = rndGamma1(s);
		else if(s>1.0)
			r = rndGamma2(s);
		else 
			r = - Math.log(generator.nextDouble());
		return r;
		
	}
	private static double rndGamma1(double s){
		
		double a = 0,p = 0,uf = 0,ss=10.0,d = 0,r,x=0,small= 1e-37,w;
		if (s!=ss){
			a = 1.0-s;
			p = a/(a+s*Math.exp(-a));
			uf = p*Math.pow(small/a, s);
			d = a*Math.log(a);
			ss = s;
		}
		for(;;){
			r = generator.nextDouble();
			if (r>p){
				x = a-Math.log((1.0-r)/(1.0-p));
				w = a*Math.log(x) -d;
			}
			else if (r>uf){
				x = a*Math.pow(r/p,1/s);
				w=x;
			}
			else return 0.0;
			
			r = generator.nextDouble();
			if(1.0-r <= w && r>0.0)
				if(r*(w+1.0)>= 1.0 || - Math.log(r)<= w)
					continue;
			break;
		}
		return x;
	}
	private static double rndGamma2(double s){
		double r,d,f,g,x,b = 0,h = 0,ss=0;
		if(s!=ss){
			b = s-1.0;
			h = Math.sqrt(3.0*s - .75);
			ss = s;
		}
		for(;;){
			r = generator.nextDouble();
			g = r- r*r;
			f = (r-.5)*h/Math.sqrt(g);
			x = b+f;
			if(x<=0)
				continue;
			r = generator.nextDouble();
			d = 64*r*r*g*g*g;
			if(d*x<x-2.0*f*f || Math.log(d)< 2*(b*Math.log(x/b)-f)){
				break;
			}
			
		}
		return x;
	}
}
