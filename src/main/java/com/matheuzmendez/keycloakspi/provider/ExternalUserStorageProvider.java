package com.matheuzmendez.keycloakspi.provider;

import javax.naming.InitialContext;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.utils.UserModelDelegate;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matheuzmendez.keycloakspi.provider.model.UserData;
import com.matheuzmendez.keycloakspi.user.service.UserDto;
import com.matheuzmendez.keycloakspi.user.service.UserMock;
import com.matheuzmendez.keycloakspi.user.service.impl.UserMockImpl;

public class ExternalUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

	private final KeycloakSession session;
	private final ComponentModel model;
	private Logger log = LoggerFactory.getLogger(ExternalUserStorageProvider.class);
	private InitialContext initCtx;

	private UserMock repo;

	public ExternalUserStorageProvider(KeycloakSession session, ComponentModel model) {
		this.session = session;
		this.model = model;
		this.repo = new UserMockImpl();
		
		try {
			initCtx = new InitialContext();
		} catch (Exception ex) {
			log.error("Cannot create InitialContext", ex);
		}
	}

	public InitialContext getInitCtx() {
		return initCtx;
	}

	public void setInitCtx(InitialContext initCtx) {
		this.initCtx = initCtx;
	}

	@Override
	public UserModel getUserById(String id, RealmModel realmModel) {
		log.info("Get User By Id: " + id);
		return null;
	}

	@Override
	public UserModel getUserByUsername(String username, RealmModel realmModel) {
		if (model.getConfig().getFirst("url") == null) {
			log.error("Security Service URL not defined!");
			return null;
		}

		UserDto user = this.repo.obter(username);

		if (user == null) {
			log.info("Username: " + username + " not found");
			return null;
		}

		try {
			return getLocalUserByUsername(user, realmModel);
		} catch (Exception ex) {
			log.error("Error getting user with username: " + username, ex);
			return null;
		}
	}

	@Override
	public UserModel getUserByEmail(String email, RealmModel realmModel) {
		log.info("Get User By Email: " + email);
		return null;
	}

	@Override
	public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
		return supportsCredentialType(credentialType);
	}

	@Override
	public boolean supportsCredentialType(String credentialType) {
		return credentialType.equals(PasswordCredentialModel.TYPE);
	}

	@Override
	public boolean isValid(RealmModel realm, UserModel userModel, CredentialInput input) {
		log.info("isValid method called for username: " + userModel.getUsername());

		try {
			if (this.repo.autenticar(model.getConfig().getFirst("url"), userModel.getUsername(),
					input.getChallengeResponse())) {
				log.info("Password Matched for:" + userModel.getUsername());
				return true;
			} else {
				log.info("Password Not Matched for:" + userModel.getEmail());
				return false;
			}
		} catch (Exception ex) {
			log.error("Password Validation Error", ex);
			return false;
		}

	}

	@Override
	public void close() {
		log.info("Closing FederationDB Provider");
	}

	private UserModel getLocalUserByUsername(UserDto user, RealmModel realmModel) {
		UserData userData = new UserData(session, realmModel, this.model);
		userData.setEmail(user.getEmail());
		userData.setUsername(user.getUsername());
		userData.setFirstName(user.getFirstname());
		userData.setLastName(user.getLastname());
		// userData.setRole("BXPLA1000");

		UserModel local = session.userLocalStorage().getUserByUsername(realmModel, user.getUsername());
		if (local == null) {
			log.info("Local User Not Found, adding user to Local");
			local = session.userLocalStorage().addUser(realmModel, userData.getUsername());
			local.setFederationLink(this.model.getId());
			local.setEmail(userData.getEmail());
			local.setUsername(userData.getUsername());
			local.setCreatedTimestamp(System.currentTimeMillis());
			local.setFirstName(userData.getFirstName());
			local.setLastName(userData.getLastName());
			local.setEnabled(true);
			local.setEmailVerified(true);

			// RoleModel roleModel = realmModel.getRole("BXPLA100");
			// if (roleModel == null)
			// realmModel.addRole("BXPLA100");

			// realmModel.getRole("BXPLA100");
			// local.grantRole(roleModel);

			log.info("Local User Succesfully Created for username: " + userData.getUsername());
		}
		return new UserModelDelegate(local) {
			@Override
			public void setUsername(String username) {
				super.setUsername(userData.getUsername());
			}
		};
	}

}
