package com.matheuzmendez.keycloakspi.user;

import java.io.IOException;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.UserStorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matheuzmendez.keycloakspi.service.SoapConnection;

public class LegacyUserStorageProvider implements UserStorageProvider, CredentialInputValidator {
	
	private static final Logger log = LoggerFactory.getLogger(LegacyUserStorageProviderFactory.class);
	private final KeycloakSession session;
	private final ComponentModel model;

	public LegacyUserStorageProvider(KeycloakSession session, ComponentModel model) {
		this.session = session;
		this.model = model;
	}

	@Override
	public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
		log.info("isConfiguredFor(realm={},user={},credentialType={})", realm.getName(), user.getUsername(),
				credentialType);
		return supportsCredentialType(credentialType);
	}

	@Override
	public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
		log.info("isValid(realm={},user={},credentialInput.type={},password={})", realm.getName(), user.getUsername(),
				credentialInput.getType(), credentialInput.getChallengeResponse());
		if (!supportsCredentialType(credentialInput.getType()) || !(credentialInput instanceof UserCredentialModel)) {
			return false;
		}
		UserCredentialModel cred = (UserCredentialModel) credentialInput;
		SoapConnection soapConnection = new SoapConnection();
		try {
			return soapConnection.callSoapServiceAndBuildUser(user.getUsername(), cred.getChallengeResponse());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean supportsCredentialType(String credentialType) {
		log.info("supportsCredentialType({})", credentialType);
		return PasswordCredentialModel.TYPE.endsWith(credentialType);
	}

	@Override
	public void close() {
		log.info("Close");
	}

}
