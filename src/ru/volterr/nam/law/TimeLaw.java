package ru.volterr.nam.law;

public interface TimeLaw {
	/**
	 * @return next delta t
	 */
	public int nextT();
	
	/**
	 * @return true if time has come
	 */
	public boolean isNow(int hour);
}
