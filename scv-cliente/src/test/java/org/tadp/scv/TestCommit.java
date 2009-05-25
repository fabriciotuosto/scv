package org.tadp.scv;

import java.util.Date;

import junit.framework.TestCase;

public class TestCommit extends TestCase {
	/*
	 * 
	 * private SCVProyectImpl proyect = null;
	 * 
	 * public void setUp() { Directory directorio = null; SCVServer server = new
	 * SCVServerImpl();
	 * 
	 * server.createProyect("ProyectoTest", new Directory("raiz",null)); proyect =
	 * (SCVProyectImpl) server.adquireProyect("ProyectoTest", directorio);
	 * proyect.setServer(server); }
	 * 
	 * public void tearDown() { proyect = null; }
	 * 
	 * public void testCommitFile() throws Exception { File file = new
	 * File("archivo","contenido",null); proyect.add(file);
	 * 
	 * proyect.commitAll();
	 *  }
	 */

	public void testDate() throws Exception {
		Date date = new Date();
		System.out.println(date);
	}
}
