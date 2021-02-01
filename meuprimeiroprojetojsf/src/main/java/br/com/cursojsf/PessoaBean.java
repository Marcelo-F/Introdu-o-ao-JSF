package br.com.cursojsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.html.HtmlCommandButton;

import com.sun.faces.taglib.html_basic.CommandButtonTag;

import javafx.scene.web.HTMLEditorSkin.Command;

@ApplicationScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private HtmlCommandButton commandButton;
	private String nome;
	private String senha;
	private String texto;
	private List<String> nomes = new ArrayList<String>();

	public String addnome() {
		nomes.add(nome);
		if (nomes.size() > 3) {
			//commandButton.setDisabled(true);
			
			return "paginanavegada?faces-redirect=true";
		}
		return ""; // null ou vazio fica na mesma página

	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<String> getNomes() {
		return nomes;
	}

	public void setNomes(List<String> nomes) {
		this.nomes = nomes;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	
	
	
	
}
