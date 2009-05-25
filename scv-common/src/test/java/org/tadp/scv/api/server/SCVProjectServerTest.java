package org.tadp.scv.api.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tadp.scv.api.Tag;
import org.tadp.scv.api.compare.DeltaGenerator;
import org.tadp.scv.api.compare.DeltaNodo;
import org.tadp.scv.api.compare.FileComparator;
import org.tadp.scv.api.compare.Revision;
import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.difference.directory.DirectoryAddedNodeDifference;
import org.tadp.scv.api.difference.directory.DirectoryRemoveNodeDifference;
import org.tadp.scv.api.difference.file.FileAddedLineDifference;
import org.tadp.scv.api.difference.file.FileUpdateDifference;
import org.tadp.scv.api.exceptions.BothSourcesModifiedException;
import org.tadp.scv.api.exceptions.FirstNeedToMakeUpdateException;
import org.tadp.scv.api.exceptions.TagNotFoundException;
import org.tadp.scv.api.exceptions.UnableToTagException;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.server.DirectoryVersionable;
import org.tadp.scv.api.filesystem.server.FileVersionable;
import org.tadp.scv.api.filesystem.server.Versionable;

public class SCVProjectServerTest {
	private Directory fileSystemLocal, fileSystemServer;
	private SCVProjectServer proyecto;
	private Directory subdir1, subdir2, subdir3, subdir1_1, subdir1_1_1;
	private File file;
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
				subdir1_1.add(file = new File("excel.xls", "Una planilla de Excel\r\n", subdir1_1));							
			subdir2 = new Directory("subdir2", fileSystemLocal);
			subdir2.add(new File("powerpoint.ppt", "Una presentacion de Power Point", subdir2));
			subdir3 = new Directory("subdir3", fileSystemLocal);
		
		subdir1_1.add(subdir1_1_1);
		subdir1.add(subdir1_1);
		
		fileSystemLocal.add(subdir1);
		fileSystemLocal.add(subdir2);
		fileSystemLocal.add(subdir3);
		
		/* Clono el filesystem para que no usen el mismo */
		try {
			fileSystemServer = fileSystemLocal.clone();
		} catch (CloneNotSupportedException e) {
			fail("No puede clonar el filesystem");
		}

		/* Creo un proyecto del server con el filesystem */
		proyecto = new SCVProjectServer(fileSystemServer);		
	}

	@After
	public void tearDown(){
		fileSystemLocal = fileSystemServer = null;
		proyecto = null;
		subdir1 = subdir2 = subdir3 = subdir1_1 = subdir1_1_1 = null;
		file = null;
	}
	
	@Test
	public void testFindNodeDir(){
		Versionable nodo = proyecto.findNode(subdir1_1_1.getPath());
		assertEquals("El findNode no trajo el mismo directorio", nodo.getPath(), subdir1_1_1.getPath());
	}
	
	@Test
	public void testFindNodeFile(){
		Versionable nodo = proyecto.findNode(file.getPath());
		assertEquals("El findNode no trajo el mismo file", nodo.getPath(), file.getPath());
	}
	
	@Test
	public void testFallarFindNodeFile(){
		Versionable nodo = proyecto.findNode(file.getPath().substring(0, 9));
		assertEquals("El findNode no trajo el mismo file", nodo, null);
	}
	
	@Test
	public void testDiff(){
		
		DirectoryVersionable copiaBase = proyecto.versionarFileSystem(fileSystemLocal);
		Directory workingCopy;
		try {
			workingCopy = copiaBase.clone();
			File file = (File)workingCopy.findNode("\\subdir1\\subdir1_1\\excel.xls");
			file.setContent("Esto fue agregado en el test.");
			
			File fileCopy = (File)workingCopy.findNode("\\subdir1\\subdir1_1\\excel.xls");
			File fileBase = ((FileVersionable)copiaBase.findNode("\\subdir1\\subdir1_1\\excel.xls")).getFile();
			  
			//System.out.println("FileBase.contenido: " + fileBase.getContent() );
			//System.out.println("FileCopy.contenido: " + fileCopy.getContent() );
			
			List<Difference<File>> diffs = new FileComparator().compare(fileBase, fileCopy);
			assertEquals("No es 1", diffs.size(), 1);
			
			// Pruebo lo mismo pero usando el deltagenerator
			FileVersionable baseVersionable = (FileVersionable)copiaBase.findNode("\\subdir1\\subdir1_1\\excel.xls");
			DeltaNodo delta = new DeltaGenerator().compare(baseVersionable, fileCopy);
			List<DeltaNodo> deltas = new ArrayList<DeltaNodo>();		
			deltas.add(delta);
			assertEquals("No detecto que cambio el file", revisarSiCambio(deltas), file.getPath());
		} catch (CloneNotSupportedException e) {
			fail("Alguien se olvido de implementar clone");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNoDiff(){
		
		DirectoryVersionable copiaBase = proyecto.versionarFileSystem(fileSystemLocal);
		Directory workingCopy;
		try {
			workingCopy = copiaBase.clone();
	
			File fileCopy = (File)workingCopy.findNode("\\subdir1\\subdir1_1\\excel.xls");
			File fileBase = ((FileVersionable)copiaBase.findNode("\\subdir1\\subdir1_1\\excel.xls")).getFile();
			
			//System.out.println("FileBase.contenido: " + fileBase.getContent() );
			//System.out.println("FileCopy.contenido: " + fileCopy.getContent() );
			
			List<Difference<File>> diffs = new FileComparator().compare(fileBase, fileCopy);
			assertEquals("No es 1", diffs.size(), 0);
			
			// Pruebo lo mismo pero usando el deltagenerator
			FileVersionable baseVersionable = (FileVersionable)copiaBase.findNode("\\subdir1\\subdir1_1\\excel.xls");
			DeltaNodo delta = new DeltaGenerator().compare(baseVersionable, fileCopy);
			List<DeltaNodo> deltas = new ArrayList<DeltaNodo>();		
			deltas.add(delta);
			assertEquals("No detecto que cambio el file", revisarSiCambio(deltas), null);
			
		} catch (CloneNotSupportedException e) {
			fail("Alguien se olvido de implementar clone");
		}
		
	}
	private String revisarSiCambio(List<DeltaNodo> deltas) {
		for(DeltaNodo<FileSystemNode> unDelta: deltas)
			if (unDelta.getRevision(0).getCantidadDifferencias() != 0)
				return unDelta.getPath();
			
		return null;
	}

	@Test
	public void testCommit(){
		
		try {
			DirectoryVersionable copiaBase = proyecto.versionarFileSystem(fileSystemLocal);
			Directory workingCopy = copiaBase.clone();
			File file = (File)workingCopy.findNode("\\subdir1\\subdir1_1\\excel.xls");
			file.setContent("Esto fue agregado en el test.");
			
			List<DeltaNodo> deltas = new DeltaGenerator().compare(copiaBase, workingCopy);	

			//	11 es la cantidad de nodos que cargue en el filesystem -> Debe crear un Delta para c/u
			assertEquals("NO detecto algun delta", deltas.size(), 11);
			
			// Verifico que haya encontrado que el cambio fue el file que cambie.
			assertEquals("No detecto que cambio el file", revisarSiCambio(deltas), file.getPath());
			
			System.out.println("\n\nRevisar si cambio "+revisarSiCambio(deltas));

			// Commiteo en el server
			proyecto.commit(deltas);
			
			// Saca los deltas del proyecto en el server
			List<DeltaNodo> deltasServer = new DeltaGenerator().compare(proyecto.getRoot(), workingCopy);
			
			// Verifico que si se commitearon los deltas anteriores, ahora los deltas deberian estar en blanco
			// ya que ambos filesystems estan iguales
			assertEquals("No detecto que cambio el file", revisarSiCambio(deltasServer), null);
			
			System.out.println("\n\nRevisar si cambio "+revisarSiCambio(deltas));

		} catch (CloneNotSupportedException e) {
			fail("No soporto la clonacion del file system local");
		} catch (FirstNeedToMakeUpdateException e) {
			fail("Soy el unico usuario que modifica, no deberia hacer update antes ");
		}
	}
	
	@Test (expected = FirstNeedToMakeUpdateException.class) 
	public void testExceptionInCommit() throws FirstNeedToMakeUpdateException{
		try {
			// Simulo dos clientes sobre el mismo proyecto, ambos inician con la misma version
			DirectoryVersionable copiaBaseCliente1 = proyecto.versionarFileSystem(fileSystemLocal);
			Directory workingCopyCliente1 = copiaBaseCliente1.clone();
			DirectoryVersionable copiaBaseCliente2 = proyecto.versionarFileSystem(fileSystemLocal);
			Directory workingCopyCliente2 = copiaBaseCliente2.clone();

			// El cliente 1 modifica su file
			File file = (File)workingCopyCliente1.findNode("\\subdir1\\subdir1_1\\excel.xls");
			file.setContent("Esto fue agregado en el test, por el cliente 1.");
			
			List<DeltaNodo> deltas = new DeltaGenerator().compare(copiaBaseCliente1, workingCopyCliente1);	

			// Commiteo en el server el Cliente 1
			proyecto.commit(deltas);

			file = (File)workingCopyCliente2.findNode("\\subdir1\\subdir1_1\\excel.xls");
			file.setContent("Esto fue agregado en el test, por el cliente 2.");
			
			deltas = new DeltaGenerator().compare(copiaBaseCliente2, workingCopyCliente2);	

			// Commiteo en el server el Cliente 2 -> Deberia estar out-of-date
			proyecto.commit(deltas);
			
		} catch (CloneNotSupportedException e) {
			fail("No soporto la clonacion del file system local");
		}
	}
	
	@Test
	public void testUpdate() {
		try {
			// Simulo dos clientes sobre el mismo proyecto, ambos inician con la misma version
			DirectoryVersionable copiaBaseCliente1 = proyecto.versionarFileSystem(fileSystemLocal);
			Directory workingCopyCliente1 = copiaBaseCliente1.clone();
			DirectoryVersionable copiaBaseCliente2 = proyecto.versionarFileSystem(fileSystemLocal);
			Directory workingCopyCliente2 = copiaBaseCliente2.clone();

			// El cliente 1 modifica su file
			File file = (File)workingCopyCliente1.findNode("\\subdir1\\subdir1_1\\excel.xls");
			file.setContent("Esto fue agregado en el test, por el cliente 1.");
			
			List<DeltaNodo> deltasCommit = new DeltaGenerator().compare(copiaBaseCliente1, workingCopyCliente1);	

			assertFalse("No encontro cambios para commitear", revisarSiCambio(deltasCommit) == null);
			
			// Commiteo en el server el Cliente 1
			proyecto.commit(deltasCommit);
			/*----------------------------------*/

			// Cliente 2 va a hacer update
			List<DeltaNodo> deltasUpdate = new DeltaGenerator().compare(copiaBaseCliente2, workingCopyCliente2);	

			deltasUpdate = proyecto.update(deltasUpdate);
			
			assertFalse("No encontro cambios para updatear", revisarSiCambio(deltasUpdate) == null);
			
			assertEquals("El delta de commit por Cli1 no es el update de Cli2", 
					revisarSiCambio(deltasCommit), revisarSiCambio(deltasUpdate));
		
		} catch (CloneNotSupportedException e) {
			fail("No soporto la clonacion del file system local");
		} catch (FirstNeedToMakeUpdateException e){
			fail("Debe hacer update el cliente cuando se commitea por 1era vez");
		} catch (BothSourcesModifiedException e) {
			fail("Ambos modificados cuando el cliente2 no modifico su copia");			
		}
	}
	
	@Test (expected = BothSourcesModifiedException.class)
	public void testExceptionInUpdate() throws BothSourcesModifiedException {
		try {
			// Simulo dos clientes sobre el mismo proyecto, ambos inician con la misma version
			DirectoryVersionable copiaBaseCliente1 = proyecto.versionarFileSystem(fileSystemLocal);
			Directory workingCopyCliente1 = copiaBaseCliente1.clone();
			DirectoryVersionable copiaBaseCliente2 = proyecto.versionarFileSystem(fileSystemLocal);
			Directory workingCopyCliente2 = copiaBaseCliente2.clone();

			// El cliente 1 modifica su file
			File file = (File)workingCopyCliente1.findNode("\\subdir1\\subdir1_1\\excel.xls");
			file.setContent("Esto fue agregado en el test, por el cliente 1.");
			
			List<DeltaNodo> deltasCommit = new DeltaGenerator().compare(copiaBaseCliente1, workingCopyCliente1);	

			assertFalse("No encontro cambios para commitear", revisarSiCambio(deltasCommit) == null);
			
			// Commiteo en el server el Cliente 1 sus cambios
			proyecto.commit(deltasCommit);
			/*----------------------------------*/

			// Cliente 2 modifica su archivo.
			file = (File)workingCopyCliente2.findNode("\\subdir1\\subdir1_1\\excel.xls");
			file.setContent("Esto fue agregado en el test, por el cliente 2.");
						
			// Cliente 2 va a hacer update de un archivo out-of-date y encima modificado por el.
			List<DeltaNodo> deltasUpdate = new DeltaGenerator().compare(copiaBaseCliente2, workingCopyCliente2);	

			deltasUpdate = proyecto.update(deltasUpdate);
			
			assertFalse("No encontro cambios para updatear", revisarSiCambio(deltasUpdate) == null);

		} catch (CloneNotSupportedException e) {
			fail("No soporto la clonacion del file system local");
		} catch (FirstNeedToMakeUpdateException e){
			fail("Debe hacer update el cliente cuando se commitea por 1era vez");
		}		
	}
	
	@Test
	public void testCreateTag(){
		try {
			// Creo un tag del proyecto con el nombre Tag_2daEntrega
			proyecto.createTag("Tag_2daEntrega");
		
			// Recupero el tag
			Tag tag = proyecto.getTag("Tag_2daEntrega");
		
			// Reviso que el filesystem del proyecto sea el mismo tageado
			assertEquals("Tag no coincide con proyecto tageado", tag.getRoot(), proyecto.getRoot());
			
			// borro un directorio del root
			DirectoryVersionable dir = proyecto.getRoot().searchDirectory(this.subdir1);
			proyecto.getRoot().remove(dir);
			
			// modifico el filesystem
			DeltaNodo<Directory> delta = new DeltaNodo<Directory>(proyecto.getRoot().getPath(), null);
			Revision<Directory> rev = new Revision<Directory>(proyecto.getRoot().getRevisionNumber(), null);
			rev.add(new DirectoryRemoveNodeDifference(dir.getDirectory()));
			delta.addRevision(rev);
			
			// commiteo
			proyecto.commit(delta);
			
			// Compruebo que el filesystem del proyecto no sea el mismo que el tageado
			//assertFalse("Tag no coincide con proyecto tageado", tag.getRoot().getDirectories().equals(proyecto.getRoot().getDirectories()));

		} catch (UnableToTagException e) {
			fail("No pudo crear el tag, posible problema por clonacion");
		} catch (TagNotFoundException e) {
			fail("No encontro el tag creado recien");
		} catch (FirstNeedToMakeUpdateException e) {
			fail("Necesita hacer update siendo el unico usuario");
		}
	}
	
	@Test (expected = TagNotFoundException.class)
	public void testFallarTag() throws TagNotFoundException{ 
		try{
			// Creo un tag del proyecto con el nombre Tag_2daEntrega
			proyecto.createTag("Tag_2daEntrega");
		
			// Recupero el tag
			proyecto.getTag("Tag_Inexistente");
		
		} catch (UnableToTagException e) {
			fail("No pudo crear el tag, posible problema por clonacion");
		}
	}
	
	private File comitearUnCambioFile(File unFile) {		
		//Creo las diferencias
		FileUpdateDifference dif1= new FileUpdateDifference("Cambio el contenido", 0);
		FileAddedLineDifference dif2= new FileAddedLineDifference("Agregado a linea 2", 1);
		FileAddedLineDifference dif3= new FileAddedLineDifference("Agregado a linea 3", 2);
		
		//Agrego las modificaiones a la copia a retornar
		File unaCopiaModificada= null;
		try {
			unaCopiaModificada= unFile.clone();
		} catch (CloneNotSupportedException e) {
			fail("Alguien se olvido de implementar clone");
		}
		unaCopiaModificada.setContent("Cambio el contenido\r\nAgregado a linea 2\r\nAgregado a linea 3\r\n");
		
		//Creo la revision y le agrego las diferencias
		Revision<File> unaRevision= new Revision<File>();
		unaRevision.add(dif1);
		unaRevision.add(dif2);
		unaRevision.add(dif3);
		
		//Creo el delta y le agrego la revision
		DeltaNodo<File> unDelta= new DeltaNodo<File>(unFile.getPath(), null);
		unDelta.addRevision(unaRevision);
		
		//Comiteo el cambio
		try {
			proyecto.commit(unDelta);
		} catch (FirstNeedToMakeUpdateException e) {
			fail("El nodo ha sido modficado con anerioridad, imposible soy el unico usuario");
		}
		
		return unaCopiaModificada;
	}
	
	/**
	 * Crear las deltas o conjuntos de differencias a aplicar al proyecto
	 * Estos deltas o cambios son los que retornara el proyecto del servidor
	 * a fin de controlar que la funcionalidad del proyecto del servidor no
	 * repercuta en el comportamiento del proyecto cliente.
	 *
	 */
	private Directory comitearUnCambioDirectory(Directory unDir){
		Directory fileSystemModificado = null;
		try {
			fileSystemModificado= unDir.clone();
		} catch (CloneNotSupportedException e) {
			fail("Alguien se olvido de implementar clone");
		}
		
		//Creo diferencias a nivel de Directorio
		Directory nuevoDir= new Directory("Nuevo_Dir", unDir);
		DirectoryAddedNodeDifference dif4= new DirectoryAddedNodeDifference(nuevoDir);
		
		//Agrego la modificacion a la copia para retornar
		nuevoDir.setPredecesor(fileSystemModificado);
		fileSystemModificado.add(nuevoDir);
		
		//Creo la revision y le agrego la diferencia
		Revision<Directory> rev2= new Revision<Directory>(1, null);
		rev2.add(dif4);
		
		//Creo el delta
		DeltaNodo<Directory> delta2= new DeltaNodo<Directory>(unDir.getPath(), null);
		delta2.addRevision(rev2);
		
		//Meto los deltas en la lista
		List<DeltaNodo> lista= new ArrayList<DeltaNodo>();
		lista.add(delta2);
		
		try {
			proyecto.commit(lista);
		} catch (FirstNeedToMakeUpdateException e) {
			fail("El nodo ha sido modficado con anerioridad, imposible soy el unico usuario");
		}
		
		return fileSystemModificado;
	}
	
	/**
	 * Para el correcto funcionamiento de este test la operacion
	 * de publicacion de cambios debe pasar su test...
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetToRevisionForFile(){
		
		//Creo una nueva revision del archivo en el Servidor
		File fileModificado= this.comitearUnCambioFile(this.file);
		
		//Pido la revision actual del archivo
		FileVersionable unFileVersionable= (FileVersionable) proyecto.getToRevision(this.file, 1);
		
		//La comparo con fileModificado que es como deberia haber quedado
		assertEquals("No trajo la version actual del archivo", fileModificado.getContent(), unFileVersionable.getContent());
		
		//Ahora pido la revision anterior, como estaba en el sistema de archivos local
		unFileVersionable= (FileVersionable) proyecto.getToRevision(this.file, 0);
		
		//La comparo con el archivo sin modificaciones que es como deberia estasr
		assertEquals("No trajo la version 0 del archivo", file.getContent(), unFileVersionable.getContent());
	}
	
	/**
	 * Para el correcto funcionamiento de este test la operacion
	 * de publicacion de cambios debe pasar su test...
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetToRevisionForDirectory(){
		
		//Creo una nueva revision del archivo en el Servidor
		Directory dirModificado= this.comitearUnCambioDirectory(fileSystemLocal);
		
		//Pido la revision actual del archivo
		DirectoryVersionable unDir= (DirectoryVersionable) proyecto.getToRevision(this.fileSystemLocal, 1);
		
		//Saco los deltas
		List<DeltaNodo> deltas= new DeltaGenerator().compare(unDir, dirModificado);
		
		//Entre la version modificada y la obtenido no deberia haber diferencias
		assertEquals("La version actual no se corresponde con la version modificada", this.revisarSiCambio(deltas), null);
		
		//Ahora pido la revision anterior, como estaba en el sistema de archivos local
		unDir= (DirectoryVersionable) proyecto.getToRevision(this.fileSystemLocal, 0);
		
		//Saco los deltas
		deltas= new DeltaGenerator().compare(unDir, fileSystemLocal);
		
		//Entre la version modificada y la obtenido no deberia haber diferencias
		assertEquals("La version 0 no es la version base", this.revisarSiCambio(deltas), null);
	}
	
	
	@Test
	public void testGetRevisionsDirectory(){
		//Comiteo cambios para que se creen revisiones
		comitearUnCambioDirectory(fileSystemLocal);
		
		//Traer el historial de cambios
		List<Revision> historia= proyecto.getRevisions(this.fileSystemLocal, 1, 1);
		
		//Me fijo que solo haya una revision
		assertEquals("Debe existir solo 1 revision",historia.size(), 1);
		
		//Controlo que sea la revision numero 0
		assertEquals("No es el numero de revision que pedi", historia.get(0).getRevisionNumber(), 0);
	}
	
}
