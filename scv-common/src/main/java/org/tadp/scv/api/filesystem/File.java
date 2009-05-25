package org.tadp.scv.api.filesystem;

import org.tadp.scv.api.compare.DirectoryComparator;
import org.tadp.scv.api.compare.FileComparator;

/**
 * @author Fabricio
 */
public class File extends FileSystemNode
{

	private String	content	= null;

	/**
	 * 
	 */
	public File()
	{
		super();
	}

	/**
	 * @param name
	 * @param esor
	 *            nodo padre del archivo
	 */
	public File(String name, Directory predecesor)
	{
		super(name, predecesor);
	}

	/**
	 * @param name
	 *            Nombre del File
	 * @param content
	 *            Contenido del File
	 * @param predecesor
	 *            nodo padre del archivo
	 */
	public File(String name, String content, Directory predecesor)
	{
		super(name, predecesor);
		this.setContent(content);
	}

	/**
	 * @return
	 */
	public String getContent()
	{
		return content;
	}

	@Override
	/**
	 * 
	 */
	public boolean isDirectory()
	{
		return !isFile();
	}

	@Override
	/**
	 * 
	 */
	public boolean isFile()
	{
		return true;
	}

	public boolean equals(Object arg0)
	{
		boolean result = false;
		try
		{
			File node = (File) arg0;
			FileComparator fileComp = new FileComparator();
			if (this.getPath().equals(node.getPath()) && fileComp.compare(this, node).size() == 0)
				result = true;
		} catch (ClassCastException e)
		{
			return result;
		}
		return result;
	}

	@Override
	/**
	 * Hay que redefinir el clone para poder hacer el tag y ademas para que
	 * funciones de manera independiente el versionado sobre cualquier
	 * filesystem
	 * 
	 * @author SDR Devuelve un File de mismo nombre y contenido
	 */
	public File clone() throws CloneNotSupportedException
	{
		File clonado = new File(this.getName(), this.getContent(), this.getPredecesor());
		return clonado;
	}

	public void setContent(String string)
	{
		this.content = string;
	}
}
