package org.tadp.scv.api.compare;

import java.util.ArrayList;
import java.util.List;

import org.tadp.scv.api.filesystem.FileSystemNode;

/**
 * Esta clase tiene por objetivo servir como unidad de transferencia
 * para las operaciones de commit y update.
 * @see commit y update en las disntintas implemtacion de la interfaz SCVProyect
 * 
 * @author martin
 *
 * @param <E>
 */
public class DeltaNodo<E extends FileSystemNode>{
	private String path;
	private List<Revision<E>> revisiones;
	
	/**
	 * 
	 * @param path: es la ruta de acceso del nodo al que pertencen las revisones
	 * @param revisiones: es un conjunto de Objeto Revision
	 */
	public DeltaNodo(String path, List<Revision<E>> revisiones){
		this.setPath(path);
		if( revisiones!= null ) {
			this.revisiones= revisiones;
		} else {
			this.revisiones= new ArrayList<Revision<E>>();
		}
	}
	/**
	 * 
	 * @param path: es la ruta de acceso del nodo al que pertencen las revisones
	 */
	public DeltaNodo(String path){
		this.setPath(path);
		this.revisiones= new ArrayList<Revision<E>>();
	}	
	
	public String getPath(){
		return path;
	}
	
	public void setPath(String path){
		this.path= path;
	}
	
	public List<Revision<E>> getRevisiones(){
		return revisiones;
	}
	
	public boolean addRevision(Revision<E> revision){
		return revisiones.add(revision);
	}

	public Revision<E> getRevision(int i) {
		return revisiones.get(i);
	}
	
	public Revision<E> getLastRevision(){
		if (revisiones.size() == 0) {
			throw new RuntimeException("No hay revisiones en el DeltaNode");
		}
		return revisiones.get(revisiones.size()-1);
	}

}
