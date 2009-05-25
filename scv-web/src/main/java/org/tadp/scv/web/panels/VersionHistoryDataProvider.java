package org.tadp.scv.web.panels;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tadp.scv.web.model.DetachableVersionModel;
import org.tadp.scv.web.model.VersionModel;

import wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import wicket.model.IModel;

@SuppressWarnings({ "serial", "hiding" })
public class VersionHistoryDataProvider extends SortableDataProvider
{

	private List<VersionModel> versions;
	private VersionModelComparator comparator;
	
	public VersionHistoryDataProvider()
	{
		super();
		versions = new LinkedList<VersionModel>();
		comparator = new VersionModelComparator();
		setSort("version",false);
	}
	
	public Iterator<VersionModel> iterator(int arg0, int arg1)
	{	
		return versions.subList(arg0, arg1).iterator();
	}

	@SuppressWarnings("unchecked")
	public IModel model(Object arg0)
	{
		return new DetachableVersionModel(arg0);
	}

	public int size()
	{
		return versions.size();
	}

	public void add(VersionModel o)
	{
		versions.add(o);
		Collections.sort(versions, comparator);
	}
	
	public void clear()
	{
		versions.clear();
	}
	
	@SuppressWarnings("unused")
	private class VersionModelComparator implements Comparator<VersionModel>,Serializable
	{

		public int compare(VersionModel o1, VersionModel o2)
		{
			return o1.getVersion().compareTo(o2.getVersion())*-1;
		}
		
	}

	public List<VersionModel> getVersions()
	{
		return versions;
	}

	public void setVersions(List<VersionModel> versions)
	{
		this.versions = versions;
	}
}
