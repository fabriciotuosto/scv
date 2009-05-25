package org.tadp.scv.api.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tadp.scv.api.compare.DeltaGenerator;
import org.tadp.scv.api.compare.DeltaNodo;
import org.tadp.scv.api.compare.Revision;
import org.tadp.scv.api.difference.directory.DirectoryAddedNodeDifference;
import org.tadp.scv.api.difference.file.FileAddedLineDifference;
import org.tadp.scv.api.difference.file.FileUpdateDifference;
import org.tadp.scv.api.exceptions.BothSourcesModifiedException;
import org.tadp.scv.api.exceptions.ConflictoNodosException;
import org.tadp.scv.api.exceptions.FirstNeedToMakeUpdateException;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNode;

/**
 * Para probar esta clase es escencial que los test del DeltaGenerator
 * no presenten errores, pues la validez de los casos de prueba usan
 * operacion de la clase mensionada.
 * 
 * @author martin
 *
 */
public class SCVProjectClienteTest {
	private Directory fileSystemLocal;
	private Directory subdir1,subdir1_1, subdir1_1_1, subdir2, subdir3;
	private File file;
	MockSCVProjectServer proyectoServidor;
	SCVProjectCliente proyectoCliente;
	
	@Before
	public void setUp(){
		/* Armo un filesystem de prueba */
		/* -+ raiz
		 * 		|-+ subdir1
		 * 			|-+ subdir1_1
		 * 				|-+ subdir1_1_1
		 * 					|- access.mdb
		 * 				|- excel.xls
		 * 		|-+ subdir2
		 * 			|- powerpoint.ppt
		 * 		|-+ subdir3
		 * 		|- texto.txt
		 * 		|- word.doc
		 */
		fileSystemLocal = new Directory("raiz", null);
		fileSystemLocal.add(new File("texto.txt", "Texto plano del notepad", fileSystemLocal));
		fileSystemLocal.add(new File("word.doc", "Texto con formato \r\n escrito en Word", fileSystemLocal));
			subdir1 = new Directory("subdir1", fileSystemLocal);
				subdir1_1 = new Directory("subdir1_1", subdir1);
					subdir1_1_1 = new Directory("subdir1_1_1", subdir1_1);
					subdir1_1_1.add(new File("access.mdb", "Una tabla en Access", subdir1_1_1));
				subdir1_1.add(file = new File("excel.xls", "Una planilla de Excel", subdir1_1));							
			subdir2 = new Directory("subdir2", fileSystemLocal);
			subdir2.add(new File("powerpoint.ppt", "Una presentacion de Power Point", subdir2));
			subdir3 = new Directory("subdir3", fileSystemLocal);
		
		subdir1_1.add(subdir1_1_1);
		subdir1.add(subdir1_1);
		
		fileSystemLocal.add(subdir1);
		fileSystemLocal.add(subdir2);
		fileSystemLocal.add(subdir3);
		
		Directory clonado= null;
		try {
			clonado= fileSystemLocal.clone();
		} catch (CloneNotSupportedException e) {
			fail("No se pudo clonar");
		}
		proyectoServidor= new MockSCVProjectServer();
		proyectoCliente= new SCVProjectCliente(clonado, proyectoServidor);
	}
	
	@After
	public void tearDown(){
		fileSystemLocal= null;
		proyectoCliente= null;
		proyectoServidor= null;
	}
	
	public void modificarDirectory(Directory dir){
		//Creo los nodos a agregar
		Directory subdir= new Directory("Agregado_"+dir.getName(), dir);
		Directory subdir2= new Directory("Otro_"+subdir.getName(), subdir);
		File unArchivo= new File("unArhivo", "nada", subdir2);
		
		//Realizo las modificaciones
		subdir2.add(unArchivo);
		subdir.add(subdir2);
		dir.add(subdir);
	}
	
	public void modificarFile(File unArchivo){
		unArchivo.setContent("Este contenido se seteo en el test");
	}
	
	private File setDeltasForFileUpdate() {		
		//Creo las diferencias
		FileUpdateDifference dif1= new FileUpdateDifference("Cambio el contenido", 0);
		FileAddedLineDifference dif2= new FileAddedLineDifference("Agregado a linea 2", 1);
		FileAddedLineDifference dif3= new FileAddedLineDifference("Agregado a linea 3", 2);
		
		//Agrego las modificaiones a la copia a retornar
		File unaCopiaModificada= null;
		try {
			unaCopiaModificada= file.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		unaCopiaModificada.setContent("Cambio el contenido\r\nAgregado a linea 2\r\nAgregado a linea 3\r\n");
		
		//Creo la revision y le agrego las diferencias
		Revision<File> unaRevision= new Revision<File>(1, null);
		unaRevision.add(dif1);
		unaRevision.add(dif2);
		unaRevision.add(dif3);
		
		//Creo el delta y le agrego la revision
		DeltaNodo<File> unDelta= new DeltaNodo<File>(file.getPath(), null);
		unDelta.addRevision(unaRevision);
		
		proyectoServidor.setUpdateDelta(unDelta);
		
		return unaCopiaModificada;
	}
	
	/**
	 * Crear las deltas o conjuntos de differencias a aplicar al proyecto
	 * Estos deltas o cambios son los que retornara el proyecto del servidor
	 * a fin de controlar que la funcionalidad del proyecto del servidor no
	 * repercuta en el comportamiento del proyecto cliente.
	 *
	 */
	private Directory setDeltasForDirectoryUpdate(){
		Directory fileSystemModificado = null;
		try {
			fileSystemModificado= fileSystemLocal.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		//Creo las diferencias
		FileUpdateDifference dif1= new FileUpdateDifference("Cambio el contenido", 0);
		FileAddedLineDifference dif2= new FileAddedLineDifference("Agregado a linea 2", 1);
		FileAddedLineDifference dif3= new FileAddedLineDifference("Agregado a linea 3", 2);
		
		//Agrego la modificacion a la copia para retornar
		File copiaFile= fileSystemModificado.searchFile(file);
		copiaFile.setContent("Cambio el contenido\r\nAgregado a linea 2\r\nAgregado a linea 3\r\n");
		
		//Creo la revision y le agrego las diferencias
		Revision<File> rev1= new Revision<File>(1, null);
		rev1.add(dif1);
		rev1.add(dif2);
		rev1.add(dif3);
		
		//Creo el delta y le agrego la revision
		DeltaNodo<File> delta1= new DeltaNodo<File>(file.getPath(), null);
		delta1.addRevision(rev1);
		
		//Creo diferencias a nivel de Directorio
		Directory nuevoDir= new Directory("Nuevo_Dir", fileSystemLocal);
		DirectoryAddedNodeDifference dif4= new DirectoryAddedNodeDifference(nuevoDir);
		
		//Agrego la modificacion a la copia para retornar
		nuevoDir.setPredecesor(fileSystemModificado);
		fileSystemModificado.add(nuevoDir);
		
		//Creo la revision y le agrego la diferencia
		Revision<Directory> rev2= new Revision<Directory>(1, null);
		rev2.add(dif4);
		
		//Creo el delta
		DeltaNodo<Directory> delta2= new DeltaNodo<Directory>(fileSystemLocal.getPath(), null);
		delta2.addRevision(rev2);
		
		//Meto los deltas en la lista
		List<DeltaNodo> lista= new ArrayList<DeltaNodo>();
		lista.add(delta1);
		lista.add(delta2);
		
		proyectoServidor.setUpdateDeltas(lista);
		
		return fileSystemModificado;
	}
	
	private String revisarSiCambio(List<DeltaNodo> deltas) {
		for(DeltaNodo<FileSystemNode> unDelta: deltas)
			if (unDelta.getRevision(0).getCantidadDifferencias() != 0)
				return unDelta.getPath();
			
		return null;
	}

	@Test
	public void testCommitDirectory() {
		DeltaGenerator unDeltaGenerator= new DeltaGenerator();
		List<DeltaNodo> listaVacia;
		
		//Hago las modificaciones
		modificarDirectory(this.subdir1_1_1);
		//las comiteo
		try {
			proyectoCliente.commit(this.subdir1_1_1);
		} catch (FirstNeedToMakeUpdateException e) {
			fail("Esto no deberia pasar ni en broma, MockServer");
			e.printStackTrace();
		}

		//Voy a comprobar que los cambios se hagan efectivos
		listaVacia= unDeltaGenerator.compare(proyectoCliente.getBase(), fileSystemLocal);
		
		//Si se actualizo el fyleSystem no habra ningun elemento en la lista que presente cambios
		assertEquals("No se actualizo el fileSystem del cliente, tras comitear", revisarSiCambio(listaVacia), null);
	}

	@Test
	public void testCommitFile() {
		DeltaGenerator unDeltaGenerator= new DeltaGenerator();
		List<DeltaNodo> listaVacia;
		
		//Hago las modificaciones
		modificarFile(this.file);
		
		//las comiteo
		try {
			proyectoCliente.commit(this.file);
		} catch (FirstNeedToMakeUpdateException e) {
			fail("Esto no deberia pasar ni en broma, MockServer");
			e.printStackTrace();
		}

		//Voy a comprobar que los cambios se hagan efectivos
		listaVacia= unDeltaGenerator.compare(proyectoCliente.getBase(), fileSystemLocal);
		
		//Si se actualizo el fyleSystem no habra ningun elemento en la lista que presente cambios
		assertEquals("No se actualizo el fileSystem del cliente, tras comitear", revisarSiCambio(listaVacia), null);
	}

	@Test
	public void testUpdateFile() {
		DeltaGenerator unDeltaGenerator= new DeltaGenerator();
		List<DeltaNodo> listaVacia;
		
		//Setear en el servidor los cambios a retornar, asi retornara
		//los cambios que nosotros decidamos para poder hacer el test
		File modificado= this.setDeltasForFileUpdate();
		
		//Hacer update del archivo
		try {
			proyectoCliente.update(file);
		} catch (ConflictoNodosException e) {
			fail("No puede tirar exception porque es un MockServer");
			e.printStackTrace();
		}
		
		//Modifico el file para que quede como deberia
		file.setContent(modificado.getContent());
		
		//Voy a comprobar que los cambios se hagan efectivos
		listaVacia= unDeltaGenerator.compare(proyectoCliente.getBase(), fileSystemLocal);
		
		//Si se actualizo el fyleSystem no habra ningun elemento en la lista que presente cambios
		assertEquals("No se actualizo el fileSystem del cliente, tras comitear", revisarSiCambio(listaVacia), null);
	}

	@Test
	public void testUpdateDirectory() {
		DeltaGenerator unDeltaGenerator= new DeltaGenerator();
		List<DeltaNodo> listaVacia;
		
		//Setear en el servidor los cambios a retornar, asi retornara
		//los cambios que nosotros decidamos para poder hacer el test
		Directory modificado= this.setDeltasForDirectoryUpdate();
		
		//Hacer update del archivo
		try {
			proyectoCliente.update(fileSystemLocal);
		} catch (BothSourcesModifiedException e) {
			fail("No puede tirar exception porque es un MockServer");
			e.printStackTrace();
		}
		
		//Voy a comprobar que los cambios se hagan efectivos
		listaVacia= unDeltaGenerator.compare(proyectoCliente.getBase(), modificado);
		
		//Si se actualizo el fyleSystem no habra ningun elemento en la lista que presente cambios
		assertEquals("No se actualizo el fileSystem del cliente, tras comitear", revisarSiCambio(listaVacia), null);
	}
}
