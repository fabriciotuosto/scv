package org.tadp.scv.api.filesystem.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tadp.scv.api.compare.Revision;
import org.tadp.scv.api.difference.file.FileUpdateDifference;
import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.filesystem.File;

public class FileVersionableTest {
	private FileVersionable file;

	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
		//file = null;
	}

	@Test
	public final void testContenidoBase(){
		String textoContenido = "El texto de contenido";
		file = new FileVersionable( new File("archivo", textoContenido, null));
		assertEquals("El contenido no coincide", file.getBasicContent(), textoContenido);
		// El contenido debe ser igual porque no hay revisiones
		assertEquals("El contenido no coincide", file.getContent(), textoContenido);
	}
	
	@Test
	public final void testRevisiones(){
		String textoContenido = "El texto de contenido \r\n esta ocupando \r\n por 3 lineas \r\n";
		file = new FileVersionable( new File("archivo", textoContenido, null));
		
		this.updateLine("Nueva linea 3", 2);
		//El contenido base no debe cambiar con una revision
		assertEquals("Contenido base cambio con revision", file.getBasicContent(), textoContenido);
		//El contenido actualizado debe ser como lo cambie
		assertEquals("Contenido actualizado no es igual", file.getContent(), "El texto de contenido \r\n esta ocupando \r\nNueva linea 3\r\n");

		this.updateLine("Nueva linea 2", 1);
		//El contenido base no debe cambiar con una revision
		assertEquals("Contenido base cambio con revision", file.getBasicContent(), textoContenido);
		//El contenido actualizado debe ser distinto
		assertFalse("Nueva revision, y el contenido es igual", file.getContent().equals(textoContenido));
		
		this.updateLine("Nueva linea 2, v2", 1);
		//El contenido base no debe cambiar con una revision
		assertEquals("Contenido base cambio con revision", file.getBasicContent(), textoContenido);
		//El contenido actualizado debe ser distinto
		assertFalse("Nueva revision, y el contenido es igual", file.getContent().equals(textoContenido));
	}
	
	@Test
	public final void testGetToRevision(){
		String textoContenido = "El texto de contenido \r\n esta ocupando \r\n por 3 lineas";
		file = new FileVersionable( new File("archivo", textoContenido, null));
		this.updateLine("Nueva linea 3", 2);
		this.updateLine("Nueva linea 2", 1);
		this.updateLine("Nueva linea 2, v2", 1);

		int nroRevision = file.getRevisionNumber();
		try {
			String contenido= file.getToRevision(0).getContent();
			assertEquals("No coinciden contenidos", file.getBasicContent(), contenido);
			contenido= file.getToRevision(file.getNumberOfLastRevision()).getContent();
			assertEquals("No coinciden contenidos", file.getContent(), contenido);
		} catch (Exception e) {
			fail("No encontro la revision " + nroRevision);
		}
	}
	
	@Test (expected= IllegalArgumentException.class)
	public final void testFallarGetToRevision() throws IllegalArgumentException, IntedToAddExistingNode{
			file = new FileVersionable( new File("archivo", "nada", null));
			int nroRevision = file.getNumberOfLastRevision()+1;
			file.getToRevision(nroRevision);
			fail("Encontro la revision " + nroRevision);
	}
	
	private void updateLine(String line, int nrolinea){
		Revision<File> revision = new Revision<File>();
		revision.add(new FileUpdateDifference(line, nrolinea));
		file.addRevision(revision);
		try {
			file.getToRevision(file.getNumberOfLastRevision());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IntedToAddExistingNode e) {
			e.printStackTrace();
		}
	}
}

/* testear:
	public FileImpl(File decorated,String content)
	public String getContent() -> Lo trae actualizado
	public File getToRevision(int revisionNumber) throws Exception
	public File clone() throws CloneNotSupportedException -> clona el decorado
*/