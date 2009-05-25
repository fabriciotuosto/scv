package org.tadp.scv.web.panels;

import java.util.ArrayList;
import java.util.List;

import org.tadp.scv.web.pages.components.ViewFileLink;

import wicket.extensions.ajax.markup.html.modal.ModalWindow;
import wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;
import wicket.model.Model;

@SuppressWarnings("serial")
public class HistoryTable extends Panel
{

	private AjaxFallbackDefaultDataTable	table	= null;
	private NavigationHistoyPanel	historyPanel;
	private ModalWindow window;
	private Label fileName;

	public HistoryTable(String id, IModel model)
	{
		super(id, model);
	}

	public HistoryTable(String id)
	{
		super(id);
		init();
	}
	
	public HistoryTable(String string, NavigationHistoyPanel panel, ModalWindow window)
	{
		super(string);
		init();
		this.historyPanel = panel;
		this.window = window;
		this.fileName = new Label("fileName","");
		add(fileName);
	}

	public void init()
	{
		setVisible(false);
		table = new AjaxFallbackDefaultDataTable("table", createColumns(), new VersionHistoryDataProvider(), 16);
		table.setOutputMarkupId(true);
		add(table);
		setOutputMarkupId(true);
	}

	@SuppressWarnings("unchecked")
	private List createColumns()
	{
		List columns = new ArrayList();
		columns.add(new ViewRevision(new Model("Actions")));
		columns.add(new PropertyColumn(new Model("Version"), "version"));
		columns.add(new PropertyColumn(new Model("Date"), "date"));
		columns.add(new PropertyColumn(new Model("Coment"), "comment"));
		return columns;
	}

	public void setDataProvider(VersionHistoryDataProvider dataProvider)
	{
		table = new AjaxFallbackDefaultDataTable("table", createColumns(), dataProvider, 16);
		table.setOutputMarkupId(true);
		if(dataProvider.getVersions()!=null && dataProvider.getVersions().size() >0)
		{
			fileName = new Label("fileName",dataProvider.getVersions().get(0).getFile().getName());
			replace(fileName);
		}
		replace(table);
		
	}	
	
	private class ViewRevision extends AbstractColumn
	{

		public ViewRevision(IModel displayModel, String sortProperty)
		{
			super(displayModel, sortProperty);
		}

		public ViewRevision(IModel displayModel)
		{
			super(displayModel);
		}

		public void populateItem(Item cellItem, String componentId, IModel model)
		{
			
			cellItem.add(new ViewFileLink(componentId,model,historyPanel,window));
		}
	}
}
