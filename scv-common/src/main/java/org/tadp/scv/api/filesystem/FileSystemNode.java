package org.tadp.scv.api.filesystem;

import java.io.Serializable;

public abstract class FileSystemNode implements FileSystemNodeInterface,Serializable {

	private String name;
	private Directory predecesor;
	
	public FileSystemNode() {
		super();
		predecesor= null;
	}

	/**
	 * Crea un nodo del file system mediante un nombre y su nodo padre
	 * 
	 * @param name
	 * @param predecesor: es el directorio padre o contenedor del nodo a crear
	 */
	public FileSystemNode(String name, Directory predecesor) {
		super();
		setName(name);
		this.predecesor= predecesor;
	}
	
	public abstract boolean isDirectory();
	
	public abstract boolean isFile();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	@Override
	public abstract FileSystemNode clone() throws CloneNotSupportedException;
	
	/**
	 * 
	 * @return ruta de acceso al nodo
	 */
	public String getPath(){
		String path= ( predecesor != null ? predecesor.getPath() : "");
		path+= this.getName();
		
		return path;
	}
	
	public Directory getPredecesor(){
		return predecesor;
	}

	public void setPredecesor(Directory root) {
		this.predecesor= root;
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
}
