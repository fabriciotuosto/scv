package org.tadp.scv.web.pages.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.api.filesystem.server.FileVersionable;
import org.tadp.scv.api.filesystem.server.Versionable;

public class FileSystemNodeTreeModel
{

	private DirectoryVersionable	root;

	public FileSystemNodeTreeModel(DirectoryVersionable file)
	{
		root = file;
	}

	public TreeModel createTreeModel()
	{
		return convertToTreeModel(getRoot());
	}

	private TreeModel convertToTreeModel(DirectoryVersionable file)
	{
		DefaultMutableTreeNode rootNode = null;
		if (file != null)
		{
			rootNode = new DefaultMutableTreeNode(file);
			addSubFiles(file, rootNode);
		}else{
			rootNode = new DefaultMutableTreeNode(new DirectoryVersionable(new Directory("ROOT",null)));
		}
		return new DefaultTreeModel(rootNode);
	}

	private void addSubFiles(Versionable<? extends FileSystemNode> file, DefaultMutableTreeNode rootNode)
	{
		if (file.isDirectory())
		{
			DirectoryVersionable dir = (DirectoryVersionable) file;
			for(FileVersionable subFile : dir.getFilesVersionable())
			{
				add(subFile,rootNode);
			}
			for(DirectoryVersionable subDir : dir.getDirectoriesVersionable())
			{
				add(subDir,rootNode);
			}
		}
	}

	private void add(Versionable<? extends FileSystemNode> file,DefaultMutableTreeNode parent)
	{
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(file);
		parent.add(child);
		addSubFiles(file, child);
	}

	public DirectoryVersionable getRoot()
	{
		return root;
	}

	public void setRoot(DirectoryVersionable root)
	{
		this.root = root;
	}
}
