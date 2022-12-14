package com.matheuzmendez.keycloakspi.user;

import java.io.IOException;
import java.util.List;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matheuzmendez.keycloakspi.service.ExternalUserProviderService;

public class ExternalUserStorageProviderFactory implements UserStorageProviderFactory<ExternalUserStorageProvider> {

	private static final Logger log = LoggerFactory.getLogger(ExternalUserStorageProviderFactory.class);
	public static final String PROVIDER_ID = "external-user-provider";

	@Override
	public ExternalUserStorageProvider create(KeycloakSession session, ComponentModel model) {
		log.info("creating new ExternalUserStorageProvider");
		ExternalUserProviderService service;
		try {
			service = new ExternalUserProviderService(model.get("url"), model.get("key"));
			return new ExternalUserStorageProvider(session, model, service);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
		create(session, model);
	}
	
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property("url", "URL", "url of your rest api", ProviderConfigProperty.STRING_TYPE, "", null)
                .property("key", "Key", "", ProviderConfigProperty.STRING_TYPE, "", null)
                .build();
    }

}