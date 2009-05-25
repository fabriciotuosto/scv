package org.tadp.scv.api.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;


public class DirectoryTest {
	private Directory directorio;

	@Before
	public void setUp() throws Exception {
		directorio = new Directory();
		
	}
	
	@After
	public void tearDown() throws Exception {
		directorio = null;
	}

	@Test
	public final void testCondicion(){
		// Verifica que responda como un Directorio
		assertTrue("No responde como un Directory", directorio.isDirectory());
		assertFalse("Responde como un File, y no lo es", directorio.isFile());
	}
	
	@Test
	public final void testClonacion(){
		try{
			directorio.clone();
		}catch (CloneNotSupportedException e){
			fail("No soporto la clonacion de un Directorio");
		}
	}
	
	@Test
	public final void testEquals(){
		try{
			Directory clonado = directorio.clone();
			assertTrue("File no es igual a su clon", clonado.equals(directorio));
		}catch (CloneNotSupportedException e){
			fail("No soporto la clonacion de un Directorio");
		}
	}
	
	@Test
	public final void testPredecesor(){
		directorio = new Directory("Dir", null);
		Directory otroDirectorio = new Directory("subdir1", directorio);
		assertEquals("El predecesor no coincide", otroDirectorio.getPredecesor(), directorio);
	}
	
	@Test
	public final void testPath(){
		String separador = System.getProperty("file.separator");
		directorio = new Directory("subdir1.1", new Directory("subdir1", new Directory("raiz", null)));
		
		String path = "raiz" + separador + "subdir1" + separador + "subdir1.1"+ separador;
		
		assertEquals("El path no coincide", directorio.getPath(), path);
	}

	@Test
	public final void testDirectories(){
		directorio = new Directory("raiz", null);
		Directory subdirectorio = new Directory("subdir1", directorio);
		
		directorio.add(subdirectorio);
		assertEquals("La cantidad de directorios no coinciden", directorio.getDirectories().size(), 1);
		
		directorio.remove(subdirectorio);
		assertEquals("La cantidad de directorios no coinciden", directorio.getDirectories().size(), 0);
	
		File file = new File("archivo", directorio);
		directorio.add(file);
		assertEquals("La cantidad de archivos no coinciden", directorio.getFiles().size(), 1);
	
		directorio.remove(file);
		assertEquals("La cantidad de archivos no coinciden", directorio.getFiles().size(), 0);

		//Intento borrar un archivo que no existe
		assertFalse("Borro un archivo inexistente", directorio.remove(file));
		assertEquals("La cantidad de archivos no coinciden", directorio.getFiles().size(), 0);
	
		directorio.add(subdirectorio);
		assertEquals("Directorio no coincide con el cargado",directorio.searchDirectory(subdirectorio), subdirectorio);
		
		directorio.add(file);
		assertEquals("Archivo no coincide con el cargado", directorio.searchFile(file), file);
	
	}
	
	@Test
	public final void testFindNode(){
		String separador = System.getProperty("file.separator");
		
		directorio = new Directory("raiz", null);
		Directory subdir1 = new Directory("subdir1", directorio);
		directorio.add(subdir1);
		Directory subdir2 = new Directory("subdir2", subdir1);
		subdir1.add(subdir2);
		
		File arch1 = new File("arch1", directorio);
		directorio.add(arch1);
		File arch2 = new File("arch2", subdir1);
		subdir1.add(arch2);
		File arch3 = new File("arch3", subdir2);
		subdir2.add(arch3);
		
		Directory contenedor = new Directory("conenedor", null);
		contenedor.add(directorio);
		
		/* Mi arbol de directorios dentro de contenedor queda
		 * \raiz
		 * \raiz\arch1
		 * \raiz\subdir1\
		 * \raiz\subdir1\arch2
		 * \raiz\subdir1\subdir2\
		 * \raiz\subdir1\subdir2\arch3
		 */
		//Cosas que existen
		assertEquals(arch1, directorio.getFileSystemNode(arch1));
		assertEquals(subdir1, directorio.getFileSystemNode(subdir1));
		assertEquals("archivo no encontrado", contenedor.findNode(arch1.getPath()), arch1);
		assertEquals("archivo no encontrado", contenedor.findNode(arch2.getPath()), arch2);
		assertEquals("archivo no encontrado", contenedor.findNode(arch3.getPath()), arch3);
		assertEquals("Directorio no encontrado", contenedor.findNode(subdir1.getPath()), subdir1);
		assertEquals("Directorio no encontrado", contenedor.findNode(subdir2.getPath()), subdir2);
		//Quiero obtener el contenedor
		assertEquals("contenedor no encontrado", contenedor.findNode(separador), contenedor);		
		//Cosas que no existen
		assertEquals("No devolvio null", contenedor.findNode("\\raiz\\subdir1\\arch3"), null);
		assertEquals("No devolvio null", contenedor.findNode("\\raiz\\subdir1\\subdir3\\"), null);
	}
}
