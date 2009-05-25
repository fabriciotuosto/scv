package org.tadp.scv.web.panels;

import org.tadp.scv.web.model.VersionModel;
import org.tadp.scv.web.pages.components.ViewedFileListPanel;

import wicket.extensions.ajax.markup.html.modal.ModalWindow;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

@SuppressWarnings("serial")
public class NavigationHistoyPanel extends Panel
{
	private ViewedFileListPanel		modelListPanel;
	private ModalWindow	window;

	public NavigationHistoyPanel(String id, IModel model)
	{
		super(id, model);
		init();
	}

	public NavigationHistoyPanel(String id,ModalWindow window)
	{
		super(id);
		this.window = window;
		init();
	}

	public void init()
	{
		add(new Label("label", "Viewed files"));
		setModelListPanel(new ViewedFileListPanel("list",window));
		getModelListPanel().setOutputMarkupId(true);
		add(getModelListPanel());
	}

	public void add(VersionModel versionModel)
	{
		getModelListPanel().add(versionModel);
	}

	public void setModelListPanel(ViewedFileListPanel modelListPanel)
	{
		this.modelListPanel = modelListPanel;
	}

	public ViewedFileListPanel getModelListPanel()
	{
		return modelListPanel;
	}
}
