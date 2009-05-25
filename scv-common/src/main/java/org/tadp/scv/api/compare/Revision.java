package org.tadp.scv.api.compare;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.filesystem.FileSystemNode;


public class Revision<E extends FileSystemNode> implements Cloneable,Serializable
{
	private List<Difference<E>> differences = null;
	/*
	 * El numero de revision lo agrego para poder implementar
	 * los controles que requiere el protocolo de commit y update
	 * referidos en la entrega 1
	 */
	private int revisionNumber= 0;
	private Date date;
	private String comment;
	
	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public Date getDate()
	{
		return date;
	}

	public Revision(){
		differences = new ArrayList<Difference<E>>();
	}
	
	public Revision(int revisionNumber, List<Difference<E>> differences){
		this.revisionNumber= revisionNumber;
		if( differences!= null)
			this.differences= differences;
		else
			this.differences= new ArrayList<Difference<E>>();
	}
	
	public int getRevisionNumber(){
		return revisionNumber;
	}
	
	public void setRevisionNumber(int revisionNumber){
		this.revisionNumber= revisionNumber;
	}
	
	/**
	 * 
	 * @param difference
	 */
	public void add(Difference<E> difference)
	{
		differences.add(difference);
	}
	
	public void addAll(List<Difference<E>> differences)
	{
		this.differences.addAll(differences);
	}	
	
	/**
	 * 
	 * @param difference
	 */
	public void remove(Difference<E> difference)
	{
		differences.remove(difference);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Difference<E>> getDifferences()
	{
		return differences;
	}
	
	@Override
	public  Revision<E> clone() throws CloneNotSupportedException
	{
		Revision<E> copia= new Revision<E>(0, null);
		for(Difference<E> diff: differences){
			copia.add(diff.clone());
		}
		return copia;
	}

	public int getCantidadDifferencias() {
		return differences.size();
	}

	public void setDate(Date time)
	{
		date = time;		
	}
}
