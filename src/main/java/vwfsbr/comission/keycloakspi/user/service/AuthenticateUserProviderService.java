package vwfsbr.comission.keycloakspi.user.service;

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

import vwfsbr.comission.keycloakspi.user.service.utils.RequestsXML;
import vwfsbr.comission.keycloakspi.user.service.utils.TypesRoles;

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

	public boolean callAutenticaUsuario(String usuario, String senha, String groupUser) {

		String requestAutenticaUsuario = RequestsXML.requestAutenticaUsuario(usuario, senha);
		String responseAutenticaUsuario = callSoapService(requestAutenticaUsuario);

		return extractValidResponse(responseAutenticaUsuario, groupUser);
	}

	private static String callSoapService(String requestAutenticaUsuario) {
		try {
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

	private static Boolean extractValidResponse(String responseAutenticaUsuario, String groupUser) {
		List<String> typesRolesList = Arrays.asList(TypesRoles.TPO_STA_GES_LOP, 
													TypesRoles.TPO_STA_GES_LOF,
													TypesRoles.TPO_STA_GES_GOP, 
													TypesRoles.TPO_STA_GES_GOF);
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
					log.info(authenticationStatus.equals(isValid) ? "Autenticado" : "Autenticacao Falhou");

					return (authenticationStatus.equals(isValid) ? true : false);
				}
			}

			NodeList nListPerfis = doc.getElementsByTagName("Perfis");
			for (int tempPerfis = 0; tempPerfis < nListPerfis.getLength(); tempPerfis++) {
				Node nNodePerfis = nListPerfis.item(tempPerfis);

				if (nNodePerfis.getNodeType() == Node.ELEMENT_NODE) {
					Element eElementPerfis = (Element) nNodePerfis;
					for (int tempPerfis1 = 0; tempPerfis1 < eElementPerfis.getElementsByTagName("a:string")
							.getLength(); tempPerfis1++) {
						if (typesRolesList.contains(
								eElementPerfis.getElementsByTagName("a:string").item(tempPerfis1).getTextContent())) {
							groupUser = eElementPerfis.getElementsByTagName("a:string").item(tempPerfis1)
									.getTextContent();
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
