package com.matheuzmendez.keycloakspi.user.service;

public class UserDto {

	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String codDealer, cargo, filial, nomeFilial, montadora, role, codMontadora;

	public UserDto(String username, String email, String firstName, String lastName,
					String codDealer, String cargo, String filial, String nomeFilial, String montadora, String role, String codMontadora) {
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.codDealer = codDealer;
		this.cargo = cargo;
		this.filial = filial;
		this.nomeFilial = nomeFilial;
		this.montadora = montadora;
		this.role = role;
		this.codMontadora = codMontadora;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public void setCodDealer(String codDealer) {
		this.codDealer = codDealer;
	}

	public String getCodDealer() {
		return codDealer;
	}
	
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getCargo() {
		return cargo;
	}
	
	public void setFilial(String filial) {
		this.filial = filial;
	}

	public String getFilial() {
		return filial;
	}
	
	public void setNomeFilial(String nomeFilial) {
		this.nomeFilial = nomeFilial;
	}

	public String getNomeFilial() {
		return nomeFilial;
	}
	
	public void setMontadora(String montadora) {
		this.montadora = montadora;
	}

	public String getMontadora() {
		return montadora;
	}
	
	public void setRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}
	
	public void setCodMontadora(String codMontadora) {
		this.codMontadora = codMontadora;
	}

	public String getCodMontadora() {
		return codMontadora;
	}
	
}
