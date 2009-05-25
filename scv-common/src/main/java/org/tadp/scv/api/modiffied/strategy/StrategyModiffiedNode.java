package org.tadp.scv.api.modiffied.strategy;

import java.util.List;

import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.filesystem.FileSystemNode;

/**
 * 
 * @author Fabricio
 *
 */
public interface StrategyModiffiedNode
{

	/**
	 * 
	 * @param workingBase FileSystemNode del servidor
	 * @param workingCopy FileSystemNode del cliente
	 * @param differences la differencias entre los nodos actuales
	 */
	public void moddifedNode(FileSystemNode workingBase,FileSystemNode workingCopy,List<Difference<FileSystemNode>> differences);
}
