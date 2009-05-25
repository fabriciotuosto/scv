package org.tadp.scv.api.difference;

import java.io.Serializable;

import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.server.Versionable;



public interface Difference<E extends FileSystemNode> extends Cloneable,Serializable
{
	public void apply(E node);
	public void apply(Versionable<E> node) throws IntedToAddExistingNode;
	public void undo(E node);
	public void undo(Versionable<E> node) throws IntedToAddExistingNode;
	public Difference<E> clone() throws CloneNotSupportedException;
}
