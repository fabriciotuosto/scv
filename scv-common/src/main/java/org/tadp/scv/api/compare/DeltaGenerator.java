package org.tadp.scv.api.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNodeInterface;
import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.api.filesystem.server.FileVersionable;

/**
 * Encapsula la logica de comparacion recursiva a travez de un directorio
 * usando tambien DirectoryComparator y FileComparator
 * 
 * 
 * @see DirectoryComparator
 * @see FileComparator
 * @see DeltaGenerator
 */

public class DeltaGenerator {
	
	/**
	 * Encapsula la logica de comparacion de dos directorios y sus subniveles.
	 * La comparacion se realiza entre nodos (archivos o directorios) teniendo en cuenta:
	 * Nodos eliminados: NodosCopiaBase - NodosComunes
	 * Nodos Agregados:	NodosCopiaDeTrabajo - NodosComunes
	 * 
	 * Por cada elemento en comun se vuelve a realizar una comparacion
	 * 
	 * @param base: es el directorio que contiene los archivos y directorios 
	 * antes de sufrir las modificaciones de la nueva version
	 * @param workingCopy: es el directorio que contiene los archivos y directorios
	 * luego de las modificacion dadas por la nueva version
	 * 
	 * @return: Devuelve una lista de DeltaNodos donde cada uno hace referencia a
	 * un nodo del proyecto y contiene los cambios realizados
	 */
	public List<DeltaNodo> compare(DirectoryVersionable base, Directory workingCopy) {
		DirectoryComparator unDirectoryComparator = new DirectoryComparator();
		List<DeltaNodo> listaDeCambios= new ArrayList<DeltaNodo>();
		
		//Agregos los deltas que existen entre los directorios en el nivel
		//actual- ejem: directorio agregados, archivos borrados, etc
		listaDeCambios.add(this.compareDirectory(base, workingCopy));
		
		//Agrego los deltas que puedan existir entre los archivos
		//que ambos directorios tengan en comun
		listaDeCambios.addAll(this.compareFiles(base, workingCopy));
		
		//Recorro los directorios en comun
		Map<String, FileSystemNodeInterface> listaDirectorios= unDirectoryComparator.getDirectoriesComunes(base.getDirectory(), workingCopy);
		for(FileSystemNodeInterface dirComun: listaDirectorios.values()){
			//Comparo el original y la copia y lo agrego a mi directorio Raiz
			listaDeCambios.addAll(this.compare(base.searchDirectory((Directory)dirComun), workingCopy.searchDirectory((Directory)dirComun)));
		}		
		
		return listaDeCambios;
	}
	
	/**
	 * 
	 * @param base: es el directorio en el cual residen los archivos
	 * sin cambios
	 * @param workingCopy: es el directorio en el cual residen los archivos
	 * modificados
	 * @param listaDeCambios: se agregaran elementos solo si alguna revision
	 * contiene diferencias, es decir, si se hallan cambios.
	 */
	private List<DeltaNodo> compareFiles(DirectoryVersionable base, Directory working){
		DirectoryComparator unDirectoryComparator = new DirectoryComparator();
		List<DeltaNodo> listaDeCambios= new ArrayList<DeltaNodo>();
		DeltaNodo<File> unDelta;
		
		//Recorro los archivos en comun
		Map<String, FileSystemNodeInterface> listaFiles= unDirectoryComparator.getFilesComunes(base.getDirectory(), working);
		for(FileSystemNodeInterface fileComun: listaFiles.values()){		
			//Genero el delta
			unDelta= this.compare(base.searchFile((File)fileComun), working.searchFile((File)fileComun));
			listaDeCambios.add(unDelta);		
		}
		return listaDeCambios;
	}
	
	public DeltaNodo<File> compare(FileVersionable base, File working){
		FileComparator unFileComparator= new FileComparator();
		
		Revision<File> revFile= new Revision<File>(base.getRevisionNumber(), null);
		revFile.addAll(unFileComparator.compare(base.getFile(), working));
		DeltaNodo<File> unDelta= new DeltaNodo<File>(base.getPath(), null);
		unDelta.addRevision(revFile);
		
		return unDelta;
	}
	
	private DeltaNodo<Directory> compareDirectory(DirectoryVersionable base, Directory workingCopy){
		DirectoryComparator unDirectoryComparator= new DirectoryComparator();
		DeltaNodo<Directory> unDelta;
		Revision<Directory> rev;
		
		//Saco las diferencias del nivel donde estoy
		rev = new Revision<Directory>(base.getRevisionNumber(), unDirectoryComparator.compare(base.getDirectory(), workingCopy));
		
		//Agregar a la lista a la lista
		unDelta= new DeltaNodo<Directory>(base.getPath(), null);
		unDelta.addRevision(rev);
		
		return unDelta;
	}
}
