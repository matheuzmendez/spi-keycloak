package com.matheuzmendez.keycloakspi.provider;

import java.util.List;

import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalUserStorageProviderFactory implements UserStorageProviderFactory<ExternalUserStorageProvider> {

	private Logger log = LoggerFactory.getLogger(ExternalUserStorageProvider.class);

	@Override
	public void init(Config.Scope arg0) {
		log.info("Creating Federation Provider Factory");
	}

	@Override
	public void postInit(KeycloakSessionFactory arg0) {
		log.info("Finish creating Federation Provider Factory");
	}

	@Override
	public void close() {
		log.info("Closing Federation Provider Factory");
	}

	@Override
	public ExternalUserStorageProvider create(KeycloakSession session, ComponentModel model) {
		log.info("Calling create");
		return new ExternalUserStorageProvider(session, model);
	}

	@Override
	public String getId() {
		log.info("Calling getId()");
		return "external-user-provider";
	}
	
	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return ProviderConfigurationBuilder.create()
				.property("urlConsulta", "URLConsulta", "Base URL of the API", ProviderConfigProperty.STRING_TYPE, "", null)
				.property("urlAutentica", "URLAutentica", "Base URL of the API", ProviderConfigProperty.STRING_TYPE, "", null)
				.build();
	}

}
