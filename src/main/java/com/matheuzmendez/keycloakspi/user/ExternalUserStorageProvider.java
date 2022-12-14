package com.matheuzmendez.keycloakspi.user;

import java.util.HashSet;
import java.util.Set;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.UserStorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matheuzmendez.keycloakspi.service.ExternalUserProviderService;

public class ExternalUserStorageProvider implements UserStorageProvider, CredentialInputValidator {

	private static final Logger log = LoggerFactory.getLogger(ExternalUserStorageProvider.class);

	protected KeycloakSession session;
	protected ComponentModel model;
	private final ExternalUserProviderService externalUserService;

	public ExternalUserStorageProvider(KeycloakSession session, ComponentModel model,
			ExternalUserProviderService externalUserService) {
		log.info("teste");
		this.session = session;
		this.model = model;
		this.externalUserService = externalUserService;
		log.info(session.toString() + "," + model.toString() + "," + externalUserService.toString());
	}

	@Override
	public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
		log.info("isConfiguredFor(realm={},user={},credentialType={})", realm.getName(), user.getUsername(),
				credentialType);
		return supportsCredentialType(credentialType);
	}

	@Override
	public boolean supportsCredentialType(String credentialType) {
		log.info("supportsCredentialType({})", credentialType);
		return PasswordCredentialModel.TYPE.equals(credentialType);
	}

	@Override
	public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
		log.info("isValid");

		if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
			return false;
		}

		UserCredentialModel cred = (UserCredentialModel) input;
		boolean rtn = externalUserService.callSoapServiceAndBuildUser(user.getUsername(), cred.getValue());

		if (rtn) {
			log.info("isValid = " + user.getUsername());
			addToStorage(realm, user.getUsername());
		}
		return rtn;
	}

	private void addToStorage(RealmModel realm, String username) {
		UserModel local = session.userLocalStorage().getUserByUsername(realm, username);
		log.info("local ======>" + local);
		if (local == null) {
			local = session.userLocalStorage().addUser(realm, username);
			local.setFederationLink(model.getId());
			local.setEnabled(true);
			// local.grantRole(realm.getRole("admin"));// here you need to added what do you
			// want...
			// local.grantRole(realm.getRole("create-realm"));// here you need to added what
			// do you want...
			log.info("added to local <======");
			session.userCache().clear();
		}
	}

	@SuppressWarnings("unused")
	private UserModel getFromStorage(RealmModel realm, String username) {
		UserModel local = session.userLocalStorage().getUserByUsername(realm, username);
		log.info("local ======>" + local);
		return local;
	}
	
	@SuppressWarnings(value = {"unused", "deprecation"})
	private Set<RoleModel> getDefaultRoles(RealmModel realm) {
		Set<RoleModel> set = new HashSet<>();
        for (String r : realm.getDefaultRoles()) {
            set.add(realm.getRole(r));
            log.info("defRole ======>" + r);
        }
 
        for (ClientModel application : realm.getClients()) {
            for (String r : application.getDefaultRoles()) {
                set.add(application.getRole(r));
                log.info("clientRole ======>" + r);
            }
        }
        
        return set;
	}
	
	@Override
	public void close() {
		log.info("Close");
	}

}
