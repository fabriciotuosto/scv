package org.tadp.scv.api.filesystem;

public interface FileSystemNodeInterface extends Cloneable{
	public boolean isDirectory();
	
	public  boolean isFile();
	
	public String getName();
	
	public void setName(String name);
	
	public boolean equals(Object arg0);
	
	public int hashCode();
	
	public String getPath();
	
	public Directory getPredecesor();

	public void setPredecesor(Directory root);
}
