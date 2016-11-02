package com.rends.camera;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Camera {

	private String endereco;
	private String username;
	private String password;

	public Camera() {
		this("http://192.168.0.1/image", "default_username", "default_password");
		//
		log.info("Camera criada com valores padrão");
		//
	}

	public Camera(String webPage, String name, String password) {
		//
		log.info("Camera - criando com valores recebidos por parametros");
		//
		this.endereco = webPage;
		this.username = name;
		this.password = password;
	}

	public String criarStringAutenticacaoCodificada64() {
		//
		log.info("Camera - gerando string autenticacao codificada");
		//

		String authString = getStringAutenticacao();
		String authStringEnc = codificarBase64(authString);
		return authStringEnc;
	}

	private String getStringAutenticacao() {
		//
		log.info("Camera - Gereanco string de autenticacao");
		//
		String authString = this.username + ":" + this.password;
		log.info("String autenticacao: " + authString);
		return authString;
	}

	private String codificarBase64(String authString) {
		//
		log.info("Camera - Codificando string em base64 ");
		//
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		log.info("Camera - String autenticacao codificada: " + authStringEnc);
		return authStringEnc;
	}

	public static String getCurrentTimeStamp() {
		//
		log.info("Camera - Obtendo TIMESTAMP");
		//
		// SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd
		// HH:mm:ss.SSS"); // exibe mili-segundos
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		log.info("Camera - TIMESTAMP: " + strDate);
		return strDate;
	}

	public String gerarCaminhoGravarArquivo(String caminho, String prefixo, String nome, String sufixo,
			String extensao) {
		//
		log.info("Camera - Montando caminho para gravar o arquivo");
		//
		String sufixoEfetivo = sufixo;
		if (sufixo.equals("TIMESTAMP")) {
			sufixoEfetivo = getCurrentTimeStamp();
		}
		String caminhoGravarArquivo = caminho + System.getProperty("file.separator") + prefixo + "_" + nome + "_"
				+ sufixoEfetivo + "." + extensao;
		log.info("Camera - Arquivo: " + caminhoGravarArquivo);
		return caminhoGravarArquivo;
	}

	public void salvarImagemDaCamera() {
		//
		log.info("Camera - Salvar Imagem com nome padrão");
		//
		salvarImagemDaCamera("capturas", "IMG", "camera", "TIMESTAMP", "jpg");

	}

	public void salvarImagemDaCamera(String caminho, String prefixo, String nome, String sufixo, String extensao) {
		//
		log.info("Camera - Salvar Imagem com nome definido por parametros");
		//
		acessarCamera(gerarCaminhoGravarArquivo(caminho, prefixo, nome, sufixo, extensao));
	}

	public void acessarCamera(String caminhoGravarArquivo) {
		//
		log.info("Camera - Acessando a camera");
		//
		try {
			log.info("Camera - Definindo leitura da camera");
			InputStream is1 = generateInputStream(this.endereco, criarStringAutenticacaoCodificada64());
			log.info("Camera - Definindo gravacao do arquivo");
			OutputStream os1 = generateOutputStream(caminhoGravarArquivo);
			log.info("Camera - Processando dados trafegados");
			saveStream(is1, os1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private InputStream generateInputStream(String streamUrl, String authStringEncoded) throws IOException {
		//
		log.info("Camera - Stream de leitura - Acesso ");
		//
		URL url = new URL(streamUrl);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
		InputStream is = urlConnection.getInputStream();
		return is;
	}

	private OutputStream generateOutputStream(String destinationFile) throws IOException {
		//
		log.info("Camera - Stream de gravacao - Acesso ");
		//
		log.debug("Arquivo destino - caminho base: " + System.getProperty("user.dir")); 
		log.debug("Arquivo destino: " + destinationFile);                               
		OutputStream os = new FileOutputStream(destinationFile);
		return os;
	}

	private void saveStream(InputStream is, OutputStream os) throws IOException {
		//
		log.info("Camera - Processando fluxo de dados");
		//
		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
		log.info("Camera - Imagem gravada");
	}

}
