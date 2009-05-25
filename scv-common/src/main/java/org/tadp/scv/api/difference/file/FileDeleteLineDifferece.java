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
public class FileDeleteLineDifferece extends FileDifference
{

	/**
	 * 
	 * @param line
	 * @param lineNumber
	 */
	public FileDeleteLineDifferece(String line, int lineNumber)
	{
		super(line, lineNumber);
	}

	@Override
	/**
	 * 
	 */
	protected void modiffieContent(List<String> listedContent)
	{
		listedContent.remove(getLineNumber());
	}

	@Override
	protected void undoModifications(List<String> listedConten) {
		listedConten.add(getLineNumber(), getLine());
	}
	
	public Difference<File> clone() throws CloneNotSupportedException{
		FileDeleteLineDifferece copia= new FileDeleteLineDifferece(this.getLine(), this.getLineNumber());
		return copia;
	}
}
