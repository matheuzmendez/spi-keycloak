package com.matheuzmendez.keycloakspi.provider.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class UserData extends AbstractUserAdapterFederatedStorage {

	static final String REAL_USERNAME_ATTRIBUTE = "realUsername";

	private ComponentModel componentModel;
	private String firstName, lastName;
	private boolean emailVerified;

	public UserData(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel) {
		super(session, realm, storageProviderModel);
		componentModel = storageProviderModel;
	}

	private String userId, email, username, role, codDealer, cargo, filial, nomeFilial, montadora;
	private boolean enabled;

	@Override
	public String getId() {
		if (storageId == null) {
			storageId = new StorageId(componentModel.getId(), userId);
		}

		return storageId.getId();
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setLastName(String lastname) {
		this.lastName = lastname;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setEmailVerified(boolean verified) {
		this.emailVerified = verified;
	}

	@Override
	public boolean isEmailVerified() {
		return emailVerified;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setCodDealer(String codDealer) {
		this.codDealer = codDealer;
	}

	public String getCodDealer() {
		return codDealer;
	}
	
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getCargo() {
		return cargo;
	}
	
	public void setFilial(String filial) {
		this.filial = filial;
	}

	public String getFilial() {
		return filial;
	}
	
	public void setNomeFilial(String nomeFilial) {
		this.nomeFilial = nomeFilial;
	}

	public String getNomeFilial() {
		return nomeFilial;
	}
	
	public void setMontadora(String montadora) {
		this.montadora = montadora;
	}

	public String getMontadora() {
		return montadora;
	}

	@Override
	public void setCreatedTimestamp(Long arg0) {

	}

	@Override
	public Long getCreatedTimestamp() {
		return System.currentTimeMillis();
	}

	@Override
	public List<String> getAttribute(String arg0) {
		List<String> list1 = new LinkedList<String>();
		list1.add("from External DB");
		return list1;
	}

//	@Override
//	public Map<String, List<String>> getAttributes() {
//		Map<String, List<String>> map1 = new HashMap<String, List<String>>();
//		List<String> list1 = new LinkedList<String>();
//		list1.add("from External DB");
//		map1.put("attribute1", list1);
//		return map1;
//	}

	@Override
	public Map<String, List<String>> getAttributes() {
		MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
		attributes.add(UserModel.USERNAME, getUsername());
		attributes.add(UserModel.EMAIL, getEmail());
		attributes.add(UserModel.FIRST_NAME, getFirstName());
		attributes.add(UserModel.LAST_NAME, getLastName());
		attributes.add("codDealer", getCodDealer());
		attributes.add("cargo", getCargo());
		attributes.add("filial", getFilial());
		attributes.add("nomeFilial", getNomeFilial());
		attributes.add("montadora", getMontadora());
		return attributes;
	}
}
