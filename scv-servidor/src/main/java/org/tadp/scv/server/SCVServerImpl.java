package org.tadp.scv.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tadp.scv.api.exceptions.ProyectNotFoundException;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.server.SCVProjectCliente;
import org.tadp.scv.api.server.SCVProjectServer;
import org.tadp.scv.api.server.SCVServer;
import org.tadp.scv.server.builder.SCVProjectClienteBuilder;


public class SCVServerImpl implements SCVServer
{
	private Map<String, SCVProjectServer> projects;
	
	
	public SCVServerImpl(){
		projects = new HashMap<String, SCVProjectServer>();
	}
	/**
	 * Obtiene el proyecto a partir del nombre del mismo
	 */

	public SCVProjectCliente adquireProyect(String name, Directory destination)
	{
		SCVProjectServer proyectoServer = projects.get(name);
		SCVProjectCliente proyectoClient = null;
		
		if (proyectoServer == null)
			throw new ProyectNotFoundException("Proyect "+name+ " not found on this server ");

		//logica de creacion del proyecto cliente
		SCVProjectClienteBuilder builder = new SCVProjectClienteBuilder();
		builder.setName(name);
		builder.setServer(this);
		builder.setProyectoServidor(proyectoServer);
		builder.setRoot(proyectoServer.getRoot()); // carga el root y el base
		proyectoClient = builder.build();

		return proyectoClient;
	}

	/**
	 *  Devuelve el proyecto que creo, aun no se bien para que
	 */
	public SCVProjectServer createProyect(String name, Directory destination)
	{
		//DirectoryVersionable root = new DirectoryVersionable(destination, new ArrayList<Revision<Directory>>());
		//SCVProjectServer proyect = new SCVProjectServer(root);
		SCVProjectServer proyect = new SCVProjectServer(destination);
		projects.put(name, proyect);
		return proyect;
	}

	public void addProject(String name,SCVProjectServer project)
	{
		projects.put(name, project);
	}
	
	public List<String> getProjectsNames()
	{
		List<String> strings = new ArrayList<String>();
		for(String string : projects.keySet())
		{
			strings.add(string);
		}	
		return strings;
	}
	
	public SCVProjectServer getProyect(String string)
	{
		return projects.get(string);
	}
}
