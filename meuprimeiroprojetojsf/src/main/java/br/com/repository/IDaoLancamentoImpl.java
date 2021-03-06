package br.com.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidade.Lancamento;
import br.com.jpautil.JPAUtil;

public class IDaoLancamentoImpl implements IDaoLancamento {
	
	 public List <Lancamento> consultar(Long codUser){
		 
		 List <Lancamento> lista= null;
		 
		 EntityManager entityManager = JPAUtil.geEntityManager();
		 EntityTransaction transaction = entityManager.getTransaction();
		 transaction.begin();
		 
		 lista = entityManager.createQuery(" from Lancamento where usuario.id = " + codUser).getResultList(); 
		 transaction.commit();
		 entityManager.close();
		 
		 
		 return lista;
		 
	 }
	
	

}
