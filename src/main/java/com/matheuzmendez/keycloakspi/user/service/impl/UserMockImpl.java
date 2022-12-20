package com.matheuzmendez.keycloakspi.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.matheuzmendez.keycloakspi.user.service.ExternalUserProviderService;
import com.matheuzmendez.keycloakspi.user.service.UserDto;
import com.matheuzmendez.keycloakspi.user.service.UserMock;

public class UserMockImpl implements UserMock {
	private List<UserDto> users;

	public UserMockImpl() {
		users = new ArrayList<UserDto>();
		users.add(new UserDto("38518815754", "kleber@mail.com", "Kleber", "Rodrigo", "1234567890"));
	}

	public UserDto obter(String username) {

		for (UserDto user : this.users) {
			if (user.getUsername().equals(username))
				return user;
		}

		return new UserDto(username, "", "", "", "");
	}

	public boolean autenticar(String url, String username, String password) {
		ExternalUserProviderService externalUserProviderService = new ExternalUserProviderService(url);
		return externalUserProviderService.callSoapServiceAndBuildUser(username, password);
	}
}
