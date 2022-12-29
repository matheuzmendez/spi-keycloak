package vwfsbr.comission.keycloakspi.provider;

import javax.naming.InitialContext;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.UserModelDelegate;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vwfsbr.comission.keycloakspi.provider.model.UserData;
import vwfsbr.comission.keycloakspi.user.service.UserDto;
import vwfsbr.comission.keycloakspi.user.service.UserMock;
import vwfsbr.comission.keycloakspi.user.service.impl.UserMockImpl;

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
		if (model.getConfig().getFirst("urlConsulta") == null) {
			log.error("Security Service URL (urlConsulta) not defined!");
			return null;
		}

		UserDto user = this.repo.obter(model.getConfig().getFirst("urlConsulta"), model.getConfig().getFirst("parametroConsulta"), username);

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
			if (this.repo.autenticar(model.getConfig().getFirst("urlAutentica"),
					model.getConfig().getFirst("parametroAutentica"), userModel.getUsername(),
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
		userData.setCodMontadora(user.getCodMontadora());

		UserModel local = session.userLocalStorage().getUserByUsername(realmModel, user.getUsername());
		if (local == null) {
			log.info("Local User Not Found, adding user to Local");
			local = session.userLocalStorage().addUser(realmModel, userData.getUsername());
			local.setFederationLink(this.model.getId());
			local.setEnabled(true);
			local.setUsername(userData.getUsername());
			local.setEmail(userData.getEmail());
			local.setCreatedTimestamp(System.currentTimeMillis());
			local.setFirstName(userData.getFirstName());
			local.setLastName(userData.getLastName());
			local.setEmailVerified(true);
			local.setSingleAttribute("codDealer", userData.getCodDealer());
			local.setSingleAttribute("cargo", userData.getCargo());
			local.setSingleAttribute("filial", userData.getFilial());
			local.setSingleAttribute("nomeFilial", userData.getNomeFilial());
			local.setSingleAttribute("montadora", userData.getMontadora());
			local.setSingleAttribute("codMontadora", userData.getCodMontadora());
			
			GroupModel group = KeycloakModelUtils.findGroupByPath(realmModel, userData.getRole());

			if (group == null) {
				throw new RuntimeException("Unable to find group specified by path: " + userData.getRole());
			}

			local.joinGroup(group);
			log.info("Local User added in group: " + group.toString());

			session.userCache().clear();
			log.info("Local User Succesfully Created for username: " + userData.getUsername());
		} else {
			GroupModel leaveGroup = KeycloakModelUtils.findGroupByPath(realmModel, local.getGroupsStream().toString());
			if (leaveGroup != null) {
				local.leaveGroup(leaveGroup);
				log.info("Local User leaving group: " + leaveGroup.toString());
			}
			GroupModel joinGroup = KeycloakModelUtils.findGroupByPath(realmModel, userData.getRole());
			local.joinGroup(joinGroup);
			log.info("Local User added in group: " + joinGroup.toString());
		}
		return new UserModelDelegate(local) {
			@Override
			public void setUsername(String username) {
				super.setUsername(userData.getUsername());
			}
		};
	}

}
