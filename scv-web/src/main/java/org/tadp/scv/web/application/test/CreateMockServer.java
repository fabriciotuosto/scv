package org.tadp.scv.web.application.test;

import org.tadp.scv.api.server.SCVServer;
import org.tadp.scv.server.SCVServerImpl;

public class CreateMockServer {

	public static SCVServer getServer()
	{
		SCVServerImpl scvServer = new SCVServerImpl();
		new ContenidoPrueba(scvServer);
		return scvServer;
	}
}
