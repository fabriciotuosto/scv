package org.tadp.scv.api.difference.directory;

import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.difference.DirectoryDifference;
import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.api.filesystem.server.FileVersionable;
import org.tadp.scv.api.filesystem.server.Versionable;


public class DirectoryRemoveNodeDifference extends DirectoryDifference
{
	public DirectoryRemoveNodeDifference(FileSystemNode nodoDiferencia){
		FileSystemNode aux;
		try {
			aux = nodoDiferencia.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Alguien se olvido de implementar clone");
		}
		aux.setPredecesor(null);
		this.setNodo(aux);
	}
	/**
	 * @author SDR
	 * toma el nodo de la diferencia, lo busca y lo quita del directorio
	 * @param dir El directorio sobre el que se aplicaran los cambios
	 */
	public void apply(Directory dir)
	{
		FileSystemNode nodo= getNodo();
		nodo.setPredecesor(dir);
		if (nodo.isDirectory())
		{
			dir.remove((Directory)nodo);
		}
		if (nodo.isFile())
		{
			dir.getFiles().remove((File)nodo);
		}
	}
	

	public void apply(Versionable<Directory> dir) throws IntedToAddExistingNode {
		FileSystemNode nodo= this.getNodo();
		nodo.setPredecesor(((DirectoryVersionable)dir).getDirectory());
		if(nodo.isDirectory()){
			DirectoryVersionable buscado= new DirectoryVersionable((Directory) nodo);
			((DirectoryVersionable) dir).remove(buscado);
		}else{
			FileVersionable buscado= new FileVersionable((File)nodo);
			((DirectoryVersionable) dir).remove(buscado);
		}
	}
	
	
	public Difference<Directory> clone() throws CloneNotSupportedException
	{
		return new DirectoryRemoveNodeDifference(getNodo().clone());
	}
	
	public void undo(Directory dir) {
		//Seteo el padre osea el directorio que me pasan por parametro.
		this.getNodo().setPredecesor(dir);
		
		if (this.getNodo().isDirectory()) {
			dir.add((Directory) this.getNodo());
		} else {
			dir.add((File) this.getNodo());
		}
	}
	public void undo(Versionable<Directory> dir) throws IntedToAddExistingNode {
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
}
