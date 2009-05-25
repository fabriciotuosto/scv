package org.tadp.scv.api.server;

import org.tadp.scv.api.Tag;
import org.tadp.scv.api.exceptions.BothSourcesModifiedException;
import org.tadp.scv.api.exceptions.FirstNeedToMakeUpdateException;
import org.tadp.scv.api.exceptions.TagNotFoundException;
import org.tadp.scv.api.exceptions.UnableToTagException;
import org.tadp.scv.api.filesystem.FileSystemNode;



public interface SCVProyect
{
	/**
	 * 
	 * @param node
	 */
	public void add(FileSystemNode node);
	/**
	 * 
	 * @param node
	 * @throws FirstNeedToMakeUpdateException
	 */
	public void commit(FileSystemNode node) throws FirstNeedToMakeUpdateException;
	/**
	 * 
	 * @param node
	 * @throws FirstNeedToMakeUpdateException
	 */
	public void commitAll() throws FirstNeedToMakeUpdateException;
	
	/**
	 * 
	 * @param node
	 */
	public void update (FileSystemNode node);

	/**
	 * @throws BothSourcesModifiedException 
	 * 
	 *
	 */
	public void updateAll() throws BothSourcesModifiedException;
	
	/**
	 * 
	 *
	 */
	public void createTag(String tagName) throws UnableToTagException;
	
	/**
	 * 
	 * @param tagName
	 * @throws UnableToTagException
	 */
	public Tag getTag(String tagName) throws TagNotFoundException;

}
