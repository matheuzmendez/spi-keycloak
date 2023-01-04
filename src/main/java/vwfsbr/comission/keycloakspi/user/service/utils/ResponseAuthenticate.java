package vwfsbr.comission.keycloakspi.user.service.utils;

public class ResponseAuthenticate {
	
	private boolean authenticated;
	private String group;
	
	public ResponseAuthenticate(boolean authenticate, String group) {
		this.authenticated = authenticate;
		this.group = group;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticate) {
		this.authenticated = authenticate;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}	
}
