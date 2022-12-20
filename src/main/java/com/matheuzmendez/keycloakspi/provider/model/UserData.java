package com.matheuzmendez.keycloakspi.provider.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class UserData extends AbstractUserAdapterFederatedStorage {

	  private ComponentModel componentModel;
	  private String firstName, lastName;
	  private boolean emailVerified;

	  public UserData(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel) {
	    super(session, realm, storageProviderModel);
	    componentModel = storageProviderModel;
	  }

	  private String userId, email, username, role;
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

	  @Override
	  public void setCreatedTimestamp(Long arg0) {

	  }

	  @Override
	  public Long getCreatedTimestamp() {
	    return System.currentTimeMillis();
	  }

	  @Override
	  public void setSingleAttribute(String name, String value) {

	  }

	  @Override
	  public List<String> getAttribute(String arg0) {
	    List<String> list1 = new LinkedList<String>();
	    list1.add("from External DB");
	    return list1;
	  }

	  @Override
	  public Map<String, List<String>> getAttributes() {
	    Map<String, List<String>> map1 = new HashMap<String, List<String>>();
	    List<String> list1 = new LinkedList<String>();
	    list1.add("from External DB");
	    map1.put("attribute1", list1);
	    return map1;
	  }
	}

