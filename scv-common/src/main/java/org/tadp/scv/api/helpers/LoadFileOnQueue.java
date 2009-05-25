package org.tadp.scv.api.helpers;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.tadp.scv.api.filesystem.File;

/**
 * 
 * @author Fabricio
 *
 */
public class LoadFileOnQueue implements Runnable
{

	private File archivo = null;
	private BlockingQueue<String> queue = null;
	private AtomicBoolean condicionSalida;
	
	/**
	 * 
	 * @param file
	 * @param workingBaseQueue2
	 * @param condicionSalida 
	 */
	public LoadFileOnQueue(File file, BlockingQueue<String> workingBaseQueue2, AtomicBoolean condicionSalida)
	{
		archivo = file;
		this.queue = workingBaseQueue2;
		this.condicionSalida= condicionSalida;
	}

	/**
	 * 
	 */
	public void run()
	{
		if (archivo == null) {
			throw new IllegalStateException("Debe estar seteado el archivo");
		}
		
		if(archivo.getContent() == null){
			condicionSalida.set(true);
			return;
		}
		
		Scanner scanner = new Scanner(archivo.getContent());
		try {
			while( scanner.hasNextLine() ) {
				synchronized (this.queue) {
					String linea = scanner.nextLine();
					if(linea != null)
							queue.put(linea);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			if(!scanner.hasNextLine()){
				condicionSalida.set(true);
			}
		}
	}

}
