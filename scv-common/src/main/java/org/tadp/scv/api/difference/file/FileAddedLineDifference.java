package org.tadp.scv.api.difference.file;

import java.util.List;

import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.difference.FileDifference;
import org.tadp.scv.api.filesystem.File;

/**
 * 
 * @author Fabricio
 *
 */
public class FileAddedLineDifference extends FileDifference
{

	/**
	 * 
	 * @param line
	 * @param lineNumber
	 */
	public FileAddedLineDifference(String line, int lineNumber)
	{
		super(line, lineNumber);
	}

	@Override
	/**
	 * 
	 */
	protected void modiffieContent(List<String> listedContent)
	{
		listedContent.add(getLineNumber(),getLine());
	}

	@Override
	protected void undoModifications(List<String> listedConten) {
		listedConten.remove(listedConten.indexOf(getLine()));
	}

	public Difference<File> clone() throws CloneNotSupportedException{
		FileAddedLineDifference copia= new FileAddedLineDifference(this.getLine(), this.getLineNumber());
		return copia;
	}

}
