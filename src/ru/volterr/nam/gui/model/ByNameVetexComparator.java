package ru.volterr.nam.gui.model;

import java.util.Comparator;

import ru.volterr.nam.model.Node;

public class ByNameVetexComparator implements Comparator<Node>{

	@Override
	public int compare(Node node1, Node node2) {
		return node1.getName().compareTo(node2.getName());
	}

}
