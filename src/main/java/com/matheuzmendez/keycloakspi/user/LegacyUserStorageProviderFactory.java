package com.matheuzmendez.keycloakspi.user;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.UserStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyUserStorageProviderFactory implements UserStorageProviderFactory<LegacyUserStorageProvider> {

	private static final Logger log = LoggerFactory.getLogger(LegacyUserStorageProviderFactory.class);
	public static final String PROVIDER_ID = "legacy-user-provider";

	@Override
	public LegacyUserStorageProvider create(KeycloakSession session, ComponentModel model) {
		return new LegacyUserStorageProvider(session, model);
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}
	
	@Override
	public void onUpdate(KeycloakSession session, RealmModel realm, ComponentModel oldModel, ComponentModel newModel) {
		log.info("onUpdate()");
	}

	@Override
	public void onCreate(KeycloakSession session, RealmModel realm, ComponentModel model) {
		log.info("onCreate()");
	}

}