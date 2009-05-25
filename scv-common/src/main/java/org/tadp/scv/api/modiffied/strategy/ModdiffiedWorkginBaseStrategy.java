package org.tadp.scv.api.modiffied.strategy;

import java.util.List;

import org.tadp.scv.api.compare.Revision;
import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.filesystem.FileSystemNode;
import org.tadp.scv.api.filesystem.server.Versionable;

/**
 * 
 * @author Fabricio
 *
 */
public class ModdiffiedWorkginBaseStrategy implements StrategyModiffiedNode
{
	
	/**
	 * 
	 */
	public void moddifedNode(FileSystemNode workingBase, FileSystemNode workingCopy, List<Difference<FileSystemNode>> differences)
	{
		Versionable<FileSystemNode> versionable = (Versionable<FileSystemNode>) workingBase;
		Revision<FileSystemNode> revision = new Revision<FileSystemNode>(0, null);
		revision.addAll(differences);
		versionable.addRevision(revision);
	}

}
