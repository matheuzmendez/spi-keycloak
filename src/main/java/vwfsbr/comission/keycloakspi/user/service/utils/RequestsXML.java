package vwfsbr.comission.keycloakspi.user.service.utils;

public class RequestsXML {
	
	public static String requestConsultaUsuario(String usuario) {
		return "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
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
	}
	
	public static String requestConsultaUsuario(String usuario, String codDealer) {
		return "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
				+ "xmlns:ser='http://www.vwfsbr.com.br/servicebus'>"
				+ "   <soapenv:Header/>"
				+ "   <soapenv:Body>"
				+ "      <ser:ConsultarUsuario>"
				+ "         <ser:request>"
				+ "            <ser:AcessoDealer>"
				+ "               <ser:CpfCnpj>" + usuario + "</ser:CpfCnpj>"
				+ "               <ser:Concess>" + codDealer + "</ser:Concess>"
				+ "            </ser:AcessoDealer>"
				+ "         </ser:request>"
				+ "      </ser:ConsultarUsuario>"
				+ "   </soapenv:Body>"
				+ "</soapenv:Envelope>";
	}
	
	public static String requestAutenticaUsuario(String usuario, String senha) {
		return "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
				+ "xmlns:ser='http://www.vwfsbr.com.br/servicebus'>"
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
	}
}
