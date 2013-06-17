package ru.volterr.nam.gui.model;

import org.apache.commons.collections15.Transformer;

import ru.volterr.nam.model.Link;

public class BandProbTransformer implements Transformer<Link, Long>{

	@Override
	public Long transform(Link l) {
		if(!l.isAvailable())
			return Long.MAX_VALUE;
		else
			return (long) (l.getBandwidth()*l.getFprob());
	}

}
