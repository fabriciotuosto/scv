package org.tadp.scv.api.compare;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.tadp.scv.api.difference.Difference;
import org.tadp.scv.api.difference.file.FileAddedLineDifference;
import org.tadp.scv.api.difference.file.FileDeleteLineDifferece;
import org.tadp.scv.api.difference.file.FileUpdateDifference;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.helpers.LoadFileOnQueue;

/**
 * 
 * @author Fabricio
 *
 */

public class FileComparator implements Comparator<File>
{
	List<Difference<File>> diferencias;
	BlockingQueue<String> workingBaseQueue;
	BlockingQueue<String> workingCopyQueue;
	
	/**
	 * @throws InterruptedException 
	 * 
	 */
	public List<Difference<File>> compare(File node1, File node2)
	{
		Thread workingBaseLoadThread = null;
		Thread workingCopyLoadThread = null;		
		workingBaseQueue = new ArrayBlockingQueue<String>(50);
		workingCopyQueue = new ArrayBlockingQueue<String>(50);		
		diferencias = new LinkedList<Difference<File>>();
		AtomicBoolean threadHasFinalized1= new AtomicBoolean(false);
		AtomicBoolean threadHasFinalized2= new AtomicBoolean(false);
		
		try{
			workingBaseLoadThread = createReadFileThread(node1,workingBaseQueue, threadHasFinalized1);
			workingCopyLoadThread = createReadFileThread(node2,workingCopyQueue, threadHasFinalized2);
		}catch(Exception e){}
		if (workingBaseLoadThread==null || workingCopyLoadThread == null)
		{
			throw new IllegalStateException("Imposible leer alguno de los archivos en la comparacion");
		}
		workingCopyLoadThread.start();
		workingBaseLoadThread.start();
		
		int i = 0;
		boolean threadsAlive= threadHasFinalized1.get()== false || threadHasFinalized2.get()== false;
		boolean moreElements= workingBaseQueue.isEmpty() == false || workingCopyQueue.isEmpty() == false;
		String line1= null, line2= null;
		while( threadsAlive || moreElements){
				line1= this.sacarElemento(threadHasFinalized1, workingBaseQueue);
				line2= this.sacarElemento(threadHasFinalized2, workingCopyQueue);
				Difference<File> diff = obtenerDiff( line1, line2,i);
				if (diff != null)
				{
					diferencias.add(diff);
				}
				i++;
				threadsAlive= threadHasFinalized1.get()== false || threadHasFinalized2.get()== false;
				moreElements= workingBaseQueue.isEmpty() == false || workingCopyQueue.isEmpty() == false;
		}
		return diferencias;
	}
	
	public synchronized String sacarElemento(AtomicBoolean isThreadDead, BlockingQueue<String> cola){
		try {
			while(isThreadDead.get() == false && cola.isEmpty()){
				//esperar que ponga un elemento en la cola
				//o finalice su ejecucion
				wait(200);
			}
			//Puso en elemento en la cola
			if(!cola.isEmpty()) {
				return cola.take();
			}	
		} catch (InterruptedException e) {
			//No puede pasar porq estoy en un metodo synchronized
		}
		return null;
	}

	/**
	 * 
	 * @param file
	 * @param workingBaseQueue2
	 * @param condicionSalida1 
	 * @return
	 * @throws Exception
	 */
	private Thread createReadFileThread(File file, BlockingQueue<String> workingBaseQueue2, AtomicBoolean condicionSalida) throws Exception {
		Thread thread = new Thread(new LoadFileOnQueue(file,workingBaseQueue2, condicionSalida));
		return thread;
	}
	
	/**
	 * 
	 * @param base
	 * @param copy
	 * @param nLinea
	 * @return
	 */
	private Difference<File> obtenerDiff(String base, String copy,int nLinea) {
		Difference<File> returnDiff = null;
		if(base == null && copy == null)
			return null;
		
		if (base == null)
		{
			returnDiff = new FileAddedLineDifference(copy,nLinea);
		}
		if (copy == null)
		{
			returnDiff = new FileDeleteLineDifferece(base,nLinea);
		}
		if (copy!= null && base != null && !base.equals(copy))
		{
			returnDiff = new FileUpdateDifference(copy,nLinea);
		}
		return returnDiff;
	}
}
