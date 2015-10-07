/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Bertrand Martel
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.protocol.http;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import fr.bmartel.protocol.http.states.HttpStates;

/**
 * Client socket main implementation
 * 
 * This socket is intended to be built with server hostname and port first.
 * 
 * Then when you write data with write(byte[] data) method, socket will be
 * connected, data will be written, response will be decoded in a reading thread
 * and eventually socket will be closed if HTTP_READING_ERROR occured which is
 * very likely to happen as server side will close socket once data will be
 * flushed to client intputstream.
 * 
 * TODO : implement some timeout to close the reading thread and socket in case
 * server socket get closed prematuraly => when unplugging ethernet wire for
 * instance which will make the connection idle for a time equal to SO_KEEPALIVE
 * (numbers of seconds before TCP socket will begin to send keep alive
 * probes),this time is 2 hour for Unix Socket
 * 
 * @author Bertrand Martel
 *
 */
public class ClientSocket implements IClientSocket {

	/**
	 * socket server hostname
	 */
	private String hostname = "";

	/**
	 * socket server port
	 */
	private int port = 0;

	/** set ssl encryption or not */
	private boolean ssl = false;

	private boolean checkCertificate = true;

	/**
	 * keystore certificate type
	 */
	private String keystoreDefaultType = "";

	/**
	 * trustore certificate type
	 */
	private String trustoreDefaultType = "";

	/**
	 * keystore file path
	 */
	private String keystoreFile = "";

	/**
	 * trustore file path
	 */
	private String trustoreFile = "";

	/**
	 * ssl protocol used
	 */
	private String sslProtocol = "TLS";

	/**
	 * keystore file password
	 */
	private String keystorePassword = "";

	/**
	 * trustore file password
	 */
	private String trustorePassword = "";

	/**
	 * define socket timeout (-1 if no timeout defined)
	 */
	private int socketTimeout = -1;

	/**
	 * socket object
	 */
	private Socket socket = null;

	/** client event listener list */
	private ArrayList<IHttpClientListener> clientListenerList = new ArrayList<IHttpClientListener>();

	/**
	 * thread used to read http inputstream data
	 */
	private Thread readingThread = null;

	/** define if reading thread is currently running */
	private volatile boolean isReading = false;

	/**
	 * Build Client socket
	 * 
	 * @param hostname
	 * @param port
	 */
	public ClientSocket(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	/**
	 * Create and connect socket
	 * 
	 * @return
	 * @throws IOException
	 */
	@Override
	public void connectAndExecuteTask(TimerTask task) {

		// close socket before recreating it
		if (socket != null) {
			closeSocket();
		}
		try {

			socket = null;

			if (ssl) {

				SSLContext ctx = null;

				if (checkCertificate) {
					/* initial server keystore instance */
					KeyStore ks = KeyStore.getInstance(keystoreDefaultType);

					if (!keystoreFile.equals("")) {
						/* load keystore from file */
						ks.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray());
					} else {
						ks.load(null, null);
					}
					/*
					 * assign a new keystore containing all certificated to be
					 * trusted
					 */
					KeyStore tks = KeyStore.getInstance(trustoreDefaultType);

					/* load this keystore from file */
					tks.load(new FileInputStream(trustoreFile), trustorePassword.toCharArray());

					/* initialize key manager factory with chosen algorithm */
					KeyManagerFactory kmf = null;

					if (!keystoreFile.equals("")) {
						kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
						/* initialize key manager factory with initial keystore */
						kmf.init(ks, keystorePassword.toCharArray());
					}

					/* initialize trust manager factory with chosen algorithm */
					TrustManagerFactory tmf;

					tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

					/*
					 * initialize trust manager factory with keystore containing
					 * certificates to be trusted
					 */
					tmf.init(tks);

					/* get SSL context chosen algorithm */
					ctx = SSLContext.getInstance(sslProtocol);
					/*
					 * initialize SSL context with key manager and trust
					 * managers
					 */
					if (kmf != null) {
						ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
					} else {
						ctx.init(null, tmf.getTrustManagers(), null);
					}
				} else {

					/* get SSL context chosen algorithm */
					ctx = SSLContext.getInstance(sslProtocol);

					if (!checkCertificate) {
						ctx.init(null, new TrustManager[] { new TrustAllX509TrustManager() }, new java.security.SecureRandom());
						HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
						HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
							@Override
							public boolean verify(String arg0, SSLSession arg1) {
								return true;
							}
						});
					} else {

					}
				}

				SSLSocketFactory sslserversocketfactory = ctx.getSocketFactory();

				/* create a SSL socket connection */
				socket = sslserversocketfactory.createSocket(hostname, port);

			} else {
				/* create a basic socket connection */
				socket = new Socket(InetAddress.getByName(hostname), port);
			}

			/* establish socket parameters */
			socket.setReuseAddress(true);
			socket.setKeepAlive(true);

			if (socketTimeout != -1) {
				socket.setSoTimeout(socketTimeout);
			}

			if (readingThread != null) {
				isReading = false;
				readingThread.join();
			}

			isReading = true;
			readingThread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (isReading) {
						try {
							HttpFrame frame = new HttpFrame();

							if (socket != null && !socket.isClosed()) {
								HttpStates httpStates = frame.parseHttp(socket.getInputStream());

								for (int i = 0; i < clientListenerList.size(); i++) {
									clientListenerList.get(i).onIncomingHttpFrame(frame, httpStates, ClientSocket.this);
								}

								if (httpStates == HttpStates.HTTP_READING_ERROR) {
									isReading = false;
									closeSocket();
								}
							} else {
								isReading = false;
							}

						} catch (SocketException e) {
							isReading = false;
							e.printStackTrace();
						} catch (Exception e) {
							isReading = false;
							e.printStackTrace();
						}
					}
				}
			});
			readingThread.start();

			if (task != null) {
				task.run();
			}
		} catch (IOException e) {
			e.printStackTrace();
			triggerFailure();
		} catch (InterruptedException e) {
			e.printStackTrace();
			triggerFailure();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			triggerFailure();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			triggerFailure();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			triggerFailure();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			triggerFailure();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			triggerFailure();
		}
	}

	public void triggerFailure(){
		for (int i = 0; i < clientListenerList.size(); i++) {
			clientListenerList.get(i).onSocketError();
		}
	}
	/**
	 * Set timeout for this socket
	 * 
	 * @param socketTimeout
	 */
	@Override
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	@Override
	public void write(final byte[] data) {

		connectAndExecuteTask(new TimerTask() {
			@Override
			public void run() {
				if (socket != null && !socket.isClosed()) {
					try {
						if (socket.getOutputStream() != null) {
							socket.getOutputStream().write(data);
							socket.getOutputStream().flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	@Override
	public void closeSocket() {

		if (socket != null) {
			try {
				socket.getOutputStream().close();
				socket.getInputStream().close();
				socket.close();
			} catch (IOException e) {
			}
		}
		socket = null;
	}

	@Override
	public void closeSocketJoinRead() {
		if (readingThread != null) {
			isReading = false;
			System.out.println("before join");
			try {
				readingThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("after join");
		}
		closeSocket();
	}

	@Override
	public boolean isConnected() {
		if (socket != null && socket.isConnected())
			return true;
		return false;
	}

	@Override
	public void addClientSocketEventListener(IHttpClientListener eventListener) {
		clientListenerList.add(eventListener);
	}

	@Override
	public void cleanEventListeners() {
		clientListenerList.clear();
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public void checkCertificate(boolean state) {
		checkCertificate = state;
	}

	/**
	 * Set ssl parameters
	 * 
	 * @param keystoreDefaultType
	 *            keystore certificates type
	 * @param trustoreDefaultType
	 *            trustore certificates type
	 * @param keystoreFile
	 *            keystore file path
	 * @param trustoreFile
	 *            trustore file path
	 * @param sslProtocol
	 *            ssl protocol used
	 * @param keystorePassword
	 *            keystore password
	 * @param trustorePassword
	 *            trustore password
	 */
	public void setSSLParams(String keystoreDefaultType, String trustoreDefaultType, String keystoreFile, String trustoreFile, String sslProtocol,
			String keystorePassword, String trustorePassword) {
		this.keystoreDefaultType = keystoreDefaultType;
		this.trustoreDefaultType = trustoreDefaultType;
		this.keystoreFile = keystoreFile;
		this.trustoreFile = trustoreFile;
		this.sslProtocol = sslProtocol;
		this.keystorePassword = keystorePassword;
		this.trustorePassword = trustorePassword;
	}

}
