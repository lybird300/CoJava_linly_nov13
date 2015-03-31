package org.renci.Random;

import java.util.Random;

public class multiNom {
		static Random generator = new Random();
	public static void multinom(int nclass, int nitem, double[] prob, int[] nbybin){
		double x,probsum;
		int which;
		
		for(int i = 0 ; i<nclass; i++){
			nbybin[i] = 0;
		}
		for(int i =0;i<nitem;i++){
			probsum = prob[0];
			x = generator.nextDouble();
			which = 0;
			while((x>probsum)&&(which<(nclass-1))){
				probsum += prob[++which];
			}
			nbybin[which]++;
		}
		return;
	}
}
