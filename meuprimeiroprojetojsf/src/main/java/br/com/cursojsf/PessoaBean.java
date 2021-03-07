package br.com.cursojsf;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.datatype.DatatypeConfigurationException;

import com.google.gson.Gson;
import javax.xml.bind.*;

import br.com.dao.DaoGeneric;
import br.com.entidade.Cidades;
import br.com.entidade.Estados;
import br.com.entidade.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDaoPessoa;
import br.com.repository.IDaoPessoaImpl;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	private List<SelectItem> estados;
	private List<SelectItem> cidades;
	private Part arquivoFoto;

	public String Salvar() throws IOException {
		/*Processar imagem*/
		System.out.println("Foto: " + arquivoFoto);
		byte [] imagem = getByte(arquivoFoto.getInputStream());
		pessoa.setFotoIconBase64Original(imagem);/*Salva a foto original*/
		/*Transformar em um bufferimage*/
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagem));
		/*Pega o tipo da imagem*/
		
		int type = bufferedImage.getType()==0? BufferedImage.TYPE_INT_ARGB :bufferedImage.getType();
		int largura = 200;
		int altura = 200;
		/*criar a miniatura*/
		BufferedImage resizedImage = new BufferedImage(largura, altura, type);
		Graphics2D g =resizedImage.createGraphics();
		g.drawImage(bufferedImage, 0, 0, largura, altura,  null);
		g.dispose();
		/*Escrever novamente a imagem em tamanho menor*/
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String extensao = arquivoFoto.getContentType().split("\\/")[1];
		ImageIO.write(resizedImage, extensao, baos);
		
		
		String miniImagem = "data:" + arquivoFoto.getContentType()+";base64," +DatatypeConverter.printBase64Binary(baos.toByteArray());
		pessoa.setFotoIconBase64(miniImagem);
		pessoa.setExtensao(extensao);
		pessoa = daoGeneric.merge(pessoa);
		carregarPessoas();
		mostrarMsg("Cadastrado com sucesso");
		return "";
	}

	private void mostrarMsg(String msg) {

		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
		// TODO Auto-generated method stub

	}

	public String novo() {

		pessoa = new Pessoa();

		return "";

	}

	public String limpar() {

		pessoa = new Pessoa();

		return "";

	}

	public void pesquisaCep(AjaxBehaviorEvent event) {

		try {
			System.out.println("Chamando função de pesquisa cep.");
			URL url = new URL("https://viacep.com.br/ws/" + pessoa.getCep() + "/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String cep = "";
			StringBuilder JsonCep = new StringBuilder();

			while ((cep = br.readLine()) != null) {
				JsonCep.append(cep);

			}

			Pessoa gson = new Gson().fromJson(JsonCep.toString(), Pessoa.class);
			pessoa.setLogradouro(gson.getLogradouro());
			pessoa.setComplemento(gson.getComplemento());
			pessoa.setBairro(gson.getBairro());
			pessoa.setLocalidade(gson.getLocalidade());
			pessoa.setUf(gson.getUf());

			System.out.println(gson);
		} catch (Exception e) {
			System.out.println(e);
			mostrarMsg("Erro ao consultaro o cep");

		}

	}

	public String remove() {

		daoGeneric.deletePorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();

		mostrarMsg("Removido com sucesso");
		return "";

	}

	public String logar() {

		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());

		if (pessoaUser != null) {

			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser);

			return "primeirapagina.jsf";

		}

		return "index.jsf";

	}

	public String deslogar() {

		System.out.println("CHAMANDO METODO DE DESLOGAR USUÁRIO");
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");

		HttpServletRequest httpServletRequest = (HttpServletRequest) context.getCurrentInstance().getExternalContext()
				.getRequest();
		httpServletRequest.getSession().invalidate();

		return "index.jsf";

	}

	public boolean permiteAcesso(String acesso) {

		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");

		return pessoaUser.getPerfiluser().equals(acesso);

	}

	@PostConstruct
	public void carregarPessoas() {
		pessoas = daoGeneric.getListEntity(Pessoa.class);

	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listaEstados();
		return estados;
	}

	public void carregaCidades(AjaxBehaviorEvent event) {
		System.out.println("Chamou evento");
		
		
		
		Estados estado = (Estados)((HtmlSelectOneMenu) event.getSource()).getValue();
		
		
			
					if(estado != null ){
						pessoa.setEstados(estado);
						
						List<Cidades> cidades = JPAUtil.geEntityManager().createQuery("from Cidades where estados.id = "
						+ estado.getId()).getResultList();
						
						
						List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
						
						
						for(Cidades cidade: cidades) {
							selectItemsCidade.add(new SelectItem(cidade, cidade.getNome()));
							
							
						}
						setCidades(selectItemsCidade);
					}
		}
		

	
	
	
	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}
	public List<SelectItem> getCidades() {
		return cidades;
	}
	
	
	
	
	public void editar() {
		
		
		
		System.out.println("Editar sendo chamado");
		
		
		if(pessoa.getCidades() != null) {
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado);
			
			
			
			List<Cidades> cidades = JPAUtil.geEntityManager().createQuery("from Cidades where estados.id = "
					+ estado.getId()).getResultList();
					
					
					List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
					
					
					for(Cidades cidade: cidades) {
						selectItemsCidade.add(new SelectItem(cidade, cidade.getNome()));
						
						
					}
					setCidades(selectItemsCidade);
			
			
			
			
		}
		
		
		
		
		
	}

	public Part getArquivoFoto() {
		return arquivoFoto;
	}

	public void setArquivoFoto(Part arquivoFoto) {
		this.arquivoFoto = arquivoFoto;
	}
	
	
	/*Conversão do inputstream para array de bytes*/
	private byte[] getByte(InputStream is) throws IOException{
		
		int len;
		int size = 1024;
		byte[] buf = null;
		
		if(is instanceof ByteArrayInputStream) {
			
			size = is.available();
			buf= new byte [size];
			len = is.read(buf,0,size);
			
		}else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];
			
			
			while((len = is.read(buf,0,size))!= -1) {
				
				bos.write(buf,0,len);
				
			}
			
			buf = bos.toByteArray();
			
		}
		return buf;
	}
	
	
	
	
	public void download() throws IOException {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		
		String fileDownloadId = params.get("fileDownloadId");
		System.out.println("Baixar imagem " +fileDownloadId);
		
		Pessoa pessoa = daoGeneric.consultar(Pessoa.class, fileDownloadId);
		System.out.println(pessoa);
		
		
		HttpServletResponse  response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.addHeader("Content-Disposition", "attachment; filename=dowload." +pessoa.getExtensao());
		response.setContentType("application/octet-stream");
		response.setContentLength(pessoa.getFotoIconBase64Original().length);
		response.getOutputStream().write(pessoa.getFotoIconBase64Original());
		response.getOutputStream().flush();
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	
	

}