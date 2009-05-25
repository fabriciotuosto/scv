/**
 * 
 */
package org.tadp.scv.api.builder;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tadp.scv.api.compare.DirectoryComparator;
import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.difference.directory.DirectoryAddedNodeDifference;
import org.tadp.scv.api.difference.directory.DirectoryRemoveNodeDifference;
import org.tadp.scv.api.difference.file.FileAddedLineDifference;
import org.tadp.scv.api.difference.file.FileDeleteLineDifferece;
import org.tadp.scv.api.difference.file.FileUpdateDifference;
import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.api.filesystem.server.FileVersionable;

/**
 * @author martin
 *
 */
public class VersionableNodeBuilderTest {
	private DirectoryComparator comparadorDeDirectorios= new DirectoryComparator();
	private FileVersionable baseFile;
	private FileVersionable finalFile;
	List<Difference<File>> fileDifferences;
	private DirectoryVersionable baseDirectory;
	private DirectoryVersionable finalDirectory;
	List<Difference<Directory>> directoryDifferences;
	private VersionableNodeBuilder<File> fileBuilder;
	private VersionableNodeBuilder<Directory> directoryBuilder;
	
	@Before
	public void setUp() throws Exception {
		//Crear Archivo
		baseFile= new FileVersionable(new File("base", "Este es un archivo de prueba\r\nAl que se le van a agregar\r\nLineas", null));
		finalFile= new FileVersionable(new File("Final", "Esta linea se agrega en el primer renglon\r\nEste es el archivo terminado\r\nLineas\r\n", null));
		
		//Crear Lista de diferencias a aplicar
		fileDifferences= new ArrayList<Difference<File>>();
		Difference<File> aFileDifference= new FileAddedLineDifference("Esta linea se agrega en el primer renglon", 0);
		fileDifferences.add(aFileDifference);
		aFileDifference= new FileDeleteLineDifferece("", 2);
		fileDifferences.add(aFileDifference);
		aFileDifference= new FileUpdateDifference("Este es el archivo terminado", 1);
		fileDifferences.add(aFileDifference);
		
		//Crear Directorio
		baseDirectory= new DirectoryVersionable(new Directory("base", null));
		DirectoryVersionable subdir= new DirectoryVersionable(new Directory("subdir1", baseDirectory.getDirectory()));
		baseDirectory.add(subdir);
		subdir= new DirectoryVersionable(new Directory("subdir2", baseDirectory.getDirectory()));
		baseDirectory.add(subdir);
		finalDirectory= (DirectoryVersionable) baseDirectory.getCopy();
		finalDirectory.remove(subdir);
		
		//Crear lista de diferencias a aplicar
		directoryDifferences= new ArrayList<Difference<Directory>>();
		//caso elemento eliminado
		Difference<Directory> aDirectoryDifference= new DirectoryRemoveNodeDifference(subdir.getDirectory());
		directoryDifferences.add(aDirectoryDifference);
		//caso elemento añadido
		subdir= new DirectoryVersionable(new Directory("subdir3", baseDirectory.getDirectory()));
		finalDirectory.add(subdir);
		aDirectoryDifference= new DirectoryAddedNodeDifference(subdir.getDirectory());
		directoryDifferences.add(aDirectoryDifference);
		

		fileBuilder= new VersionableNodeBuilder<File>();
		directoryBuilder= new VersionableNodeBuilder<Directory>();
	}

	@After
	public void tearDown() throws Exception {
		baseFile= null;
		finalFile= null;
		baseDirectory= null;
		finalDirectory= null;
		fileDifferences= null;
		directoryDifferences= null;
		fileBuilder= null;
		directoryBuilder= null;
	}

	/**
	 *
	 * Dado un archivo cualquiera voy a cambiarlo utilizando el FileNodeBuilder, 
	 * los cambios seran diferencias que ire seteando. Luego para probar que el
	 * resultado es correcto utilizare para comparar los resultados un modelo que 
	 * previante se seteo en el codigo, con los resultados correctos.
	 * 
	 */
	@Test
	public final void testBuildFile() {
		FileVersionable buildedFile= null;
		
		//Seteo el nodo, cargo diferencias
		fileBuilder.setNode(baseFile);
		fileBuilder.append(fileDifferences);
		
		//Obtengo el archivo con modificaciones
		try {
			buildedFile= (FileVersionable) fileBuilder.build();
		} catch (IntedToAddExistingNode e) {
			//Esta excepcion es imposible pertenece a la interfaz
			//solo para poder mantener el polimorfismo
			fail("No se puede construir");
		}
		
		//Compararo el construido con la version final:
		assertEquals("Modelo Final no concuerda con lo construido", finalFile.getContent(), buildedFile.getContent());
	}
	
	/**
	 * Dado un directorio base se le aplicaran una lista de modificaciones 
	 * mediante el FileSystemNodeBuilder y se obtendra un nuevo directorio.
	 * Este nuevo directorio sera comparado con un modelo que tiene los 
	 * resultados correctos de aplicar las diferencias.
	 * @throws IntedToAddExistingNode 
	 * 
	 */
	@Test
	public final void testBuildDirectory() throws IntedToAddExistingNode {
		DirectoryVersionable buildedDirectory;
		
		//Seteo el nodo, cargo diferencias
		directoryBuilder.setNode(baseDirectory);
		directoryBuilder.append(directoryDifferences);
		
		//Aplico todas las diferencias al directorio base
		buildedDirectory= (DirectoryVersionable) directoryBuilder.build();

		//Obtengo las diferencias existes entre el modelo del final del directorio
		//y el construido, no deberia haber ninguna
		List<Difference<Directory>> listaVacia= comparadorDeDirectorios.compare(finalDirectory.getDirectory(), buildedDirectory.getDirectory());
		assertEquals("Hay diferencias entre la version FINAL y el CONSTRUIDO", listaVacia.size(), 0);
	}

}
