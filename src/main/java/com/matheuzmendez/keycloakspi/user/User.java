package com.matheuzmendez.keycloakspi.user;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class User {
	
    private String username;
    private String codigoDealer;
    private String cargo;
    private List<String> permissoes;
    private String filial;
    private String nomeFilial;
    private String montadora;
    
	public User(String username, String codigoDealer, String cargo, List<String> permissoes, String filial,
			String nomeFilial, String montadora) {
		this.username = username;
		this.codigoDealer = codigoDealer;
		this.cargo = cargo;
		this.permissoes = permissoes;
		this.filial = filial;
		this.nomeFilial = nomeFilial;
		this.montadora = montadora;
	}
    
}
