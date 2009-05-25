package org.tadp.scv.web.pages.components;

import java.util.ArrayList;
import java.util.List;

import org.tadp.scv.web.model.VersionModel;
import org.tadp.scv.web.panels.FileViewWindowPanel;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxLink;
import wicket.extensions.ajax.markup.html.modal.ModalWindow;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

@SuppressWarnings("serial")
public class ViewedFileListPanel extends Panel
{
	private List<VersionModel> list;
	private LinkViewList linkList;
	private ModalWindow	window;
	
	public ViewedFileListPanel(String id,ModalWindow window)
	{
		super(id);
		list = new ArrayList<VersionModel>();
		linkList = new LinkViewList("fileLinks",list);
		add(linkList);
		setOutputMarkupId(true);
		this.window = window;
	}
	
	public void add(VersionModel versionModel)
	{
		if (list.contains(versionModel))
		{
			list.remove(versionModel);
		}
		list.add(0,versionModel);
		linkList.modelChanged();		
	}
	
	private class LinkViewList extends ListView
	{
		public LinkViewList(String id, List<VersionModel> list)
		{
			super(id, list);
		}

		@Override
		protected void populateItem(ListItem item)
		{
			VersionModel versionModel = (VersionModel) item.getModelObject();
			FileLink link = new FileLink("fileLink",versionModel);
			String label = versionModel.getFile().getName()+" version ="+versionModel.getVersion();
			link.add(new Label("fileLabel",label));
			
			item.add(link);
		}
		
	}
	@SuppressWarnings("unused")
	private class FileLink extends AjaxLink
	{
		private VersionModel	versionModel;

		public FileLink(String id, IModel model)
		{
			super(id, model);
		}

		public FileLink(String id)
		{
			super(id);
		}

		public FileLink(String id, VersionModel model)
		{
			super(id);
			versionModel = model;
		}

		@Override
		public void onClick(AjaxRequestTarget target)
		{
			window.setTitle("Viewing "+versionModel.getFile().getName()+" on version "+versionModel.getVersion());
			window.setContent(new FileViewWindowPanel(window.getContentId(),versionModel));
			window.show(target);
		}
	}

}