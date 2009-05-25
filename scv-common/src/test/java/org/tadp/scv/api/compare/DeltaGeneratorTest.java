package org.tadp.scv.api.compare;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.api.filesystem.server.FileVersionable;

public class DeltaGeneratorTest {
	private DirectoryVersionable base;

	private Directory workingCopy;

	private DeltaGenerator unDeepComparator;

	public void crearCopiaBase() {
		Directory raiz = new Directory("raiz", null);
		base = new DirectoryVersionable(raiz);

		// --Nivel 1 de anidamiento
		// Agrego subdirectorios
		try {
			DirectoryVersionable nodo = new DirectoryVersionable(new Directory("subdir1", raiz));
			base.add(nodo);

			// Agrego archivos
			FileVersionable archivo = new FileVersionable(new File("archivo1",raiz));
			base.add(archivo);
			archivo = new FileVersionable(new File("archivo2", raiz));
			base.add(archivo);

			// --Nivel 2 de anidamiento
			nodo = new DirectoryVersionable(new Directory("subdir2", raiz));
			base.add(nodo);

			// Agrego archivos al ultimo subdirectorio creado
			archivo = new FileVersionable(new File("archivo3", nodo.getDirectory()));
			nodo.add(archivo);
			archivo = new FileVersionable(new File("archivo4", nodo.getDirectory()));
			nodo.add(archivo);

			// --Nivel 3 de anidamiento
			DirectoryVersionable subNodo = new DirectoryVersionable(new Directory("subdir2.1", nodo.getDirectory()));
			nodo.add(subNodo);

			// Agrego archivos al ultimo subdirectorio creado
			archivo = new FileVersionable(new File("archivo3", subNodo.getDirectory()));
			subNodo.add(archivo);
			archivo = new FileVersionable(new File("archivo4", subNodo.getDirectory()));
			subNodo.add(archivo);
		} catch (IntedToAddExistingNode e) {
			
		}

	}

	public String modificacion1erNivelWorkingCopy() {
		/*
		 * Este es un directorio cualquiera del primer nivel en la jerarquia de
		 * la working copy
		 */
		Directory unDirectorio = workingCopy.getDirectories().iterator().next();
		workingCopy.remove(unDirectorio);
		return workingCopy.getPath();
	}

	public String modificacion2doNivelWorkingCopy() {
		Iterator it = workingCopy.getDirectories().iterator();
		
		//Esto es por si el directorio es justo el subdir1 que no tiene archivos.
		Directory unDirectorio= (Directory)it.next();
		while( unDirectorio!= null && unDirectorio.getFiles().size() == 0) {
			unDirectorio= (Directory)it.next();
		}
		
		/*
		 * Este es un archivo cualquiera del segundo nivel en la jerarquia de la
		 * working copy
		 */
		File unArchivo = unDirectorio.getFiles().iterator().next();
		unDirectorio.remove(unArchivo);

		return unDirectorio.getPath();
	}

	
	private String revisarSiCambio(List<DeltaNodo> deltas) {
		for(DeltaNodo<FileSystemNode> unDelta: deltas)
			if (unDelta.getRevision(0).getCantidadDifferencias() != 0) {
				return unDelta.getPath();
			}
			
		return null;
	}

	@Before
	public void setUp() throws Exception {
		crearCopiaBase();
		try {
			workingCopy = base.clone();
		} catch (CloneNotSupportedException e) {
			fail("Alguien no implemento clone");;
		}
		unDeepComparator = new DeltaGenerator();
	}

	@After
	public void tearDown() throws Exception {
		base = null;
		workingCopy = null;
	}

	/**
	 * Lo que se intenta probar es que el DeepComparator genere una estructura
	 * con revisiones vacias porque se compararan copias identicas de una misma
	 * jerarquia.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testNadaDeCambios() {
		List<DeltaNodo> deltas;

		deltas = unDeepComparator.compare(base, workingCopy);

		assertEquals("Se detectaron cambios en copias IDENTICAS", revisarSiCambio(deltas), null);
	}

	/**
	 * Lo que se intenta probar es que el DeepComparator genere una estructura
	 * con revisiones que detecten un cambio en el primer eslabon de la
	 * jerarquia. No importa el numero de cambios o la naturaleza de los mismos
	 * porque de eso se encarga los comparators. El DeepComparator solo debe
	 * detectar que existen diferencias Los test para saber cuales son la
	 * realizan los respectivos DirectoryComparator y FileComparator
	 * 
	 */
	@Test
	public void testCambios1erNivel() {
		List<DeltaNodo> deltas;
		String pathDelModificado = null;

		pathDelModificado = modificacion1erNivelWorkingCopy();
		deltas = unDeepComparator.compare(base, workingCopy);


		assertEquals("El nodo que presenta los cambios no es el que deberia", 
				revisarSiCambio(deltas), pathDelModificado);
	}

	@Test
	public void testCambios2doNivel() {
		List<DeltaNodo> deltas;
		String pathDelModificado = null;

		pathDelModificado = modificacion2doNivelWorkingCopy();
		deltas = unDeepComparator.compare(base, workingCopy);

		assertEquals("El nodo que presenta los cambios no es el que deberia",revisarSiCambio(deltas), pathDelModificado);
	}

}
