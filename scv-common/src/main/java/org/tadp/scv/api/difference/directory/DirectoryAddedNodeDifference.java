package org.tadp.scv.api.difference.directory;

import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.difference.DirectoryDifference;
import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.FileSystemNodeInterface;
import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.api.filesystem.server.FileVersionable;
import org.tadp.scv.api.filesystem.server.Versionable;

/**
 * @author Fabricio
 * 
 */
public class DirectoryAddedNodeDifference extends DirectoryDifference {
	public DirectoryAddedNodeDifference(FileSystemNode nodoDiferencia) {
		//Le saco el padre porque sino se mandan una parte mas del arbol
		//del file system que no quiero
		FileSystemNode aux;
		try {
			aux = nodoDiferencia.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("Alguien se olvido de implementar clone");
		}
		aux.setPredecesor(null);
		this.setNodo(aux);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tadp.scv.api.difference.Difference#apply(org.tadp.scv.api.filesystem.FileSystemNode)
	 */
	/**
	 * @author SDR toma el nodo de la diferencia y lo agrega a dir
	 * @param dir
	 *            El directorio sobre el que se aplicaran los cambios
	 */
	public void apply(Directory dir) {
		//Seteo el padre osea el directorio que me pasan por parametro.
		this.getNodo().setPredecesor(dir);
		
		if (this.getNodo().isDirectory()) {
			dir.add((Directory) this.getNodo());
		} else {
			dir.add((File) this.getNodo());
		}
	}

	public Difference<Directory> clone() throws CloneNotSupportedException {
		return new DirectoryAddedNodeDifference(getNodo().clone());
	}

	public void apply(Versionable<Directory> dir) throws IntedToAddExistingNode {
		//Seteo el padre osea el directorio que me pasan por parametro.
		this.getNodo().setPredecesor(((DirectoryVersionable)dir).getDirectory());
		
		if (this.getNodo().isDirectory()) {
			this.addNodesRecursively((DirectoryVersionable) dir,(Directory) this.getNodo());
		} else {
			((DirectoryVersionable) dir).add(new FileVersionable((File) this.getNodo()));
		}
	}

	private void addNodesRecursively(DirectoryVersionable dir,Directory directory) throws IntedToAddExistingNode {
		DirectoryVersionable subDirVersionable;
		
		//Creo mi subdirectorio pero VACIO
		subDirVersionable = new DirectoryVersionable(new Directory(directory
				.getName(), directory.getPredecesor()));
		
		/*----------------------------------------------------------------
		 * Completo el subdir creado con elementos que aguantan
		 * el Versionado
		 */
		//Relleno mi subdirectorio llamando a este mismo metodo recursivamente
		for (Directory subdirectorio : directory.getDirectories())
			this.addNodesRecursively(subDirVersionable, subdirectorio);
		
		//Ahora versiono y le cuelgo los files a mi subdirectorio
		for (File unArchivo : directory.getFiles()){
			FileVersionable subArchivo= new FileVersionable(unArchivo);
			subDirVersionable.add(subArchivo);
		}
		/*------------------------------------------------------------------*/
		
		//Me cuelgo el subdirectorio que cree y rellene 
		dir.add(subDirVersionable);
	}

	public void undo(Directory dir) {
		FileSystemNode node= this.getNodo();
		if(node.isDirectory())
			dir.remove((Directory)node);
		else
			dir.remove((File)node);
	}

	public void undo(Versionable<Directory> dir) {
		FileSystemNodeInterface node= this.getNodo();
		node.setPredecesor(((DirectoryVersionable)dir).getDirectory());
		if(node.isDirectory())
			((DirectoryVersionable)dir).remove(new DirectoryVersionable((Directory)node));
		else
			((DirectoryVersionable)dir).remove(new FileVersionable((File)node));
	}
}
