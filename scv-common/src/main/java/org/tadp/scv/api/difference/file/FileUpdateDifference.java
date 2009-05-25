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
public class FileUpdateDifference extends FileDifference
{
	private String lineAux;
	/**
	 * 
	 * @param line
	 * @param lineNumber
	 */
	public FileUpdateDifference(String line, int lineNumber)
	{
		super(line, lineNumber);
		
	}

	@Override
	/**
	 * 
	 */
	protected void modiffieContent(List<String> listedContent)
	{	
		lineAux= listedContent.get(getLineNumber());
		listedContent.remove(getLineNumber());
		listedContent.add(getLineNumber(),getLine());
	}

	@Override
	protected void undoModifications(List<String> listedContent) {
		listedContent.remove(getLineNumber());
		listedContent.add(getLineNumber(), lineAux);
	}
	
	public Difference<File> clone() throws CloneNotSupportedException{
		FileUpdateDifference copia= new FileUpdateDifference(this.getLine(), this.getLineNumber());
		copia.setLineAux(this.getLineAux());
		return copia;
	}
	
	public void setLineAux(String lineAux){
		this.lineAux= lineAux;
	}
	
	public String getLineAux(){
		return this.lineAux;
	}
}
