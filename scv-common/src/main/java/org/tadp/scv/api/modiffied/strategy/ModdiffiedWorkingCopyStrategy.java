package org.tadp.scv.api.modiffied.strategy;

import java.util.List;

import org.tadp.scv.api.builder.FileSystemNodeBuilder;
import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.filesystem.FileSystemNode;

/**
 * 
 * @author Fabricio
 *
 */
public class ModdiffiedWorkingCopyStrategy implements StrategyModiffiedNode
{

	/**
	 * 
	 */
	public void moddifedNode(FileSystemNode workingBase, FileSystemNode workingCopy, List<Difference<FileSystemNode>> differences)
	{	
		FileSystemNodeBuilder<FileSystemNode> builder = new FileSystemNodeBuilder<FileSystemNode>();
		builder.setNode(workingCopy);
		builder.append(differences);
		workingCopy = builder.build();		
	}

}
