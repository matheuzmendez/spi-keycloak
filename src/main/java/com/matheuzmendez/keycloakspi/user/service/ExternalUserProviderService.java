package com.matheuzmendez.keycloakspi.user.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.matheuzmendez.keycloakspi.user.service.enums.TypesQuery;

public class ExternalUserProviderService {
	private static final Logger log = LoggerFactory.getLogger(ExternalUserProviderService.class);
	private static HttpURLConnection con;

	public ExternalUserProviderService(String url) {
		try {
			buildClient(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	private void buildClient(String url) throws IOException {
		log.info("buildClient: " + url);
		URL obj = new URL(url);
		this.con = (HttpURLConnection) obj.openConnection();
	}
	
	public boolean callAutenticaUsuario(String usuario, String senha, TypesQuery typeQuery) {

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

		String response = callSoapService(xml, typeQuery);
//		System.out.println(response);

		return extractValidResponse(response);
	}
	
	public UserDto callConsultaUsuario(String usuario, TypesQuery typeQuery) {

		String xml = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ser='http://www.vwfsbr.com.br/servicebus'>"
				+ "   <soapenv:Header/>"
				+ "   <soapenv:Body>"
				+ "      <ser:ConsultarUsuario>"
				+ "         <ser:request>"
				+ "            <ser:AcessoDealer>"
				+ "               <ser:CpfCnpj>" + usuario + "</ser:CpfCnpj>"
				+ "            </ser:AcessoDealer>"
				+ "         </ser:request>"
				+ "      </ser:ConsultarUsuario>"
				+ "   </soapenv:Body>"
				+ "</soapenv:Envelope>";

		String response = callSoapService(xml, typeQuery);
//		System.out.println(response);

		return extractInfoUser(usuario, response);
	}

	private static String callSoapService(String soapRequest, TypesQuery typeQuery) {
		try {
			// URL do serviço
			// String url = "http://integration-uat/SecuritySvc/SegurancaService.svc";
			// String url = "http://integration-uat/SecuritySvc/DealerUserService.svc";

			// adiciona o método que deseja utilizar no SOAPUI
			if (typeQuery.equals(TypesQuery.AutenticaUsuario)) {
				con.setRequestProperty("SOAPAction", "AutenticarUsuarioDealer");
			} else {
				con.setRequestProperty("SOAPAction", "ConsultarUsuario");
			}

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
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(responseXML)));
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

	private static UserDto extractInfoUser(String usuario, String responseXML) {
		List<String> typesRolesList = Arrays.asList(TypesRoles.TPO_STA_GES_LOP, TypesRoles.TPO_STA_GES_LOF,
				TypesRoles.TPO_STA_GES_GOP, TypesRoles.TPO_STA_GES_GOF);
		String username = usuario, firstName, lastName;
		String email, codDealer, cargo, filial = "", nomeFilial = "", montadora = "", role = "";
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(responseXML)));
			NodeList nList = doc.getElementsByTagName("Usuario");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					email = eElement.getElementsByTagName("Email").item(0).getTextContent();
					firstName = eElement.getElementsByTagName("Nome").item(0).getTextContent();
					lastName = "";
					codDealer = eElement.getElementsByTagName("Concess").item(0).getTextContent();
					cargo = eElement.getElementsByTagName("Cargo").item(0).getTextContent();
					
					NodeList nListPerfis = doc.getElementsByTagName("Perfis");
					for (int tempPerfis = 0; tempPerfis < nListPerfis.getLength(); tempPerfis++) {
						Node nNodePerfis = nListPerfis.item(tempPerfis);

						if (nNodePerfis.getNodeType() == Node.ELEMENT_NODE) {
							Element eElementPerfis = (Element) nNodePerfis;
							for (int tempPerfis1 = 0; tempPerfis1 < eElementPerfis.getElementsByTagName("a:string")
									.getLength(); tempPerfis1++) {
								if (typesRolesList.contains(eElementPerfis.getElementsByTagName("a:string")
										.item(tempPerfis1).getTextContent())) {
									role = eElementPerfis.getElementsByTagName("a:string").item(tempPerfis1)
											.getTextContent();
								}

							}
						}
					}
					
					NodeList nListRegional = doc.getElementsByTagName("Regional");
					for (int tempRegional = 0; tempRegional < nListRegional.getLength(); tempRegional++) {
						Node nNodeRegional = nListRegional.item(tempRegional);

						if (nNodeRegional.getNodeType() == Node.ELEMENT_NODE) {
							Element eElementRegional = (Element) nNodeRegional;
							filial = eElementRegional.getElementsByTagName("ID").item(0).getTextContent();
							nomeFilial = eElementRegional.getElementsByTagName("Nome").item(0).getTextContent();
						}
					}

					NodeList nListConcessionaria = doc.getElementsByTagName("Concessionaria");
					for (int tempConcessionaria = 0; tempConcessionaria < nListConcessionaria
							.getLength(); tempConcessionaria++) {
						Node nNodeConcessionaria = nListConcessionaria.item(tempConcessionaria);

						if (nNodeConcessionaria.getNodeType() == Node.ELEMENT_NODE) {
							Element eElementConcessionaria = (Element) nNodeConcessionaria;
							montadora = eElementConcessionaria.getElementsByTagName("Nome").item(0).getTextContent();
						}
					}
					return new UserDto(username, email, firstName, lastName, codDealer, cargo, filial, nomeFilial,
							montadora, role);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
