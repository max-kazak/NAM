package ru.volterr.nam.gui.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class TreeRenderer extends DefaultTreeModel{

	private DefaultMutableTreeNode usersNode;
	private DefaultMutableTreeNode routersNode;
	
	public TreeRenderer() {
		super(null);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Agents");
		usersNode = new DefaultMutableTreeNode("Users");
		routersNode = new DefaultMutableTreeNode("Routers");
		root.add(usersNode);
		root.add(routersNode);	
	}

}
