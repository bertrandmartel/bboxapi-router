package fr.bmartel.protocol.http;

import fr.bmartel.protocol.http.states.HttpStates;

/**
 * Http client event listener
 * 
 * @author Bertrand Martel
 *
 */
public interface IHttpClientListener {

	/**
	 * notify client for incoming http frames
	 * 
	 * @param frame
	 *            http frame
	 * @param httpStates
	 *            state of incoming http frame decoded
	 * @param clientSocket
	 *            client socket object
	 */
	public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates,
			IClientSocket clientSocket);
	
	public void onSocketError();
}
