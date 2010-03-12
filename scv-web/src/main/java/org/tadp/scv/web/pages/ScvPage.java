package org.tadp.scv.web.pages;

import java.util.HashMap;
import java.util.Map;

import org.tadp.scv.web.application.test.CreateMockServer;
import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.api.server.SCVServer;
import org.tadp.scv.web.pages.components.AjaxListDropDown;
import org.tadp.scv.web.pages.components.FileTree;
import org.tadp.scv.web.pages.model.FileSystemNodeTreeModel;
import org.tadp.scv.web.panels.FileViewPanel;
import org.tadp.scv.web.panels.NavigationHistoyPanel;

import wicket.extensions.ajax.markup.html.modal.ModalWindow;

public class ScvPage extends HomePage
{
	private static final long	serialVersionUID	= 1L;
	private ModalWindow fileWindow;
	public ScvPage()
	{
		super();
		SCVServer server = CreateMockServer.getServer();
		Map<String,DirectoryVersionable> map = new HashMap<String, DirectoryVersionable>();
		for(String string : server.getProjectsNames())
		{
			map.put(string, server.getProyect(string).getRoot());
		}
		
		fileWindow = new ModalWindow("fileWindow");
		add(fileWindow);
		
		NavigationHistoyPanel historyPanel = new NavigationHistoyPanel("history",fileWindow);
		add(historyPanel);		
		
		FileViewPanel fileView = new FileViewPanel("editFile",historyPanel,fileWindow);
		fileView.setOutputMarkupId(true);
		add(fileView);
		
		FileSystemNodeTreeModel model = new FileSystemNodeTreeModel(null);
		FileTree fileTree = new FileTree("tree",model.createTreeModel());
		fileTree.setPanel(fileView);
		fileTree.setOutputMarkupId(true);
		add(fileTree);
		
		AjaxListDropDown dropDown = new AjaxListDropDown("proyectos");
		dropDown.setChoices(map);
		dropDown.setFileTree(fileTree);
		dropDown.setContainer(this);
		add(dropDown);
	}
	public ModalWindow getFileWindow()
	{
		return fileWindow;
	}
	public void setFileWindow(ModalWindow fileWindow)
	{
		this.fileWindow = fileWindow;
	}
}
