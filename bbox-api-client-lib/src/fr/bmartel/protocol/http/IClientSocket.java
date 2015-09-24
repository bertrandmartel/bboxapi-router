package fr.bmartel.protocol.http;

import java.util.TimerTask;

/**
 * Common template for an http client socket. Call to client socket class should
 * be done through this interface
 * 
 * @author Bertrand Martel
 *
 */
public interface IClientSocket {

	/**
	 * close client socket
	 */
	public void closeSocket();

	/**
	 * Write data to client socket outputstream
	 * 
	 * @param data
	 *            data to write
	 */
	public void write(byte[] data);

	/**
	 * connect to client socket
	 * 
	 * @return socket created
	 */
	public void connectAndExecuteTask(TimerTask task);

	/**
	 * to know if socket is connected or not
	 * 
	 * @return
	 */
	public boolean isConnected();

	/**
	 * Add client event listener to list
	 * 
	 * @param eventListener
	 */
	public void addClientSocketEventListener(IHttpClientListener eventListener);

	/**
	 * clean event listener list
	 */
	public void cleanEventListeners();

	/**
	 * close socket and wait for socket reading thread to die
	 */
	public void closeSocketJoinRead();

	/**
	 * Set timeout for this socket
	 * 
	 * @param socketTimeout
	 */
	public void setSocketTimeout(int socketTimeout);
}
