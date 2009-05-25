package org.tadp.scv.api.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.tadp.scv.api.Tag;
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

/**
 * @author Alejandro Es el proyecto del lado del servidor
 */
public class SCVProjectServer{
	private Map<String, Tag> tags = null;

	private DirectoryVersionable root;

	public SCVProjectServer() {
		super();
		tags = new HashMap<String, Tag>();
	}

	/**
	 * @param root
	 */
	public SCVProjectServer(Directory root) {
		this();
		this.root = versionarFileSystem(root);
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

	public void commit(List<DeltaNodo> listaDeltas) throws FirstNeedToMakeUpdateException{
		this.commit(listaDeltas, "");
	}
	
	/**
	 * 
	 * @param listaDeltas
	 * @throws FirstNeedToMakeUpdateException
	 */
	public void commit(List<DeltaNodo> listaDeltas, String comentario) throws FirstNeedToMakeUpdateException{
		// Verifico los nro de revisiones del cliente, para ver si primero debe hacer update
		for(DeltaNodo delta : listaDeltas){
			compararNroRevisiones(delta);
		}
		// No tiro excepcion, asi que hay que aplicar los cambios
		actualizarFileSystem(listaDeltas, comentario);
	}
	
	public void commit(DeltaNodo delta) throws FirstNeedToMakeUpdateException{
		this.commit(delta, "");
	}
	/**
	 * 
	 * @param delta
	 * @throws FirstNeedToMakeUpdateException
	 */	
	public void commit(DeltaNodo delta, String comentario) throws FirstNeedToMakeUpdateException{
		// Verifica que el nro de revision del cliente no sea menor que el del server.
		compararNroRevisiones(delta);
		
		// No tiro excecpcion, asi que hay que aplicar los cambios
		actualizarFileSystem(delta, comentario);		
	}
	
	/**
	 * 
	 * @param deltas
	 */
	private void actualizarFileSystem(List<DeltaNodo> deltas, String comentario){
		for(DeltaNodo unDelta: deltas){
			actualizarFileSystem(unDelta, comentario);
		}
	}
	
	/**
	 * 
	 * @param delta
	 */
	@SuppressWarnings("unchecked")
	private void actualizarFileSystem(DeltaNodo delta, String comentario){
		// Busco el nodo a actualizar
		Versionable<FileSystemNode> unVersionable= this.findNode(delta.getPath());
		List<Revision> revisiones = (List<Revision>)delta.getRevisiones();
		for(Revision r: revisiones){
			r.setComment(comentario);
			unVersionable.addRevision(r);
		}
		try {
			unVersionable.getToRevision(unVersionable.getNumberOfLastRevision());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IntedToAddExistingNode e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author Alejandro Sanchez
	 * @param delta
	 * @throws FirstNeedToMakeUpdateException
	 */
	@SuppressWarnings("unchecked")
	private void compararNroRevisiones(DeltaNodo delta) throws FirstNeedToMakeUpdateException{
		Versionable<FileSystemNode> node = this.findNode(delta.getPath());
		if (node.getNumberOfLastRevision() > delta.getLastRevision().getRevisionNumber()){
			throw new FirstNeedToMakeUpdateException();
		}
	}
	/**
	 * @author Alejandro Sanchez
	 * @param listaNodos:
	 *            Lista de DeltaNodos con el ultimo nro de revision, y sus
	 *            posibles diferencias con la copia base local
	 * @throws BothSourcesModifiedException:
	 *             Conflicto entre version modificada del cliente y version
	 *             modificada del servidor
	 */
	public List<DeltaNodo> update(List<DeltaNodo> listaDeltas) throws BothSourcesModifiedException{
		List<DeltaNodo> nodosUpdate = new ArrayList<DeltaNodo>();
		List<Versionable> nodosConflicto = new ArrayList<Versionable>();
		DeltaNodo deltaForCliente = null; //delta para devolver con las revisiones faltantes en el cliente
		
		for(DeltaNodo delta : listaDeltas){
			try{
				if((deltaForCliente = update(delta))!= null)
					nodosUpdate.add(deltaForCliente);
			}catch(ConflictoNodosException e){
				nodosConflicto.add(e.getNodoConflicto());
			}
		}
		if (nodosConflicto.size() > 0)
			throw new BothSourcesModifiedException("Hay nodos en conflicto", nodosConflicto);
		return nodosUpdate;
	 }		
		
	/**
	  * @author Alejandro Sanchez
	  * @param delta
	  * @return Otro delta con las revisiones que le faltan al cliente, o null si
	  *         no hay que actualizar ese nodo
	  * @throws ConflictoNodosException
	  */

	@SuppressWarnings("unchecked")
	public DeltaNodo update(DeltaNodo delta) throws ConflictoNodosException {
		Versionable<FileSystemNode> node = this.findNode(delta.getPath());
		if (node.getNumberOfLastRevision() > delta.getLastRevision().getRevisionNumber()){
			if (delta.getLastRevision().getCantidadDifferencias() > 0){
				// Conflicto: ej. Cliente v3' y Server v4
				throw new ConflictoNodosException("Nodos en conflicto", node);
			}
			else{
				DeltaNodo deltaCliente = new DeltaNodo(delta.getPath(), null);
				// El cliente esta out-of-date: ej. Cliente v3 y Server v4
				for(int i=delta.getLastRevision().getRevisionNumber(); 
				 					i<node.getNumberOfLastRevision(); i++){
						 deltaCliente.addRevision(node.getRevision(i));
				}
				return deltaCliente;
			}
		}
		else{
			 // Si el nro del nodo es igual, no hay que actualizar
			 return null;
		}
	}

	/**
	 * 
	 */
	public void createTag(String tagName) throws UnableToTagException {
		Tag tag = null;
		tag = new Tag(tagName, (DirectoryVersionable) root.getCopy());
		tags.put(tagName, tag);
	}

	/**
	 * 
	 */
	public Tag getTag(String tagName) throws TagNotFoundException {
		Tag tag = tags.get(tagName);
		if (tag == null) {
			throw new TagNotFoundException("El tag " + tagName + " no se encuentra en este proyecto");
		}
		return tag;
	}

	public DirectoryVersionable getRoot() {
		return root;
	}

	/**
	 * @author SDR
	 * @param path: el path del nodo buscado (incluye el nodo raiz)
	 * @return el nodo encontrado o null
	 * 
	 * Esta funcion parsea el primer directorio del path (que es el raiz) y
	 * busca el resto en root
	 */
	public Versionable findNode(String path) {
		StringTokenizer tokenizer = new StringTokenizer(path);
		String separador = System.getProperty("file.separator");

		tokenizer.nextToken(separador);
		return root.findNode(tokenizer.nextToken(""));
	}
	
	/**
	 * Este metodo implementa la funcionalidad de obtener de un nodo 
 	 * cualquiera que posea el cliente su imagen tal como estaba en una
 	 * revision anterior.
	 * 
	 * @param nodo: parametro que se utiliza para realizar un QueryByExample
	 * entre los nodos del sistema.
	 * @param nroRevision: numero de la revision a la que quiero volver.
	 * @return: un Versionable es decir un objeto que soporta el uso de revisiones
	 * y el cual sale del servidor con la capacidad para poder permutar a cualquier 
	 * revision anterior.
	 * Es una gran carga para el trafico de red, pero como funcionalidad esta bueno
	 * sino conviene simplemente se puede devolver el objeto que decora... en la
	 * version correspondiente.
	 */
	public Versionable getToRevision(FileSystemNode nodo, int nroRevision){
		//Busco el elemento a devolver
		Versionable unVersionable= root.searchVersionable(nodo);
		
		//Chequeos
		if(unVersionable== null ) 
			throw new IllegalArgumentException("El nodo no esta versionado");
		
		//Obtengo una copia para no modificar el file system
		Versionable copia= unVersionable.getCopy();
		
		//Obtengo la version requerida
		try {
			copia.getToRevision(nroRevision);
		} catch (IntedToAddExistingNode e) {
			e.printStackTrace();
		}
		
		return copia;
	}
	
	/**
	 * Este metodo intenta responder a la necesidad de proveer un
	 * historial de los nodos del sistema de archivos. Pasa las revisiones
	 * o historia de cambios de cada nodo requerido.
	 * 
	 * @param nodo: se usa para realizar un query by example del nodo
	 * de un nodo del sistema remoto que contiene las revisiones
	 * @param nroRevInicial: numero de la revision inicial, es a partir
	 * de la cual se considera la historia del nodo, siempre empieza en 1
	 * @param nroRevFinal: numero de la revision final;
	 * 
	 * @return: una lista de revisiones, donde cada revision tiene la
	 * informacion pertinente para cada cambio ocurrido en el nodo.
	 */
	public List<Revision> getRevisions(FileSystemNode nodo, int nroRevInicial, int nroRevFinal){
		List<Revision> historia= new ArrayList<Revision>();
		
		//buscar el nodo
		Versionable unVersionable= root.searchVersionable(nodo);
		if(unVersionable == null)
			throw new IllegalArgumentException("El nodo no esta versionado");
		
		//Poner las revisiones 
		for(int i= nroRevInicial-1; i < nroRevFinal; i++){
			try {
				historia.add(unVersionable.getRevision(i).clone());
			} catch (CloneNotSupportedException e) {
				//Lo pongo asi porque el clone esta implementado pero es una excepcion
				//que me obligan a atrapar.
				throw new RuntimeException("Alguien se olvido de imp clone");
			}
		}
		
		return historia; 
	}
}
