package org.tadp.scv.web.pages.components;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.server.Versionable;
import org.tadp.scv.web.panels.FileViewPanel;

import wicket.ajax.AjaxRequestTarget;
import wicket.extensions.markup.html.tree.Tree;


@SuppressWarnings("serial")
public class FileTree extends Tree
{
	private FileViewPanel panel;
	
	public FileTree(String id)
	{
		super(id);
	}
	
	public FileTree(String id, TreeModel model)
	{		
		super(id, model);
		getTreeState().collapseAll();
		setOutputMarkupId(true);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node)
	{
			DefaultMutableTreeNode dNode = (DefaultMutableTreeNode)node;
			Versionable<FileSystemNode> file = (Versionable<FileSystemNode>)dNode.getUserObject();
			panel.setFile(file);
			target.addComponent(panel);
	}

	public void setPanel(FileViewPanel panel)
	{
		this.panel = panel;
	}
	
	public FileViewPanel getPanel()
	{
		return panel;
	}
}
