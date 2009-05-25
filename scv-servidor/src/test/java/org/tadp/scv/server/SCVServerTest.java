package org.tadp.scv.server;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tadp.scv.api.exceptions.ProyectNotFoundException;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.server.SCVProjectCliente;
import org.tadp.scv.api.server.SCVProjectServer;
import org.tadp.scv.api.server.SCVProyect;
import org.tadp.scv.api.server.SCVServer;

public class SCVServerTest {
	private SCVServer server;
	private Directory base;

	@Before
	public void setUp() throws Exception {
		server = new SCVServerImpl();

		// Creo el root
		base = new Directory("raiz", null);
		
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
	}

	@After
	public void tearDown() throws Exception {
		base= null;
		
	}

	@Test
	public final void testCrearProyecto(){
		// Cargo el proyecto
		String name = "ProyectoValido";
		SCVProjectServer proyectoValido = server.createProyect(name, base);

		//assertTrue("El hashmap no aumento", server.);
	}
	
	@Test
	public final void testRecuperarProyecto(){
		// Cargo el proyecto
		String name = "ProyectoValido";
		SCVProjectServer proyectoValido = server.createProyect(name, base);
	
		// Recupero el proyecto
		SCVProjectCliente proyectoRecuperado = server.adquireProyect(name, base);
		
		//Si llego hasta aca es porque pude recuperar el proyecto bien
	}
	

	@Test(expected = ProyectNotFoundException.class) 
	public final void testFallarRecuperacionProyecto(){
			SCVProjectServer proyectoValido = server.createProyect("ProyectoValido", base);
			SCVProjectCliente proyectoRecuperado = server.adquireProyect("ProyectoInvalido", base);
	}

}
