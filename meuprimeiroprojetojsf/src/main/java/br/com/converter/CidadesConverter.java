package br.com.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidade.Cidades;
import br.com.entidade.Estados;
import br.com.jpautil.JPAUtil;

@FacesConverter(forClass = Cidades.class, value="cidadeConverter")
public class CidadesConverter implements Converter, Serializable {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String codigoCidade) {

		EntityManager entityManager = JPAUtil.geEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();

		Cidades cidade = (Cidades) entityManager.find(Cidades.class, Long.parseLong(codigoCidade));

		return cidade;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object cidade) {
		// TODO Auto-generated method stub

		if (cidade == null) {

			return null;
		}

		if (cidade instanceof Cidades) {
			return ((Cidades) cidade).getId().toString();
		} else {
			return cidade.toString();

		}

		
	}

}
