package br.com.repository;

import java.util.List;

import br.com.entidade.Lancamento;

public interface IDaoLancamento {

	List<Lancamento> consultar(Long id);
	
	

}
