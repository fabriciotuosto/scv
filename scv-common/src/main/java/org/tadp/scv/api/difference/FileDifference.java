/**
 * 
 */
package org.tadp.scv.api.difference;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.server.FileVersionable;
import org.tadp.scv.api.filesystem.server.Versionable;

/**
 * @author Fabricio
 *
 */
public abstract class FileDifference implements Difference<File>
{
	protected String line = null;
	private int lineNumber;
	
	/**
	 * 
	 * @param line
	 * @param lineNumber
	 */
	public FileDifference(String line,int lineNumber)
	{
		super();
		this.line = line;
		this.lineNumber = lineNumber;
	}
	
	/**
	 * 
	 * @param lineas
	 * @return
	 */
	protected String buildStringFrom(List<String> lineas)
	{		
		String endOfLine = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		for(String string : lineas)
		{
			sb.append(string);
			sb.append(endOfLine);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param content
	 * @return
	 */
	protected List<String> createListFromString(String content)
	{
		List<String> resultado = new ArrayList<String>();
		Scanner scanner = new Scanner(content);
		while(scanner.hasNextLine())
		{
			resultado.add(scanner.nextLine());
		}
		return resultado;
	}
	
	/**
	 * 
	 * @return
	 */
	protected int getLineNumber()
	{
		return lineNumber;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getLine()
	{
		return line;
	}
	
	public void apply(File node)
	{
		List<String> listedContent = createListFromString(node.getContent());
		modiffieContent(listedContent);
		node.setContent(buildStringFrom(listedContent));
	}
	
	public void undo(File node){
		List<String> listedContent = createListFromString(node.getContent());
		this.undoModifications(listedContent);
		node.setContent(buildStringFrom(listedContent));
	}

	protected abstract void modiffieContent(List<String> listedContent);
	
	protected abstract void undoModifications(List<String> listedConten);
	
	public void apply(Versionable<File> node){
		this.apply(((FileVersionable)node).getFile());
	}
	
	public void undo(Versionable<File> node){
		this.undo(((FileVersionable)node).getFile());
	}
	
	public abstract Difference<File> clone() throws CloneNotSupportedException;
}