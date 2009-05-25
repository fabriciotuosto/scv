package org.tadp.scv.api.filesystem.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.tadp.scv.api.builder.VersionableNodeBuilder;
import org.tadp.scv.api.compare.Revision;
import org.tadp.scv.api.exceptions.IntedToAddExistingNode;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;


@SuppressWarnings("serial")
public class FileVersionable extends File implements Versionable<File>
{

	private File decorated;
	private VersionableNodeBuilder<File> fileBuilder = null;
	private int revisionActual;
	private List<Revision<File>> revisions = null;
	private Date creationDate;

	/**
	 * 
	 * @param name
	 * @param content
	 */
	public FileVersionable(File decorated)
	{
		try {
			this.creationDate = new Date();
			this.fileBuilder = new VersionableNodeBuilder<File>();
			this.revisions = new ArrayList<Revision<File>>();
			this.decorated = decorated.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unable to create instance");
		}
	}

	/**
	 * 
	 * @param rev
	 */
	public void addRevision(Revision<File>rev)
	{
		rev.setRevisionNumber(this.getNumberOfLastRevision()+1);
		rev.setDate(Calendar.getInstance().getTime());
		this.revisions.add(rev);
	}
	
	@Override
	/**
	 * Este clone va a devolver el estado inicial del directorio sin tener
	 * en cuenta las modificaciones  en las revisiones al igual que en
	 * DirectoryVersionable
	 */
	public File clone() throws CloneNotSupportedException
	{
		return this.decorated.clone();
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof FileVersionable) &&
		 		this.decorated.equals(((FileVersionable)o).getFile());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBasicContent()
	{ 
		int revision= this.getRevisionNumber();
		File basico= null;
		String contenido= null;
		try {
			basico= this.getToRevision(0);
			contenido= basico.getContent();
			this.getToRevision(revision);
			//Estas excepciones no las trato porque no pueden ocurrir
			//si se utilizan estos parametros
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IntedToAddExistingNode e) {
			e.printStackTrace();
		}
		return contenido;
	}
	
	public String getContent(){
		return decorated.getContent();
	}
	
	/**
	 * 
	 * @param revisionNumber
	 * @return
	 * @throws IntedToAddExistingNode 
	 * @throws IllegalArgumentException 
	 */
	public String getContent(int revisionNumber) throws IntedToAddExistingNode
	{
		return this.getToRevision(revisionNumber).getContent();
	}
	
	/**
	 * @return devuelve el archivo que decora
	 */
	public File getFile() {
		return this.decorated;
	}
	
	public String getName() {
		return this.decorated.getName();
	}

	public int getNumberOfLastRevision()
	{
		int cantidadRevisiones= this.revisions.size();
		if(cantidadRevisiones != 0)
			return this.revisions.get(cantidadRevisiones-1).getRevisionNumber();
		
		return 0;
	}
	
	public String getPath() {
		return this.decorated.getPath();
	}

	public Directory getPredecesor() {
		return this.decorated.getPredecesor();
	}

	public Revision<File> getRevision(int nroRevision) throws IllegalArgumentException{
		if (nroRevision > this.revisions.size())
			throw new IllegalArgumentException("El numero de revision no puede ser mayor a la revision maxima del archivo");

		return this.revisions.get(nroRevision);
	}

	/**
	 * 
	 * @return
	 */
	public int getRevisionNumber()
	{
		return this.revisionActual;
	}

	/**
	 * Este metodo devuelve el archivo con el contenido del File 
	 * hasta la revision dada
	 * 
	 * @param revisionNumber
	 * @return
	 * @throws IntedToAddExistingNode 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public File getToRevision(int revisionNumber) throws IllegalArgumentException, IntedToAddExistingNode
	{
		if (revisionNumber > this.revisions.size() || revisionNumber < 0)
		{
			throw new IllegalArgumentException("El numero de revision no puede ser mayor a la revision maxima del archivo");
		}
		//Cargo el nodo a modificar
		this.fileBuilder.setNode(this);
		
		//Veo si quiere aplicar cambios o ir a una version anterior
		if( revisionNumber > this.revisionActual){
			//cargo las modificaciones desde mi version hasta la que quiere ir
			for(int i = this.revisionActual; i < revisionNumber;i++)
			{
				this.fileBuilder.append(this.revisions.get(i).getDifferences());
			}		
			//aplico cambios
			this.fileBuilder.build();
		}else if(revisionNumber < this.revisionActual){ 
			//cargo las modificaciones desde la version a la que quiere volver
			//hasta la mia.
			for(int i = this.revisionActual-1; i >= revisionNumber ;i--)
			{
				this.fileBuilder.append(this.revisions.get(i).getDifferences());
			}		
			this.fileBuilder.undoBuild();
		}
		revisionActual= revisionNumber;
		
		return this.decorated;
	}
	
	@SuppressWarnings("unchecked")
	public Versionable getCopy(){
		FileVersionable copia= new FileVersionable(this.decorated);
		for(Revision r: this.revisions){
			try {
				copia.addRevision(r.clone());
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException("Alguien se olvido de implementar clone");
			}
		}
		copia.setRevisionActual(this.revisionActual);
		return copia;
	}

	@Override
	public int hashCode() {
		return this.decorated.hashCode();
	}

	public boolean isDirectory() {
		return this.decorated.isDirectory();
	}

	public boolean isFile() {
		return this.decorated.isFile();
	}

	public void setContent(String string) {
		this.decorated.setContent(string);
	}

	public void setName(String name) {
		this.decorated.setName(name);
	}

	public void setPredecesor(Directory root) {
		this.decorated.setPredecesor(root);
	}

	public void setRevisionActual(int revisionActual) {
		this.revisionActual = revisionActual;
	}
	
	@Override
	public String toString()
	{
		return decorated.toString();
	}

	public List<Revision<File>> getRevisions()
	{
		return revisions;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}

