package org.tadp.scv.api.server;

import java.util.List;

import org.tadp.scv.api.compare.DeltaNodo;
import org.tadp.scv.api.exceptions.BothSourcesModifiedException;
import org.tadp.scv.api.exceptions.ConflictoNodosException;
import org.tadp.scv.api.exceptions.FirstNeedToMakeUpdateException;

public class MockSCVProjectServer extends SCVProjectServer {
	private List<DeltaNodo> deltas;
	private DeltaNodo unDelta;
	
	@Override
	public void commit(List<DeltaNodo> listaDeltas, String comentario) throws FirstNeedToMakeUpdateException{
		return;
	}

	@Override
	public void commit(DeltaNodo unDelta, String comentario) throws FirstNeedToMakeUpdateException{
		return;
	}
	
	@Override
	public List<DeltaNodo> update(List<DeltaNodo> listaDeltas) throws BothSourcesModifiedException{
		return this.deltas;
	 }		
		
	/**
	  * @author Alejandro Sanchez
	  * @param delta
	  * @return Otro delta con las revisiones que le faltan al cliente, o null si
	  *         no hay que actualizar ese nodo
	  * @throws BothSourcesModifiedException
	  */
	@Override
	public DeltaNodo update(DeltaNodo delta) throws ConflictoNodosException {
		return this.unDelta;
	}

	public void setUpdateDeltas(List<DeltaNodo> deltas) {
		this.deltas = deltas;
	}

	public void setUpdateDelta(DeltaNodo unDelta) {
		this.unDelta = unDelta;
	}
}
