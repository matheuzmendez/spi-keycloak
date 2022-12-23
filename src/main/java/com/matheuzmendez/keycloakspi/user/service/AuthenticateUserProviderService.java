package com.matheuzmendez.keycloakspi.user.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class AuthenticateUserProviderService {
	private static final Logger log = LoggerFactory.getLogger(AuthenticateUserProviderService.class);
	private static HttpURLConnection con;

	public AuthenticateUserProviderService(String url, String parametro) {
		try {
			buildClient(url, parametro);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	private void buildClient(String url, String parametro) throws IOException {
		log.info("buildClient: " + url);
		URL obj = new URL(url);
		this.con = (HttpURLConnection) obj.openConnection();
		this.con.setRequestProperty("SOAPAction", parametro);
	}
	
	public boolean callAutenticaUsuario(String usuario, String senha) {

		String requestAutenticaUsuario = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ser='http://www.vwfsbr.com.br/servicebus'>"
						+ "<soapenv:Header/>" 
							+ "<soapenv:Body>" 
								+ "<ser:AutenticarUsuarioDealer>" 
									+ "<ser:request>"
										+ "<ser:LoginUsuario>" + usuario + "</ser:LoginUsuario>" 
										+ "<ser:Senha>" + senha + "</ser:Senha>"
									+ "</ser:request>" 
								+ "</ser:AutenticarUsuarioDealer>" 
								+ "</soapenv:Body>" 
						+ "</soapenv:Envelope>";

		String responseAutenticaUsuario = callSoapService(requestAutenticaUsuario);
		return extractValidResponse(responseAutenticaUsuario);
	}

	private static String callSoapService(String requestAutenticaUsuario) {
		try {
			// adiciona o método que deseja utilizar no SOAPUI
			
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(requestAutenticaUsuario);
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			in.close();
			
			return response.toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	private static Boolean extractValidResponse(String responseAutenticaUsuario) {
		String isValid = "AutenticacaoOk";
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(responseAutenticaUsuario)));
			NodeList nList = doc.getElementsByTagName("Authentication");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String authenticationStatus = eElement.getElementsByTagName("AuthenticationStatus").item(0)
							.getTextContent();
					System.out.println(authenticationStatus);

					return (authenticationStatus.equals(isValid) ? true : false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
