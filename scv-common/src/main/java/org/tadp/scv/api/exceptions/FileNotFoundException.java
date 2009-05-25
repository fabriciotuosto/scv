package org.tadp.scv.api.exceptions;

public class FileNotFoundException  extends RuntimeException
{
	public FileNotFoundException(String string){
		super(string);
	}
	
	private static final long	serialVersionUID	= 1L;
}
