package ru.volterr.nam;

import ru.volterr.nam.law.SizeParetLaw;
import ru.volterr.nam.law.TimePuasLaw;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*TimePuasLaw law = new TimePuasLaw();
		for(int i=0;i<25;i++)
			System.out.println(law.isNow());*/
		SizeParetLaw law = new SizeParetLaw();
		for(int i=0;i<100;i++)
			law.nextSize();
	}

}
