package org.tadp.scv.api.compare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.difference.DirectoryDifference;
import org.tadp.scv.api.difference.directory.DirectoryAddedNodeDifference;
import org.tadp.scv.api.difference.directory.DirectoryRemoveNodeDifference;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNodeInterface;


public class DirectoryComparatorTest {
	private Directory base;
	private Directory workingCopy;
	private DirectoryComparator comparator;

	@Before
	public void setUp() throws Exception {
		
		comparator= new DirectoryComparator();
		base= new Directory("raiz", null);
		
		//Agrego 3 subdirectorios a la copia base
		Directory subdirectorio= new Directory("subdir1", base);
		base.add(subdirectorio);
		subdirectorio= new Directory("subdir2", base);
		base.add(subdirectorio);
		subdirectorio= new Directory("subdir3", base);
		base.add(subdirectorio);
		
		//Agrego 3 archivos
		File file= new File("archivo1", base);
		base.add(file);
		file= new File("archivo2", base);
		base.add(file);
		file= new File("archivo3", base);
		base.add(file);
		
		//Para empezar las dos copias contienen lo mismo
		workingCopy= base.clone();
	}

	@After
	public void tearDown() throws Exception {
		base= null;
		workingCopy= null;
	}

	@Test
	public final void testCompare(){
		List<Difference<Directory>> differences;
		File unArchivo;
		Directory unDirectorio;
		
		//Comparo los directorios sin haber hecho ninguna modificacion
		differences=  comparator.compare(base, workingCopy);
		assertEquals("Comparacion de iguales no hay diferencias", differences.size(), 0);
		
		//Elimino un archivo
		Collection<File> files= workingCopy.getFiles();
		unArchivo=files.iterator().next(); 
		workingCopy.remove(unArchivo);
		differences=  comparator.compare(base, workingCopy);
		assertEquals("Comparacion Archivo Eliminado hay 1 objeto en la lista", differences.size(), 1);
		assertTrue("El objeto es del tipo DirectoryRemovedNodeDifference", differences.get(0) instanceof DirectoryRemoveNodeDifference);
		assertEquals("El objeto que se elimino es el correcto", ((DirectoryDifference)differences.get(0)).getNodo().getName(), unArchivo.getName());
		workingCopy.add(unArchivo);
		
		//Agrego un archivo
		unArchivo= new File("Archivo4", workingCopy);
		workingCopy.add(unArchivo);
		differences=  comparator.compare(base, workingCopy);
		assertEquals("Comparacion Archivo Agregado hay 1 objeto en la lista", differences.size(), 1);
		assertTrue("El objeto es del tipo DirectoryAddedNodeDifference", differences.get(0) instanceof DirectoryAddedNodeDifference);
		assertEquals("El objeto que se agregado es el correcto", ((DirectoryDifference)differences.get(0)).getNodo().getName(), unArchivo.getName());
		workingCopy.remove(unArchivo);
		
		//Elimino un archivo
		Collection<Directory> directories= workingCopy.getDirectories();
		unDirectorio= directories.iterator().next(); 
		workingCopy.remove(unDirectorio);
		differences=  comparator.compare(base, workingCopy);
		assertEquals("Comparacion Directorio Eliminado hay 1 objeto en la lista", differences.size(), 1);
		assertTrue("El objeto es del tipo DirectoryRemovedNodeDifference", differences.get(0) instanceof DirectoryRemoveNodeDifference);
		assertEquals("El objeto que se elimino es el correcto", ((DirectoryDifference)differences.get(0)).getNodo().getName(), unDirectorio.getName());
		workingCopy.add(unDirectorio);
		
		//Agrego un archivo
		unDirectorio= new Directory("subdirectrio4", workingCopy);
		workingCopy.add(unDirectorio);
		differences=  comparator.compare(base, workingCopy);
		assertEquals("Comparacion Directorio Agregado hay 1 objeto en la lista", differences.size(), 1);
		assertTrue("El objeto es del tipo DirectoryAddedNodeDifference", differences.get(0) instanceof DirectoryAddedNodeDifference);
		assertEquals("El objeto que se agregado es el correcto", ((DirectoryDifference)differences.get(0)).getNodo().getName(), unDirectorio.getName());
		workingCopy.remove(unDirectorio);
	}
	@Test
	public final void testGetFilesComunes(){
		Map<String, FileSystemNodeInterface> lista;
		File unArchivo;
		
		//Comparo los directorios sin haber hecho ninguna modificacion
		lista=  comparator.getFilesComunes(base, workingCopy);
		assertEquals("Tiene 3 files comunes", lista.size(), 3);
		//chequeo que los 3 files comunes sean los mismos
		for(FileSystemNodeInterface fileComun: lista.values()){
			assertEquals("Los 3 Files tienen que coincidir", base.searchFile((File)fileComun).getPath(), workingCopy.searchFile((File) fileComun).getPath());
		}
		
		//Elimino un archivo
		Collection<File> files= workingCopy.getFiles();
		unArchivo=files.iterator().next(); 
		workingCopy.remove(unArchivo);
		lista=  comparator.getFilesComunes(base, workingCopy);
		assertEquals("Comparacion Archivo Eliminado hay 2 objetos en la lista", lista.size(), 2);
		for(FileSystemNodeInterface fileComun: lista.values()){
			assertEquals("Los 2 Files tienen que coincidir",  base.searchFile((File)fileComun).getPath(), workingCopy.searchFile((File)fileComun).getPath());
		}
		
		//Agrego un archivo en workingCopy
		unArchivo= new File("Archivo4", workingCopy);
		workingCopy.add(unArchivo);
		lista=  comparator.getFilesComunes(base, workingCopy);
		assertEquals("Comparacion Archivo Agregado en 1 hay 2 objetos en la lista", lista.size(), 2);

		//Agrego el mismo archivo en baseCopy
		unArchivo= new File("Archivo4", base);
		base.add(unArchivo);
		lista=  comparator.getFilesComunes(base, workingCopy);
		assertEquals("Comparacion Archivo Agregado en ambos hay 3 objetos en la lista", lista.size(), 3);		
	}

	@Test
	public final void testGetDirectoriesComunes(){
		Map<String, FileSystemNodeInterface> lista;
		Directory unDir;
		
		//Comparo los directorios sin haber hecho ninguna modificacion
		lista=  comparator.getDirectoriesComunes(base, workingCopy);
		assertEquals("Tiene 3 directorios comunes", lista.size(), 3);
		//chequeo que los 3 directorios comunes sean los mismos
		for(FileSystemNodeInterface dirComun: lista.values()){
			assertEquals("Los 3 Directorios tienen que coincidir", base.searchDirectory((Directory)dirComun).getPath(), workingCopy.searchDirectory((Directory)dirComun).getPath());
		}
		
		//Elimino un archivo
		Collection<Directory> dirs= workingCopy.getDirectories();
		unDir=dirs.iterator().next(); 
		workingCopy.remove(unDir);
		lista=  comparator.getDirectoriesComunes(base, workingCopy);
		assertEquals("Comparacion Directorio Eliminado hay 2 objetos en la lista", lista.size(), 2);
		for(FileSystemNodeInterface dirComun: lista.values()){
			assertEquals("Los 2 Directorios tienen que coincidir", base.searchDirectory((Directory)dirComun).getPath(), workingCopy.searchDirectory((Directory)dirComun).getPath());
		}
		
		//Agrego un archivo en workingCopy
		unDir= new Directory("subdirectrio4", workingCopy);
		workingCopy.add(unDir);
		lista=  comparator.getDirectoriesComunes(base, workingCopy);
		assertEquals("Comparacion Directorio Agregado en 1 hay 2 objetos en la lista", lista.size(), 2);

		//Agrego el mismo archivo en baseCopy
		unDir= new Directory("subdirectrio4", base);
		base.add(unDir);
		lista=  comparator.getDirectoriesComunes(base, workingCopy);
		assertEquals("Comparacion Archivo Agregado en ambos hay 3 objetos en la lista", lista.size(), 3);		
	}
	
}
