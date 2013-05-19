package ru.volterr.nam.gui.model;

import java.util.Comparator;

import ru.volterr.nam.model.Link;

public class ByNameLinkComparator implements Comparator<Link>{

	@Override
	public int compare(Link link1, Link link2) {
		return link1.getName().compareTo(link2.getName());
	}

}
