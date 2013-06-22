package ru.volterr.nam.law;

import java.util.Random;

public class SizeParetLaw implements SizeLaw{

	private Random rand = new Random();
	
	private int xmax = 1;
	
	@Override
	public int nextSize() {
		double u = rand.nextDouble();
		double rv = xmax/Math.pow(u,2./3);
		if(rv>15)
			return nextSize();
		//System.out.println("par= " + (int)rv + "    u= " + u + "   sqrt= " + Math.pow(u,2./3) + "   rv= " + rv);
		return (int) rv;
	}

	

}
