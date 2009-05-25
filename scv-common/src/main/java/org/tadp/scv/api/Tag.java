package org.tadp.scv.api;

import org.tadp.scv.api.filesystem.server.DirectoryVersionable;


/**
 * 
 * @author Fabricio
 *
 */
public class Tag
{

	private String name;
	private DirectoryVersionable root;
	
	
	public Tag()
	{
		super();
	}
	
	public Tag(String name,DirectoryVersionable root)
	{
		this.name = name;
		this.root = root;
	}
	
	public String getName()
	{
		return name;
	}
	
	public DirectoryVersionable getRoot()
	{
		return root;
	}
}
