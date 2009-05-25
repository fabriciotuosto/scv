package org.tadp.scv.web.panels;

import org.tadp.scv.api.compare.Revision;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.server.FileVersionable;
import org.tadp.scv.api.filesystem.server.Versionable;
import org.tadp.scv.web.model.VersionModel;

import wicket.extensions.ajax.markup.html.modal.ModalWindow;
import wicket.markup.html.panel.Panel;

@SuppressWarnings("serial")
public class FileViewPanel extends Panel
{

	private FileSystemNode	file;

	private HistoryTable	fileVersionTable;
	
	public FileViewPanel(String id)
	{
		super(id);
		fileVersionTable = new HistoryTable("historyTable");
		add(fileVersionTable);
		setOutputMarkupId(true);
	}
	public FileViewPanel(String id, NavigationHistoyPanel panel, ModalWindow window)
	{
		super(id);
		fileVersionTable = new HistoryTable("historyTable",panel,window);
		add(fileVersionTable);
		setOutputMarkupId(true);
	}
	
	public FileSystemNode getFile()
	{
		return file;
	}

	public void setFile(Versionable<? extends FileSystemNode> file)
	{
		VersionHistoryDataProvider dataProvider = new VersionHistoryDataProvider();
		boolean visible = false;
		if (file.isFile())
		{
			FileVersionable fileVer = (FileVersionable) file;
			for (Revision<File> revision : fileVer.getRevisions())
			{
				VersionModel model = new VersionModel(revision);
				model.setFile(fileVer);
				dataProvider.add(model);
			}
			VersionModel model0 = new VersionModel();
			
			model0.setComment("creacion");
			model0.setDate(fileVer.getCreationDate());
			model0.setVersion(0);
			model0.setFile(fileVer);
			dataProvider.add(model0);
			
			fileVersionTable.setDataProvider(dataProvider);
			visible = true;
		}
		fileVersionTable.setVisible(visible);
		replace(fileVersionTable);
	}
}
