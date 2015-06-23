/******************************************************************************
 * Product: ADempiereLBR - ADempiere Localization Brazil                      *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.adempierelbr.nfe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Random;

import org.adempierelbr.model.MLBRCFOP;
import org.adempierelbr.model.MLBRNotaFiscal;
import org.adempierelbr.model.MLBRNotaFiscalLine;
import org.adempierelbr.model.X_LBR_NCM;
import org.adempierelbr.model.X_LBR_NFTax;
import org.adempierelbr.model.X_LBR_TaxGroup;
import org.adempierelbr.nfe.v200.beans.infnfe.NFeDetBean;
import org.adempierelbr.nfe.v200.beans.infnfe.NFeIdeBean;
import org.adempierelbr.nfe.v200.beans.infnfe.NFeInfNFeBean;
import org.adempierelbr.nfe.v200.beans.infnfe.NFeLEntregaBean;
import org.adempierelbr.nfe.v200.beans.infnfe.NFeLRetiradaBean;
import org.adempierelbr.nfe.v200.beans.infnfe.cobranca.NFeCobrBean;
import org.adempierelbr.nfe.v200.beans.infnfe.cobranca.NFeDupBean;
import org.adempierelbr.nfe.v200.beans.infnfe.cobranca.NFeFatBean;
import org.adempierelbr.nfe.v200.beans.infnfe.dest.NFeDestBean;
import org.adempierelbr.nfe.v200.beans.infnfe.dest.NFeDestEndBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.prod.NFeProdBean;
import org.adempierelbr.nfe.v200.beans.infnfe.emit.NFeEmitBean;
import org.adempierelbr.nfe.v200.beans.infnfe.emit.NFeEndEmitBean;
import org.adempierelbr.nfe.v200.beans.infnfe.ide.NFeNFRefBean;
import org.adempierelbr.nfe.v200.beans.infnfe.infadic.NFeInfAdicBean;
import org.adempierelbr.nfe.v200.beans.infnfe.transporte.NFeTranspBean;
import org.adempierelbr.nfe.v200.beans.infnfe.transporte.NFeTransportadorBean;
import org.adempierelbr.nfe.v200.beans.infnfe.transporte.NFeVolBean;
import org.adempierelbr.nfe.v200.beans.totais.NFeICMSTotBean;
import org.adempierelbr.nfe.v200.beans.totais.NFeTotaisBean;
import org.adempierelbr.nfe.v200.converters.COFINSConverter;
import org.adempierelbr.nfe.v200.converters.ICMSConverter;
import org.adempierelbr.nfe.v200.converters.PISConverter;
import org.adempierelbr.util.AssinaturaDigital;
import org.adempierelbr.util.BPartnerUtil;
import org.adempierelbr.util.NFeUtil;
import org.adempierelbr.util.RemoverAcentos;
import org.adempierelbr.util.TextUtil;
import org.adempierelbr.util.ValidaXML;
import org.adempierelbr.validator.ValidatorBPartner;
import org.compiere.model.MAttachment;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoicePaySchedule;
import org.compiere.model.MLocation;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.MProduct;
import org.compiere.model.MRegion;
import org.compiere.model.X_C_City;
import org.compiere.model.X_C_Country;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

import br.gov.sp.fazenda.dsge.brazilutils.uf.UF;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Gera o arquivo XML 
 * @author Fernando de Oliveira Moraes (fernando.moraes @ faire.com.br)
 * @version $Id: NFeXMLGenerator.java, 24/03/2011
 */

public class NFeXMLGenerator
{
	
	private String versao="2.00";
		
	/** Log				*/
	private static CLogger log = CLogger.getCLogger(NFeXMLGenerator.class);
	private Properties ctx = Env.getCtx();
		
	/** XML             */
	private static final String FILE_EXT      = "-nfe.xml";
	
	/**Nota Fiscal		*/
	private MLBRNotaFiscal nf;
	private MDocType docType; 
	private ChaveNFE chaveNFe;
    private NFeInfNFeBean infNFe;
    private String arquivoXML;
    private File file;
    
    /** MInvoice */
    private MInvoice m_invoice;

	/** Emitente */
	private MOrgInfo emitenteInfo;
	private MLocation emitenteLocation;
	private X_C_City orgCity;
	
	/** Destinatario */
	private MLocation bpLoc;
	
	private String modNF;
	private String serie;

	
	/** Classe para representacao da chave da NFe */
	class ChaveNFE {
		
		/**			Peso			**/
		private static final String PESO = "4329876543298765432987654329876543298765432";

		private String cUF; 	// Codigo da UF do emitente do Documento Fiscal
		private String AAMM; 	// (AAMM) Ano e Mes de emissao da NF-e
		private String CNPJ; 	// CNPJ do emitente
		private String mod; 	// Modelo do Documento Fiscal
		private String serie; 	// Serie do Documento Fiscal
		private String tpEmis; 	// Tipo de Emiss??o
		private String nNF; 	// Numero do Documento Fiscal
		private String cNF; 	// Codigo Numerico que compoe a Chave de Acesso

		public String getRepresentacao(){
			return getCUF()    + getAAMM()  + getCNPJ()
	             + getMod()    + getSerie() + getNNF()
	             + getTpEmis() + getCNF();
		} //toString
		

		/**
		 * 	Retorna o digito na NFe atrav??s da chave de acesso.
		 * 
		 * @param chave
		 * @return	Digito
		 * @throws Exception 
		 */
		
		private int gerarDigito () throws Exception
		{
			String chave = getRepresentacao();
			int x = 0;
			int soma = 0;
			int dv = 0;
			//
			try
			{
				for (int i = 0; i < chave.length(); i++)
				{
					x = Integer.parseInt(chave.substring(i, i + 1))
							* Integer.parseInt(PESO.substring(i, i + 1));
					soma += x;
				}
				
				dv = 11 - (soma % 11);

				if (dv > 9)
				{
					dv = 0;
				}
			}
			catch (Exception e)
			{
				log.severe("Chave Invalida: " + chave);
				throw new Exception("Chave Invalida: " + chave + "." + e.getMessage() );
			}

			return dv;
		}	// gerarDigito
		
		public String getCUF() {
			return cUF;
		}

		public void setCUF(String cuf) {
			cUF = cuf;
		}

		public String getAAMM() {
			return AAMM;
		}

		public void setAAMM(String aamm) {
			AAMM = aamm;
		}

		public String getCNPJ() {
			return CNPJ;
		}

		public void setCNPJ(String cnpj) {
			CNPJ = cnpj;
		}

		public String getMod() {
			return mod;
		}

		public void setMod(String mod) {
			this.mod = mod;
		}

		public String getSerie() {
			return serie;
		}

		public void setSerie(String serie) {
			this.serie = serie;
		}

		public String getNNF() {
			return nNF;
		}

		public void setNNF(String nnf) {
			nNF = nnf;
		}
		
		public String getTpEmis() {
			return tpEmis;
		}
		
		public void setTpEmis(String tpemis){
			tpEmis = tpemis;
		}

		public String getCNF() {
			return cNF;
		}

		public void setCNF(String cnf) {
			cNF = cnf;
		}
	}
	
	/**Construtor */
	public NFeXMLGenerator(MLBRNotaFiscal nf){
		this.nf=nf;
		init();
	}
	
	private void init(){
		docType = new MDocType(ctx, nf.getC_DocTypeTarget_ID(), null);
		emitenteInfo = MOrgInfo.get(nf.getCtx(), nf.getAD_Org_ID());
		m_invoice = new MInvoice(ctx,nf.getC_Invoice_ID(),nf.get_TrxName());
		
		// Dados do documento da NF
		modNF = docType.get_ValueAsString("lbr_NFModel");
		serie = docType.get_ValueAsString("lbr_NFSerie");

	}
	
	public final void doit() throws Exception{
		generetaNFeBean();
		addSignature();
		validate();
		anexarArquivo();
	}
		
	/**Gerar Bean da NF-e */
	private void generetaNFeBean() throws Exception {

		getChaveNFe();
		
		
		infNFe = new NFeInfNFeBean();
		infNFe.setVersao(versao);
		infNFe.setId("NFe" + chaveNFe.getRepresentacao() + chaveNFe.gerarDigito());
		infNFe.setIde(getIdentificacaoNFe());
		infNFe.setEmit(getIdenEmitNFe());
		infNFe.setDest(getIdenDestNFe());
		infNFe.setRetirada(getLRetirada());
		infNFe.setEntrega(getLEntrega());
		
		MLBRNotaFiscalLine[] nfLines = nf.getLines(null);
		for (MLBRNotaFiscalLine nfl:nfLines){
			infNFe.getDet().add(getDetProdServ(nfl));
		}
		
		infNFe.setTotal(getNFeTotais());
		infNFe.setTransp(getNFetranspBean());
		infNFe.setCobr(getInfoCobranNFe());
		infNFe.setInfAdic(getInfoAdicionais());
		
	}
	
	/**
	 * Obtem representacao do XML da NF
	 * @return
	 */
	private String getXML(){
		
        XStream converte = new XStream(new DomDriver());                 
        converte.autodetectAnnotations(true);    
        converte.registerConverter(new ICMSConverter());
        converte.registerConverter(new PISConverter());
        converte.registerConverter(new COFINSConverter());
		String NFeEmXML = NFeUtil.geraCabecNFe() +  TextUtil.removeEOL(converte.toXML(infNFe)) + NFeUtil.geraRodapNFe();

		//NFeEmXML = RemoverAcentos.remover(NFeEmXML);
		
		return NFeEmXML;
	}
	
	private void addSignature() throws Exception{
		
		try
		{
			String nfeID = infNFe.getId().substring(3);
			arquivoXML = nfeID + FILE_EXT;
			log.fine("Assinando NF-e");
			arquivoXML = TextUtil.generateTmpFile(NFeUtil.removeIndent(getXML()), arquivoXML);
			AssinaturaDigital.Assinar(arquivoXML, emitenteInfo, AssinaturaDigital.RECEPCAO_NFE);
			
		}
		catch (Exception e){
			log.severe(e.getMessage());
			throw new Exception(e.getMessage());
		}
	}

	
	private void validate() throws Exception{
		String retValidacao = "";
		file = new File(arquivoXML);

		try{
			log.fine("Validando NF-e");

			retValidacao = NFeUtil.validateSize(file);
			if (retValidacao != null)
				throw new Exception(retValidacao);

			FileInputStream stream = new FileInputStream(file);
			InputStreamReader streamReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(streamReader);

			String validar = "";
			String line    = null;
			while( (line=reader.readLine() ) != null ) {
				validar += line;
			}
			//
			retValidacao = ValidaXML.validaXML(validar);
		}
		catch (Exception e){
			log.severe(e.getMessage());
		}

		if (!retValidacao.equals("")){
			log.severe(retValidacao);
			throw new Exception(retValidacao);
		}

	}
	
	private void anexarArquivo() throws Exception {

		// Grava ID
		String nfeID = infNFe.getId().substring(3);
		nf.set_CustomColumn("lbr_NFeID", nfeID);
		boolean save = nf.save();
		if (save) {
			// Anexa o XML na NF
			MAttachment attachNFe = nf.getAttachment(true);
			if (attachNFe==null)
				attachNFe = nf.createAttachment();
			
			//attachNFe.setClientOrg(nf.getAD_Client_ID(), nf.getAD_Org_ID());
			attachNFe.addEntry(file);
		
			boolean ok = attachNFe.save();
			if (!ok)
				throw new Exception("Erro ao salvar o o arquivo");

		} else
			throw new Exception("Erro ao salvar o o arquivo");

	}

	/**
	 * Get Chave da Nota Fiscal Eletronica
	 * @param tipoEmissao
	 * @return
	 * @throws Exception
	 */
	private ChaveNFE getChaveNFe() throws Exception {
		chaveNFe = new ChaveNFE();
		
		Timestamp datedoc = nf.getDateDoc();
		String aamm = TextUtil.timeToString(datedoc, "yyMM");
		String orgCPNJ = TextUtil.toNumeric(nf.getlbr_CNPJ()); 

		emitenteLocation = new MLocation(emitenteInfo.getCtx(),emitenteInfo.getC_Location_ID(),null);
	
		chaveNFe.setAAMM(aamm);
		chaveNFe.setCUF(BPartnerUtil.getRegionCode(emitenteLocation));	//UF EMITENTE
		chaveNFe.setCNPJ(orgCPNJ);
		chaveNFe.setMod("55");
		chaveNFe.setSerie(TextUtil.lPad(serie, 3));
		chaveNFe.setTpEmis("1");
		chaveNFe.setNNF(TextUtil.lPad(nf.getDocumentNo(), 9));
		chaveNFe.setCNF(TextUtil.lPad("" + new Random().nextInt(99999999), 8));

		return chaveNFe;

	}
	
	/**
	 * Get Identificacao da NF-e
	 * @return
	 */
	private NFeIdeBean getIdentificacaoNFe() throws Exception{
		try{
			NFeIdeBean ide = new NFeIdeBean();
			ide.setcUF(chaveNFe.getCUF());
			ide.setcNF(chaveNFe.getCNF()); 
			ide.setNatOp(TextUtil.checkSize(RemoverAcentos.remover(nf.getlbr_CFOPNote()), 1, 60).trim());
			//TODO
			if (m_invoice.isPayScheduleValid())
				ide.setIndPag("1");
			else{
				int paymentTerm = m_invoice.getC_PaymentTerm_ID();
				MPaymentTerm t = new MPaymentTerm(Env.getCtx(),paymentTerm,null);
				ide.setIndPag(t.getNetDays() > 0 ? "1":"0");
					
			}

			ide.setMod(modNF);
			ide.setSerie(serie);
			ide.setnNF(nf.getDocumentNo().trim());
			
			//Data de Emissao do Documento Fiscal
			//BEGIN PALMETAL
			String dEmi = TextUtil.timeToString(nf.getDateDoc(), "yyyy-MM-dd");
			String dInOut;
			if(nf.getlbr_DateInOut()!=null)				
			    dInOut =  TextUtil.timeToString(nf.getlbr_DateInOut(), "yyyy-MM-dd");
			else
				dInOut =  TextUtil.timeToString(nf.getDateDoc(), "yyyy-MM-dd");
			//END PALMETAL
			
			ide.setdEmi(dEmi);
			ide.setdSaiEnt(dInOut);
			//ide.sethSaiEnt(TextUtil.timeToString(new Timestamp(System.currentTimeMillis()), "hh:mm:ss"));
			ide.setTpNF(nf.isSOTrx() ? "1" : "0");
			
			//Municipio de ocorrencia do fator gerador do ICMS
			orgCity = BPartnerUtil.getX_C_City(ctx,emitenteLocation,null);
			if (orgCity.get_Value("lbr_cityCode") != null){
				ide.setcMunFG(orgCity.get_ValueAsString("lbr_CityCode"));
			}else
				throw new Exception("Codigo do Municipio do Emitente Invalido");
				
			ide.setTpImp(docType.get_ValueAsString("lbr_DANFEFormat"));
			ide.setTpEmis("1");
			ide.setcDV(Integer.toString(chaveNFe.gerarDigito()));
			ide.setTpAmb(docType.get_ValueAsString("lbr_NFeEnv"));
			ide.setFinNFe("1"); //nf.getlbr_FinNFe()
			ide.setProcEmi("0");
			ide.setVerProc(NFeUtil.VERSAO_APP);
			
			//NotaFiscal Referenciada
			if (nf.getlbr_NFReference()!=0)
			{
				NFeNFRefBean nfRef = new NFeNFRefBean();
				MLBRNotaFiscal nfReferenciada = new MLBRNotaFiscal(ctx,nf.getlbr_NFReference(),null);
				String lbr_nfeid=nfReferenciada.get_ValueAsString("lbr_nfeid");
				if (lbr_nfeid.trim().length()!=44)
					throw new Exception("Chave da NF-e Referefenciada Invalida");
				
				nfRef.setRefNFe(lbr_nfeid.trim());
				ide.getNFref().add(nfRef);
			}
			
			
			//Contingencia
			//ide.setDhCont(dhCont);
			//ide.setxJust(xJust);
			
			return ide;
			
		}catch(Exception ex){
			throw new Exception("Erro ao obter as informacoes da NF-e." +
					" \n Msg: " + ex.getMessage());
		}

	}
	
	/**
	 * Get Identificacao do Emitente da NF-e
	 * @return
	 */
	private NFeEmitBean getIdenEmitNFe() throws Exception {
		try{
			NFeEmitBean emit = new NFeEmitBean();
			emit.setCNPJ(chaveNFe.getCNPJ());
			
			emit.setxNome(RemoverAcentos.remover(emitenteInfo.get_ValueAsString("lbr_LegalEntity")).trim());
			String orgXFant = RemoverAcentos.remover(emitenteInfo.get_ValueAsString("lbr_Fantasia"));  //TODO: Copiar para NF
			if (orgXFant != null && orgXFant.trim().length()!=0)
				emit.setxFant(orgXFant.trim());
			
			emit.setEnderEmit(getEnderecoEmitente());
			
			String orgIE = emitenteInfo.get_ValueAsString("lbr_IE");
			UF uf = UF.valueOf(emitenteLocation.getRegion().getName());
			// VALIDACAO IE
			orgIE = ValidatorBPartner.validaIE(orgIE,uf);
			if ( orgIE == null ){
				throw new Exception( "IE do Emitente incorreto " );
			}
			
			emit.setIE(orgIE.trim());
			//emit.setIEST();
			
			//Inscricao Municipal
			String orgIM = TextUtil.formatStringCodes(emitenteInfo.get_ValueAsString("lbr_CCM"));
			if (orgIM != null && orgIM.trim().length()!=0){
				emit.setIM(orgIM.trim());
				String orgCNAE = TextUtil.formatStringCodes(emitenteInfo.get_ValueAsString("lbr_CNAE"));
				emit.setCNAE(orgCNAE.trim());
		

			}

			emit.setCRT("3");
	
			return emit;
			
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do " +
					"	Emitente da NF-e. \n Msg: " + ex.getMessage());
		}
	}

	/**
	 * Obter as Informacoes do Enderedo do Emitente
	 * @return
	 */
	private NFeEndEmitBean getEnderecoEmitente() throws Exception{
		try{
			NFeEndEmitBean enderEmit = new NFeEndEmitBean();	
			
			enderEmit.setxLgr(RemoverAcentos.remover(emitenteLocation.getAddress1()));
			enderEmit.setNro(emitenteLocation.getAddress2().trim());
			enderEmit.setxBairro(RemoverAcentos.remover(emitenteLocation.getAddress3()));
			enderEmit.setcMun(orgCity.get_ValueAsString("lbr_CityCode"));
			enderEmit.setxMun(RemoverAcentos.remover(orgCity.getName()));
			
			MRegion orgRegion = new MRegion (orgCity.getCtx(),orgCity.getC_Region_ID(),null);
			enderEmit.setUF(orgRegion.getName().trim());
			enderEmit.setCEP(TextUtil.toNumeric(emitenteLocation.getPostal()));
			enderEmit.setcPais("1058");
			enderEmit.setxPais("Brasil");
			
			if (emitenteInfo.getPhone()!=null && (emitenteInfo.getPhone().length() >=6 || emitenteInfo.getPhone().length() <=14))
				enderEmit.setFone(TextUtil.toNumeric(emitenteInfo.getPhone()));			
			
			return enderEmit;
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do " +
					"	Endereco Emitente da NF-e. \n Msg: " + ex.getMessage());
		}
		
	}
	
	/**
	 * Get Identificacao do Destinatario da NF-e
	 * @return
	 */
	private NFeDestBean getIdenDestNFe()throws Exception {
		try{
			NFeDestBean dest = new NFeDestBean();
			

			
			if (docType.get_ValueAsString("lbr_NFeEnv").equals("2")){
				dest.setCPF(null);
				dest.setCNPJ("99999999000191");
				dest.setxNome("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
				dest.setIE("");
			}else{
				String bpCNPJ = TextUtil.formatStringCodes(nf.getlbr_BPCNPJ());
				if (bpCNPJ.length() == 11)
					dest.setCPF(bpCNPJ);
				else 
					dest.setCNPJ(bpCNPJ);
				
				dest.setxNome(RemoverAcentos.remover(nf.getBPName()));
				
				
				//Inscricao Estadual - TODO - alterar validacao
				String bpIE = nf.getlbr_BPIE();
				if (bpIE==null)
					bpIE="ISENTO";
				
				UF uf = UF.valueOf(nf.getlbr_BPRegion());
				bpIE = ValidatorBPartner.validaIE(bpIE,uf);
				if ( bpIE != null)
					dest.setIE(TextUtil.toNumeric(bpIE));	
				else
					throw new IllegalArgumentException("IE do Parceiro incorreto");
			}
			
			dest.setEnderDest(getEndDestinatario());
			//dest.setISUF(iSUF);
			//dest.setEmail(email);
			
			return dest;
			
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do " +
					"	Destinatario da NF-e. \n Msg: " + ex.getMessage());
		}
	}

	/**
	 * GET Grupo de Endereco do Destinatario
	 * @return
	 * @throws Exception 
	 */
	private NFeDestEndBean getEndDestinatario() throws Exception {
		try{
			NFeDestEndBean enderDest = new NFeDestEndBean();
			
			if (nf.getlbr_BPAddress1()!=null && nf.getlbr_BPAddress1().trim().length()!=0)
				enderDest.setxLgr(RemoverAcentos.remover(nf.getlbr_BPAddress1()));
			else
				throw new IllegalArgumentException("Endereco do Parceiro Invalido [Logradouro]");
				
			if(nf.getlbr_BPAddress2() != null && nf.getlbr_BPAddress2().trim().length()!=0)
				enderDest.setNro(RemoverAcentos.remover(nf.getlbr_BPAddress2()));
			else
				throw new IllegalArgumentException("Endereco do Parceiro Invalido [Numero]");
			
			if (nf.getlbr_BPAddress3() != null && nf.getlbr_BPAddress3().trim().length()!=0)
				enderDest.setxBairro(RemoverAcentos.remover(nf.getlbr_BPAddress3()));
			else
				throw new IllegalArgumentException("Endere??o do Parceiro Inv??lido [Bairro]");

			if (nf.getC_BPartner_Location_ID()==0)
				throw new IllegalArgumentException("Localizao do destinatario incorreta");
			
			
			if (nf.getlbr_BPPhone()!=null && (nf.getlbr_BPPhone().length() >=6 && nf.getlbr_BPPhone().length() <=14)){
				String fone = TextUtil.toNumeric(nf.getlbr_BPPhone());
				if (fone!=null & fone.trim().length() > 0)
					enderDest.setFone(fone);

			}
			
			MBPartnerLocation bpartLoc = new MBPartnerLocation(ctx, nf.getC_BPartner_Location_ID(), null);
			bpLoc = bpartLoc.getLocation(false);
			if(bpLoc.getC_Country_ID() == 0)
				throw new IllegalArgumentException ("Parceiro de negocios sem Pais Cadastrado");
			
			MRegion bpRegion = bpLoc.getRegion();
			X_C_Country bpcountry = new X_C_Country(ctx,bpRegion.getC_Country_ID(),null);
			
			if (bpcountry.get_Value("lbr_countrycode")!=null){
				int codPais =  bpcountry.get_ValueAsInt("lbr_countrycode");
				enderDest.setcPais(Integer.toString(codPais));
				enderDest.setxPais(bpcountry.getName().trim());
				
				if (codPais==1058){ //Brasil
					enderDest.setUF(nf.getlbr_BPRegion().trim());
					if (bpLoc.getPostal() != null)
						enderDest.setCEP(TextUtil.checkSize(TextUtil.formatStringCodes(nf.getlbr_BPPostal()),8,8,'0').trim());
					
					X_C_City bpCity =BPartnerUtil.getX_C_City(ctx,bpLoc,null);
					if (bpCity == null || bpCity.getC_City_ID()==0)
						throw new IllegalArgumentException( "Cidade do Parceiro de Neg??cios n??o encontrada");
					else if (bpCity.get_Value("lbr_CityCode") != null) {
							enderDest.setcMun(bpCity.get_Value("lbr_CityCode").toString().trim());
							enderDest.setxMun(bpCity.getName().trim());
					}else
						throw new IllegalArgumentException("C??digo da cidade (IBGE) n??o cadastrado");
					
				}else{
					enderDest.setcMun("9999999");
					enderDest.setxMun("EXTERIOR");
					enderDest.setUF("EX");
				}
			}
			else
				throw new IllegalArgumentException ("Codigo do Pais (IBGE) incorreto");
			

			//enderDest.setFone();
			
			return enderDest;
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do do grupo E -Endereco do " +
				"Destinatario da NF-e. \n Msg: " + ex.getMessage());
		}
	}

	/**
	 * Obter o Local Retirada
	 * @return
	 */
	private NFeLRetiradaBean getLRetirada() {
		return null;
	}
	
	/**
	 * Obter o Local Retirada
	 * @return
	 */
	private NFeLEntregaBean getLEntrega() throws Exception  {
		try{
			//TODO
			return null;
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do grupo G -" +
					"	Local de Entrega. \n Msg: " + ex.getMessage());
		}
	}
	
	/**
	 * Get Detalhamento de Produtos e Servicos da NF-e
	 * @param nfl 
	 * @return
	 */
	private NFeDetBean getDetProdServ(MLBRNotaFiscalLine nfLine) throws Exception  {
		try{
			NFeDetBean det = new NFeDetBean();
			det.setNItem(Integer.toString(nfLine.getLine()));
			det.setProd(getProdServ(nfLine));
			
			NFeXMLGrupoMGenerator grupoM = new NFeXMLGrupoMGenerator(nfLine);
			grupoM.init();
			det.setImposto(grupoM.getNFeImpostosLinha());
	
			
			return det;
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes de Detalhamento de Prod e Serv. " +
					"\n Msg: Linha" + nfLine.getLine() + ": "+ ex.getMessage());
		}
	}
	
	/**
	 * Obtem grupo de detalhamento de produtos e servicos da NF-e
	 * @param nfLine
	 * @return
	 * @throws Exception
	 */
	private NFeProdBean getProdServ(MLBRNotaFiscalLine nfLine) throws Exception  {
		try{
			NFeProdBean prod = new NFeProdBean();
			prod.setCProd(RemoverAcentos.remover(nfLine.getProductValue()));
			prod.setXProd(RemoverAcentos.remover(nfLine.getProductName()));

			MProduct prdt = new MProduct(ctx, nfLine.getM_Product_ID(), null);
			if (prdt.getUPC() == null || (prdt.getUPC().trim().length() < 12 || prdt.getUPC().trim().length() > 14))
				prod.setCEAN("");
			else
				prod.setCEAN(prdt.getUPC().trim());
			
			
			if(nfLine.getLBR_NCM_ID() == 0)
				throw new  Exception( "NCM Obrigat??rio. Linha: " + nfLine.getLine());
			
			else{
				X_LBR_NCM ncm = new X_LBR_NCM(ctx, nfLine.getLBR_NCM_ID(), null);
				String ncmValue = ncm.getValue();
				if (!nfLine.islbr_IsService() && ncmValue.trim().length() != 10)
					throw new  Exception("NCM Obrigat??rio. Linha: " + nfLine.getLine());
				else if (nfLine.islbr_IsService())
					ncmValue = "99"; //SERVICO INFORMAR 99
				
				prod.setNCM(TextUtil.toNumeric(ncmValue));
			}
			
			//prod.setEXTIPI(extipi);
			
			//CFOP
			MLBRCFOP cfop = new MLBRCFOP(ctx, nfLine.getLBR_CFOP_ID(),null);
			String cfopName = cfop.getValue();
			if (cfopName == null || cfopName.equals(""))
				throw new  Exception("CFOP Linha: " + nfLine.getLine() + " Inv??lido");
			else if (!cfop.validateCFOP(nf.isSOTrx(), emitenteLocation, bpLoc, false))
				throw new  Exception("CFOP Linha: " + nfLine.getLine() + " Inv??lido");
			else
				prod.setCFOP(TextUtil.formatStringCodes(cfopName));
			
			prod.setUCom(RemoverAcentos.remover(nfLine.getlbr_UOMName()));
			prod.setQCom(TextUtil.bigdecimalToString(nfLine.getQty(),4));
			prod.setVUnCom(TextUtil.bigdecimalToString(nfLine.getPrice(),10));
			prod.setVProd(TextUtil.bigdecimalToString(nfLine.getLineTotalAmt()));
			prod.setCEANTrib("");
			prod.setUTrib(RemoverAcentos.remover(nfLine.getlbr_UOMName()));
			prod.setQTrib(TextUtil.bigdecimalToString(nfLine.getQty(),4));
			prod.setVUnTrib(TextUtil.bigdecimalToString(nfLine.getPrice(),10));
			//prod.setVDesc(desc);
			//prod.setVOutro(outro);
			
			BigDecimal qCom = new BigDecimal(prod.getQCom());
			BigDecimal uCom =  new BigDecimal(prod.getVUnCom());
			BigDecimal vP = qCom.multiply(uCom);
			
			BigDecimal total = new BigDecimal(prod.getVProd());
			BigDecimal diferenca= vP.subtract(total);
			if (diferenca.abs().compareTo(new BigDecimal("0.01"))>0)
				throw new  Exception("Linha.:" + nfLine.getLine() + " Valor do Unitario X Quantidade Difere em R$0.01 o valor total da linha " +
						"apos o arredondamento para 4 casas decimais. /n SEFAZ Norma Tecnica 2011.005. Por favor verifique os valores");
			
			if (nf.getFreightAmt().signum() == 1) //FRETE
				prod.setVFrete(TextUtil.bigdecimalToString(nfLine.getFreightAmt(nf.getTotalLines(), nf.getFreightAmt())));
			if (nf.getlbr_InsuranceAmt().signum() == 1) //SEGURO
				prod.setVSeg(TextUtil.bigdecimalToString(nfLine.getInsuranceAmt(nf.getTotalLines(), nf.getlbr_InsuranceAmt())));

			prod.setIndTot("1"); //v2.0 = 0 ??? VL ?? ENTRA NO TOT 1 - VL ENTRA
			//prod.DI - TODO - DI e Adicoes
			
			//Informacao de Interesse do Emissor para Controle do B2B
			//prod.setXPed(ped);
			//prod.setNItemPed(itemPed);
			
			
			return prod;
			
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do " +
					" grupo I-Produtos e Servicos. \n Msg: " + ex.getMessage());
		}
	}

	/**
	 * Get Valores Totais da NFe
	 * @return
	 */
	private NFeTotaisBean getNFeTotais() throws Exception {
		try{
			NFeTotaisBean totais = new NFeTotaisBean();
			totais.setICMSTot(getICMSTotBean());
			//totais.setISSQNtot(iSSQNtot);
			//totais.setRetTrib(retTrib);
			
			return totais;
			
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do grupo W " +
					"Valores Totais da NF-e. \n Msg: " + ex.getMessage());
		}

	}

	/**
	 * Get Valores Totais referentes ao ICMS
	 * @return
	 * @throws Exception
	 */
	private NFeICMSTotBean getICMSTotBean()  throws Exception{
		try{
			NFeICMSTotBean icmsTot = new NFeICMSTotBean();
			icmsTot.setvNF(TextUtil.bigdecimalToString(nf.getGrandTotal())); // vNF - Valor Total da NF-e
			icmsTot.setvProd(TextUtil.bigdecimalToString(nf.getTotalLines())); // vProd - Valor Total dos produtos e servi??os
			icmsTot.setvFrete(TextUtil.bigdecimalToString(nf.getFreightAmt())); // vFrete - Valor Total do Frete
			icmsTot.setvSeg(TextUtil.bigdecimalToString(nf.getlbr_InsuranceAmt())); // vSeg - Valor Total do Seguro
			icmsTot.setvDesc(TextUtil.ZERO_STRING); // vDesc - Valor Total do Desconto
			icmsTot.setvBCST(TextUtil.ZERO_STRING); // vBCST - BC do ICMS ST
			icmsTot.setvST(TextUtil.ZERO_STRING); // vST - Valor Total do ICMS ST
			icmsTot.setvBC(TextUtil.ZERO_STRING); // vBC - BC do ICMS
			icmsTot.setvICMS(TextUtil.ZERO_STRING); // vICMS - Valor Total do ICMS
			icmsTot.setvPIS(TextUtil.ZERO_STRING); // vPIS - Valor do PIS
			icmsTot.setvCOFINS(TextUtil.ZERO_STRING); // vCOFINS - Valor do COFINS
			icmsTot.setvIPI(TextUtil.ZERO_STRING); // vIPI - Valor Total do IPI
			icmsTot.setvII(TextUtil.ZERO_STRING); // vII - Valor Total do II
			icmsTot.setvOutro(TextUtil.ZERO_STRING); // vOutro - Despesa acess??rias
			
			X_LBR_NFTax[] nfTaxes = nf.getTaxes();
			for (X_LBR_NFTax nfTax : nfTaxes){
				X_LBR_TaxGroup taxGroup = new X_LBR_TaxGroup(ctx, nfTax.getLBR_TaxGroup_ID(), null);
				if (taxGroup.getName().toUpperCase().equals("ICMSST")) {
					icmsTot.setvBCST(TextUtil.bigdecimalToString(nfTax.getlbr_TaxBaseAmt())); // vBCST - BC do ICMS ST
					icmsTot.setvST(TextUtil.bigdecimalToString(nfTax.getlbr_TaxAmt())); // vST - Valor Total do ICMS ST
				}

				if (taxGroup.getName().toUpperCase().equals("ICMS")) {
					icmsTot.setvBC(TextUtil.bigdecimalToString(nfTax.getlbr_TaxBaseAmt())); // vBC - BC do ICMS
					icmsTot.setvICMS(TextUtil.bigdecimalToString(nfTax.getlbr_TaxAmt())); // vICMS - Valor Total do ICMS
				}

				if (taxGroup.getName().toUpperCase().equals("PIS")) {
					icmsTot.setvPIS(TextUtil.bigdecimalToString(nfTax.getlbr_TaxAmt())); // vPIS - Valor do PIS
				}

				if (taxGroup.getName().toUpperCase().equals("COFINS")) {
					icmsTot.setvCOFINS(TextUtil.bigdecimalToString(nfTax.getlbr_TaxAmt())); // vCOFINS - Valor do COFINS
				}

				if (taxGroup.getName().toUpperCase().equals("IPI")) {
					icmsTot.setvIPI(TextUtil.bigdecimalToString(nfTax.getlbr_TaxAmt())); // vIPI - Valor Total do IPI
				}

				if (taxGroup.getName().toUpperCase().equals("II")) {
					icmsTot.setvII(TextUtil.bigdecimalToString(nfTax.getlbr_TaxAmt())); // vII - Valor Total do II
				}

			} //for - nfTaxes
			
			return icmsTot;
			
		}catch (Exception ex){
			throw new Exception("Erro ao obter Totais referentes ao ICMS" +
					" \n Msg: " + ex.getMessage());
		}

	}


	/**
	 * Informacoes do Transporte da NF-e
	 * @return
	 */
	private NFeTranspBean getNFetranspBean() throws Exception {
		try{
			NFeTranspBean transp = new NFeTranspBean();
			String freightCR = nf.getFreightCostRule();
			//BEGIN PALMETAL
			if (freightCR == null || freightCR.equals("") || freightCR.equals("I"))
				//transp.setModFrete("0");
				transp.setModFrete("1");
			else
				//transp.setModFrete("1");
				transp.setModFrete("0");
			//END PALMETAL
			
			// TRANSPORTADORA - VOLUMES TRANSPORTADOS
			if (nf.getlbr_BPShipperCNPJ() != null && !nf.getlbr_BPShipperCNPJ().equals("")) {
				String shipperIE = nf.getlbr_BPShipperIE();
				UF shipperUF = UF.valueOf(nf.getlbr_BPShipperRegion());
				//
				if (shipperUF != null) {
					// VALIDACAO IE
					shipperIE = ValidatorBPartner.validaIE(shipperIE,shipperUF);
					if ( shipperIE == null){
						throw new Exception( "IE da Transportadora incorreto " );
					}
					//
					String end = nf.getlbr_BPShipperAddress1() + ", " + 	//	Rua
						nf.getlbr_BPShipperAddress2() + " - " + 			//	N??mero
						nf.getlbr_BPShipperAddress4() + " - " +				//	Complemento
						nf.getlbr_BPShipperAddress3();						//	Bairro
					//
					NFeTransportadorBean transportador = new NFeTransportadorBean();
					
					//BEGIN PALMETAL					
					if(nf.getlbr_BPShipperCNPJ()!=null){	
						//se for CPF e tiver 11 posicoes é sem caracteres especiais (05188607794) e com 14 tem ponto e traço (051.886.077-94)
						if(nf.getlbr_BPShipperCNPJ().length()==11 || (nf.getlbr_BPShipperCNPJ().length()==14 && nf.getlbr_BPShipperCNPJ().indexOf("-")>0)) transportador.setCPF( TextUtil.formatStringCodes(nf.getlbr_BPShipperCNPJ()));
						//se não for CPF assumo que é CNPJ
						else transportador.setCNPJ( TextUtil.formatStringCodes(nf.getlbr_BPShipperCNPJ()));
					}
					//END PALMETAL
					
					transportador.setxNome(RemoverAcentos.remover(nf.getlbr_BPShipperName()));
					transportador.setIE(shipperIE.trim());
					transportador.setxEnder(RemoverAcentos.remover(end));
					transportador.setxMun(RemoverAcentos.remover(nf.getlbr_BPShipperCity()));
					transportador.setUF(RemoverAcentos.remover(nf.getlbr_BPShipperRegion()));
					
					//
					transp.setTransporta(transportador);
				}
			}
			
			if ((nf.getNoPackages()!=null && nf.getNoPackages().compareTo(Env.ZERO)> 0)
						|| (nf.getlbr_NetWeight()!=null && nf.getlbr_NetWeight().compareTo(Env.ZERO)> 0)
						|| (nf.getlbr_GrossWeight()!=null && nf.getlbr_GrossWeight().compareTo(Env.ZERO)> 0)
						)
				{
					NFeVolBean volume = new NFeVolBean();
					volume.setqVol(nf.getNoPackages()!=null && nf.getNoPackages().compareTo(Env.ZERO)> 0 ?TextUtil.bigdecimalToString(nf.getNoPackages(),0):null);
					volume.setEsp(nf.getlbr_PackingType());
					volume.setPesoL(nf.getlbr_NetWeight()!=null && nf.getlbr_NetWeight().compareTo(Env.ZERO)> 0 ?TextUtil.bigdecimalToString(nf.getlbr_NetWeight(),3):null);
					volume.setPesoB(nf.getlbr_GrossWeight()!=null && nf.getlbr_GrossWeight().compareTo(Env.ZERO)> 0 ?TextUtil.bigdecimalToString(nf.getlbr_GrossWeight(),3):null);
					transp.getVol().add(volume);
				}

			
			
			return transp;
			
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do grupo X " +
					"Informacoes de Transporte da NF-e. \n Msg: " + ex.getMessage());
		}
	}


	/**
	 * Dados da Cobranca
	 * @return
	 */
	private NFeCobrBean getInfoCobranNFe() throws Exception {
		try{
			if (nf.getC_Invoice_ID() > 0){
				
				MInvoice invoice = new MInvoice(ctx,nf.getC_Invoice_ID(),nf.get_TrxName());
				MDocType dt = MDocType.get(ctx, invoice.getC_DocTypeTarget_ID());
				boolean HasOpenItems = dt.get_ValueAsBoolean("lbr_HasOpenItems");

				if (HasOpenItems && nf.isSOTrx()){
					NFeCobrBean cobr = new NFeCobrBean();
					NFeFatBean fat = new NFeFatBean();
					
					fat.setnFat(invoice.getDocumentNo()); // Codigo NFE
					fat.setvOrig(TextUtil.bigdecimalToString(nf.getGrandTotal())); // Valor Bruto
					fat.setvLiq(TextUtil.bigdecimalToString(nf.getGrandTotal())); // Valor Liquido
				    //cobrfat.setvDesc(TextUtil.ZERO_STRING); // Desconto
				    cobr.setFat(fat);

				    //Adiciona as duplicatas da fatura
				    if (invoice.isPayScheduleValid()){
				    	MInvoicePaySchedule [] schedules = MInvoicePaySchedule.getInvoicePaySchedule(ctx, invoice.getC_Invoice_ID(), 0, invoice.get_TrxName());
				    	int i=1;
				    	for (MInvoicePaySchedule sch: schedules){
				    		if (sch.getDueAmt().compareTo(Env.ZERO)==0)
				    			continue;
				    		
				    		NFeDupBean dup = new NFeDupBean();
				    		dup.setnDup(fat.getnFat()+"/"+Integer.toString(i+1));
				    		dup.setdVenc(TextUtil.timeToString(sch.getDueDate(),"yyyy-MM-dd"));
				    		dup.setvDup(TextUtil.bigdecimalToString(sch.getDueAmt()));
				    		
				    		cobr.getDup().add(dup);
				    		
				    	}
				    }
				    
				    return cobr;
	
				}else 
					return null;
			}else
				return null;
			
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do grupo Y " +
					"Dados da Cobranca. \n Msg: " + ex.getMessage());
		}
	}

	/**
	 * Informacoes Adicionais da NFe
	 * @return
	 */
	private NFeInfAdicBean getInfoAdicionais() throws Exception {
		try{
			String dadosAdi = RemoverAcentos.remover(TextUtil.removeEOL(nf.getDescription()==null? "" : nf.getDescription()));

			String notaDocumento = nf.getDocumentNote();
			if (notaDocumento!=null && notaDocumento.length() > 0)
				dadosAdi += " " + RemoverAcentos.remover(notaDocumento.replace("\r", "").replace("\n", ". "));
			
			//Info Procon
			MDocType docType = new MDocType(ctx, nf.getC_DocTypeTarget_ID(), null);
			String endProcon = docType.getDocumentNote();
			if (endProcon!=null && endProcon.length() > 0)
				dadosAdi += RemoverAcentos.remover(docType.getDocumentNote().replace("\r", "").replace("\n", ". "));
			
			if (dadosAdi != null && !dadosAdi.equals(""))
			{
				NFeInfAdicBean infAdi = new NFeInfAdicBean();
				infAdi.setInfCpl(dadosAdi);
				
				return infAdi;
			}else
				return null;
			
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do grupo Z " +
					"Informacoes Adicionais da NF-e. \n Msg: " + ex.getMessage());
		}
	}
	
	public MInvoice getM_invoice() {
		return m_invoice;
	}

}	//	NFeXMLGenerator
