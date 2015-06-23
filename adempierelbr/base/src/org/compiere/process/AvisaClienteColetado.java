package org.compiere.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MClient;
import org.compiere.model.MInvoice;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.MProduct;
import org.compiere.model.MShipper;
import org.compiere.model.MUser;
import org.compiere.util.DB;
import org.compiere.util.EMail;
import org.compiere.util.Env;
import org.compiere.util.Trx;

public class AvisaClienteColetado extends SvrProcess{

	private int id = 0; 

	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("C_Invoice_ID"))
				id = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
		//id = getRecord_ID();
		//id = 3020362
	}	
	
	protected String doIt() throws Exception{		
		System.out.println("+++++++++++++++++++ ID: " + id);
		MInvoice mo = new MInvoice(getCtx(), id, "criaMI");
		fechaTransacao("criaMI");
		
		//verificando campos obrigatorios
		MOrder order = null;
		if (mo.getC_Order_ID()!=0) {
			order = new MOrder(getCtx(), mo.getC_Order_ID(), "criaOrder");

			fechaTransacao("criaOrder");
		}
		
		avisaCliente(mo, order);
		
		return "";
		
	}
	
	
	public void avisaCliente (MInvoice invoice, MOrder mo) {

		try{
			System.out.println("======================== ENTROU NO AvisaClienteColetado avisaCliente===================");
			String titulo = "Email";
			String conteudo = "Conteudo";

			String docno = "";
			if (mo!=null)
				docno = mo.getDocumentNo();
			//dados entrega
			int parcentr = 0;
			int locentr = 0;
			int userentr = 0;

			if (mo.get_Value("Dropship_BPartner_ID")!=null)
				parcentr = (Integer)mo.get_Value("Dropship_BPartner_ID");
			if (mo.get_Value("Dropship_Location_ID")!=null)
				locentr = (Integer)mo.get_Value("Dropship_Location_ID");
			if (mo.get_Value("Dropship_User_ID")!=null)
				userentr = (Integer)mo.get_Value("Dropship_User_ID");

			MBPartner pentr=null;
			MBPartnerLocation lentr=null;
			MUser uentr=null;
			MBPartner pfat;
			MBPartnerLocation lfat;
			MUser ufat;

			MLocation location = null;

			//objetos com dados da entrega
			if (parcentr!=0) {
				pentr = new MBPartner(getCtx(), parcentr, "criaMB");
				fechaTransacao("criaMB");
			}
			if (locentr!=0) {
				lentr = new MBPartnerLocation(getCtx(), locentr, "criaMBLoc");
				fechaTransacao("criaMBLoc");
				location = new MLocation(getCtx(), lentr.getC_Location_ID(), "CriarLocation");
				fechaTransacao("CriarLocation");
			}
			if (userentr!=0) {
				uentr = new MUser(getCtx(), userentr,  "criaMU");
				fechaTransacao("criaMU");
			}
			//objetos com dados do faturamento
			pfat = new MBPartner(getCtx(), mo.getBill_BPartner_ID(),  "criaMB2");
			fechaTransacao("criaMB2");
			lfat = new MBPartnerLocation(getCtx(), mo.getBill_Location_ID(), "criaMBLoc2");
			fechaTransacao("criaMBLoc2");
			ufat = new MUser(getCtx(), mo.getBill_User_ID(), "criaMU2");
			fechaTransacao("criaMU2");
			MLocation locationF = new MLocation(getCtx(), lfat.getC_Location_ID(), "CriarLocationF");
			fechaTransacao("CriarLocationF");


			MUser mu = new MUser(getCtx(),mo.getAD_User_ID(), "criaMU3");
			fechaTransacao("criaMU3");
			String name = mu.getName();
			String emailuser = mu.getEMail();
			System.out.println("EMAIL: " + emailuser);
			MUser idusuario = new MUser(getCtx(), Env.getAD_User_ID(getCtx()), "criaMU4");
			fechaTransacao("criaMU4");

			//pegando template
			int idtemplate = 0;			
			String sql = "SELECT * FROM x_templateemail WHERE tipo='PE' ORDER BY RANDOM()";
			PreparedStatement pst = DB.prepareStatement(sql, "selectTemplateEmail");
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {

				titulo = rs.getString("titulo");
				conteudo = rs.getString("conteudo");
				idtemplate = rs.getInt("x_templateemail_id");				

			}
			DB.close(rs, pst);
			fechaTransacao("selectTemplateEmail");
			
			String frete = "";
			if (mo!=null) {
				String tipofrete = (String)mo.get_Value("DeliveryViaRule");


				if (tipofrete.equals("D")) {
					frete = "CIF";
				}
				else {
					frete = "FOB";
				}
			}
			//pegando template das linhas
			String contlinha = "";
			String sql1 = "SELECT * FROM x_templateemaillinha WHERE x_templateemail_id="+ idtemplate +" and isactive='Y' ORDER BY RANDOM()";
			PreparedStatement pst1 = DB.prepareStatement(sql1, "selectTemplateEmailLinha");
			ResultSet rs1 = pst1.executeQuery();
			if (rs1.next()) {
				contlinha = rs1.getString("conteudo");
			}
			DB.close(rs1, pst1);
			fechaTransacao("selectTemplateEmailLinha");			
			
			String transp = "";
			String telefone = "";
			String email2 = "";
			
			MShipper ms = new MShipper(Env.getCtx(), (Integer)invoice.get_Value("Transportadora_ID"), "criaShipper");
			fechaTransacao("criaShipper");
			int id_transportadora = (Integer)ms.get_Value("C_BPartner_ID");
			MBPartner mp = new MBPartner(Env.getCtx(), (Integer)ms.get_Value("C_BPartner_ID"), "criaPartner");			
			fechaTransacao("criaPartner");
			
			if (id_transportadora != 3001170){
				transp = mp.getName();
				telefone = getTelefoneTransportadora(id_transportadora);
				email2 = getEmailTransportadora(id_transportadora);
			}
			else {
				//Transportadora = 'O PRÓPRIO', nesse caso é o cliente quem vai cuidar da coleta do produto
				transp = mp.getName();
				if(lentr != null){
					telefone = lentr.get_Value("DDD").toString() + " - " + lentr.get_Value("Phone").toString();
				}
				if(uentr != null){
					email2 = (String)uentr.get_Value("Email");
				}
			}

			String conhecimento = "";

			if (invoice.get_Value("Coleta")!=null)
				conhecimento = invoice.get_Value("Coleta").toString();

			String pedidoref = "";
			if (mo!=null)
				pedidoref = mo.getPOReference();
			if (pedidoref==null)
				pedidoref = "";

			//trocando os dados da mascara
			titulo = titulo.replaceAll("<<pedido>>", docno);
			conteudo = conteudo.replaceAll("<<pedido>>", docno);
			conteudo = conteudo.replaceAll("<<pedidocompra>>", pedidoref);
			conteudo = conteudo.replaceAll("<<numero_nota>>", (String)invoice.get_Value("NotaPalmetal"));
			conteudo = conteudo.replaceAll("<<valor_total>>", invoice.getGrandTotal().setScale(2,BigDecimal.ROUND_HALF_UP).toString());
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		    Date dataAtual = new Date(System.currentTimeMillis());
			conteudo = conteudo.replaceAll("<<data_coleta>>", sd.format(dataAtual).toString());
			DateFormat df = new SimpleDateFormat("HH:mm");  
			String hora = df.format(new Date());  
			conteudo = conteudo.replaceAll("<<hora_coleta>>", hora);
			conteudo = conteudo.replaceAll("<<conhecimento>>", trocaHtml(conhecimento, mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<transportadora>>", trocaHtml(transp, mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<telefone_transportadora>>", telefone);
			conteudo = conteudo.replaceAll("<<email_transportadora>>", email2);
			conteudo = conteudo.replaceAll("<<frete>>", frete);
			conteudo = conteudo.replaceAll("<<nome>>", trocaHtml(name, mo.getDocumentNo()));

			MOrderLine[] mil = mo.getLines();
			String str = "";
			BigDecimal qtdeprods = BigDecimal.ZERO;
			for (int i=0; i<mil.length; i++) {
				MOrderLine mi = mil[i];
				MProduct mp2 = mi.getProduct();
				String strlinha = contlinha;				
				//pegando o campo comprimento do pedido
				String strcomp = "";
				Object comprimento = mi.get_Value("Comprimento");
				if (comprimento!=null) {
					BigDecimal valorcomp = (BigDecimal)mi.get_Value("Comprimento");
					if (valorcomp.compareTo(BigDecimal.ZERO)>0) {
						strcomp = " Comprimento: " + valorcomp.multiply(new BigDecimal(1000)).intValue() + " mm ";
					}
				}
				qtdeprods = qtdeprods.add(mi.getQtyEntered());
				strlinha = strlinha.replaceAll("<<noitem>>", String.valueOf(mi.getLine()/10));
				strlinha = strlinha.replaceAll("<<nomeproduto>>", mp2.getName());
				strlinha = strlinha.replaceAll("<<codigoproduto>>", mp2.getValue());
				strlinha = strlinha.replaceAll("<<desclinha>>", mi.getDescription());
				strlinha = strlinha.replaceAll("<<qtde>>", String.valueOf(mi.getQtyEntered()));
				strlinha = strlinha.replaceAll("<<valorunitario>>", String.valueOf(mi.getPriceEntered().setScale(2, BigDecimal.ROUND_UP)));
				strlinha = strlinha.replaceAll("<<totallinha>>", String.valueOf(mi.getPriceEntered().multiply(mi.getQtyEntered()).setScale(2, BigDecimal.ROUND_UP)));
				
				str += strlinha;
				//str += "\nDescrição: " + mi.getDescription() + " " + strcomp + " Qtde: " + mi.getQtyEntered();

			}
			conteudo = conteudo.replaceAll("<<linhas>>", str);
			conteudo = conteudo.replaceAll("<<valorfrete>>", String.valueOf(mo.getFreightAmt().setScale(2, BigDecimal.ROUND_UP)));
			conteudo = conteudo.replaceAll("<<totalpedido>>", String.valueOf(mo.getGrandTotal().setScale(2, BigDecimal.ROUND_UP)));
			conteudo = conteudo.replaceAll("<<qtdeprods>>", String.valueOf(qtdeprods));
			conteudo = conteudo.replaceAll("<<formapgto>>", (new MPaymentTerm(getCtx(),mo.getC_PaymentTerm_ID(), "newPayment").getName()));
			fechaTransacao("newPayment");
			if(pentr != null)
			conteudo = conteudo.replaceAll("<<parcentr>>", pentr.getName());

			//dados faturamento
			if (!pfat.get_ValueAsString("lbr_Cnpj").equals(""))
				conteudo = conteudo.replaceAll("<<cnpj>>", pfat.get_ValueAsString("lbr_Cnpj"));
			else
				conteudo = conteudo.replaceAll("<<cnpj>>", pfat.get_ValueAsString("lbr_CPF"));
			conteudo = conteudo.replaceAll("<<ie>>", trocaHtml(pfat.get_ValueAsString("lbr_ie"), mo.getDocumentNo()));
			//conteudo = conteudo.replaceAll("<<im>>", trocaHtml(pfat.get_ValueAsString("Inscr_Municipal"), mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<rsocial_faturamento>>", trocaHtml(pfat.getName2(), mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<endereco_faturamento>>", trocaHtml(locationF.get_ValueAsString("address1"), mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<numero_faturamento>>", trocaHtml(locationF.get_ValueAsString("address2"), mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<complemento_faturamento>>", trocaHtml(locationF.get_ValueAsString("address4"), mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<bairro_faturamento>>", trocaHtml(locationF.get_ValueAsString("address3"), mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<cidade_faturamento>>", trocaHtml(buscaCidade(locationF.get_Value("C_City_ID")), mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<uf_faturamento>>", buscaEstado(locationF.get_Value("C_Region_ID")));
			conteudo = conteudo.replaceAll("<<cep_faturamento>>", locationF.get_ValueAsString("Postal"));
			conteudo = conteudo.replaceAll("<<telefone_faturamento>>", ufat.get_ValueAsString("Phone"));
			conteudo = conteudo.replaceAll("<<contato_faturamento>>", trocaHtml(ufat.getName(), mo.getDocumentNo()));

			//dados entrega
			if (lentr!=null) {
				conteudo = conteudo.replaceAll("<<endereco_entrega>>", trocaHtml(location.get_ValueAsString("address1"), mo.getDocumentNo()));
				conteudo = conteudo.replaceAll("<<numero_entrega>>", location.get_ValueAsString("address2"));
				conteudo = conteudo.replaceAll("<<complemento_entrega>>", trocaHtml(location.get_ValueAsString("address4"), mo.getDocumentNo()));
				conteudo = conteudo.replaceAll("<<bairro_entrega>>", trocaHtml(location.get_ValueAsString("address3"), mo.getDocumentNo()));
				conteudo = conteudo.replaceAll("<<cidade_entrega>>", trocaHtml(buscaCidade(location.get_Value("C_City_ID")), mo.getDocumentNo()));
				conteudo = conteudo.replaceAll("<<uf_entrega>>", buscaEstado(location.get_Value("C_Region_ID")));
				conteudo = conteudo.replaceAll("<<cep_entrega>>", location.get_ValueAsString("Postal"));
			} else {
				conteudo = conteudo.replaceAll("<<endereco_entrega>>", "");
				conteudo = conteudo.replaceAll("<<numero_entrega>>", "");
				conteudo = conteudo.replaceAll("<<complemento_entrega>>", "");
				conteudo = conteudo.replaceAll("<<bairro_entrega>>", "");
				conteudo = conteudo.replaceAll("<<cidade_entrega>>", "");
				conteudo = conteudo.replaceAll("<<uf_entrega>>", "");
				conteudo = conteudo.replaceAll("<<cep_entrega>>", "");
			}
			if (uentr!=null) {
				conteudo = conteudo.replaceAll("<<telefone_entrega>>", uentr.get_ValueAsString("Phone"));
				conteudo = conteudo.replaceAll("<<contato_entrega>>", trocaHtml(uentr.getName(), mo.getDocumentNo()));
			}
			else {
				conteudo = conteudo.replaceAll("<<telefone_entrega>>","");
				conteudo = conteudo.replaceAll("<<contato_entrega>>", "");				
			}


			//titulo = trocaHtml(titulo);
			conteudo = trocaHtmlInverso(conteudo,"");
			//conteudo = UtilsPalmetal.trocaHtml(conteudo,"");

			System.out.println(conteudo);

			//pegar confs do servidor de email
			MClient mc = new MClient(getCtx(), Env.getAD_Client_ID(getCtx()), "criaMClient");
			fechaTransacao("criaMClient");
			String host = mc.getSMTPHost();
			String user = mc.getRequestUser();
			String pwd = mc.getRequestUserPW();

			//pegar usuário do pedido
			//emailuser
			EMail email = new EMail(getCtx(), host, user, emailuser, titulo, conteudo, user, pwd); // Contato do Pedido

			System.out.println("ufat.getEMail()" + ufat.getEMail());
			System.out.println("uentr.getEMail()" + uentr.getEMail());
			if (!ufat.getEMail().equals(emailuser))
				email.addTo(ufat.getEMail());
				//emailuser += "; " + ufat.getEMail(); // Contato do Faturamento
			if (uentr!=null && uentr.getEMail().equals(emailuser) && !uentr.getEMail().equals(ufat.getEMail()))
				email.addTo(uentr.getEMail());
				//emailuser += "; " + uentr.getEMail();
			email.addBcc("palmetal@palmetal.com.br");
			email.setMessageHTML(conteudo);
			email.send();

		}catch(Exception ex) {
			System.out.println("======================== Erro NO AvisaClienteColetado avisaCliente===================");
			ex.printStackTrace();
			String connectMsg = "Erro ao avisar coleta para cliente da nota " + invoice.getDocumentNo() + " " + ex.getLocalizedMessage();
			System.out.println(connectMsg);
			
		}
		System.out.println("======================== FIM NO AvisaClienteColetado avisaCliente===================");
	}
	
	
	private String diasUteis(String data, int dias) throws SQLException { 
		// recebe data no formato yyyy-mm-dd

		System.out.println("DATA: " + data);
		
		String[] args = data.split("-");
		int mes = Integer.parseInt(args[1]) - 1;
		int ano = Integer.parseInt(args[0]);
		int dia = Integer.parseInt(args[2]) + 1;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(ano, mes, dia);  
		int i = 1;
		DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
		while (i<=dias) {
			if (calendar.get(Calendar.DAY_OF_WEEK ) == Calendar.SUNDAY ) {  
				// System.out.println(df.format (calendar.getTime()) + " DOMINGO");  
				calendar.add(Calendar.DATE, 1);
			}
			else if (calendar.get(Calendar.DAY_OF_WEEK ) == Calendar.SATURDAY) {  
				//System.out.println(df.format (calendar.getTime()) + " S�BADO");
				calendar.add(Calendar.DATE, 1);
			}
			/*else if (ehFeriado(calendar)) {  
				//System.out.println(df.format (calendar.getTime()) + " FERIADO");
				calendar.add(Calendar.DATE, 1);
			}*/
			else {
				// System.out.println(df.format (calendar.getTime()) + " DIA �TIL");
				i++;
				if (i<=dias)
					calendar.add(Calendar.DATE, 1);
			}
			System.out.println("DATA " + calendar.getTime());
			System.out.println ("DATA FINAL: " +df.format (calendar.getTime()));  
		}


		System.out.println("DATA " + calendar.getTime());
		System.out.println ("DATA FINAL: " +df.format (calendar.getTime()));  
		return df.format(calendar.getTime());

	}

	private boolean ehFeriado(Calendar cal) throws SQLException {

		int ano = cal.get(Calendar.YEAR);
		Calendar calendar = null;  
		DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");  
		ArrayList<Calendar> feriados = new ArrayList<Calendar>();
		String sql = "select date_part('year'::text, date1) as ano, date_part('month'::text, date1) as mes, date_part('day'::text, date1) as dia from c_nonbusinessday where isactive='Y' and date_part('year'::text, date1)=" + ano;
		PreparedStatement pst = DB.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		
		while (rs.next()) {
			calendar = Calendar.getInstance();
			calendar.set(Integer.parseInt(rs.getString("ano")), Integer.parseInt(rs.getString("mes"))-1, Integer.parseInt(rs.getString("dia"))); 
			feriados.add(calendar);
		}

		boolean retorno = false;

		for (int i=0; i<feriados.size(); i++) {
			Calendar cal2 = feriados.get(i);
			//System.out.print ("COMPARANDO: " +df.format(cal.getTime()) + " - " + df.format(cal2.getTime()));  
			if (df.format(cal.getTime()).equals(df.format(cal2.getTime()))) {
				//System.out.println(" TRUE ");
				return true; 
			}
			else {
				//System.out.println(" FALSE ");
			}
		}
		return retorno;
	}

	/**
	 * Busca o nome da cidade
	 * @param id objeto id
	 * @return nome da cidade
	 * @throws SQLException
	 */
	private String buscaCidade(Object id) throws SQLException {
		try {//
			if (id!=null) {

				int i = (Integer)id;
				String sql = "SELECT name from c_city where c_city_id=" + i;
				System.out.println(sql);
				PreparedStatement pst = DB.prepareStatement(sql, "buscaCidade");
				ResultSet rs = pst.executeQuery();
				if (rs.next()) {
					String cidade  = rs.getString("name");
					//UtilsPalmetal.fechaTransacao("buscaCidade");
					return cidade;

				}
				//UtilsPalmetal.fechaTransacao("buscaCidade");
				DB.close(rs, pst);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//UtilsPalmetal.avisaErro("AutorizarPedido.buscaCidade(): Erro de sql. Pedido " + pedido, UtilsPalmetal.stack2string(e));
		} catch (Exception e) {
			//UtilsPalmetal.avisaErro("AutorizarPedido.buscaCidade(): Erro. Pedido " + pedido, UtilsPalmetal.stack2string(e));
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Busca o nome do estado.
	 * @param id objeto id
	 * @return nome do estado
	 * @throws SQLException
	 */
	private String buscaEstado(Object id) throws SQLException {
		try {
			if (id!=null) {

				int i = (Integer)id;
				String sql = "SELECT name from c_region where c_region_id=" + i;
				System.out.println(sql);
				PreparedStatement pst = DB.prepareStatement(sql, "buscaEstado");
				ResultSet rs = pst.executeQuery();
				if (rs.next()) {
					String estado  = rs.getString("name");
					//UtilsPalmetal.fechaTransacao("buscaEstado");
					return estado;
				}
				DB.close(rs, pst);
				//UtilsPalmetal.fechaTransacao("buscaEstado");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//UtilsPalmetal.avisaErro("AutorizarPedido.buscaEstado(): Erro de sql. Pedido " + pedido, UtilsPalmetal.stack2string(e));
		} catch (Exception e) {
			e.printStackTrace();
			//UtilsPalmetal.avisaErro("AutorizarPedido.buscaEstado(): Erro. Pedido " + pedido, UtilsPalmetal.stack2string(e));
		}

		return null;
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

	private String getEmailTransportadora(int idParceiro) throws Exception{
		String retorno="";
		String sql1 = "Select * from ad_user where c_bpartner_id=" + idParceiro;
		PreparedStatement pst1 = DB.prepareStatement(sql1, "selectEmailTransp");
		ResultSet rs1 = pst1.executeQuery();
		if (rs1.next()) {
			retorno = rs1.getString("email");
		}
		DB.close(rs1, pst1);
		fechaTransacao("selectEmailTransp");		
		
		return retorno;
	}
	
	private String getTelefoneTransportadora(int idParceiro) throws Exception{
		String retorno="";
		String sql1 = "Select * from c_bpartner_location where c_bpartner_id=" + idParceiro;
		PreparedStatement pst1 = DB.prepareStatement(sql1, "selectTelTransp");
		ResultSet rs1 = pst1.executeQuery();
		if (rs1.next()) {
			retorno = rs1.getString("DDD") + " - " + rs1.getString("Phone");
		}
		DB.close(rs1, pst1);
		fechaTransacao("selectTelTransp");		
		
		return retorno;
	}
	
}
