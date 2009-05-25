package org.tadp.scv.web.model;

import wicket.Component;
import wicket.model.AbstractReadOnlyDetachableModel;
import wicket.model.IModel;

@SuppressWarnings("serial")
public class DetachableVersionModel extends AbstractReadOnlyDetachableModel
{
	private VersionModel vesionModel;
	
	public DetachableVersionModel()
	{
		super();
	}
	public DetachableVersionModel(Object versionModel)
	{
		super();
		vesionModel = (VersionModel) versionModel;
	}

	@Override
	public IModel getNestedModel()
	{
		return null;
	}

	@Override
	protected void onAttach()
	{
		if (vesionModel == null)
		{
		}
	}

	@Override
	protected void onDetach()
	{
		vesionModel = null;
	}

	@Override
	protected Object onGetObject(Component arg0)
	{
		return vesionModel;
	}

	public VersionModel getVesionModel()
	{
		return vesionModel;
	}
	
	public void setVesionModel(VersionModel vesionModel)
	{
		this.vesionModel = vesionModel;
	}
}
