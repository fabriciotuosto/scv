package org.tadp.scv.web.pages.components;

import org.tadp.scv.web.model.DetachableVersionModel;
import org.tadp.scv.web.model.VersionModel;
import org.tadp.scv.web.panels.FileViewWindowPanel;
import org.tadp.scv.web.panels.NavigationHistoyPanel;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxLink;
import wicket.extensions.ajax.markup.html.modal.ModalWindow;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

@SuppressWarnings("serial")
public class ViewFileLink extends Panel
{

	private FileLink fileLink;
	private NavigationHistoyPanel	panel;
	private ModalWindow	window;
	
	public ViewFileLink(String id, IModel model)
	{
		super(id, model);
		fileLink = new FileLink("view",model);
		init();
	}

	private void init()
	{		
		add(fileLink);
	}

	public ViewFileLink(String id)
	{
		super(id);
		fileLink = new FileLink("view");
		init();
	}



	public ViewFileLink(String componentId, IModel model, NavigationHistoyPanel historyPanel)
	{
		this(componentId,model);
		this.panel = historyPanel;
	}



	public ViewFileLink(String componentId, IModel model, NavigationHistoyPanel historyPanel, ModalWindow window)
	{
		this(componentId,model,historyPanel);
		this.window = window;
	}



	private class FileLink extends AjaxLink	
	{

		private VersionModel versionModel;
		public FileLink(String id)
		{
			super(id);
		}

		public FileLink(String id, IModel model)
		{
			super(id, model);
			DetachableVersionModel dVersionModel = (DetachableVersionModel) model;
			versionModel = dVersionModel.getVesionModel();
		}

		@Override
		public void onClick(AjaxRequestTarget target)
		{
			if (panel != null)
			{
				panel.add(versionModel);
			}
			target.addComponent(panel.getModelListPanel());
			window.setTitle("Viewing "+versionModel.getFile().getName()+" on version "+versionModel.getVersion());
			window.setContent(new FileViewWindowPanel(window.getContentId(),versionModel));
			window.show(target);
		}
		
	}
	

}
