package org.compiere.process;


import java.util.logging.*;
import org.compiere.model.*;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperRunManager;  
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.JasperReport;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import jcifs.smb.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.compiere.util.*;

import com.itextpdf.text.Document;  
import com.itextpdf.text.pdf.BaseFont;  
import com.itextpdf.text.pdf.PdfContentByte;  
import com.itextpdf.text.pdf.PdfImportedPage;  
import com.itextpdf.text.pdf.PdfReader;  
import com.itextpdf.text.pdf.PdfWriter;  
import java.util.ArrayList;  
import java.util.Iterator;  
import java.util.List;  

/**
 * Processo usado para envio de email com o orcamento em pdf para o cliente
 * A classe gera o pdf do pedido selecionado, coloca no servidor e 
 * envia como anexo para o cliente
 * @author root
 *
 */

public class EnviaEmailOrcamentoCompra  extends SvrProcess{

	private String caminho="http://192.168.1.1/";

	int tipodoc=0; 
	private int id = 0;
	String servidor = "";
	String usuariosamba = "";
	String senhasamba = "";

	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("C_Order_ID"))
				id = para[i].getParameterAsInt();

			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
		//id = getRecord_ID();

	}	//	prepare

	/**
	 * Constructor for ReportDriver
	 */


	protected String doIt() throws Exception
	{

		try{

			String titulo = "Email";
			String conteudo = "Conteúdo";

			MOrder mo = new MOrder(getCtx(), id, "getOrder");
			fechaTransacao("getOrder");
			String docno = mo.getDocumentNo();
			MUser mu = new MUser(getCtx(), mo.getAD_User_ID(), "getUser");
			fechaTransacao("getUser");
			String name = "";
			if (mu.get_Value("name")!=null)
				name = (String)mu.get_Value("name");
			else
				name = mu.getName();
			String emailuser = mu.getEMail();
			System.out.println("EMAIL: " + emailuser);
			MUser idusuario = new MUser(getCtx(), Env.getAD_User_ID(getCtx()), "getUser2");
			fechaTransacao("getUser2");
			BigDecimal versao1 = (BigDecimal)mo.get_Value("Version");

	    	String datapedido = mo.getCreated().toString();
			int versao = versao1.intValue();
	    	//int versao =1;

			MDocType mdt = new MDocType(getCtx(),mo.getC_DocTypeTarget_ID(),"DocType");
			String temail = "PC"; //padrao 
			
			//pegando template
			String sql = "SELECT * FROM x_templateemail WHERE tipo='"+temail+"' and isactive='Y' ORDER BY RANDOM()";
			PreparedStatement pst = DB.prepareStatement(sql, get_TrxName());
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {

				titulo = rs.getString("titulo");
				conteudo = rs.getString("conteudo");

			}

			//trocando os dados da mascara
			titulo = titulo.replaceAll("<<documentno>>", docno);
			conteudo = conteudo.replaceAll("<<documentno>>", docno);
			conteudo = conteudo.replaceAll("<<name>>", name);
			conteudo = conteudo.replaceAll("<<usuario>>", idusuario.getName());
			titulo = trocaHtml(titulo, mo.getDocumentNo());
			conteudo = trocaHtmlInverso(conteudo, mo.getDocumentNo());
			conteudo = trocaHtmlInverso(conteudo, mo.getDocumentNo());
			conteudo = conteudo.replaceAll("<<chave>>", (String)mo.get_Value("Chave"));
			

			//pegar confs do servidor de email
			MClient mc = new MClient(getCtx(), Env.getAD_Client_ID(getCtx()), get_TrxName());
			String host = mc.getSMTPHost();
			
			String pwd = mc.getRequestUserPW();

			//definindo nome do arquivo
	    	  String nomeArquivo = "";
	    	  String ano1 = null,mes1 = "0";
	    	  //pedido de compra
	    	  if (docno.indexOf("PC")!=-1) {
	    		  nomeArquivo = docno.replace("_", "") + ".pdf";
	    		  String[] data1 = datapedido.split(" ");
	    		  String[] data2 = data1[0].split("-");
	    		  ano1 = data2[0];
	    		  mes1 = data2[1];
	    	  }
	    	  
			//pegar usuário do pedido
			EMail email = new EMail(getCtx(), host, "projeto@palmetal.com.br", emailuser, titulo, conteudo, "projeto@palmetal.com.br", "8020designotimo");
			byte[] anexo = geraAnexo();	
			
			
			//salvar pdf
			salvaPDF2(anexo, mo.getDocumentNo(), nomeArquivo, ano1, mes1);
			
			// NOVO
			
			String caminho = "https://s3-us-west-2.amazonaws.com/palmetal-images/";
			//Servidor de arquivos - essa linha foi comentada pq o servidor de arquivo parou de funcionar
			//String servidor = "192.168.1.6";
			String servidor = "192.168.1.246";
    		String usuariosamba = "compiere";
    		String senhasamba = "qwe123";

			String samba1 = "smb://compiere:"+senhasamba+"@"+servidor+"/orcamentos/" + ano1 + "/" + mes1 + "/" + nomeArquivo;
			//FileInputStream fis = new FileInputStream("https://s3-us-west-2.amazonaws.com/palmetal-images/Propaganda_Orçamento_V03.pdf");
			
			byte[] b1 = new byte[1008192];
	        SmbFile fs1 = new SmbFile( samba1 );
	        
			SmbFileInputStream out11 = new SmbFileInputStream( fs1 );
			out11.read(b1);
			
			File f21 = new File("saida1.pdf");
			FileOutputStream fo21 = new FileOutputStream(f21);
			fo21.write(b1);

    		
    		
			String samba = "smb://compiere:"+senhasamba+"@"+servidor+"/orcamentos/Propaganda_Orçamento_V03.pdf";
			//FileInputStream fis = new FileInputStream("https://s3-us-west-2.amazonaws.com/palmetal-images/Propaganda_Orçamento_V03.pdf");
			
			byte[] b = new byte[1008192];
	        SmbFile fs = new SmbFile( samba );
	        
			SmbFileInputStream out1 = new SmbFileInputStream( fs );
			out1.read(b);
			
			File f2 = new File("saida2.pdf");
			FileOutputStream fo2 = new FileOutputStream(f2);
			fo2.write(b);
			
			anexo = mergePDF(f2,f21);			
			
			//salvar pdf agora com o merge
			salvaPDF2(anexo, mo.getDocumentNo(), nomeArquivo, ano1, mes1);
			
			// NOVO			
			
						
			email.setMessageHTML(conteudo);
			
			//
			email.addAttachment(anexo,"application/pdf", nomeArquivo);
			email.addBcc("projeto@palmetal.com.br");
			email.send();
			
			//salvar pdf
			//salvaPDF2(anexo, mo.getDocumentNo(), nomeArquivo, ano1, mes1);

			
			//Salva historico do pedido.
			String sped = mo.get_ValueAsString("Situacao");
			//UtilsPalmetal.salvaStatusPedidoHistorico(mo.get_ID(), "OE", mo.getDocumentNo());
			if (sped.equals("DR")) {
				String sql2 = "UPDATE c_order SET situacao='OE', version=version+1 WHERE c_order_id=" + id;
				DB.executeUpdate(sql2, "upOrder");
				
				//inserir uma linha na tabela de historico				
				sql2="INSERT INTO x_statuspedido (isactive,created,createdby,updated,updatedby,ad_client_id,ad_org_id,c_order_id,status,horacriacao) VALUES ('Y',now(),2000000,now(),2000000,2000000,2000000," + id + ",'OE',now());";
				DB.executeUpdate(sql2, "upOrder");
				
				fechaTransacao("upOrder");
			}
			else {
				String sql2 = "update c_order set version=version+1 where c_order_id=" + id;
				DB.executeUpdate(sql2, "upOrder");
				fechaTransacao("upOrder");
			}
			//salvar pdf
			//salvaPDF(anexo, mo.getDocumentNo(), nomeArquivo, ano1, mes1);

		}catch(Exception ex) {
			ex.printStackTrace();
			String connectMsg = "Could not create the report " + ex.getMessage() + " " + ex.getLocalizedMessage();
			System.out.println(connectMsg);
			return "ERRO";
		}

		return "Orçamento enviado";

	}
	
	private byte[] mergePDF(File f1, File f2) throws Exception{
		List<InputStream> pdfs = new ArrayList<InputStream>();	
        pdfs.add(new FileInputStream(f1));  
        pdfs.add(new FileInputStream(f2));  
        
        boolean paginate=true;

        OutputStream outputStream = new FileOutputStream("saida4.pdf");  
        
        
        Document document = new Document();  
        
        List<PdfReader> readers = new ArrayList<PdfReader>();  
        int totalPages = 0;  
        Iterator<InputStream> iteratorPDFs = pdfs.iterator();  
  
        // Create Readers for the pdfs.  
        while (iteratorPDFs.hasNext()) {  
            InputStream pdf = iteratorPDFs.next();  
            PdfReader pdfReader = new PdfReader(pdf);  
            readers.add(pdfReader);  
            totalPages += pdfReader.getNumberOfPages();  
        }  
        // Create a writer for the outputstream  
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);  
  
            document.open();  
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,  
                    BaseFont.CP1252, BaseFont.NOT_EMBEDDED);  
            PdfContentByte cb = writer.getDirectContent(); // Holds the PDF  
        // data  
  
            PdfImportedPage page;  
            int currentPageNumber = 0;  
            int pageOfCurrentReaderPDF = 0;  
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();  
  
            // Loop through the PDF files and add to the output.  
            while (iteratorPDFReader.hasNext()) {  
                PdfReader pdfReader = iteratorPDFReader.next();  
  
                // Create a new page in the target for each source page.  
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {  
                    document.newPage();  
                    pageOfCurrentReaderPDF++;  
                    currentPageNumber++;  
                    page = writer.getImportedPage(pdfReader,  
                            pageOfCurrentReaderPDF);  
                    cb.addTemplate(page, 0, 0);  
  
                    // Code for pagination.  
                if (paginate) {  
                    cb.beginText();  
                    cb.setFontAndSize(bf, 9);  
                    cb.showTextAligned(PdfContentByte.ALIGN_CENTER, ""  
                            + currentPageNumber + " of " + totalPages, 520,  
                            5, 0);  
                    cb.endText();  
                }  
            }  
            pageOfCurrentReaderPDF = 0;  
        }  
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.writeTo(outputStream);
        
        
        outputStream.flush();  
        document.close();  
        outputStream.close();  
       
        byte[] b = new byte[1000000];
		File e = new File("saida4.pdf");
		FileInputStream in1 = new FileInputStream( e );
		in1.read(b);		
		in1.close();        
        
        return b;

	}
	
	protected String doIt2() throws Exception{
		MOrder mo = new MOrder(getCtx(), id, "getOrder");
		fechaTransacao("getOrder");
		String docno = mo.getDocumentNo();
		MUser mu = new MUser(getCtx(), mo.getAD_User_ID(), "getUser");
		fechaTransacao("getUser");
		String name = "";
		if (mu.get_Value("name")!=null)
			name = (String)mu.get_Value("name");
		else
			name = mu.getName();
		String emailuser = mu.getEMail();
		System.out.println("EMAIL: " + emailuser);
		
		//pegar confs do servidor de email
		MClient mc = new MClient(getCtx(), Env.getAD_Client_ID(getCtx()), get_TrxName());
		String host = mc.getSMTPHost();
		String user = mc.getRequestUser();
		String pwd = mc.getRequestUserPW();
		

	    String from = user;
	    String to = emailuser;
	    byte[] anexo = geraAnexo();	

	    // Get system properties
	    Properties props = System.getProperties();

	    // Setup mail server
	    props.put("mail.smtp.host", host);
	    System.out.println(host);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.user", user);
        System.out.println(user);
        props.put("mail.smtp.password", pwd);
        System.out.println(pwd);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        
        MailAuthenticator  authentication = new MailAuthenticator(user,pwd);
        javax.mail.Message message = new MimeMessage(Session.getDefaultInstance(props, authentication));
        
        
        
	     //Get session
	    //Session session = 
	     // Session.getInstance(props, null);

	    // Define message
	    message.setFrom(
	      new InternetAddress(from));
	    message.addRecipient(
	      Message.RecipientType.TO, 
	      new InternetAddress(to));
	    message.setSubject(
	      "Hello JavaMail Attachment");

	    // create the message part 
	    MimeBodyPart messageBodyPart = 
	      new MimeBodyPart();

	    //fill message
	    messageBodyPart.setText("Hi");

	    Multipart multipart = new MimeMultipart();
	    multipart.addBodyPart(messageBodyPart);

	    // Part two is attachment
	    messageBodyPart = new MimeBodyPart();
	    DataSource source = new ByteArrayDataSource(anexo,"application/pdf");	      
	    messageBodyPart.setDataHandler(new DataHandler( source));
	    messageBodyPart.setFileName("orcamento.pdf");
	    multipart.addBodyPart(messageBodyPart);

	    // Put parts in message
	    message.setContent(multipart);

	    // Send the message
	    Transport.send( message );
	  
	    return "Orçamento enviado";
		
		
	}

	public byte[] geraAnexo() {
		File arquivopdf = null;
		byte[] bytes = null; 

		try{
			//buscando caminho do arquivos de relatorios
			String sql0 = "SELECT caminho_relatorio, servidor_samba,usuario_samba,senha_samba from AD_Client where ad_client_id= " +
			Env.getAD_Client_ID(this.getCtx());
			PreparedStatement ps0 = DB.prepareStatement(sql0,"selectRelat");
			ResultSet rs0 = ps0.executeQuery();
			if (rs0.next()) {
				caminho = rs0.getString("caminho_relatorio");
	    		servidor = rs0.getString("servidor_samba");
	    		usuariosamba = rs0.getString("usuario_samba");
	    		senhasamba = rs0.getString("senha_samba");
			}

			DB.close(rs0,ps0);
			fechaTransacao("selectRelat");
			
			
			//hashtable de parametros que sao usados no relatorio
			Hashtable parametros = new Hashtable();

			//colocando o ID da venda no parametro do relatorio
			parametros.put("OrderID",id);
			//parametros.put("Caminho",caminho);

			// buscando id do tipo de documento
			String sql2 = "SELECT c_doctypetarget_id, documentno as codigo from c_order where c_order_id= " +
			id;
			String codigo="";
			PreparedStatement ps2 = DB.prepareStatement(sql2,"selectRelat");
			ResultSet rs2 = ps2.executeQuery();
			if (rs2.next()) {
				tipodoc = rs2.getInt("c_doctypetarget_id");
				codigo=rs2.getString("codigo");

			}
			DB.close(rs2,ps2);
			fechaTransacao("selectRelat");


			//buscando nome do arquivo do relatorio na tabela de tipo de documento
			String nomearquivo="orcamento_padrao_compra.jrxml";
			
			/*String sql1 = "SELECT arquivorelatorio FROM " +
			"c_doctype where c_doctype_id =" + tipodoc;
			PreparedStatement ps1 = DB.prepareStatement(sql1,"selectRelat");
			ResultSet rs1 = ps1.executeQuery();
			if (rs1.next()) {
				nomearquivo = rs1.getString("arquivorelatorio");
			}
			DB.close(rs1,ps1);
			fechaTransacao("selectRelat");
			*/
			
			
			//lendo o arquivo jrxml do relatorio

			//compilacao do arquivo
			URL u = null;
			JasperDesign jasperDesign=null;
			try {
				u = new URL(caminho+nomearquivo);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			try {
				jasperDesign = JRXmlLoader.load(u.openStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			System.out.println("QUERY " + jasperReport.getQuery().getText());
			//conexao ao banco
			Connection jdbcConnection = connectDB();

			//preenchendo o relatorio compilado com os parametros passados
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, jdbcConnection);


			// inicio trans windows
			bytes = JasperRunManager.runReportToPdf(jasperReport,parametros,jdbcConnection);
			
			
		}catch(Exception ex) {
			String connectMsg = "Could not create the report " + ex.getMessage() + " " + ex.getLocalizedMessage();
			System.out.println(connectMsg);
		}
		//	    salvando historico

		return bytes;


	}

	public static Connection connectDB() {
		Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		con = DB.getConnectionRW();

		//con = DriverManager.getConnection( "jdbc:postgresql://192.168.0.5:5432/palmetal2", "compiere", "compiere" );
		return con;
		//  return jdbcConnection;
	}


	public void salvaPDF2(byte[] bytes, String codigo, String nome, String ano1, String mes1) throws IOException {



		System.out.println("Enviando arquivo "+nome+"...");
        String sambaano = "smb://compiere:"+senhasamba+"@"+servidor+"/orcamentos/"+ano1;
        System.out.println(sambaano);
        SmbFile fano = new SmbFile( sambaano );
        //se pasta com ano nao existe entao ela eh criada
        if (!fano.exists())
      	  fano.mkdir();
        
        String sambames = "smb://compiere:"+senhasamba+"@"+servidor+"/orcamentos/"+ano1+"/" +mes1;
        System.out.println(sambames);
        SmbFile fmes = new SmbFile( sambames );
        //se pasta do mes nao existe entao ela eh criada
        if (!fmes.exists())
      	  fmes.mkdir();
        
        String samba = "smb://compiere:"+senhasamba+"@"+servidor+"/orcamentos/"+ano1+"/" +mes1+"/" + nome;
        System.out.println(samba);
        SmbFile f = new SmbFile( samba );
        
		SmbFileOutputStream out = new SmbFileOutputStream( f );
		
		long t0 = System.currentTimeMillis();
		byte[] b = new byte[8192];
		int n, tot = 0;
		out.write(bytes);
		out.close();

	}

	public void salvaPDF(byte[] bytes, String codigo, String nome, String ano1, String mes1) throws IOException {
		System.out.println("********** SalvaPDF2 *************");

        String samba = "smb://compiere:"+senhasamba+"@"+servidor+"/orcamentos/2013/12/teste_" + nome;
        System.out.println(samba);
        SmbFile f = new SmbFile( samba );
        
		SmbFileOutputStream out = new SmbFileOutputStream( f );
		long t0 = System.currentTimeMillis();
		byte[] b = new byte[8192];
		int n, tot = 0;
		out.write(bytes);
		out.close();

		System.out.println("********** FIM SalvaPDF2 *************");
	}
	
	
	/**
	 * Metodo que fecha a transacao.
	 * @param nomeTrx Nome da transacao.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	private void fechaTransacao(String nomeTrx) {
		try {
			DB.commit(false, nomeTrx);
			Trx trx = Trx.get(nomeTrx, false);
			if (trx!=null)
				trx.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Substitui caracteres com acento por tags HTML.
	 * @param titulo titulo do e-mail
	 * @return retorna o titulo com os caracteres modificados
	 */
	private String trocaHtml(String titulo, String pedido) {
		try {
			// á - &aacute;
			// é - &eacute;
			// í - &icute;
			// ó - &oacute;
			// ú - &uacute;
			// ç - &ccedil;
			// ã - &atilde;
			// õ - otilde;
			// ê - &ecirc;
			// â - &acirc;
			String texto = titulo.replaceAll("&Aacute;", "Á");
			texto = texto.replaceAll("&aacute;", "á");
			texto = texto.replaceAll("&Eacute;", "É");
			texto = texto.replaceAll("&eacute;", "é");
			texto = texto.replaceAll("&Iacute;", "Í");
			texto = texto.replaceAll("&iacute;", "í");
			texto = texto.replaceAll("&Oacute;", "Ó");
			texto = texto.replaceAll("&oacute;", "ó");
			texto = texto.replaceAll("&Uacute;", "Ú");
			texto = texto.replaceAll("&uacute;", "ú");
			texto = texto.replaceAll("&Ccedil;", "Ç");
			texto = texto.replaceAll("&ccedil;", "ç");
			texto = texto.replaceAll("&Atilde;", "Ã");
			texto = texto.replaceAll("&atilde;", "ã");
			texto = texto.replaceAll("&Otilde;", "Õ");
			texto = texto.replaceAll("&otilde;", "õ");
			texto = texto.replaceAll("&Ecirc;", "Ê");
			texto = texto.replaceAll("&ecirc;", "ê");
			return texto;
		} catch (Exception e) {
			//UtilsPalmetal.avisaErro("AutorizarPedido.buscaEstado(): Erro. Pedido " + pedido, UtilsPalmetal.stack2string(e));
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Metodo que recebe uma string com acentos e troca por codigos html
	 * @param titulo string a ser trocada
	 * @param pedido codigo do pedido - usado para rastreamento do processo
	 * @return string alterada
	 */
	private String trocaHtmlInverso(String titulo, String pedido) {
		try {
			// á - &aacute;
			// é - &eacute;
			// í - &icute;
			// ó - &oacute;
			// ú - &uacute;
			// ç - &ccedil;
			// ã - &atilde;
			// õ - otilde;
			// ê - &ecirc;
			// â - &acirc;
			String texto = titulo.replaceAll("Á","&Aacute;");
			texto = texto.replaceAll("á","&aacute;");
			texto = texto.replaceAll("é","&eacute;");
			texto = texto.replaceAll("É","&Eacute;");
			texto = texto.replaceAll("í","&iacute;");
			texto = texto.replaceAll("Í","&Iacute;");
			texto = texto.replaceAll("ó","&oacute;");
			texto = texto.replaceAll("Ó","&Oacute;");
			texto = texto.replaceAll("ú","&uacute;");
			texto = texto.replaceAll("Ú","&Uacute;");
			texto = texto.replaceAll("Ç","&Ccedil;");
			texto = texto.replaceAll("ç","&ccedil;");
			texto = texto.replaceAll("Ã","&Atilde;");
			texto = texto.replaceAll("ã","&atilde;");
			texto = texto.replaceAll("Õ","&Otilde;");
			texto = texto.replaceAll("õ","&otilde;");
			texto = texto.replaceAll("Ê","&Ecirc;");
			texto = texto.replaceAll("ê","&ecirc;");
			texto = texto.replaceAll("Ô","&Ocirc;");
			texto = texto.replaceAll("ô","&ocirc;");
			texto = texto.replaceAll("Â","&Acirc;");
			texto = texto.replaceAll("â","&acirc;");
			return texto;
		} catch (Exception e) {
			//UtilsPalmetal.avisaErro("AutorizarPedido.buscaEstado(): Erro. Pedido " + pedido, UtilsPalmetal.stack2string(e));
			e.printStackTrace();
		}
		return null;
	}

	class MailAuthenticator extends Authenticator {
          private String user;
          private String pwd;
		  
		  public MailAuthenticator() {
		  }

		  public MailAuthenticator(String user, String pwd) {
			  super();
			  this.user=user;
			  this.pwd=pwd;
			  
		  }
		  
		  public PasswordAuthentication getPasswordAuthentication() {
		    return new PasswordAuthentication(user, pwd);
		  }
	}
		  

}