package ru.volterr.nam.gui.model;

import org.apache.commons.collections15.Transformer;

import ru.volterr.nam.model.Link;

public class BandTransformer implements Transformer<Link, Long>{

	@Override
	public Long transform(Link l) {
		return l.getBandwidth();
	}

}
