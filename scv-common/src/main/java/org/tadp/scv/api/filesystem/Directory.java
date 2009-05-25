package org.tadp.scv.api.filesystem;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.tadp.scv.api.compare.DirectoryComparator;
import org.tadp.scv.api.helpers.NodeFinder;


public class Directory extends FileSystemNode
{
	private Map<String, Directory> directories;
	private Map<String, File> files;

	public Directory()
	{
		directories= new HashMap<String, Directory>();
		files= new HashMap<String, File>();
	}
	
	/**
	 * 
	 * @param name
	 * @param predecesor nodo padre del directorio
	 */
	public Directory(String name, Directory predecesor)
	{
		super(name, predecesor);
		directories= new HashMap<String, Directory>();
		files= new HashMap<String, File>();
	}
	
	@Override
	public boolean isDirectory()
	{
		return !isFile();
	}

	@Override
	public boolean isFile()
	{
		return false;
	}
	
	@Override
	public String getPath(){
		//Los directorios terminan con \
		return super.getPath()+ System.getProperty("file.separator");
	}
	
	public boolean equals(Object arg0)
	{
		boolean result = false; 
		try{
			Directory node = (Directory) arg0;
			DirectoryComparator dirComp = new DirectoryComparator();
			if( this.getPath().equals(node.getPath()) && dirComp.compare(this, node).size() == 0)
				result= true;
		}catch(ClassCastException e){
			return result;
		}
		return result;
	}
	
	public Map<String, Directory> getDirectoriesMap()
	{
		return Collections.unmodifiableMap(directories);
	}

	
	public Map<String, File> getFilesMap()
	{
		return Collections.unmodifiableMap(files);
	}	
	
	/**
	 * Agrega un archivo al directorio
	 * Si el archivo ya existe la operacion devuelve FALSE,
	 * de lo contratio TRUE.
	 * 
	 * @param node: es el archivo a agregar al directorio
	 */
	public boolean add(File node)
	{
		if(!files.containsKey(node.getName()))	{ 
			files.put(node.getName(), node);
			return true;
		}	
		
		return false;
	}
	
	/**
	 * Agrega un directorio al directorio
	 * Si el directorio ya existe la operacion devuelve FALSE,
	 * de lo contratio TRUE.
	 * 
	 * @param node: es el directorio a agregar
	 */
	public boolean add(Directory node)
	{
		if(!directories.containsKey(node.getName()))	{ 
			directories.put(node.getName(), node);
			return true;
		}
	
		//Existe
		return false;
	}
	
	/**
	 * Remueve un subdirectorio
	 * 
	 * @param node: directorio a remover
	 * @return TRUE: si se encontro y removio, FALSE de lo contrario
	 * 
	 */
	public boolean remove(Directory node)
	{
		if(directories.containsKey(node.getName())){ 
			directories.remove(node.getName());
			return true;
		}
			
		//NO Existe
		return false;
	}
	
	/**
	 * Remueve un archivo
	 * 
	 * @param node: archivo a remover
	 * @return TRUE: si se encontro y removio, FALSE de lo contrario
	 * 
	 */
	public boolean remove(File node)
	{
		if(files.containsKey(node.getName()))	{ 
			files.remove(node.getName());
			return true;
		}
		
		//NO Existe
		return false;
	}
	
	@Override
	/**
	 * @author SDR
	 * Implementa un deep clone
	 */
	public Directory clone() throws CloneNotSupportedException
	{
		//Me clono a mi mismo y copio mis datos
		Directory dirClonado = new Directory(this.getName(), this.getPredecesor());
		
		//Primero clono los directorios
		for(Directory dir: directories.values()){
			dirClonado.add(dir.clone());
		}
		//Despues clono los archivos
		for(File arch: files.values()){
			dirClonado.add(arch.clone());
		}
		return dirClonado;
	}

	/**
	 * Busca el nodo bajo la jerarquia de este directorio
	 * que tiene el mismo path que el dado como patron
	 * de busqueda.
	 * 
	 * @param arch
	 * @return
	 */
	public FileSystemNode getFileSystemNode(FileSystemNodeInterface node){
		if(node.isFile())
			return this.searchFile((File) node);
		else
			return this.searchDirectory((Directory)node);
	}	
	
	/**
	 * Retorna un Directorio si es que esta presente entre 
	 * los subdirectorios de este nodo, sin importar el 
	 * nivel de anidamiento en la jerarquia.
	 * Ejemplo:
	 * raiz
	 * 	subdir1
	 * 		subdir2
	 * 
	 * raiz.searchDirectory(aux) retorna subdir2
	 * Donde aux tiene el mismo path que subdir2
	 * 
	 * @dirBuscado es un directorio que tiene el mismo path que el
	 * nodo buscado
	 * 
	 * @return el Directorio que tiene el mismo path que el 
	 * proporcionado o null en caso de no encontrar nada.
	 */
	
	public Directory searchDirectory(Directory dirBuscado)
	{
		Directory buscado= null;
		
		//Veo sino soy yo
		if (this.getPath().equals(dirBuscado.getPath()))
			return this;
		
		//Si no empieza como yo directamente salgo
		if(!dirBuscado.getPath().startsWith(this.getPath())){
			return null;
		}
		
		//Lo busco entre mis subdirectorios
		//TODO SDR: Aca podriamos usar el MAP y un tokenizer y algun manejo de strings para preguntarle solo al subdirectorio correspondiente

		buscado= (Directory) NodeFinder.getNode(dirBuscado, directories.values());

		if (  buscado == null ){
			//No lo encontre voy a buscarlo en mis subdir=>recursividad
			for(Directory subdir: directories.values()){
				buscado= subdir.searchDirectory(dirBuscado);
				//Por cada subdir me fijo si lo encontre
				if (buscado != null)
					return buscado;
			}
		}
		
		return buscado;

		/*
		 * 	Comentado por SDR, uso el metodo findNode que hace lo mismo
		
		FileSystemNodeInterface buscado= this.findNode(dirBuscado.getPath());
		if(buscado == null){
			return null;
		}else{
			return (Directory) buscado;
		}*/
	}

	/**
	 * Retorna un File si es que existe en el 
	 * Directorio o sus subdirectorios, en cualquier
	 * nivel de anidamiento.
	 *  Ejemplo:
	 *  raiz
	 *  	subdir1
	 *  		subdir2
	 *  			arch
	 *  
	 *  raiz.searFile(aux) retorna arch
	 *  Donde aux es tiene el mismo path que arch para
	 *  satisfacer el criterio de equals.
	 *  
	 *  @archBuscado: es un File que tiene el mismo path
	 *  que el arhivo buscado
	 *  
	 *  @return el archivo que tiene el path buscado dentro
	 *   de la jerarquia o null en caso de no encontrarlo
	 */
	public File searchFile(File archBuscado)  
	{
		File buscado= null;
		//Busco el nodo entre mis archivos

		//TODO SDR: Esto no esta muy bueno, porque usar un helper? estoy delegando responsabilidades que podria resolver yo y mejor
		/*SDR: Volvi a escribir el metodo, es todo lo que esta comentado, le agregue algunos controles para que sea mas eficiente

		if(!archBuscado.getPath().startsWith(this.getPath())){
			//Si no empieza como yo, directamente devuelvo null
			return null;
		}
		if(archBuscado.getPredecesor().getPath().equals(this.getPath())){
			//Si su predecesor tiene el mismo path que yo (podria ser yo)
			return files.get(archBuscado.getName());
		}
		
		//Lo busco en los subdirectorios
		//Esto todavia se puede hacer mejor
		//voy a buscarlo en mis subdir=>recursividad
		for(Directory subdir: directories.values()){
			buscado= subdir.searchFile(archBuscado);
			//Por cada subdir me fijo si lo encontre
			if (buscado != null)
				return buscado;
		}
		*/
		buscado=(File) NodeFinder.getNode(archBuscado, files.values());
		
		
		if (  buscado == null ){
			//No lo encontre voy a buscarlo en mis subdir=>recursividad
			for(Directory subdir: directories.values()){
				buscado= subdir.searchFile(archBuscado);
				//Por cada subdir me fijo si lo encontre
				if (buscado != null)
					return buscado;
			}
		}
		
		return buscado;
		
		/*
		FileSystemNodeInterface buscado= this.findNode(archBuscado.getPath());
		if(buscado == null){
			return null;
		}else{
			return (File) buscado;
		}*/
	}	
	
	/**
	 * Indica si un directorio contiene directamente a un nodo basandose en el nombre
	 * @param nodo El nodo que deseo verificar
	 * @return True: Lo contiene False: No lo contiene
	 */
	public boolean Contains(FileSystemNodeInterface nodo){
		if(nodo.isDirectory()){
			//Lo busco en directorios
			return(directories.containsKey(nodo.getName()));
		}else{
			//Lo busco en files
			return(files.containsKey(nodo.getName()));
		}
	}
	
	/**
	 * Busca un nodo en el directorio y sus subdirectorios
	 * @author SDR
	 * @param path: el path del nodo buscado sin el directorio actual
	 * EJ: si mi directorio es dir1 y busco /dir1/dir2/arch1
	 * path debe ser /dir2/arch1
	 * si el nodo hoja es un directorio, tiene que terminar con /
	 * EJ: dir1/dir2/
	 * @return Si lo encuentra el nodo buscado, sino null
	 */
	public FileSystemNodeInterface findNode(String path){
		StringTokenizer tokenizer= new StringTokenizer(path);
		
		String separador = System.getProperty("file.separator");		
		
		if(path.equals(separador)){
			//path = "\" quiere decir que estoy en el directorio que buscaba
			return this;
		}
		
		String aux = tokenizer.nextToken(separador);
		if(tokenizer.hasMoreElements() || path.endsWith(separador))
		{//Lo busco en directorios
			Directory subdir = directories.get(aux); 
			if(subdir==null){
				return null;
			}else{
				return subdir.findNode(tokenizer.nextToken(""));
			}
		}else{
			//Lo busco en archivos
			return files.get(aux);
		}
	}

	public Collection<File> getFiles() {
		return files.values();
	}
	
	public Collection<Directory> getDirectories() {
		return directories.values();
	}
}
