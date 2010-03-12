package org.tadp.scv.web.application.test;

import org.tadp.scv.api.exceptions.FirstNeedToMakeUpdateException;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.server.SCVProjectCliente;
import org.tadp.scv.api.server.SCVServer;

public class ContenidoPrueba
{
	/**
	 * Este metodo construye 3 proyectos en el servidor con directorios y
	 * archivos funciona para probar la aplicacion
	 * 
	 * @param servidor
	 *            el servidor que voy a usar
	 * @throws FirstNeedToMakeUpdateException
	 */
	public ContenidoPrueba(SCVServer servidor)
	{
		try
		{
			// Creo mi primer proyecto
			// Creo el directorio raiz
			Directory dir1 = new Directory("Musica", null);
			servidor.createProyect("prMusica", dir1);
			SCVProjectCliente pr1 = servidor.adquireProyect("prMusica", dir1);
			// Creo los subDirectorios
			dir1 = pr1.getRoot();
			Directory dir11 = new Directory("Soda", dir1);
			dir1.add(dir11);
			Directory dir12 = new Directory("Redondos", dir1);
			dir1.add(dir12);
			Directory dir13 = new Directory("Pescado", dir1);
			dir1.add(dir13);

			// Creo mas subdirectorios
			Directory dir111 = new Directory("Dynamo", dir11);
			dir11.add(dir111);
			Directory dir112 = new Directory("Comfort", dir11);
			dir11.add(dir112);

			// Creo archivos
			File arch1111 = new File("Ameba.txt", "Toda esa gente\ndice que te ama", dir111);
			dir111.add(arch1111);
			File arch1112 = new File("Toma La Ruta.txt", "No preguntes mas por mi\nnadie sabe nada", dir111);
			dir111.add(arch1112);
			File arch1121 = new File("Ella uso mi cabeza como un revolver.txt",
					"Ella uso mi cabeza como un revolver\ne incendio mi conciencia con sus demonios", dir112);
			dir112.add(arch1121);

			// Commiteo todo
			pr1.commitAll("se comitea el proyecto Musica");

			// Modifico el 1er archivo
			// 2da version
			arch1111.setContent(arch1111.getContent() + "\ntoda esa gente dice\nque te odia");
			pr1.commit(arch1111);
			// 3er version
			arch1111.setContent(arch1111.getContent() + "\ny te vas.. y te vas\nconsumiendo");
			pr1.commit(arch1111);
			// Modifico el directorio dir11
			Directory dir113 = new Directory("Cancion Animal", dir11);
			dir11.add(dir113);
			File arch1131 = new File("1990.txt", "No habra secretos infalibles\nte contare", dir113);
			dir113.add(arch1131);
			pr1.commit(dir11,"se hizo el cambio de version");

			// Creo el 2do proyecto
			Directory dir2 = new Directory("Futbol", null);
			servidor.createProyect("prFutbol", dir2);
			SCVProjectCliente pr2 = servidor.adquireProyect("prFutbol", dir2);
			// Creo subdirectorios con archivos
			Directory dir21 = new Directory("River", null);
			Directory dir22 = new Directory("San Lorenzo", null);
			File arch211 = new File("Arqueros.txt", "Carrizo", dir21);
			dir21.add(arch211);
			File arch212 = new File("Defensores.txt", "Lusenhoff\nGerlo", dir21);
			dir21.add(arch212);
			File arch221 = new File("Delanteros.txt", "Silvera", dir22);
			dir22.add(arch221);
			File arch222 = new File("Mediocampistas.txt", "Ledesma", dir22);
			dir22.add(arch222);
			pr2.add(dir21);
			pr2.add(dir22);
			pr2.commitAll("Se comitea el proyecto Futbol");
			// Hago modificaciones
			// Agrego un nuevo directorio
			Directory dir23 = new Directory("Velez", null);
			File arch231 = new File("Tecnicos.txt", "Lavolpe", dir23);
			dir23.add(arch231);
			pr2.add(dir23);
			// Modifico archivos
			arch212.setContent("Rivas\nNasutti");
			pr2.commitAll();

			// Creo el 3er proyecto
			Directory dir3 = new Directory("Materias", null);
			servidor.createProyect("prMaterias", dir3);
			SCVProjectCliente pr3 = servidor.adquireProyect("prMaterias", dir3);
			// Creo subdirectorios con archivos
			Directory dir31 = new Directory("Adm Rec", null);
			Directory dir32 = new Directory("IA", null);
			File arch311 = new File("Profesores.txt", "Benia", dir31);
			dir31.add(arch311);
			File arch312 = new File("Ayudantes.txt", "Damian\nGaston", dir31);
			dir31.add(arch312);
			File arch321 = new File("Profesores.txt", "Pollo", dir32);
			dir31.add(arch321);
			pr3.add(dir31);
			pr3.add(dir32);
			pr3.commitAll("Se comitea el proyecto Materias");

			pr3.add(dir32);
			pr3.commitAll("se agrego el IA");
		} catch (Throwable t)
		{
			throw new RuntimeException("Hubo un error que no deberia existir", t);
		}
	}
}
