package org.tadp.scv.api.exceptions;

import java.util.List;

import org.tadp.scv.api.filesystem.server.Versionable;

/**
 * Esta excepcion deberia darse cuando tanto la copia remota como local ha sufrido modificaciones
 * por lo cual no se puede aplicar la operacion solicitada (commit o update). Este comportamiento
 * se establece segun la entrega 1 del trabajo practico.
 * 
 * @author alejandro
 */
public class BothSourcesModifiedException extends Exception {
	private static final long serialVersionUID = 1L;
	private List<Versionable> nodosConflicto;
	

	/**
	 * 
	 * @param message: descripcion de la exception
	 * @param nodosConflicto: lista de nodos modificados tanto en el server como en el cliente.
	 */
	public BothSourcesModifiedException(String message, List<Versionable> nodosConflicto){
		super(message);
		this.nodosConflicto = nodosConflicto;
	}
	
	public List<Versionable> getNodosConflicto(){
		return nodosConflicto;
	}
}
