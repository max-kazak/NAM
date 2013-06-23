package ru.volterr.nam;

public class ModelingPair extends Pair<Long, Boolean> {

	/**
	 * @param time - modeling time
	 * @param mode - true = direct mode, false = reverse mode
	 */
	public ModelingPair(Long time, Boolean mode) {
		super(time, mode);
	}

}
