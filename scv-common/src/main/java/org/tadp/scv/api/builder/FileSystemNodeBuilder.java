package org.tadp.scv.api.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.filesystem.FileSystemNode;

/**
 * 
 * @author Fabricio
 *
 * @param <E> Es la implementacion de fileSystemNode que se va a construir a partir de
 * sus diferencias
 */
public class FileSystemNodeBuilder<E extends FileSystemNode> implements Serializable
{

	// la lista de diferencias a aplicar al nodo
	private List<Difference<E>> differences;
	//el nodo
	private E node; 
	
	public FileSystemNodeBuilder(){
		this.differences= new ArrayList<Difference<E>>();
	}
	
	/**
	 * Se setea el nodo al cual se le van a aplicar
	 * las diferencias para llevarlo a la revision deseada
	 *  
	 * @param node
	 * @return
	 */
	public FileSystemNodeBuilder<E> setNode(E node)
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
	public FileSystemNodeBuilder<E> append(List<Difference<E>> differences)
	{
		this.differences.addAll(differences);
		return this;
	}
	
	public FileSystemNodeBuilder<E> append(Difference<E> difference)
	{
		this.differences.add(difference);
		return this;
	}	
	

	/**
	 * Este metodo devuelve el una copia del nodo
	 * con las differencias que tenga al momento de invocacion del metodo
	 * 
	 * @return
	 */
	public E build()
	{
		E buildedNode = this.getNodeToBuildFromNode();
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
		return buildedNode;
	}
	
	/**
	 * Getter Mehod
	 * 
	 * @return
	 */
	public E getNode()
	{
		return node;
	}
	
	
	/**
	 * Devuelve el nodo una copia del nodo
	 * este es al cual se le van a aplicar las diferencias 
	 * 
	 */
	@SuppressWarnings({ "unchecked"})
	private E getNodeToBuildFromNode()
	{
		E temp = null;
		try
		{
			temp = (E) getNode().clone();
		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return temp;
	}
}
