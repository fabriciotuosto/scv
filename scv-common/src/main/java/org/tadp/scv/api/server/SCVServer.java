package org.tadp.scv.api.server;

import java.util.List;

import org.tadp.scv.api.filesystem.Directory;


public interface SCVServer
{

	public SCVProjectCliente adquireProyect(String name, Directory destination);
	public SCVProjectServer createProyect(String name, Directory destination);
	public List<String> getProjectsNames();
	public SCVProjectServer getProyect(String name);
}
