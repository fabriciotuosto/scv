package org.tadp.scv.api.builder;

import static org.junit.Assert.assertEquals;

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
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;

/**
 * Esta clase no hace demasiadas pruebas porque la funcionalidad requerida es
 * CONSTRUIR y la logica de construccion corresponde a cada objeto Difference
 * y no al FileSystemNodeBuiler. Por lo tanto, aca solo se prueba si puede aplicar
 * los pasos de construccion correctamente.
 * 
 * @author martin
 *
 */
public class FileSystemNodeBuilderTest {
	private DirectoryComparator comparadorDeDirectorios= new DirectoryComparator();
	private File baseFile;
	private File finalFile;
	List<Difference<File>> fileDifferences;
	private Directory baseDirectory;
	private Directory finalDirectory;
	List<Difference<Directory>> directoryDifferences;
	private FileSystemNodeBuilder<File> fileBuilder;
	private FileSystemNodeBuilder<Directory> directoryBuilder;
	
	@Before
	public void setUp() throws Exception {
		//Crear Archivo
		baseFile= new File("base", "Este es un archivo de prueba\r\nAl que se le van a agregar\r\nLineas", null);
		finalFile= new File("Final", "Esta linea se agrega en el primer renglon\r\nEste es el archivo terminado\r\nLineas\r\n", null);
		
		//Crear Lista de diferencias a aplicar
		fileDifferences= new ArrayList<Difference<File>>();
		Difference<File> aFileDifference= new FileAddedLineDifference("Esta linea se agrega en el primer renglon", 0);
		fileDifferences.add(aFileDifference);
		aFileDifference= new FileDeleteLineDifferece("", 2);
		fileDifferences.add(aFileDifference);
		aFileDifference= new FileUpdateDifference("Este es el archivo terminado", 1);
		fileDifferences.add(aFileDifference);
		
		//Crear Directorio
		baseDirectory= new Directory("base", null);
		Directory subdir= new Directory("subdir1", baseDirectory);
		baseDirectory.add(subdir);
		subdir= new Directory("subdir2", baseDirectory);
		baseDirectory.add(subdir);
		finalDirectory= baseDirectory.clone();
		finalDirectory.remove(subdir);
		
		//Crear lista de diferencias a aplicar
		directoryDifferences= new ArrayList<Difference<Directory>>();
		//caso elemento eliminado
		Difference<Directory> aDirectoryDifference= new DirectoryRemoveNodeDifference(subdir);
		directoryDifferences.add(aDirectoryDifference);
		//caso elemento añadido
		subdir= new Directory("subdir3", baseDirectory);
		finalDirectory.add(subdir);
		aDirectoryDifference= new DirectoryAddedNodeDifference(subdir);
		directoryDifferences.add(aDirectoryDifference);
		

		fileBuilder= new FileSystemNodeBuilder<File>();
		directoryBuilder= new FileSystemNodeBuilder<Directory>();
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
		File buildedFile= null;
		
		//Seteo el nodo, cargo diferencias
		fileBuilder.setNode(baseFile);
		fileBuilder.append(fileDifferences);
		
		//Obtengo el archivo con modificaciones
		buildedFile= fileBuilder.build();
		
		//Compararo el construido con la version final:
		assertEquals("Modelo Final no concuerda con lo construido", finalFile.getContent(), buildedFile.getContent());
	}
	
	/**
	 * Dado un directorio base se le aplicaran una lista de modificaciones 
	 * mediante el FileSystemNodeBuilder y se obtendra un nuevo directorio.
	 * Este nuevo directorio sera comparado con un modelo que tiene los 
	 * resultados correctos de aplicar las diferencias.
	 * 
	 */
	@Test
	public final void testBuildDirectory() {
		Directory buildedDirectory;
		
		//Seteo el nodo, cargo diferencias
		directoryBuilder.setNode(baseDirectory);
		directoryBuilder.append(directoryDifferences);
		
		//Aplico todas las diferencias al directorio base
		buildedDirectory= directoryBuilder.build();
		
		//Obtengo las diferencias existes entre el modelo del final del directorio
		//y el construido, no deberia haber ninguna
		List<Difference<Directory>> listaVacia= comparadorDeDirectorios.compare(finalDirectory, buildedDirectory);
		assertEquals("Hay diferencias entre la version FINAL y el CONSTRUIDO", listaVacia.size(), 0);
	}
}
