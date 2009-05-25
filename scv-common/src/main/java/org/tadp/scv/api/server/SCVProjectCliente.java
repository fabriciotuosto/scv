package org.tadp.scv.api.server;

import java.util.List;

import org.tadp.scv.api.Tag;
import org.tadp.scv.api.compare.DeltaGenerator;
import org.tadp.scv.api.compare.DeltaNodo;
import org.tadp.scv.api.compare.Revision;
import org.tadp.scv.api.exceptions.BothSourcesModifiedException;
import org.tadp.scv.api.exceptions.ConflictoNodosException;
import org.tadp.scv.api.exceptions.FirstNeedToMakeUpdateException;
import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.exceptions.TagNotFoundException;
import org.tadp.scv.api.exceptions.UnableToTagException;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.api.filesystem.server.FileVersionable;
import org.tadp.scv.api.filesystem.server.Versionable;

public class SCVProjectCliente {
	private String name;
	private Directory root;
	private DirectoryVersionable base;
	private SCVProjectServer proyectoServidor;
	private SCVServer server;
	

	/** 
	 * @param root
	 * @param proyectoServidor: es un parametro auxiliar para poder probar el
	 * proyecto sin necesidad de establecer un objeto mediador para la transmicion
	 * de mensajes entre proyectos del cliente y servidor. Cuando se definan las
	 * interfaces requeridas, en las entregas este parametro debera desaparacer. 
	 */
	public SCVProjectCliente(Directory root, SCVProjectServer proyectoServidor){
		this.base = versionarFileSystem(root);
		try {
			this.root= base.getDirectory().clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		this.proyectoServidor= proyectoServidor;
		
	}

	public DirectoryVersionable versionarFileSystem(Directory rootViejo){
		Directory dirToVersion = new Directory(rootViejo.getName(), rootViejo.getPredecesor());
		DirectoryVersionable rootNuevo = new DirectoryVersionable(dirToVersion);
		
		for (File fileViejo : rootViejo.getFiles()){
			try {
				fileViejo.setPredecesor(dirToVersion);
				rootNuevo.add(new FileVersionable(fileViejo));
			} catch (IntedToAddExistingNode e) {
				// No se, se supone que no deberia llega con un file repetido
				e.printStackTrace();
			}
		}
		
		for(Directory dirViejo : rootViejo.getDirectories()){
			try {
				dirViejo.setPredecesor(dirToVersion);
				rootNuevo.add(versionarFileSystem(dirViejo));
			} catch (IntedToAddExistingNode e) {
				// No se, se supone que no deberia llega con un file repetido
				e.printStackTrace();
			}
		}
		return rootNuevo;
			
	}
	
	/**
	 * 
	 * En este metodo solo hay que agregar el arbol de FilSystemNode
	 * a el directorio raiz => root que posee el proyecto
	 */
	public void add(FileSystemNode node)
	{
		node.setPredecesor(root);
		if (node.isFile())
		{
			root.add((File)node);		
		}
		if (node.isDirectory())
		{
			root.add((Directory)node);
		}		
	}
	
	public void commit(Directory node, String comentario) throws FirstNeedToMakeUpdateException {
		DeltaGenerator unDeltaGenerator= new DeltaGenerator();
		List<DeltaNodo> deltas= null;
		//Comparar versiones
		deltas= unDeltaGenerator.compare(base.searchDirectory(node), node);
		proyectoServidor.commit(deltas, comentario);
		this.actualizarFileSystem(deltas);
	}
	public void commit(Directory node) throws FirstNeedToMakeUpdateException {
		this.commit(node, "");
	}

	/*public void commit(File node) throws FirstNeedToMakeUpdateException {
		this.commit(node, "");
	}*/
	
	public void commit(File node/*, String comentario*/) throws FirstNeedToMakeUpdateException {
		DeltaGenerator unDeltaGenerator= new DeltaGenerator();
		DeltaNodo unDelta= null;
		//Comparar versiones
		unDelta= unDeltaGenerator.compare(base.searchFile(node), node);
		proyectoServidor.commit(unDelta);
		this.actualizarFileSystem(unDelta, true);
	}

	public void commitAll() throws FirstNeedToMakeUpdateException{
		this.commitAll("");
	}
	
	/**
	 * 
	 * Idem anterior pero a partir del nodo raiz
	 */
	public void commitAll(String comentario) throws FirstNeedToMakeUpdateException
	{
		this.commit(root, comentario);
	}

	/**
	 * @throws ConflictoNodosException 
	 * 
	 */
	public void update(File node) throws ConflictoNodosException{	
		DeltaGenerator unDeltaGenerator= new DeltaGenerator();
		DeltaNodo<File> unDelta= null;
		
		//Obtengo la combinacion path del nodo a actualizar, modificaciones
		//realizadas por el cliente.
		unDelta= unDeltaGenerator.compare(base.searchFile(node), node);
		
		//pido los cambios al servidor
		DeltaNodo updateDelServidor = proyectoServidor.update(unDelta);
		
		//aplico los cambios.
		this.actualizarFileSystem(updateDelServidor, true);
	}
	
	/**
	 * @throws ConflictoNodosException 
	 * @throws BothSourcesModifiedException 
	 * 
	 */
	public void update(Directory node) throws BothSourcesModifiedException{	
		DeltaGenerator unDeltaGenerator= new DeltaGenerator();
		List<DeltaNodo> deltas= null;
		
		//Obtengo la combinacion path del nodo a actualizar, modificaciones
		//realizadas por el cliente.
		deltas= unDeltaGenerator.compare(base.searchDirectory(node), node);
		
		//pido los cambios al servidor
		List<DeltaNodo> updateDelServidor = proyectoServidor.update(deltas);
		
		//aplico los cambios.
		this.actualizarFileSystem(updateDelServidor);
	}
	
	/**
	 * Dada una lista de deltas (revisiones asociadas a un nodo)
	 * se aplicann los cambios contenidos en las mismas a cada nodo
	 * de la copia base (sin modificaciones).
	 * Luego se obtiene mediante una cloanacion de los nodos decorados
	 * de la copia base la actualizacion de la copia de trabajo. 
	 * 
	 * @param deltas
	 */
	private void actualizarFileSystem(List<DeltaNodo> deltas){	
		for(DeltaNodo unDelta: deltas){
			this.actualizarFileSystem(unDelta, false);
		}
		try {
			root= base.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("Problemas, alguien se olvido de implementar clone()");
		}
	}
	
	/**
	 * Dado un delta (revision asociada a un nodo) se aplican los cambios
	 * contenido en la revision que se agrega al nodo del fileSystem del
	 * cliente.
	 * El parametro actualizarRoot se refiere a obtener el nodo actualizado
	 * a partir de los resultados generados por la aplicacion de las revisiones
	 * y reemplazar la copia de trabajo, este trabajo puede ser arduo y este
	 * metodo puede ser invocado sucesivas veces desde otro por lo tanto seria
	 * un gran overhead una actualizacion continua.
	 * 
	 * @param unDelta
	 * @param actualizarRoot
	 */
	@SuppressWarnings("unchecked")
	private void actualizarFileSystem(DeltaNodo unDelta, boolean actualizarRoot){
		//Busco el nodo a actualizar
		Versionable unVersionable= base.searchVersionable(unDelta.getPath());

		//Le agrego las revisiones que me proporciono el servidor
		List<Revision> revisiones = (List<Revision>)unDelta.getRevisiones();
		for(Revision r: revisiones){
			unVersionable.addRevision(r);
		}
		
		//Actualizacion hasta la ultima revision
		try {
			unVersionable.getToRevision(unVersionable.getNumberOfLastRevision());
			if( actualizarRoot )
				root= base.clone();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("Problemas, alguien se olvido de implementar clone()");
		} catch (IntedToAddExistingNode e) {
			e.printStackTrace();
		}
	}

	/**
	 * Idem anterior pero a partir del nodo raiz
	 * @throws BothSourcesModifiedException 
	 *   
	 */
	public void updateAll() throws BothSourcesModifiedException
	{
		update(root);
	}

	/**
	 * Crea un tag invocando al servidor, el tag se crea con las versiones
	 * actuales al momento de la invocacion
	 */
	public void createTag(String tagName) throws UnableToTagException
	{
		SCVProyect serverProyect = (SCVProyect) getServer().adquireProyect(getName(), getRoot());
		serverProyect.createTag(tagName);
	}

	/**
	 * Devuelve el tag con su contenido el cual es fijo
	 * sin embargo se puede revisar su historial e ir recorriendolo a partir de las version inicial
	 * hasta la del momento de creacion del tag
	 */
	public Tag getTag(String tagName) throws TagNotFoundException
	{
		SCVProyect serverProyect = (SCVProyect) getServer().adquireProyect(getName(), new Directory("serverRoot", null));
		return serverProyect.getTag(tagName);	
	}
	
	// A partir de aca son setters y getters
	public String getName(){
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	
	public Directory getRoot()
	{
		return this.root;
	}
	
	public DirectoryVersionable getBase(){
		return this.base;
	}

	
	public void setRoot(DirectoryVersionable base)
	{
		this.base= (DirectoryVersionable) base.getCopy();
		this.root = base.getDirectory();
	}

	
	public SCVServer getServer()
	{
		return server;
	}

	
	public void setServer(SCVServer server)
	{
		this.server = server;
	}
}