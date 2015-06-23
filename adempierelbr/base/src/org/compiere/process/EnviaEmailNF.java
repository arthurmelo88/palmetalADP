package org.compiere.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.adempierelbr.model.MLBRNotaFiscal;
import org.compiere.model.MAttachment;
import org.compiere.model.MAttachmentEntry;
import org.compiere.model.MClient;
import org.compiere.model.MImage;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MProcess;
import org.compiere.util.DB;
import org.compiere.util.EMail;

public class EnviaEmailNF extends SvrProcess {
	private String NOMEARQUIVO="ImpressaoDanfeRetratoA4Report.jasper";
	/** JASPER */
	private InputStream danfe;
	private Map params = new HashMap();
	
	/** XML DataSource **/
	private JRXmlDataSource dataSource;

	/**XPATH */ 
	private String expressao="//NFe";


	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		try{
			ArrayList<NotaFiscalVO> al = new ArrayList<NotaFiscalVO>();
			System.out.println("======================== ENTROU NO EnviaEmailNF ===================");
			
			//pegando notas
			String sql = "select n.lbr_notafiscal_id, n.bpname, n.documentno as nf, c.documentno as pedido, p.emailnf as emailnf,n.datedoc as dataEmissao, n.lbr_NFeProt as protocolo,n.DateTrx as dataTransicao,n.lbr_NFeID as chaveAcesso,n.grandTotal as valorTotal from  LBR_NotaFiscal n, c_order c, c_bpartner p where n.EnviaEmail='Y' and n.processed='Y' and c.c_order_id=n.c_order_id and c.c_bpartner_id=p.c_bpartner_id and emailnf is not null and n.lbr_NFeDesc ilike '%Autorizado o uso da NF-e%'";
			PreparedStatement pst = DB.prepareStatement(sql, get_TrxName());
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				NotaFiscalVO vo = new NotaFiscalVO();
				
				vo.setId(rs.getString("lbr_notafiscal_id"));
				vo.setBpname(rs.getString("bpname"));
				vo.setNf(rs.getString("nf"));
				vo.setEmail(rs.getString("emailnf"));
				//vo.setEmail("rodolfosavieira@gmail.com");
				vo.setPedido(rs.getString("pedido"));
				vo.setChaveAcesso(rs.getString("chaveAcesso"));
				vo.setDataEmissao(rs.getString("dataEmissao"));
				vo.setProtocolo(rs.getString("protocolo"));
				vo.setDataTransicao(rs.getString("dataTransicao"));
				vo.setValorTotal(rs.getString("valorTotal"));
				
				al.add(vo);

			}

			//trocando os dados da mascara
			/**
			titulo = titulo.replaceAll("<<documentno>>", docno);
			conteudo = conteudo.replaceAll("<<documentno>>", docno);
			conteudo = conteudo.replaceAll("<<name>>", name);
			conteudo = conteudo.replaceAll("<<usuario>>", idusuario.getName());
			titulo = trocaHtml(titulo, mo.getDocumentNo());
			conteudo = trocaHtmlInverso(conteudo, mo.getDocumentNo());
			conteudo = trocaHtmlInverso(conteudo, mo.getDocumentNo());
			conteudo = conteudo.replaceAll("<<chave>>", (String)mo.get_Value("Chave"));
			**/

			

			for(int x=0;x<al.size();x++){
				System.out.println("======================== ITERACAO EnviaEmailNF ===================");
				NotaFiscalVO vo = al.get(x);
				
				byte[] anexo=null;
				String nomeArquivo=null;
				Integer idArquivo=null;
				
				//pegando anexos
				String sql2 = "select * from ad_attachment where ad_table_id=1000027 and record_id=" + vo.getId();
				PreparedStatement pst2 = DB.prepareStatement(sql2, get_TrxName());
				ResultSet rs2 = pst2.executeQuery();
				while (rs2.next()) {
					idArquivo = Integer.parseInt(rs2.getString("ad_attachment_id"));
					nomeArquivo = rs2.getString("title");
					anexo=rs2.getBytes("binarydata");
					
				}

				
				MClient mc = new MClient(getCtx(), 2000000, get_TrxName());
				String host = mc.getSMTPHost();
				String user = mc.getRequestUser();
				String pwd = mc.getRequestUserPW();
				System.out.println("HostEnviarNotaFiscal: [" + host + "] User: [" + user + "] pwd: [" + pwd + "]");


				//Criando Email
				EMail email = new EMail(getCtx(), host, user,vo.getEmail() , "Palmetal - Nota Fiscal Eletrônica N." + vo.getNf(), "", user, pwd);
				
				//Anexo, Cópia e Envia
				email.setMessageHTML(geraCorpoEmail2(vo.getBpname(),vo.getNf(), vo.getPedido(),vo.getDataEmissao(),vo.getValorTotal(),vo.getChaveAcesso(),vo.getProtocolo().concat(" "+vo.getDataTransicao())));

				
				email.addAttachment(anexo,"application/zip", "palmetal_notafiscal_xml."+nomeArquivo);
				email.addBcc("adm@palmetal.com.br");
				email.send();
				
				desmarcaEnviar(Integer.valueOf(vo.getId()));
					
			}
			
			System.out.println("======================== ACABOU O EnviaEmailNF ===================");
			return "";
		}
		catch(Exception e){
			e.printStackTrace();
			log.log (Level.SEVERE, "EnviaEmailNF", e);
		}
		return "";
	}
	
		

	
	private void desmarcaEnviar(int LBR_NotaFiscal_ID) throws Exception{
		
		String sql = "update LBR_NotaFiscal set enviaemail='N' where LBR_NotaFiscal_ID="+LBR_NotaFiscal_ID;
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());
		
	}

	private String geraCorpoEmail(String parceiro, String nf, String pedido){
		StringBuffer msg = new StringBuffer();
		
		msg.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'");
		msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		msg.append("<head>");
		msg.append("<title>Nota Fiscal Eletrônica</title>");
		msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		msg.append("<style type=\"text/css\">td img {display: block;}</style>");
		msg.append("<!--Fireworks CS5 Dreamweaver CS5 target.  Created Fri Jun 14 09:30:08 GMT-0300 (Hora oficial do Brasil) 2013-->");
		msg.append("</head>");
		msg.append("<body bgcolor=\"#ffffff\">");
		msg.append("<table style=\"display: inline-table;\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		msg.append("<!-- fwtable fwsrc=\"Untitled\" fwpage=\"Page 1\" fwbase=\"pesquisasatisfacao_palmetal.jpg\" fwstyle=\"Dreamweaver\" fwdocid = \"56928565\" fwnested=\"0\" -->");
		msg.append("  <tr>");
		msg.append("   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"407\" height=\"1\" border=\"0\" style=\"display: block;\" alt=\"\" /></td>");
		msg.append("   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"159\" height=\"1\" border=\"0\" style=\"display: block;\" alt=\"\" /></td>");
		msg.append("   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"34\" height=\"1\" border=\"0\" style=\"display: block;\" alt=\"\" /></td>");
		msg.append("   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"1\" height=\"1\" border=\"0\" style=\"display: block;\" alt=\"\" /></td>");
		msg.append("  </tr>");

		msg.append("  <tr>");
		msg.append("   <td colspan=\"3\" background=\"\" ><a href=\"http://www.palmetal.com.br/Palmetal/Home\"></a>Prezado(a) " + parceiro + ", <br>Segue em anexo sua nota fiscal eletrônica da Palmetal Metalurgica referente ao pedido " + pedido + ".<br>Nota Fiscal Eletrônica " + nf + "</td>");
		msg.append("   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"1\" height=\"54\" border=\"0\" style=\"display: block;\"  alt=\"\" /></td>");
		msg.append("  </tr>");
		
		msg.append("  <tr>");
		msg.append("   <td colspan=\"3\" background=\"\" ><a href=\"http://www.palmetal.com.br/Palmetal/Home\"></a>A Palmetal agradece pela parceria e queremos, assim que sua empresa precisar,<br> voltar a fazer excelentes negócios com os nossos móveis em aço inox.</td>");
		msg.append("   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"1\" height=\"54\" border=\"0\" style=\"display: block;\"  alt=\"\" /></td>");
		msg.append("  </tr>");
	
		msg.append("  <tr>");
		msg.append("   <td colspan=\"3\"><a href=\"http://www.palmetal.com.br/blog\"><img name=\"assinatura_nota_fiscal\" src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/assinatura_email_palmetal_2014_nfe.jpg\" width=\"500\" height=\"280\" border=\"0\" style=\"display: block;\" id=\"pesquisasatisfacao_palmetal_r1_c1\" alt=\"\" /></a></td>");
		msg.append("   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"1\" height=\"600\" border=\"0\" style=\"display: block;\"  alt=\"\" /></td>");
		msg.append("  </tr>");

		msg.append("</table>");
		msg.append("</body>");
		msg.append("</html>");
		
		return msg.toString();
	}
	private String geraCorpoEmail2(String parceiro, String nf, String pedido,String dataEmissao,String valorTotal,String chaveAcesso,String protocolo){
		StringBuffer msg = new StringBuffer();
		
		msg.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'");
		msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		msg.append("<head>");
		msg.append("<title>Nota Fiscal Eletrônica</title>");
		msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		msg.append("<style type=\"text/css\">td img {display: block;}</style>");
		msg.append("<!--Fireworks CS5 Dreamweaver CS5 target.  Created Fri Jun 14 09:30:08 GMT-0300 (Hora oficial do Brasil) 2013-->");
		msg.append("</head>");
		msg.append("<body bgcolor=\"#ffffff\">");
		msg.append("<table style=\"display: inline-table;\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		msg.append("<!-- fwtable fwsrc=\"Untitled\" fwpage=\"Page 1\" fwbase=\"pesquisasatisfacao_palmetal.jpg\" fwstyle=\"Dreamweaver\" fwdocid = \"56928565\" fwnested=\"0\" -->");
		msg.append("<tr>");

		msg.append("<td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"407\" height=\"1\" border=\"0\" style=\"display: block;\" alt=\"\" /></td>");
		msg.append("<td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"159\" height=\"1\" border=\"0\" style=\"display: block;\" alt=\"\" /></td>");
		msg.append("		   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"34\" height=\"1\" border=\"0\" style=\"display: block;\" alt=\"\" /></td>");
		msg.append("		   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"1\" height=\"1\" border=\"0\" style=\"display: block;\" alt=\"\" /></td>");
		msg.append("		  </tr>");

		msg.append("		<tr>");
		msg.append("			<td>");
		msg.append("			<a href=\"http://www.palmetal.com.br\">");
		msg.append("			<br>");
		msg.append("			<br>");
		msg.append("			<img style=\"border: 0px solid ; width: 150px; height: 53px;\" alt=\"\" src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/emails/logo.gif\"></a>");
		msg.append("			<br>");
		msg.append("			<br>");
		msg.append("			</td>");

		msg.append("		</tr>");
		msg.append("		  <tr>");
		msg.append("		   <td colspan=\"3\" background=\"\" ><a href=\"http://www.palmetal.com.br/Palmetal/Home\"></a><span style=\"style=\"font-weight: bold;\">Prezado(a) "+parceiro+", </span>");
		msg.append("			<br><br>Segue em anexo o arquivo eletrônico da Nota Fiscal Eletrônica N: "+nf+" referente ao pedido " + pedido + ",<br>");
		msg.append("			conforme disposto no \"Manual de Integração do Contribuinte\" da Nota Fiscal Eletrônica.");
		msg.append("		   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"1\" height=\"54\" border=\"0\" style=\"display: block;\"  alt=\"\" /></td>");
		msg.append("		  </tr>");
				
		msg.append("		<tr>");
		msg.append("			<td  colspan=\"3\" background=\"\">Caso tenha alguma dúvida, favor entrar em contato conosco.</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("			<td  colspan=\"3\" background=\"\">_______________________________________________________________________________</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("			<td  colspan=\"3\" background=\"\">Resumo da sua Nota Fiscal:");
		msg.append("			<br>- Número da Nota: "+nf);
		msg.append("			<br>- Data de emissão: "+dataEmissao);
		msg.append("			<br>- Valor Total: R$ "+valorTotal);
		msg.append("			<br><br>Informações de autorização da NF-e:");
		msg.append("			<br>- Chave de acesso: "+chaveAcesso);
		msg.append("			<br>- Protocolo: "+protocolo);
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("		  <tr>");
		msg.append("		   <td colspan=\"3\" background=\"\" ><a href=\"http://www.palmetal.com.br/Palmetal/Home\"></a>A Palmetal agradece pela parceria e queremos, assim que sua empresa precisar,<br> voltar a fazer excelentes negócios com os nossos móveis em aço inox.</td>");
		msg.append("		   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"1\" height=\"54\" border=\"0\" style=\"display: block;\"  alt=\"\" /></td>");
		msg.append("		  </tr>");
			
		msg.append("		  <tr>");
		msg.append("		   <td colspan=\"3\"><a href=\"http://www.palmetal.com.br/blog\"><img name=\"assinatura_nota_fiscal\" src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/assinatura_email_palmetal_2014_nfe.jpg\" width=\"500\" height=\"280\" border=\"0\" style=\"display: block;\" id=\"pesquisasatisfacao_palmetal_r1_c1\" alt=\"\" /></a></td>");
		msg.append("		   <td><img src=\"https://s3-us-west-2.amazonaws.com/palmetal-images/Pesquisa/Palmetal/atendimento/spacer.gif\" width=\"1\" height=\"600\" border=\"0\" style=\"display: block;\"  alt=\"\" /></td>");
		msg.append("		  </tr>");

		msg.append("		</table>");
		msg.append("		</body>");
		msg.append("		</html>");		
		return msg.toString();
	}

}
