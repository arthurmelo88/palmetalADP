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
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.MProduct;
import org.compiere.model.MUser;
import org.compiere.util.DB;
import org.compiere.util.EMail;
import org.compiere.util.Env;
import org.compiere.util.Trx;

public class AvisaClienteAutorizado extends SvrProcess{

	private int id = 0; 
	private String situacao="CL";

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
			else if (name.equals("Situacao"))
				situacao = (String)para[i].getParameter();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
		//id = getRecord_ID();
	}	
	
	protected String doIt() throws Exception{		
		
		MOrder mo = new MOrder(getCtx(), id, "criarObjMO");
		fechaTransacao("criarObjMO");
		
		avisaCliente(mo, situacao);
		
		return "";
		
	}
	
	private void avisaCliente (MOrder mo, String statusped) {

		try{

			String titulo = "Email";
			String conteudo = "Conteúdo";

			String docno = mo.getDocumentNo();
			//dados entrega
			int parcentr = 0;
			int locentr = 0;
			int userentr = 0;
			int prazo = Integer.valueOf((String)mo.get_Value("Prazo"));
			
			Date hoje = new Date();			
			SimpleDateFormat formatador = new SimpleDateFormat("yyyy-MM-dd");
			String dataatual = formatador.format(hoje);

			String dataprazo = diasUteis(dataatual, Integer.parseInt(String.valueOf(prazo)));

			if (mo.get_Value("Dropship_BPartner_ID")!=null)
				parcentr = (Integer)mo.get_Value("Dropship_BPartner_ID");
			if (mo.get_Value("Dropship_Location_ID")!=null)
				locentr = (Integer)mo.get_Value("Dropship_Location_ID");
			if (mo.get_Value("Dropship_User_ID")!=null)
				userentr = (Integer)mo.get_Value("Dropship_User_ID");

			MBPartner pentr=null, pfat=null;
			MBPartnerLocation lentr=null, lfat=null;
			MUser uentr=null,ufat=null;

			MLocation location = null;
			//objetos com dados da entrega
			if (parcentr!=0) {
				pentr = new MBPartner(getCtx(), parcentr, "criaBPartner");
				fechaTransacao("criaBPartner");
			}
			if (locentr!=0) {
				lentr = new MBPartnerLocation(getCtx(), locentr,  "criaBPartnerLocation");
				fechaTransacao("criaBPartnerLocation");
				location = new MLocation(getCtx(), lentr.getC_Location_ID(), "CriarLocation");
				fechaTransacao("CriarLocation");
			}
			if (userentr!=0) {
				uentr = new MUser(getCtx(), userentr,  "criaUser");
				fechaTransacao("criaUser");
			}
			//objetos com dados do faturamento
			pfat = new MBPartner(getCtx(), mo.getBill_BPartner_ID(),  "criaBPartner");
			fechaTransacao("criaBPartner");
			lfat = new MBPartnerLocation(getCtx(), mo.getBill_Location_ID(),  "criaBPartnerLocation");
			fechaTransacao("criaBPartnerLocation");
			MLocation locationF = new MLocation(getCtx(), lfat.getC_Location_ID(), "CriarLocationF");
			fechaTransacao("CriarLocationF");
			
			ufat = new MUser(getCtx(), mo.getBill_User_ID(),  "criaUser");
			fechaTransacao("criaUser");


			MUser mu = new MUser(getCtx(), mo.getAD_User_ID(),  "criaUser2");
			fechaTransacao("criaUser2");


			String name = "";
			//if (mu.get_Value("NomeReduzido")!=null)
				//name = (String)mu.get_Value("NomeReduzido");
			//else
				name = mu.getName();
			String emailuser = mu.getEMail();
			System.out.println("EMAIL: " + emailuser);
			MUser idusuario = new MUser(getCtx(), Env.getAD_User_ID(getCtx()),  "criaUsuario");
			fechaTransacao("criaUsuario");

			//pegando template
			int idtemplate = 0;
			String sql = "SELECT * FROM x_templateemail WHERE tipo='AU' and isactive='Y' ORDER BY RANDOM()";
			PreparedStatement pst = DB.prepareStatement(sql,  "criaTemplate");

			ResultSet rs = pst.executeQuery();
			if (rs.next()) {

				titulo = rs.getString("titulo");
				conteudo = rs.getString("conteudo");
				idtemplate = rs.getInt("x_templateemail_id");

			}
			DB.close(rs, pst);
			fechaTransacao("criaTemplate");

			//pegando template das linhas
			String contlinha = "";
			String sql1 = "SELECT * FROM x_templateemaillinha WHERE x_templateemail_id="+ idtemplate +" and isactive='Y' ORDER BY RANDOM()";
			PreparedStatement pst1 = DB.prepareStatement(sql1,  "criaTemplate2");

			ResultSet rs1 = pst1.executeQuery();
			if (rs1.next()) {

				contlinha = rs1.getString("conteudo");

			}
			fechaTransacao("criaTemplate2");
			String tipofrete = (String)mo.get_Value("DeliveryViaRule");
			String transp = "";
			String telefone = "";
			String email2 =  "";
			String frete = "";
			/**
			if (tipofrete.equals("D")) {
				if (mo.get_Value("Transportadora")!=null) {

					MBPartner mp = new MBPartner(Env.getCtx(), (Integer)mo.get_Value("Transportadora"),  "criaBPartner");
					fechaTransacao("criaBPartner");
					transp = mp.getName();
					telefone = mp.get_Value("DDD").toString() + " - " + mp.get_Value("Telefone").toString();
					email2 = (String)mp.get_Value("Email");
				}

				frete = "CIF";
			}
			else {
				frete = "FOB";
			}

			**/

			SimpleDateFormat dtfGeral = new java.text.SimpleDateFormat("dd/mm/yyyy");

			String pedidoref = mo.getPOReference();

			if (pedidoref==null)
				pedidoref = "";

			//trocando os dados da mascara
			titulo = titulo.replaceAll("<<pedido>>", mo.getDocumentNo());
			titulo = titulo.replaceAll("<<pedidocompra>>", pedidoref);
			conteudo = conteudo.replaceAll("<<pedido>>", mo.getDocumentNo());
			conteudo = conteudo.replaceAll("<<pedidocompra>>", pedidoref);
			conteudo = conteudo.replaceAll("<<valor_total>>", mo.getGrandTotal().setScale(2,BigDecimal.ROUND_HALF_UP).toString());
			conteudo = conteudo.replaceAll("<<transportadora>>", transp);
			conteudo = conteudo.replaceAll("<<frete>>", frete);
			conteudo = conteudo.replaceAll("<<telefone_transportadora>>", telefone);
			conteudo = conteudo.replaceAll("<<email_transportadora>>", email2);
			if (statusped.equals("RT"))
				conteudo = conteudo.replaceAll("<<status>>", "Em Faturamento");
			else
				conteudo = conteudo.replaceAll("<<status>>", "Em Fabricação");

			conteudo = conteudo.replaceAll("<<nome>>", name);

			MOrderLine[] mil = mo.getLines();
			String str = "";
			BigDecimal qtdeprods = BigDecimal.ZERO;

			for (int i=0; i<mil.length; i++) {
				MOrderLine mi = mil[i];
				MProduct mp = mi.getProduct();
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
				strlinha = strlinha.replaceAll("<<nomeproduto>>", mp.getName());
				strlinha = strlinha.replaceAll("<<codigoproduto>>", mp.getValue());
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
			conteudo = conteudo.replaceAll("<<formapgto>>", (new MPaymentTerm(getCtx(),mo.getC_PaymentTerm_ID(), "avisaCliente").getName()));
			fechaTransacao("avisaCliente");
			conteudo = conteudo.replaceAll("<<parcentr>>", pentr.getName());


			//dados faturamento
			if (!pfat.get_ValueAsString("lbr_Cnpj").equals(""))
				conteudo = conteudo.replaceAll("<<cnpj>>", pfat.get_ValueAsString("lbr_Cnpj"));
			else
				conteudo = conteudo.replaceAll("<<cnpj>>", pfat.get_ValueAsString("lbr_CPF"));
			conteudo = conteudo.replaceAll("<<ie>>", pfat.get_ValueAsString("lbr_ie"));
			//conteudo = conteudo.replaceAll("<<im>>", pfat.get_ValueAsString("Inscr_Municipal"));
			conteudo = conteudo.replaceAll("<<rsocial_faturamento>>", pfat.getName2());
			conteudo = conteudo.replaceAll("<<endereco_faturamento>>", locationF.get_ValueAsString("address1"));
			conteudo = conteudo.replaceAll("<<numero_faturamento>>", locationF.get_ValueAsString("address2"));
			conteudo = conteudo.replaceAll("<<complemento_faturamento>>", locationF.get_ValueAsString("address4"));
			conteudo = conteudo.replaceAll("<<bairro_faturamento>>", locationF.get_ValueAsString("address3"));
			conteudo = conteudo.replaceAll("<<cidade_faturamento>>", buscaCidade(locationF.get_Value("C_City_ID"), mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<uf_faturamento>>", buscaEstado(locationF.get_Value("C_Region_ID"), mo.getDocumentNo()));
			conteudo = conteudo.replaceAll("<<cep_faturamento>>", locationF.get_ValueAsString("Postal"));
			//conteudo = conteudo.replaceAll("<<telefone_faturamento>>", ufat.get_ValueAsString("DDD") + " " + ufat.get_ValueAsString("Phone"));
			conteudo = conteudo.replaceAll("<<telefone_faturamento>>", ufat.get_ValueAsString("Phone"));
			conteudo = conteudo.replaceAll("<<contato_faturamento>>", ufat.getName());

			//dados entrega
			if (lentr!=null) {
				conteudo = conteudo.replaceAll("<<data_prazo>>", dataprazo);
				conteudo = conteudo.replaceAll("<<endereco_entrega>>", location.get_ValueAsString("address1")); //rua
				conteudo = conteudo.replaceAll("<<numero_entrega>>", location.get_ValueAsString("address2")); //numero
				conteudo = conteudo.replaceAll("<<complemento_entrega>>", location.get_ValueAsString("address4")); //complemento
				conteudo = conteudo.replaceAll("<<bairro_entrega>>", location.get_ValueAsString("address3")); //bairro
				conteudo = conteudo.replaceAll("<<cidade_entrega>>", buscaCidade(location.get_Value("C_City_ID"), mo.getDocumentNo()));
				conteudo = conteudo.replaceAll("<<uf_entrega>>", buscaEstado(location.get_Value("C_Region_ID"), mo.getDocumentNo()));
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
				//conteudo = conteudo.replaceAll("<<telefone_entrega>>", ufat.get_ValueAsString("DDD") + " " + uentr.get_ValueAsString("Phone"));
				conteudo = conteudo.replaceAll("<<telefone_entrega>>", uentr.get_ValueAsString("Phone"));
				conteudo = conteudo.replaceAll("<<contato_entrega>>", uentr.getName());
			}
			else {
				conteudo = conteudo.replaceAll("<<telefone_entrega>>","");
				conteudo = conteudo.replaceAll("<<contato_entrega>>", "");				
			}

			titulo = trocaHtml(titulo, mo.getDocumentNo());
			conteudo = trocaHtmlInverso(conteudo, mo.getDocumentNo());
			//conteudo = UtilsPalmetal.trocaHtml(conteudo, mo.getDocumentNo());


			System.out.println(conteudo);
			Trx[] trxs = Trx.getActiveTransactions();
			System.out.println("Transacoes Ativas Finais " + trxs.length);
			for (int ct=0; ct<trxs.length; ct++) {
				Trx t = trxs[ct];
				System.out.println("Nome trx " + t.getTrxName());


			}

			//pegar confs do servidor de email
			MClient mc = new MClient(getCtx(), Env.getAD_Client_ID(getCtx()),  "criaEmail");
			fechaTransacao("criaEmail");
			
			String host = mc.getSMTPHost();
			String user = mc.getRequestUser();
			String pwd = mc.getRequestUserPW();

			//pegar usuário do pedido
			EMail email = new EMail(getCtx(), host, user, emailuser, titulo, conteudo, user, pwd); // Contato do Pedido
					
			if (!ufat.getEMail().equals(emailuser))
				email.addTo(ufat.getEMail());
			//emailuser += "; " + ufat.getEMail(); // Contato do Faturamento
			if (!uentr.getEMail().equals(emailuser) && !uentr.getEMail().equals(ufat.getEMail()))
				email.addTo(uentr.getEMail());
			//emailuser += "; " + uentr.getEMail();
			email.addBcc("palmetal@palmetal.com.br");
			email.setMessageHTML(conteudo);			
			String ablufs = email.send();

			System.out.println(ablufs);
			

		}catch(Exception ex) {
			ex.printStackTrace();
			String connectMsg = "Could not create the report " + ex.getMessage() + " " + ex.getLocalizedMessage();
			System.out.println(connectMsg);
			//UtilsPalmetal.avisaErro("AutorizarPedido.avisaCliente(): Calcular a quantidade de OS necessária para a linha do pedido " + mo.getDocumentNo(), UtilsPalmetal.stack2string(ex));
		}

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
	private String buscaCidade(Object id, String pedido) throws SQLException {
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
	private String buscaEstado(Object id, String pedido) throws SQLException {
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

}
