package org.tadp.scv.server.builder;

import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.api.server.SCVProjectCliente;
import org.tadp.scv.api.server.SCVProjectServer;
import org.tadp.scv.api.server.SCVServer;

public class SCVProjectClienteBuilder {
	private SCVProjectCliente proyecto = null;
	private String name = null;
	private DirectoryVersionable root = null;
	private SCVServer server = null; 
	private SCVProjectServer proyectoServidor = null;
	
	
	
	/*public SCVProjectClienteBuilder(){
		proyecto = new SCVProjectCliente();
	}*/
	
	public SCVProjectClienteBuilder setServer(SCVServer server){
		this.server = server;
		return this;
	}
	
	public SCVProjectClienteBuilder setName(String name){
		this.name = name;
		return this;
	}
	
	public SCVProjectClienteBuilder setRoot(DirectoryVersionable root){
		this.root = root;
		return this;
	}

	public SCVProjectCliente build(){
		this.proyecto = new SCVProjectCliente(this.root.getDirectory() , this.proyectoServidor);
		proyecto.setServer(this.server);
		proyecto.setName(this.name);
		return proyecto;
	}

	public void setProyectoServidor(SCVProjectServer proyectoServidor) {
		this.proyectoServidor = proyectoServidor;
	}
}
