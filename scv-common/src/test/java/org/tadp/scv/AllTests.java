package org.tadp.scv;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.tadp.scv.api.builder.FileSystemNodeBuilderTest;
import org.tadp.scv.api.builder.VersionableNodeBuilderTest;
import org.tadp.scv.api.compare.DeltaGeneratorTest;
import org.tadp.scv.api.compare.DirectoryComparatorTest;
import org.tadp.scv.api.filesystem.DirectoryTest;
import org.tadp.scv.api.filesystem.FileTest;
import org.tadp.scv.api.filesystem.server.FileVersionableTest;
import org.tadp.scv.api.server.SCVProjectClienteTest;
import org.tadp.scv.api.server.SCVProjectServerTest;
import junit.framework.Test;

@RunWith(value=Suite.class)
@SuiteClasses(value={FileTest.class, 
					 DirectoryTest.class,
					 DirectoryComparatorTest.class,
					 DeltaGeneratorTest.class,
					 FileSystemNodeBuilderTest.class,
					 VersionableNodeBuilderTest.class,
					 FileVersionableTest.class,
					 SCVProjectClienteTest.class,
					 SCVProjectServerTest.class	 
					 })
public class AllTests {
	// Esto para probar como hacer en el JUnit 4 para hacer TestSuite
	public static Test suite(){
		TestSuite suite = new TestSuite("TestSuite de todo el proyecto");
		return suite;
	}
}
