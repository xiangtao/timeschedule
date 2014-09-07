package com.deve.timeschedule.listener;

public interface ContextListener {
	/**
	 * This method will be called when container startup. 
	 * The application could init its resources on this method. 
	 * @return true, container will be initialized.
	 * 		   false, initialization will be halted.
	 */
	public boolean onStartup() throws Exception;
	
	/**
	 * This method will be called when container shutdown.
	 * The application could deallocate its resources on this method.
	 */
	public void onShutdown();
}
