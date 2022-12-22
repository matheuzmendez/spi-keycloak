package com.matheuzmendez.keycloakspi.provider;

import javax.naming.InitialContext;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
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
	private GroupModel groupModel;

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
		if (model.getConfig().getFirst("urlConsulta") == null) {
			log.error("Security Service URL (urlConsulta) not defined!");
			return null;
		}

		UserDto user = this.repo.obter(model.getConfig().getFirst("urlConsulta"), username);

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

		if (model.getConfig().getFirst("urlAutentica") == null) {
			log.error("Security Service URL (urlAutentica) not defined!");
			return false;
		}

		try {
			if (this.repo.autenticar(model.getConfig().getFirst("urlAutentica"), userModel.getUsername(),
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
//		UserDto(username, email, firstName, lastName, codDealer, cargo, filial, nomeFilial, montadora);
		UserData userData = new UserData(session, realmModel, this.model);
		userData.setUsername(user.getUsername());
		userData.setEmail(user.getEmail());
		userData.setFirstName(user.getFirstName());
		userData.setLastName(user.getLastName());
		userData.setCodDealer(user.getCodDealer());
		userData.setCargo(user.getCargo());
		userData.setFilial(user.getFilial());
		userData.setNomeFilial(user.getNomeFilial());
		userData.setMontadora(user.getMontadora());
		userData.setRole(user.getRole());

		UserModel local = session.userLocalStorage().getUserByUsername(realmModel, user.getUsername());
		if (local == null) {
			log.info("Local User Not Found, adding user to Local");
			local = session.userLocalStorage().addUser(realmModel, userData.getUsername());
			log.info("1");
			local.setFederationLink(this.model.getId());
			log.info("2");
			local.setEnabled(true);
			log.info("3");
			local.setUsername(userData.getUsername());
			log.info("4");
			local.setEmail(userData.getEmail());
			log.info("5");
			local.setCreatedTimestamp(System.currentTimeMillis());
			log.info("6");
			local.setFirstName(userData.getFirstName());
			log.info("7");
			local.setLastName(userData.getLastName());
			log.info("8");
			local.setEmailVerified(true);
			log.info("9");
			local.setSingleAttribute("codDealer", userData.getCodDealer());
			log.info("10");
			local.setSingleAttribute("cargo", userData.getCargo());
			log.info("11");
			local.setSingleAttribute("filial", userData.getFilial());
			log.info("12");
			local.setSingleAttribute("nomeFilial", userData.getNomeFilial());
			log.info("13");
			local.setSingleAttribute("montadora", userData.getMontadora());
			log.info("14");
//			local.grantRole(realmModel.getRole(userData.getRole()));
//			log.info("15");
			groupModel = null;
			groupModel.setName(userData.getRole());				
			local.joinGroup(groupModel);
			log.info("15");

//			if (roleModel == null) {
//				realmModel.addRole(userData.getRole());
//			}
//			 
//			 local.grantRole(roleModel);
//			Set<GroupModel> getGroups(realmModel, user.getUsername());

			
			session.userCache().clear();
			log.info("Local 1User Succesfully Created for username: " + userData.getUsername());
		}
		return new UserModelDelegate(local) {
			@Override
			public void setUsername(String username) {
				super.setUsername(userData.getUsername());
			}
		};
	}
	
	

}
