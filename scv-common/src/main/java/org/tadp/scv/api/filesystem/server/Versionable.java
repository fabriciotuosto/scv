package org.tadp.scv.api.filesystem.server;

import java.io.Serializable;
import java.util.List;

import org.tadp.scv.api.compare.Revision;
import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.FileSystemNodeInterface;

/**
 *
 * @param <E>
 */
public interface Versionable<E extends FileSystemNode> extends FileSystemNodeInterface,Serializable
{
	/**
	 * 
	 * @param rev
	 */
	public void addRevision(Revision<E>rev);
	/**
	 * 
	 * @return
	 */
	public int getRevisionNumber();
	/**
	 * 
	 * @param revisionNumber
	 * @return
	 * @throws IntedToAddExistingNode 
	 * @throws Exception
	 */
	public E getToRevision(int revisionNumber) throws IllegalArgumentException, IntedToAddExistingNode;
	
	/**
	 * 
	 * @return: El nro de la ultima revision cargada en el nodo
	 */
	public int getNumberOfLastRevision();
	
	public Revision<E> getRevision(int i);
	
	@SuppressWarnings("unchecked")
	public Versionable getCopy();
	
	public List<Revision<E>> getRevisions();
}
