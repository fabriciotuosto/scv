package org.tadp.scv.api.helpers;

import java.util.Collection;

import org.tadp.scv.api.filesystem.FileSystemNodeInterface;

/**
 * 
 * @author Alejandro Sanchez
 *	Este helper se encarga de realizar busquedas de nodos abstrayendose de la implementacion de las colecciones,
 *  por lo que pueden usarlo tanto los Directory y File, o bien los DirectoryVersionable y FileVersionable
 *  Solo compara por el nombre. 
 */
public class NodeFinder {

	/**
	 * Busca un nodo en una coleccion por nombre
	 * @author SDR
	 * @param nodePath path del nodo buscado
	 * @param collectionNodos la coleccion de FileSystemNodes donde voy a buscar
	 * @return el nodo si lo encuentra, sino null
	 */
	public static <E extends FileSystemNodeInterface> FileSystemNodeInterface getNode(String nodePath, Collection<E> collectionNodos)
	{
		//TODO si no lo encuentro tiro una excpecion
		for(FileSystemNodeInterface nodeIt: collectionNodos){
			if(nodeIt.getPath().equals(nodePath)){
				//Lo encontre y lo devuelvo
				return nodeIt;
			}
		}		
		//Aca tiro una excepcion?
		return null;
	}	
	
	
	/**
	 * Metodo generico que encapsula el comportamiento de como se debe buscar
	 * un nodo en una coleccion de nodos del sistema de archivos.
	 * 
	 * @param nodo buscado
	 * @param files2 la coleccion de FileSystemNodes donde voy a buscar
	 * @return el nodo si lo encuentra, sino null
	 */
	public static <E extends FileSystemNodeInterface> FileSystemNodeInterface getNode(FileSystemNodeInterface node , Collection<E> files2)
	{
		//TODO si no lo encuentro tiro una excepcion
		for(FileSystemNodeInterface nodeIt: files2){
			if(nodeIt.getPath().equals(node.getPath())){
				//Lo encontre y lo devuelvo
				return nodeIt;
			}
		}		
		//Aca tiro una excepcion?
		return null;
	}
}
