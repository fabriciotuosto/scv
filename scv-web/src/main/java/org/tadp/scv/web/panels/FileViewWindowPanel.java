package org.tadp.scv.web.panels;

import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.web.model.VersionModel;

import wicket.markup.html.basic.MultiLineLabel;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

@SuppressWarnings("serial")
public class FileViewWindowPanel extends Panel
{
	public FileViewWindowPanel(String id)
	{
		super(id);
	}

	public FileViewWindowPanel(String id, IModel model)
	{
		super(id, model);
	}

	public FileViewWindowPanel(String string, VersionModel versionModel)
	{
		this(string);
		try
		{
			String content = versionModel.getVersion().equals(new Integer(0))?
							 versionModel.getFile().getBasicContent():
						     versionModel.getFile().getContent(versionModel.getVersion());
			add(new MultiLineLabel("fileContent",content));
		} catch (IntedToAddExistingNode e)
		{
			throw new RuntimeException("Hubo un problema en el servidor que no deberia existir con las versiones",e);
		}
	}

}
