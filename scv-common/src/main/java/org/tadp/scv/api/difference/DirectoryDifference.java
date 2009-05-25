/**
 * 
 */
package org.tadp.scv.api.difference;

import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.FileSystemNode;

/**
 * @author Fabricio, SDR
 * 
 */
public abstract class DirectoryDifference implements Difference<Directory>
{
	private FileSystemNode	nodo;

	/**
	 * @author SDR Indica el nodo que es diferente en esta version
	 */
	public FileSystemNode getNodo()
	{
		return nodo;
	}

	/**
	 * @param nodo
	 *            the nodo to set
	 */
	public void setNodo(FileSystemNode nodo)
	{
		this.nodo = nodo;
	}
	
	public abstract Difference<Directory> clone() throws CloneNotSupportedException;
}
