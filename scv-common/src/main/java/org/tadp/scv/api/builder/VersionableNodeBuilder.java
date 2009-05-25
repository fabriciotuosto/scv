package org.tadp.scv.api.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.server.Versionable;

public class VersionableNodeBuilder<E extends FileSystemNode> implements Serializable{
	// la lista de diferencias a aplicar al nodo
	private List<Difference<E>> differences;
	//el nodo
	private Versionable<E> node; 
	
	public VersionableNodeBuilder() {
		this.differences= new ArrayList<Difference<E>>();
	}
	
	/**
	 * Se setea el nodo al cual se le van a aplicar
	 * las diferencias para llevarlo a la revision deseada
	 *  
	 * @param node
	 * @return
	 */
	public VersionableNodeBuilder<E> setNode(Versionable<E> node)
	{
		this.node = node;
		return this;
	}

	/**
	 * Se agregan las diferencias que se le van a aplicar al nodo
	 * 
	 * @param differences
	 * @return
	 */
	public VersionableNodeBuilder<E> append(List<Difference<E>> differences)
	{
		this.differences.addAll(differences);
		return this;
	}
	
	public VersionableNodeBuilder<E> append(Difference<E> difference)
	{
		this.differences.add(difference);
		return this;
	}	
	

	/**
	 * Este metodo devuelve el una copia del nodo
	 * con las differencias que tenga al momento de invocacion del metodo
	 * 
	 * @return
	 * @throws IntedToAddExistingNode 
	 */
	public Versionable build() throws IntedToAddExistingNode
	{
		Versionable<E> buildedNode = node;
		// se itera sobre las diferencias para aplicarlas
		for (Difference<E> diff : differences)
		{
			/*
			 * El responsable de aplicar las differencias
			 * es la diferencia en si asi no hay que revisar
			 * que tipo de diferencia o sobre que tipo de nodo se las esta
			 * aplicando
			 */ 
			diff.apply(buildedNode);
		}
		differences.clear();
		return buildedNode;
	}
	
	/**
	 * Ejecuta una serie de comandos para deshacer las acciones
	 * sobre el nodo.
	 * 
	 * @return
	 * @throws IntedToAddExistingNode
	 */
	public Versionable undoBuild() throws IntedToAddExistingNode{
		Versionable<E> buildedNode= node;
		
		for(Difference<E> diff: differences){
			diff.undo(buildedNode);
		}
		differences.clear();
		return buildedNode;
	}
	
	/**
	 * Getter Mehod
	 * 
	 * @return
	 */
	public Versionable<E> getNode()
	{
		return node;
	}
}
