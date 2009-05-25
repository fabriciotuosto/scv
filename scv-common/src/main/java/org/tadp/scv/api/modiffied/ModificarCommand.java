package org.tadp.scv.api.modiffied;

import java.util.List;

import org.tadp.scv.api.compare.Comparator;
import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.exceptions.BothSourcesModifiedException;
import org.tadp.scv.api.exceptions.FirstNeedToMakeUpdateException;
import org.tadp.scv.api.filesystem.Directory;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.modiffied.strategy.ModdiffiedWorkginBaseStrategy;
import org.tadp.scv.api.modiffied.strategy.ModdiffiedWorkingCopyStrategy;
import org.tadp.scv.api.modiffied.strategy.StrategyModiffiedNode;


public class ModificarCommand
{
	private Comparator<FileSystemNode> dirComparator;
	private Comparator<FileSystemNode> fileComparator;
	
	public ModificarCommand()
	{
		
	}
	/**
	 * 
	 * @param base
	 * @param copy
	 */
	public void update(FileSystemNode base,FileSystemNode copy)
	{
		modificarNodo(base,copy,new ModdiffiedWorkingCopyStrategy());
	}
	
	/**
	 * 
	 * @param base
	 * @param copy
	 */
	public void commit(FileSystemNode base,FileSystemNode copy)
	{
		modificarNodo(base,copy,new ModdiffiedWorkginBaseStrategy());
	}
	
	/**
	 * 
	 * @param base
	 * @param copy
	 * @throws FirstNeedToMakeUpdateException
	 * @throws BothSourcesModifiedException
	 */
	public void checkRevisions(FileSystemNode base, FileSystemNode copy)throws FirstNeedToMakeUpdateException, BothSourcesModifiedException{
		
	}

	/**
	 * 
	 * @param base
	 * @param copy
	 * @param strategy
	 */
	private void modificarNodo(FileSystemNode base, FileSystemNode copy, StrategyModiffiedNode strategy)
	{
		List<Difference<FileSystemNode>> differences = null;
		if (copy.isFile())
		{
			differences = fileComparator.compare(base, copy);
		}else
		{
			Directory dirCopy = (Directory) copy;
			Directory dirBase = (Directory) base;
			
			//comparo los archivos solo si existen en ambos lugares, sino se crean en la comparacion de directorio
			for (File file : dirCopy.getFiles())
			{
				//Si no existe la base no se compara sino que se agrega cuando se compara a nivel directorio
				if (dirBase.searchFile(file)!=null)
				{
					modificarNodo(dirBase.searchFile(file), file, strategy);
				}
			}
			//comparo los directorios solo si existen en ambos lugares, sino se crean en la comparacion de directorio
			for(Directory dir : dirCopy.getDirectories())
			{
				//Si no existe la base no se compara sino que se agrega cuando se compara a nivel directorio
				if (dirBase.searchDirectory(dir)!=null)
				{
					modificarNodo(dirBase.searchDirectory(dir), dir, strategy);
				}				
			}
			//Finalmente se comparan los directorios y se actualiza segun corresponda
			differences = dirComparator.compare(base, copy);
			strategy.moddifedNode(base, copy, differences);			
		}
		strategy.moddifedNode(base, copy, differences);

	}
	
}
