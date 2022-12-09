package com.matheuzmendez.keycloakspi.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

import com.matheuzmendez.keycloakspi.user.LegacyUserStorageProviderFactory;

public class SoapConnection {
	private static final Logger log = LoggerFactory.getLogger(LegacyUserStorageProviderFactory.class);
	
	public boolean callSoapServiceAndBuildUser(String usuario, String senha) throws IOException {

		String xml = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ser='http://www.vwfsbr.com.br/servicebus'>"
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

		String response = callSoapService(xml);
		System.out.println(response);

		return extractValidResponse(response);
	}
	
	private static String callSoapService(String soapRequest) {
		try {
			// URL do serviço
			String url = "http://integration-uat/SecuritySvc/SegurancaService.svc";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// adiciona o método que deseja utilizar no SOAPUI
			con.setRequestProperty("SOAPAction", "AutenticarUsuarioDealer");
//			con.setRequestProperty("SOAPAction", "ConsultarUsuario");
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(soapRequest);
			wr.flush();
			wr.close();
			String responseStatus = con.getResponseMessage();
			System.out.println(responseStatus);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// You can play with response which is available as string now:
			String finalvalue = response.toString();

			return finalvalue;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	private static Boolean extractValidResponse(String responseXML) {
		String isValid = "AutenticacaoOk";
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(responseXML);
			doc.getDocumentElement().normalize();
			log.info("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("Authentication");
			System.out.println("----------------------------");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				log.info("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String authenticationStatus = eElement.getElementsByTagName("AuthenticationStatus").item(0)
							.getTextContent();
					log.info(authenticationStatus);
					
					return (authenticationStatus == isValid ? true : false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
