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

import com.matheuzmendez.keycloakspi.user.service.roles.TypesRoles;

public class FindUserProviderService {
	private static Logger log = LoggerFactory.getLogger(FindUserProviderService.class);
	private static HttpURLConnection con;

	public FindUserProviderService(String url, String parametro) {
		try {
			buildClient(url, parametro);
		} catch (IOException e) {
			log.error("Error during build Client: " + url, e);
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	private void buildClient(String url, String parametro) throws IOException {
		URL obj = new URL(url);
		this.con = (HttpURLConnection) obj.openConnection();
		this.con.setRequestProperty("SOAPAction", parametro);
	}

	public UserDto callConsultaUsuario(String usuario) {
		
		String requestConsultaUsuario = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
				+ "xmlns:ser='http://www.vwfsbr.com.br/servicebus'>"
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

		String responseConsultaUsuario = callSoapService(requestConsultaUsuario);
		
		return (responseConsultaUsuario != null) ? extractInfoUser(usuario, responseConsultaUsuario) : null;
		
	}

	private static String callSoapService(String requestConsultaUsuario) {
		try {
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(requestConsultaUsuario);
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
			log.error("Error calling Soap Service: " + e);
			return null;
		}
	}

	private static UserDto extractInfoUser(String usuario, String responseConsultaUsuario) {
		List<String> typesRolesList = Arrays.asList(TypesRoles.TPO_STA_GES_LOP, 
													TypesRoles.TPO_STA_GES_LOF,
													TypesRoles.TPO_STA_GES_GOP, 
													TypesRoles.TPO_STA_GES_GOF);
		String username, firstName, lastName;
		String email, codDealer, cargo, filial = "", nomeFilial = "", montadora = "", role = "", codMontadora = "";
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(responseConsultaUsuario)));
			NodeList nList = doc.getElementsByTagName("Usuario");
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					username = eElement.getElementsByTagName("CpfCnpj").item(0).getTextContent();
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
							codMontadora = eElementConcessionaria.getElementsByTagName("CodigoMontadora").item(0).getTextContent();
						}
					}
					
					return (username.equals(usuario)) ? new UserDto(username, email, firstName, lastName, codDealer, cargo, filial, nomeFilial,
								montadora, role, codMontadora) : null;
				}
			}
		} catch (Exception e) {
			log.error("Error extracting info of User: " + e);
			return null;
		}
		return null;
	}
}
