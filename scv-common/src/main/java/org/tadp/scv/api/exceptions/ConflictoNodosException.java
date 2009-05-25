package org.tadp.scv.api.exceptions;

import org.tadp.scv.api.filesystem.server.Versionable;

public class ConflictoNodosException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Versionable node;
	
	public ConflictoNodosException(String descripcion, Versionable node){
		super(descripcion);
		this.node = node;
	}

	public Versionable getNodoConflicto(){
		return this.node;
	}
}
