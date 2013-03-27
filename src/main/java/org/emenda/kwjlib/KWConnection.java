package org.emenda.kwjlib;

import java.lang.String;
import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
/*
 * Class KW_Connection represents a connection (including authorization) to
 * a Klocwork server and thereby the Web API. This class can be used to: 
 * 	-	establish the connection between the plug-in and the Klocwork server
 * 	-	send requests in the form of POST HTTP
 * 	-	receive results in the form of JSON records
 */

public class KWConnection {
	//Member variables
	private URL m_kwserver_url;
	private HttpURLConnection m_kwserver_connection;
        private HttpsURLConnection m_kwserver_connection_SSL;
	private String m_kwserver_ltoken; //Authorization token
	private String m_kwserver_response;
	private String m_s_lastrequest;
	private boolean m_successful_init = false;
        private boolean m_b_useSSL;
        
        private TrustManager[] trustAllCerts;
	
	/*
	 * Default constructor
	 */
	public KWConnection() {
                this.m_b_useSSL = false;
		//Construct with default values
		KW_Connection_init("localhost", 8080, "");
	}
	
	/*
	 * Legacy constructor without SSL support
	 */
	public KWConnection(String a_KWserver_url, int a_KWserver_port, String a_KWserver_user) {
                this.m_b_useSSL = false;
		KW_Connection_init(a_KWserver_url, a_KWserver_port, a_KWserver_user);
	}
        
        /*
	 * Argument constructors
	 */
	public KWConnection(String a_KWserver_url, int a_KWserver_port, boolean a_KWserver_useSSL, String a_KWserver_user) {
                this.m_b_useSSL = a_KWserver_useSSL;
		KW_Connection_init(a_KWserver_url, a_KWserver_port, a_KWserver_user);
	}
	
	//Initialise function called by constructors
	private void KW_Connection_init(String a_KWserver_url, int a_KWserver_port, String a_KWserver_user) {
		//Set member variable values and test connection to server
		try {
                        if(this.m_b_useSSL) {
                            m_kwserver_url = new URL("https", a_KWserver_url, a_KWserver_port, "/review/api");
                            //Because we're using SSL: Create a trust manager to validate Klocwork certificate chains
                            // Create a trust manager that does not validate certificate chains
                            trustAllCerts = new TrustManager[]{new X509TrustManager(){
                                public X509Certificate[] getAcceptedIssuers(){return null;}
                                public void checkClientTrusted(X509Certificate[] certs, String authType){}
                                public void checkServerTrusted(X509Certificate[] certs, String authType){}
                            }};
                        }
                        else {
                            m_kwserver_url = new URL("http", a_KWserver_url, a_KWserver_port, "/review/api");
                        }
		}
		catch (IOException e) {
			KWWebAPIService.appendError("private void KW_Connection_init(String a_KWserver_url, int a_KWserver_port):\n" +
					"\tError initialising connection object; could not create URL object:\n" +
					"\t" + e.getMessage());
			m_successful_init = false;
		}
		
		m_kwserver_connection = null;
                m_kwserver_connection_SSL = null;
		m_kwserver_ltoken = "";
		m_kwserver_response = "";
		m_s_lastrequest = "";
		m_successful_init = getLToken(m_kwserver_url.getHost(), m_kwserver_url.getPort(), a_KWserver_user);
	}
	
	/*
	 * Function to connect to server using member variables URL and connection
	 */
	public boolean connect() {
		try {
                        if(this.m_b_useSSL) {
                            m_kwserver_connection_SSL = (HttpsURLConnection) m_kwserver_url.openConnection();
                            //Settings for the connection
                            m_kwserver_connection_SSL.setDoOutput(true);
                            m_kwserver_connection_SSL.setDoInput(true);
                            m_kwserver_connection_SSL.setInstanceFollowRedirects(false);
                            //Set the request method to POST (accepted by KW Web API)
                            m_kwserver_connection_SSL.setRequestMethod("POST");
                            m_kwserver_connection_SSL.setUseCaches (false);
                            //Install the trust manager for SSL use
                            SSLContext sc = SSLContext.getInstance("TLS");
                            sc.init(null, trustAllCerts, new SecureRandom());
                            m_kwserver_connection_SSL.setSSLSocketFactory(sc.getSocketFactory());
                        }
                        else {
                            m_kwserver_connection = (HttpURLConnection) m_kwserver_url.openConnection();
                            //Settings for the connection
                            m_kwserver_connection.setDoOutput(true);
                            m_kwserver_connection.setDoInput(true);
                            m_kwserver_connection.setInstanceFollowRedirects(false);
                            //Set the request method to POST (accepted by KW Web API)
                            m_kwserver_connection.setRequestMethod("POST");
                            m_kwserver_connection.setUseCaches (false);
                        }
		}
		catch(IOException e){
			KWWebAPIService.appendError("public boolean connect():\n" +
					"\tException occured while opening connection:\n" +
					"\t" + e.getMessage());
			m_kwserver_connection = null;
                        m_kwserver_connection_SSL = null;
			return false;
		}
                catch(Exception e) {
                    KWWebAPIService.appendError("public boolean connect():\n" +
					"\tException occured installing SSL trust manager:\n" +
					"\t" + e.getMessage());
			m_kwserver_connection = null;
                        m_kwserver_connection_SSL = null;
			return false;
                }
		
		return true;
	}
	
	//Disconnects the connection
	public void disconnect() {
		if(m_kwserver_connection != null) {
			m_kwserver_connection.disconnect();
		}
		
		m_kwserver_connection = null;
                
                if(m_kwserver_connection_SSL != null) {
			m_kwserver_connection_SSL.disconnect();
		}
		
		m_kwserver_connection_SSL = null;
	}
	
	public boolean isConnected() {
		return (m_kwserver_connection != null || m_kwserver_connection_SSL != null);
	}
	
	//Function to send a POST HTTP request to server
	//Argument a_s_request is sent as POST HTTP to server
	//Server response (result) is stored in a_s_response
	//Return value indicates function success or failure
	public boolean sendRequest(String a_request,
							   String a_user) {
		String request = a_request;
		
		//Connect if not connected
		if(!isConnected()) {
			if(!connect()) {
				//Connection failed
				KWWebAPIService.appendError("public boolean sendRequest(String a_project, String a_user, ...):\n" +
						"\tConnection was not open when attempting to send request, and connecting failed.");
				return false;
			}
		}
		
		//Fetch ltoken if we do not have it
		if(m_kwserver_ltoken == null || 
				m_kwserver_ltoken.length() < 1) {
			//Check if token is fetched successfully
			if(!getLToken(m_kwserver_url.getHost(),
							m_kwserver_url.getPort(),
							a_user)) {
				KWWebAPIService.appendError("public boolean sendRequest(String a_project, String a_user, ...):\n" +
						"\tFetching ltoken failed.");
				disconnect();
				return false;
			}
		}
		
		//Append the ltoken value to the request
		request = request.concat("&ltoken=" + m_kwserver_ltoken);
		
		//Set the request parameters of the connection (the length of the request string)
                if(this.m_b_useSSL) {
                    m_kwserver_connection_SSL.setRequestProperty("Content-Length", Integer.toString(request.length()));
                }
                else {
                    m_kwserver_connection.setRequestProperty("Content-Length", Integer.toString(request.length()));
                }
		
		//Write the request to the connection
		try {
                        DataOutputStream wr = null;
                        if(this.m_b_useSSL) {
                            wr = new DataOutputStream(m_kwserver_connection_SSL.getOutputStream());
                        }
                        else {
                            wr = new DataOutputStream(m_kwserver_connection.getOutputStream());
                        }
                        if(wr == null) {
                            KWWebAPIService.appendError("public boolean sendRequest(String a_project, String a_user, ...):\n" +
					"\tError while retrieving DataOutputStream from connection.");
                        }
			System.out.println("OutputStream open, writing request: " + request);
			//Store the request as the last sent request
			m_s_lastrequest = request;
			wr.writeBytes(request);
			//Close the streams
			wr.flush();
			wr.close();
			//Read response from server
                        InputStream instream;
                        if(this.m_b_useSSL) {
                            instream = m_kwserver_connection_SSL.getInputStream();
                        }
                        else {
                            instream = m_kwserver_connection.getInputStream();
                        }
			BufferedReader buf = new BufferedReader(new InputStreamReader(instream));
			while(buf.ready()){
				m_kwserver_response = m_kwserver_response.concat(buf.readLine() + "\n");
			}
			//Close the streams
			buf.close();
			instream.close();
		}
		catch(IOException e) {
			System.out.println("Error writing to connection: " + e.getMessage());
			KWWebAPIService.appendError("public boolean sendRequest(String a_project, String a_user, ...):\n" +
					"\tError while writing to/reading from connection:\n" +
					"\t" + e.getMessage());
			disconnect();
			return false;
		}
		
		//Finish by disconnecting
		disconnect();
		
		return true;
	}
        
        public boolean GetSSLSocketFactory() {
            boolean result = false;
            
            
            
            return result;
        }

	
	public boolean getLToken(String host,
							 int port,
							 String user) {
		m_kwserver_ltoken = KWUtil.fetchLToken(host, port, user);
		return (m_kwserver_ltoken != null);
	}
	
	/*
	 * Accessor function for m_kwserver_response
	 */
	public String getResponse() {
		return m_kwserver_response;
	}
	
	/*
	 * Accessor function for m_successful_init
	 */
	public boolean successfulInitialisation() {
		return m_successful_init;
	}
	
	/*
	 * Clear function for m_kwserver_response
	 */
	public void clearResponse() {
		m_kwserver_response = "";
	}
	
	/*
	 * Accessor function for m_s_lastrequest
	 */
	public String getLastRequest() {
		return m_s_lastrequest;
	}
}
