package org.tadp.scv.api.compare;

import java.util.List;

import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.filesystem.FileSystemNode;



public interface Comparator<E extends FileSystemNode>
{
	List<Difference<E>> compare(E base,E workingCopy);
}
