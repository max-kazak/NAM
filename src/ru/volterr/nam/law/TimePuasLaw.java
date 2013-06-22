package ru.volterr.nam.law;

import java.util.Random;

/**
 * @author Max
 *
 * param mT - avarage time delta
 */
public class TimePuasLaw implements TimeLaw{

	public TimePuasLaw(){
		mT=2;
	}
	public TimePuasLaw(int averDeltaT){
		mT=averDeltaT;
	}
	
	private Random rand = new Random();
	
	public int mT;
	private int lastT = 0;
	
	@Override
	public int nextT() {
		double u = rand.nextDouble();
		int rv = (int) ((-ln(u))*mT);
		//System.out.println("u = "+u+"   -Ln(u) = "+(-ln(u))+"   puas = "+rv);
		return rv;
	}
	
	public int nextT(int hour) {
		double u = rand.nextDouble();
		int rv;
		if( (hour>21) || (hour<9) )
			rv = (int) ((-ln(u))*mT*1.5);
		else
			rv = (int) ((-ln(u))*mT);
		//System.out.println("u = "+u+"   -Ln(u) = "+(-ln(u))+"   puas = "+rv);
		return rv;
	}

	@Override
	public boolean isNow(int hour) {
		
		//System.out.println("curT = "+lastT);
				
		if(lastT-- == 0){
			lastT = nextT(hour);
			//System.out.println("newT = "+lastT);
			return true;
		}
				
		return false;
	}

	private static double ln(double x){
		return Math.log(x);		
	}

}
