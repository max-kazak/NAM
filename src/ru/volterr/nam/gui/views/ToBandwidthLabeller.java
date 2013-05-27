package ru.volterr.nam.gui.views;

import org.apache.commons.collections15.Transformer;

import ru.volterr.nam.model.Link;

public class ToBandwidthLabeller implements Transformer<Link, String> {

	@Override
	public String transform(Link link) {
		return ""+link.getBandwidth();
	}

}
