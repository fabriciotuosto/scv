package org.tadp.scv.api.filesystem.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.tadp.scv.api.builder.VersionableNodeBuilder;
import org.tadp.scv.api.compare.Revision;
import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNodeInterface;
import org.tadp.scv.api.helpers.NodeFinder;

public class DirectoryVersionable implements Versionable<Directory>
{
	private List<Revision<Directory>> revisions;
	private VersionableNodeBuilder<Directory> directoryBuilder;
	private Directory decorated;
	private Map<String, DirectoryVersionable> directories;
	private Map<String, FileVersionable> files;
	private int revisionActual;

	public DirectoryVersionable(Directory directory)
	{
		try {
			decorated = directory.clone(); 
			revisions = new ArrayList<Revision<Directory>>();
			directoryBuilder = new VersionableNodeBuilder<Directory>();
			directories= new HashMap<String, DirectoryVersionable>();
			files= new HashMap<String, FileVersionable>();
			revisionActual= 0;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unable to create instance");
		}
	}
	
	/**
	 * Este constructor lo uso para crear el tag, ya que usa el clone que devuelve un directory Impl, y despues 
	 * clona la las revisiones para poder agregarselas al Directorio
	 * @param directory
	 * @param revisions2
	 */
	public DirectoryVersionable(Directory directory, List<Revision<Directory>> revisions)
	{
		try {
			decorated = directory.clone();
			this.revisions = revisions;
			directories= new HashMap<String, DirectoryVersionable>();
			files= new HashMap<String, FileVersionable>();
			revisionActual= 0;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unable to create instance");
		}
	}

	/**
	 * Este metodo devuelve una copia del directory con el contenido del File 
	 * hasta la revision dada
	 * 
	 * @param revisionNumber
	 * @return
	 * @throws IntedToAddExistingNode 
	 * @throws Exception
	 */
	public Directory getToRevision(int revisionNumber) throws IntedToAddExistingNode
	{
		if (revisionNumber > revisions.size() || revisionNumber < 0)
		{
			throw new IllegalArgumentException("El numero de revision no puede ser mayor a la revision maxima del archivo");
		}
		
		//Cargo el builder con el nodo a modificar
		directoryBuilder.setNode((Versionable<Directory>)this);
		
		
		//Veo si quieren aplicar nuevas revisiones o retroceder.
		@SuppressWarnings("unused")
		DirectoryVersionable dir= null;
		if(revisionNumber > revisionActual){
			for(int i = revisionActual ; i < revisionNumber; i++)
			{
				directoryBuilder.append(revisions.get(i).getDifferences());
			}
			//aplico cambios
			directoryBuilder.build();
		}else if (revisionNumber < revisionActual){
			for(int i = revisionNumber ; i < revisionActual; i++)
			{
				directoryBuilder.append(revisions.get(i).getDifferences());
			}
			//deshago cambios
			directoryBuilder.undoBuild();
		}
		this.revisionActual= revisionNumber;
		
		return this.getDirectory();
	}
	
	public void addRevision(Revision<Directory> rev)
	{
		rev.setRevisionNumber(this.getNumberOfLastRevision()+1);
		rev.setDate(Calendar.getInstance().getTime());
		revisions.add(rev);
	}
	/**
	 * En un directorio bien formado, este metodo y
	 * getNumberOfLastRevision deberian retornar el mismo
	 * resultado.
	 */
	public int getRevisionNumber()
	{
		return revisionActual;
	}
	
	/**
	 * Metodo para el control del sistema, que algunas veces
	 * se utiliza para realizar operaciones internas (·manganetas").
	 */
	public int getNumberOfLastRevision()
	{
		if (revisions.size() == 0)
			return 0;
		return revisions.get(revisions.size()-1).getRevisionNumber();
	}

	/**
	 * Este clone va a devolver el estado inicial del directorio sin tener
	 * en cuenta las modificaciones  en las revisiones al igual que en FileImpl
	 */
	@Override
	public Directory clone() throws CloneNotSupportedException
	{	//SDR: si no se puede tocar la clase directory, la clonacion va a tener que ir aca
		return decorated.clone();
	}

	/**
	 * Lo uso para el tag ya que el clone es necesario en otro lado para generar
	 * el estado del directorio hasta alguna revision ya que de esa forma consegui una forma generica de
	 * hacer un builder indistintamente del tipo de FileSystemNode que se este generando
	 * 
	 * @return
	 */
	public Versionable getCopy()
	{
		DirectoryVersionable dir= null;
		try{
			dir = new DirectoryVersionable(this.clone());
			dir.setRevisionActual(this.revisionActual);
			for(Revision<Directory> revision : revisions)
			{
					dir.addRevision(revision.clone());
			}
			for(DirectoryVersionable subdir: directories.values()){
				DirectoryVersionable copia= ((DirectoryVersionable) subdir.getCopy());
				dir.directories.put(copia.getName(), copia);
			}
			for(FileVersionable unFile: files.values()){
				FileVersionable copia=(FileVersionable) unFile.getCopy(); 
				dir.files.put(copia.getName(), copia);
			}
		
		} catch (CloneNotSupportedException e) {;
			throw new RuntimeException("Alguien se olvido de clone");
		}
		return dir;
	}
	
	/**
	 * @return directorio que esta decorando la clase actual
	 */
	public Directory getDirectory(){
		return decorated;
	}
	
	/**
	 * Añade un subdirectorio al directorio actual y 
	 * al que decorado
	 * @throws IntedToAddExistingNode 
	 * cuando se intenta añadir un nodo que ya existe en la Directorio
	 */
	public void add(DirectoryVersionable unDirectorio) throws IntedToAddExistingNode{
		boolean result2=directories.containsKey(unDirectorio.getName());
		boolean result1= false;
		
		if(!result2){
			 if(result1= decorated.add(unDirectorio.getDirectory())){
				 directories.put(unDirectorio.getName(), unDirectorio);
			 }
		}
		
		if ( !result1 || result2 )
			throw new IntedToAddExistingNode("Ya existe el directorio: " + unDirectorio.getPath());
	}
	
	/**
	 * Añade un arhivo al directorio actual y 
	 * al que decorado
	 * 
	 * @throws IntedToAddExistingNode 
	 * cuando se intenta añadir un nodo que ya existe en la Directorio
	 */
	public void add(FileVersionable unArchivo) throws IntedToAddExistingNode{
		boolean result2= files.containsKey(unArchivo.getName());
		boolean result1= false;
				
		if(!result2){
			if(result1 = decorated.add(unArchivo.getFile())){
				files.put(unArchivo.getName(), unArchivo);
			}
		}

		if (  !result1 || result2 )
			throw new IntedToAddExistingNode("Ya existe el archivo: " + unArchivo.getPath());
	}
	
	public boolean remove(DirectoryVersionable unDirectorio){
		boolean result1= false;
		boolean result2= false;
		
		if( result2 = directories.containsKey(unDirectorio.getName())){
			directories.remove(unDirectorio.getName());
			result1= decorated.remove(unDirectorio.getDirectory());
		}
		
		return result1 && result2;
	}
	
	public boolean remove(FileVersionable unArchivo){
		boolean result1= false;
		boolean result2= false;
		
		if(result2= files.containsKey(unArchivo.getName())){
			files.remove(unArchivo.getName());
			result1= decorated.remove(unArchivo.getFile());
		}	
		
		return result1 && result2;
	}
		
	public Collection<DirectoryVersionable> getDirectoriesVersionable(){
		return Collections.unmodifiableCollection(directories.values());
	}
	
	public Collection<FileVersionable> getFilesVersionable(){
		return Collections.unmodifiableCollection(files.values());
	}

	public Map<String, DirectoryVersionable> getDirectoriesVersionableMap(){
		//TODO SDR: Deberia ser unmodifiableMap??
		//En algun lugar lo esta usando directamente?
		return directories;
	}
	
	public Map<String, FileVersionable> getFilesVersionableMap(){
		//TODO SDR: Deberia ser unmodifiableMap??
		//En algun lugar lo esta usando directamente?
		return files;
	}
	
	
	public String getName() {
		return decorated.getName();
	}
	
	public boolean isDirectory() {
		return decorated.isDirectory();
	}
	
	public boolean isFile() {
		return decorated.isFile();
	}
	
	public void setName(String name) {
		decorated.setName(name);
	}
	
	//TODO SDR: importante! Esto no viola el contrato de Equals??
	//No esta comparando las versiones, osea que 2 con versiones diferentes dan igual..
	public boolean equals(Object o) {
		return (o instanceof DirectoryVersionable) &&
		 		decorated.equals(((DirectoryVersionable)o).getDirectory());
	}

	public String getPath() {
		return decorated.getPath();
	}

	public Directory getPredecesor() {
		return decorated.getPredecesor();
	}

	public int hashCode() {
		return decorated.hashCode();
	}

	public void setPredecesor(Directory root) {
		decorated.setPredecesor(root);
	}

	public String toString() {
		return decorated.toString();
	}
	
	public Revision<Directory> getRevision(int nroRevision){
		if (nroRevision > revisions.size())
			throw new IllegalArgumentException("El numero de revision no puede ser mayor a la revision maxima del archivo");

		return revisions.get(nroRevision);
	}
	
	public void setRevisionActual(int revisionActual) {
		this.revisionActual = revisionActual;
	}

	/**
	 * Retorna un Nodo de la jerarquia de este directorio
	 * enmascarado bajo la interfaz Versioable.
	 * 
	 * @param path: es el path del nodo buscado
	 * @return
	 */
	public Versionable findNode(String path){
		StringTokenizer tokenizer= new StringTokenizer(path);
		
		String separador = System.getProperty("file.separator");		
		
		if(path.equals(separador)){
			//path = "\" quiere decir que estoy en el directorio que buscaba
			return this;
		}
		
		String aux = tokenizer.nextToken(separador);
		if(tokenizer.hasMoreElements() || path.endsWith(separador))
		{//Lo busco en directorios
			DirectoryVersionable subdir = directories.get(aux);
			if(subdir == null){
				return null;
			}else{
				return subdir.findNode(tokenizer.nextToken(""));
			}
		}else{
			//Lo busco en archivos
			return files.get(aux);
		}
	}
	
	/**
	 * Retorna un FileVersionable si es que existe en el 
	 * DirectorioVersionables o sus subdirectorios, en cualquier
	 *  nivel de anidamiento.
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

	//TODO: SDR: Ver en Directory
	public FileVersionable searchFile(File archBuscado)  
	{

		FileVersionable buscado= null;
		//Busco el nodo entre mis archivos
		buscado=(FileVersionable) NodeFinder.getNode(archBuscado, files.values());
		
		
		if (  buscado == null ){
			//No lo encontre voy a buscarlo en mis subdir=>recursividad
			for(DirectoryVersionable subdir: directories.values()){
				buscado= subdir.searchFile(archBuscado);
				//Por cada subdir me fijo si lo encontre
				if (buscado != null)
					return buscado;
			}
		}
		
		return buscado;		 
		/*
		 * Comentado por SDR, uso el metodo findNode que hace lo mismo
		Versionable buscado= this.findNode(archBuscado.getPath());
		if(buscado == null){
			//No lo encontre
			return null;
		}else{
			return (FileVersionable) buscado;
		}		
		*/
	}
	
	/**
	 * Retorna un DirectorioVersionable si es que esta presente
	 * entre los subdirectorios de este nodo, sin importar el 
	 * nivel de anidamiento en la jerarquia.
	 * Ejemplo:
	 * raiz
	 * 	subdir1
	 * 		subdir2
	 * 
	 * raiz.searchDirectory(aux) retorna subdir2
	 * Donde aux tiene el mismo path que subdir2 para satisfacer
	 * el criterio de equals
	 * 
	 * @dirBuscado es un directorio que tiene el mismo path que el
	 * nodo buscado
	 * 
	 * @return el Directorio que tiene el mismo path que el 
	 * proporcionado o null en caso de no encontrar nada.
	 */

	//TODO: SDR: Ver en Directory
	public DirectoryVersionable searchDirectory(Directory dirBuscado)  
	{
		DirectoryVersionable buscado= null;
		
		//Veo si soy yo
		if (this.getPath().equals(dirBuscado.getPath()))
			return this;
		
		//Lo busco entre mis subdirectorios
		buscado= (DirectoryVersionable) NodeFinder.getNode(dirBuscado, this.directories.values());
		
		if (  buscado == null ){
			//No lo encontre voy a buscarlo en mis subdir=>recursividad
			for(DirectoryVersionable subdir: this.directories.values()){
				buscado= subdir.searchDirectory(dirBuscado);
				//Por cada subdir me fijo si lo encontre
				if (buscado != null)
					return buscado;
			}
		}
		return buscado;
	
			
		//Comentado por SDR, uso el metodo findNode que hace lo mismo 
		 
		/*Versionable buscado= this.findNode(dirBuscado.getPath());
		if(buscado == null){
			//No lo encontre
			return null;
		}else{
			return (DirectoryVersionable) buscado;
		}
		*/
	}
	
	/**
	 * Busca si existe el nodo en alguna parte de la jerarquia
	 * de este directorio.
	 * 
	 * @param nodo: es un nodo del sistema de archivos que tiene el
	 * mismo path que el nodo buscado.
	 * 
	 * @return
	 */
	public Versionable searchVersionable(FileSystemNodeInterface nodo){
		Versionable buscado= null;
		
		if (nodo.isDirectory())
			buscado= this.searchDirectory((Directory)nodo);
		else
			buscado= this.searchFile((File)nodo);
		
		return buscado;
	}
	
	/**
	 * Este es un metodo muy dependiente de la formacion del path
	 * Busca si existe el nodo en alguna parte de la jerarquia
	 * de este directorio.
	 * 
	 * @param path: es la ruta de acceso del nodo buscado
	 * 
	 * @return
	 */
	public Versionable searchVersionable(String path){
		FileSystemNodeInterface nodo= null;
		
		//Analizo el path para ver que es... y creo un auxiliar para la busqueda
		if( path.charAt(path.length()-1) == System.getProperty("file.separator").charAt(0)){
			//Es directorio porque terminan con "\" tengo que sacarle la "\"
			//porque el nombre no puede terminar con "\"
			nodo= new Directory(path.substring(0, path.length()-1), null);
		}else{
			//Es directorio porque terminan con "\"
			nodo= new File(path, null);
		}
		
		return this.searchVersionable(nodo);
	}

	public List<Revision<Directory>> getRevisions()
	{
		return revisions;
	}
}
