package org.tadp.scv.api.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.difference.directory.DirectoryAddedNodeDifference;
import org.tadp.scv.api.difference.directory.DirectoryRemoveNodeDifference;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNodeInterface;

public class DirectoryComparator implements Comparator<Directory> {

	/**
	 * Encapsula la logica de comparacion de dos directorios.
	 * La comparacion se realiza entre nodos (archivos o directorios) teniendo en cuenta:
	 * Los que permanenecen en comun entre ambas copias
	 * Nodos eliminados: NodosCopiaBase - NodosComunes
	 * Nodos Agregados:	NodosCopiaDeTrabajo - NodosComunes
	 * 
	 * @param base: es el directorio que contiene los archivos y directorios 
	 * antes de sufrir las modificaciones de la nueva version
	 * @param workingCopy: es el directorio que contiene los archivos y directorios
	 * luego de las modificacion dadas por la nueva version
	 * 
	 * @return: lista de nodos eliminados y agregados respectos de los directorios comparados
	 * en el nivel mas superior, el nivel dado.
	 */
	public List<Difference<Directory>> compare(Directory base, Directory workingCopy) {
		List<Difference<Directory>> differences = new ArrayList<Difference<Directory>>();
		
		// establecer los nodos eliminados
		//los nodos que estan en base pero no en working
		differences.addAll(nodosEliminados(base, workingCopy));

		// pongo en la lista los nodos agregados
		//los nodos que estan en la working pero no en la base
		differences.addAll(nodosAgregados(base, workingCopy));

		return differences;
	}
	
	
	/**
	 * La unico significativo que aporta este metodo es la encapsulacion de los nodos
	 * en objetos DirectoryRemoveNodeDifference.
	 * NodosEliminados= NodosBase - NodosComunes
	 *  
	 */
	private List<Difference<Directory>> nodosEliminados(Directory base, Directory workingCopy) {
		List<Difference<Directory>> differences = new ArrayList<Difference<Directory>>();
		
		//Obtengo los files en comun 
		Map<String, FileSystemNodeInterface> comumes = this.getFilesComunes(base, workingCopy);
		
		//Obtengo las keys eliminadas
		Map<String, File> filesBase= base.getFilesMap(); 
		Set<String> keysEliminadas = new HashSet<String>(filesBase.keySet());
		keysEliminadas.removeAll(comumes.keySet());

		//Cargo las diferencias
		for(String nombre: keysEliminadas){
			differences.add(new DirectoryRemoveNodeDifference(filesBase.get(nombre)));
		}
		
		//Obtengo los directories en comun 
		comumes = this.getDirectoriesComunes(base, workingCopy);
		
		//Obtengo las keys eliminadas
		Map<String, Directory> directoriesBase= base.getDirectoriesMap(); 
		keysEliminadas = new HashSet<String>(directoriesBase.keySet());
		keysEliminadas.removeAll(comumes.keySet());

		//Cargo las diferencias
		for(String nombre: keysEliminadas){
			differences.add(new DirectoryRemoveNodeDifference(directoriesBase.get(nombre)));
		}
		
		return differences;
	}
	
	/**
	 * La unico significativo que aporta este metodo es la encapsulacion de los nodos
	 * en objetos DirectoryAddedNodeDifference.
	 * NodosAgregados= NodosWorking - NodosComunes
	 * 
	 */
	private List<Difference<Directory>> nodosAgregados(Directory base, Directory workingCopy) {
		List<Difference<Directory>> differences = new ArrayList<Difference<Directory>>();
		
		//Obtengo los files en comun 
		Map<String, FileSystemNodeInterface> comumes = this.getFilesComunes(base, workingCopy);
		
		//Obtengo las keys eliminadas
		Map<String, File> filesWorking= workingCopy.getFilesMap(); 
		Set<String> keysAgregadas = new HashSet<String>(filesWorking.keySet());
		keysAgregadas.removeAll(comumes.keySet());

		//Cargo las diferencias
		for(String nombre: keysAgregadas){
			differences.add(new DirectoryAddedNodeDifference(filesWorking.get(nombre)));
		}
		
		//Obtengo los directorios en comun 
		comumes = this.getDirectoriesComunes(base, workingCopy);
		
		//Obtengo las keys agregadas
		Map<String, Directory> directoriesWorking= workingCopy.getDirectoriesMap(); 
		keysAgregadas = new HashSet<String>(directoriesWorking.keySet());
		keysAgregadas.removeAll(comumes.keySet());

		//Cargo las diferencias
		for(String nombre: keysAgregadas){
			differences.add(new DirectoryAddedNodeDifference(directoriesWorking.get(nombre)));
		}
		
		return differences;
	}
	
	public Map<String, FileSystemNodeInterface> getDirectoriesComunes(Directory base, Directory workingCopy){
		Map<String, FileSystemNodeInterface> nodosBase = new HashMap<String, FileSystemNodeInterface>();
		Map<String, FileSystemNodeInterface> nodosWorkingCopy = new HashMap<String, FileSystemNodeInterface>();
		Map<String, FileSystemNodeInterface> nodosComunes = new HashMap<String, FileSystemNodeInterface>();

		// obtener conjunto de subdirectorios y archivos del Directory Base
		nodosBase.putAll(base.getDirectoriesMap());

		// obtener conjunto de subdirectorios y archivos del Directory
		// workingCopy
		nodosWorkingCopy.putAll(workingCopy.getDirectoriesMap());
		
		//Dejo solo los nombres que coinciden
		Set<String> keysComunes= nodosBase.keySet();
		keysComunes.retainAll(nodosWorkingCopy.keySet());
		
		//Cargo los nodos con esos nombres al mapa
		for(String nombre: keysComunes){
			nodosComunes.put(nombre, nodosBase.get(nombre));
		}
		
		return nodosComunes;
	}

	public Map<String, FileSystemNodeInterface> getFilesComunes(Directory base, Directory workingCopy){
		Map<String, FileSystemNodeInterface> nodosBase = new HashMap<String, FileSystemNodeInterface>();
		Map<String, FileSystemNodeInterface> nodosWorkingCopy = new HashMap<String, FileSystemNodeInterface>();
		Map<String, FileSystemNodeInterface> nodosComunes = new HashMap<String, FileSystemNodeInterface>();

		// obtener conjunto de subdirectorios y archivos del Directory Base
		nodosBase.putAll(base.getFilesMap());

		// obtener conjunto de subdirectorios y archivos del Directory
		// workingCopy
		nodosWorkingCopy.putAll(workingCopy.getFilesMap());
		
		//Dejo solo los nombres que coinciden
		Set<String> keysComunes= nodosBase.keySet();
		keysComunes.retainAll(nodosWorkingCopy.keySet());
		
		//Cargo los nodos con esos nombres al mapa
		for(String nombre: keysComunes){
			nodosComunes.put(nombre, nodosBase.get(nombre));
		}
		
		return nodosComunes;
	}
}
