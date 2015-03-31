package org.renci.Random;

public class gammaLn {
	static double x,tmp,ser;
	static double[] cof = {76.18009173, -86.50532033, 24.01409822, 
	       -1.231739516, 0.120858003e-2, -0.536382e-5};
	
	public static double gammLn(double xx){
		x = xx - 1.0;
		tmp = x + 5.5;
		tmp -= (x + .5)* Math.log(tmp);
		ser = 1.0;
		for (int j=0;j<=5;j++){
			x += 1.0;
			ser += cof[j]/x;
		}
		return -tmp + Math.log(2.50662827465 * ser);
	}
}
