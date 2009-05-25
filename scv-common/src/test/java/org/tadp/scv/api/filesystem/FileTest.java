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

public class FileTest {
	private File file;

	@Before
	public void setUp() throws Exception {
		file = new File();
	}
	
	@After
	public void tearDown() throws Exception {
		file = null;
	}

	@Test
	public final void testContenido(){
		// Setea un contenido y corrobora que al recuperarlo sea el mismo
		 file.setContent("Este es el contenido del\r\nFile con dos lineas.");
		 assertEquals("El contenido es diferente al seteado", file.getContent(), "Este es el contenido del\r\nFile con dos lineas."); 
		 
		 File otroFile = new File("doc","Este es el contenido del\r\nFile con dos lineas.", null);
		 assertEquals("El contenido es diferente al seteado", otroFile.getContent(), "Este es el contenido del\r\nFile con dos lineas.");
	}
	
	@Test
	public final void testCondicion(){
		// Verifica que responda como un File
		assertTrue("No responde como un File", file.isFile());
		assertFalse("Responde como un Directory, y no lo es", file.isDirectory());
	}
	
	@Test
	public final void testClonacion(){
		try{
			file.clone();
		}catch (CloneNotSupportedException e){
			fail("No soporto la clonacion de un File");
		}
	}
	
	@Test
	public final void testEquals(){
		try{
			File clonado = file.clone();
			assertTrue("File no es igual a su clon", clonado.equals(file));
		}catch (CloneNotSupportedException e){
			fail("No soporto la clonacion de un File");
		}
	}
	
	@Test
	public final void testPredecesor(){
		Directory directorio = new Directory("Dir", null);
		file = new File("archivo1", directorio);
		assertEquals("El predecesor no coincide", file.getPredecesor(), directorio);
	}
	
	@Test
	public final void testPath(){
		String separador = System.getProperty("file.separator");
		file = new File("planilla", "Relleno", new Directory("subdir", new Directory("raiz", null)));
		
		String path = "raiz" + separador + "subdir" + separador + "planilla";
		
		assertEquals("El path no coincide", file.getPath(), path);
	}
}
