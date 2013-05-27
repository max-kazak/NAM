package ru.volterr.nam.gui.model;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
 
public class CellTreeRenderer extends DefaultTreeCellRenderer {
	ImageIcon routersIcon;
	ImageIcon usersIcon;

    public CellTreeRenderer() {
    	routersIcon = new ImageIcon("res/images/img16x16/routers.png");
    	usersIcon = new ImageIcon("res/images/img16x16/users.png");
    }

    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        
        if (!leaf && isRoutersRoot(value)) {
            setIcon(routersIcon);
            setToolTipText("List of routers in the network");
        } else {
        	if (!leaf && isUsersRoot(value)) {
                setIcon(usersIcon);
                setToolTipText("List of users in the network");
            } else {
            	setIcon(null);
                setToolTipText(null);
            } 
        } 

        return this;
    }

    private boolean isRoutersRoot(Object value) {
        return ((String)((DefaultMutableTreeNode)value).
        			getUserObject()).
        			equals("Routers");
    }
    
    private boolean isUsersRoot(Object value) {
    	return ((String)((DefaultMutableTreeNode)value).
    			getUserObject()).
    			equals("Users");
    }
}
