package org.adempierelbr.nfe;

import java.util.HashMap;
import java.util.Map;

import org.adempiere.exceptions.AdempiereException;
import org.adempierelbr.model.MLBRNFLineTax;
import org.adempierelbr.model.MLBRNotaFiscalLine;
import org.adempierelbr.model.X_LBR_TaxGroup;
import org.adempierelbr.nfe.v200.beans.INFeBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.NFeCOFINSBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.NFeICMSBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.NFeIPIBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.NFeImpostoBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.NFePISBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins.NFeCOFINSAliqBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins.NFeCOFINSOutrBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.ICMSFactory;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.INFeICMSBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS00Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS10Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS20Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS30Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS40Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS51Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS60Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS70Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS90Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN101Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN102Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN201Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN202Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN500Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN900Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.ipi.IPIFactory;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.ipi.NFeIPINTBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.ipi.NFeIPITribBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis.NFePISAliqBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis.NFePISOutr;
import org.adempierelbr.util.TextUtil;
import org.compiere.model.MInvoice;
import org.compiere.util.Env;

/**
 * Get Tributos incidentes no Produto ou Servico
 * @author Fernando
 *
 */
public class NFeXMLGrupoMGenerator {
	
	private MLBRNotaFiscalLine nfLine; 
	private Map<String, MLBRNFLineTax> txs;
	
	//private MProduct product;
	
	public NFeXMLGrupoMGenerator(MLBRNotaFiscalLine nfLine){
		this.nfLine=nfLine;
	}

	/**
	 * Obtem os impostos e armazena em um hashmap
	 * @throws Exception 
	 */
	public void init() throws Exception {
		
		MLBRNFLineTax [] lineTaxes = nfLine.getTaxes();
		
		txs = new HashMap<String, MLBRNFLineTax>();
		//
		for (MLBRNFLineTax lt : lineTaxes)
		{
			X_LBR_TaxGroup taxGroup = new X_LBR_TaxGroup(Env.getCtx(), lt.getLBR_TaxGroup_ID(), null);
			String taxIndicator = taxGroup.getName().trim();

			if (txs.containsKey(taxIndicator))
				throw new AdempiereException("Imposto duplicado: " + taxIndicator);
			else
				txs.put(taxIndicator, lt);
		}
		
		//product = new MProduct(Env.getCtx(),nfLine.getM_Product_ID(),null);
	}

	/**
	 * Get Tributos incidentes no Produto ou Servico 
	 * @param MLBRNFLineTaxs 
	 * @return
	 */
	public NFeImpostoBean getNFeImpostosLinha() throws Exception {
		try{
			NFeImpostoBean impostoBean = new NFeImpostoBean();
			impostoBean.setPIS(getNFeImpostoPISBean());
			impostoBean.setCOFINS(getNFeImpostoCOFINSBean());
			//impostoBean.setPISST(getNFeImpostoPISSTBean());
			//impostoBean.setCOFINSST(getNFeImpostoCOFINSSTBean());
			
			impostoBean.setICMS(getNFeImpostoICMSBean());
			impostoBean.setIPI(getNFeImpostoIPIBean());
			//impostoBean.setII(getNFeImpostoIIBean());
			
			//impostoBean.setISSQN();
			

			return impostoBean;
		}catch (Exception ex){
			throw new Exception("Erro ao obter as informacoes do grupo M - " +
					"Tributos incidentes no Produto ou Servico. \n Msg: " + ex.getMessage());
		}
	}

	/**
	 * Obtem um Bean referente aos valores do COFINS
	 * Conforme Layout da NF-e
	 * @return
	 */
	private NFeCOFINSBean getNFeImpostoCOFINSBean() throws Exception {
		try{
			MLBRNFLineTax taxCOFINS = txs.get("COFINS");
			if (taxCOFINS==null)
				throw new Exception ("Valor do COFINS nao calculado.");
			
			NFeCOFINSBean cofins = new NFeCOFINSBean();
			
			String taxCST = taxCOFINS.getTaxStatus(nfLine.isSOTrx());
			String CST = taxCST==null || taxCST.trim().length()==0 ? "01":taxCST;
			
			
			if (CST.equalsIgnoreCase("01") 
					|| (CST.equalsIgnoreCase("02")))
			{
				NFeCOFINSAliqBean cofinsAliq = new NFeCOFINSAliqBean() ;
				cofinsAliq.setCST(CST);
				cofinsAliq.setPCOFINS(TextUtil.bigdecimalToString(taxCOFINS.getlbr_TaxRate()));
				cofinsAliq.setVBC(TextUtil.bigdecimalToString(taxCOFINS.getlbr_TaxBaseAmt()));
				cofinsAliq.setVCOFINS(TextUtil.bigdecimalToString(taxCOFINS.getlbr_TaxAmt()));
				cofins.setCOFINS(cofinsAliq);
			}
//			else if (CST.equalsIgnoreCase(OperacaoTributavel03))
//				return new NFeCOFINSQtdeBean();	
//			else if (CST.equalsIgnoreCase(OperacaoTributavel04) 
//					||  CST.equalsIgnoreCase(OperacaoTributavel06)
//					||  CST.equalsIgnoreCase(OperacaoTributavel07)
//					||  CST.equalsIgnoreCase(SemIncidencia)
//					||  CST.equalsIgnoreCase(SuspensaoContribuicao)
//					)
//				return new NFeCOFINSNTBean();	
			
			else if (CST.equalsIgnoreCase("99")){
				NFeCOFINSOutrBean cofinsOutras = new NFeCOFINSOutrBean() ;
				cofinsOutras.setCST(CST);
				cofinsOutras.setPCOFINS(TextUtil.bigdecimalToString(taxCOFINS.getlbr_TaxRate()));
				cofinsOutras.setVBC(TextUtil.bigdecimalToString(taxCOFINS.getlbr_TaxBaseAmt()));
				cofinsOutras.setVCOFINS(TextUtil.bigdecimalToString(taxCOFINS.getlbr_TaxAmt()));
				cofins.setCOFINS(cofinsOutras);
			}
			
			
			
			
			return cofins;
			
		}catch(Exception ex){
			throw new Exception ("Grupo do COFINS invalido:" + ex.getMessage());
		}

	}

	/**
	 * Obtem um Bean referente aos valores do PIS
	 * Conforme Layout da NF-e
	 * @return
	 */
	private NFePISBean getNFeImpostoPISBean()  throws Exception{
		
		try{
			MLBRNFLineTax taxPIS = txs.get("PIS");
			if (taxPIS==null)
				throw new Exception ("Valor do PIS nao calculado.");
			
			NFePISBean pis = new NFePISBean();
			String taxCST = taxPIS.getTaxStatus(nfLine.isSOTrx());
			String CST = taxCST==null || taxCST.trim().length()==0 ?"01":taxCST;
			
			if (CST.equalsIgnoreCase("01") 
					|| (CST.equalsIgnoreCase("02")))
			{
				NFePISAliqBean pisAliq = new NFePISAliqBean();
				pisAliq.setCST(CST);
				pisAliq.setVPIS(TextUtil.bigdecimalToString(taxPIS.getlbr_TaxAmt()));// vPIS - Valor do PIS
				pisAliq.setVBC(TextUtil.bigdecimalToString(taxPIS.getlbr_TaxBaseAmt()));// vBC - Base de calculo do PIS
				pisAliq.setPPIS(TextUtil.bigdecimalToString(taxPIS.getlbr_TaxRate()));// pPIS - percentual do pis
				pis.setPis(pisAliq);
			}
			//else if (CST.equalsIgnoreCase(OperacaoTributavel03))
			//	return new NFePISQtde();	
//			else if (CST.equalsIgnoreCase(OperacaoTributavel04) 
//					||  CST.equalsIgnoreCase(OperacaoTributavel06)
//					||  CST.equalsIgnoreCase(OperacaoTributavel07)
//					||  CST.equalsIgnoreCase(SemIncidencia)
//					||  CST.equalsIgnoreCase(SuspensaoContribuicao)
//					)
//				return new NFePISNT();	
			
			else if (CST.equalsIgnoreCase("99")){
				NFePISOutr pisOutra = new NFePISOutr();
				pisOutra.setCST(CST);
				pisOutra.setVPIS(TextUtil.bigdecimalToString(taxPIS.getlbr_TaxAmt()));// vPIS - Valor do PIS
				pisOutra.setVBC(TextUtil.bigdecimalToString(taxPIS.getlbr_TaxBaseAmt()));// vBC - Base de calculo do PIS
				pisOutra.setPPIS(TextUtil.bigdecimalToString(taxPIS.getlbr_TaxRate()));// pPIS - percentual do pis
				pis.setPis(pisOutra);
			}

			
			return pis;
		}catch(Exception ex){
			throw new Exception ("Grupo do PIS invalido:" + ex.getMessage());
		}

	}

	/**
	 * Obtem um Bean referente aos valores do IPI Conforme Layout da NF-e 2.0
	 * @return
	 */
	private NFeIPIBean getNFeImpostoIPIBean() throws Exception{
		try {

				MLBRNFLineTax taxIPI = txs.get("IPI");
				if (taxIPI==null)
					return null;//throw new Exception ("Valor do IPI nao calculado. Verifique o CST do IPI e calculo do respectivo imposto");
				
				String taxCST = taxIPI.getTaxStatus(nfLine.isSOTrx());
				String CST = taxCST.trim().length()==0 ?nfLine.get_ValueAsString("lbr_TaxStatusIPI"):taxCST;
				
				if (CST==null || CST.trim().length()==0)
					throw new Exception ("Valor do IPI nao calculado. Verifique o CST do IPI e calculo do respectivo imposto");
				
				NFeIPIBean ipi = new NFeIPIBean();
				//ipi.clEnq(); //Classe de enquadramento do IPI para Cigarros e Bebidas
				//ipi.setCNPJProd(prod); CNPJ do produtor da mercadoria, quando diferente do emitente. Somente para os casos de exportacao.
				//ipi.setCSelo(selo);
				//ipi.setQSelo(selo);
				ipi.setCEnq("999"); //	Deixar 999 ate a RBF criar a regra.
				INFeBean bean = IPIFactory.getNFeBeanIPI(CST);
				if (bean instanceof NFeIPITribBean){
					NFeIPITribBean ipiTrib = (NFeIPITribBean) bean;
					ipiTrib.setCST(CST);
					//Por Aliquota
					ipiTrib.setVBC(TextUtil.bigdecimalToString(taxIPI.getlbr_TaxBaseAmt()));
					ipiTrib.setPIPI(TextUtil.bigdecimalToString(taxIPI.getlbr_TaxRate()));
					//Por Unidade
					//ipiTrib.setQUnid(unid);
					//ipiTrib.setVUnid(unid);
					
					ipiTrib.setVIPI(TextUtil.bigdecimalToString(taxIPI.getlbr_TaxAmt()));
					
					ipi.setIPITrib(ipiTrib);
				} else if (bean instanceof NFeIPINTBean ){
					NFeIPINTBean ipiNT = (NFeIPINTBean) bean;
					ipiNT.setCST(CST);
					ipi.setIPINT(ipiNT);
				}
				
				return ipi;
			//}
				
		} catch (Exception ex) {
			throw new Exception("Grupo de IPI invalido."+ ex.getMessage());
		}
	}

	/**
	 * Obtem um Bean referente aos valores de ICMS
	 * Conforme Layout da NF-e 2.0
	 * @return
	 * @throws Exception
	 */
	private NFeICMSBean getNFeImpostoICMSBean() throws Exception{
		try{


			
			NFeICMSBean icmsBean = new NFeICMSBean();
			
			MLBRNFLineTax icmsTax = txs.get("ICMS");
			MLBRNFLineTax icmsSubTax = txs.get("ICMSST");
			if (icmsSubTax==null)
				icmsSubTax = new MLBRNFLineTax(Env.getCtx(),0,null);
				
			String cstICMS = icmsTax.getTaxStatus(nfLine.isSOTrx());
			String cstICMSST = icmsSubTax.getTaxStatus(nfLine.isSOTrx());
			
			
			String CST =  
					(cstICMSST==null || cstICMSST.trim().length()==0
						?(cstICMS==null || cstICMS.trim().length()==0 
								?(nfLine.getlbr_TaxStatus()==null?"00":nfLine.getlbr_TaxStatus())
								:cstICMS
						  )
						:cstICMSST);
				
			String origem = "0"; //product.get_ValueAsString("lbr_ProductSource");
			if (CST == null)
				throw new Exception("Cod. Sit. Trib. do ICMS Invalido");
				
		
				INFeICMSBean bean = ICMSFactory.getNFeBeanICMS(CST);
				bean.setCST(CST);
				
				if (bean instanceof NFeICMS00Bean){
				if (icmsTax == null)
					throw new Exception("Valor do ICMS nao calculado");

					NFeICMS00Bean icms00 =(NFeICMS00Bean) bean;
					icms00.setOrig(origem);
					icms00.setModBC("3");
					icms00.setVBC(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxBaseAmt())); // Base de Calculo
					icms00.setPICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxRate()));// Aliquota
					icms00.setVICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxAmt()));//Valor do Imposto
					icmsBean.setIcms(icms00);
					
				}else if (bean instanceof NFeICMS10Bean){

					if (icmsTax==null)
						throw new Exception("Valor do ICMS nao calculado");
					
					NFeICMS10Bean icms10 =(NFeICMS10Bean) bean;
					icms10.setOrig(origem);
					icms10.setModBC("3"); //TODO Modalidade de determinacao da BC do ICMS
					icms10.setVBC(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxBaseAmt())); // Base de Calculo
					icms10.setPICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxRate()));// Aliquota
					icms10.setVICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxAmt()));//Valor do Imposto
					
					//ICMS de Substituicao
					//MLBRNFLineTax icmsSubTax = txs.get("ICMSST");
					if (icmsSubTax==null)
						throw new Exception("Valor do ICMS Substituicao nao calculado");
					icms10.setModBCST("4"); //TODO
					//if(productConfig.getimposto_icms_pMVAST().compareTo(Env.ZERO)!=0)
					//	icms10.setPMVAST(TextUtil.bigdecimalToString(productConfig.getimposto_icms_pMVAST()));
					//if(productConfig.getimposto_icms_pRedBCST().compareTo(Env.ZERO)!=0)
					//	icms10.setPRedBCST(TextUtil.bigdecimalToString(productConfig.getimposto_icms_pRedBCST()));
					if (icmsSubTax.getlbr_TaxBase().signum() != 0)
						icms10.setPRedBCST(TextUtil.bigdecimalToString (icmsSubTax.getlbr_TaxBase()));
					
					
					icms10.setVBCST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxBaseAmt())); // Base de Calculo
					icms10.setPICMSST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxRate()));// Aliquota
					icms10.setVICMSST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxAmt()));//Valor do Imposto
					icmsBean.setIcms(icms10);
				}else if (bean instanceof NFeICMS20Bean){
					if (icmsTax==null)
						throw new Exception("Valor do ICMS nao calculado");
					
					NFeICMS20Bean icms20 =(NFeICMS20Bean) bean;
					icms20.setOrig(origem);
					icms20.setModBC("3"); //TODO Modalidade de determinacao da BC do ICMS
					icms20.setPRedBC(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxBase()));//TODO a verificar
					icms20.setVBC(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxBaseAmt())); // Base de Calculo
					icms20.setPICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxRate()));// Aliquota
					icms20.setVICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxAmt()));//Valor do Imposto
					icmsBean.setIcms(icms20);
				}else if (bean instanceof NFeICMS30Bean){
					NFeICMS30Bean icms30 =(NFeICMS30Bean) bean;
					icms30.setOrig(origem);
					icms30.setModBCST("4"); 
					//icms30.setPMVAST(pmvast);
					
					//MLBRNFLineTax icmsSubTax = txs.get("ICMSST");
					if (icmsSubTax==null)
						throw new Exception("Valor do ICMS Substituicao nao calculado");
					icms30.setPRedBCST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxBase()));//TODO a verificar
					icms30.setVBCST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxBaseAmt())); // Base de Calculo
					icms30.setPICMSST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxRate()));// Aliquota
					icms30.setVICMSST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxAmt()));//Valor do Imposto
					icmsBean.setIcms(icms30);
				}else if (bean instanceof NFeICMS40Bean){
					NFeICMS40Bean icms40 =(NFeICMS40Bean) bean;
					icms40.setOrig(origem);
					//Deve ser informado apenas nas operacoes com ve������������������culos beneficiados com a desonera������������������������������������o condicional do ICMS.
					//icms40.setVICMS(vicms); 
					//icms40.setMotDesICMS(motDesICMS)
					icmsBean.setIcms(icms40);
					
				}else if (bean instanceof NFeICMS51Bean){
					if (icmsTax==null)
						throw new Exception("Valor do ICMS nao calculado");
					
					NFeICMS51Bean icms051 =(NFeICMS51Bean) bean;
					icms051.setOrig(origem);
					icms051.setModBC("3");
					icms051.setPRedBC(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxBase()));//TODO a verificar
					icms051.setVBC(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxBaseAmt())); // Base de Calculo
					icms051.setPICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxRate()));// Aliquota
					icms051.setVICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxAmt()));//Valor do Imposto
					icmsBean.setIcms(icms051);
				}else if (bean instanceof NFeICMS60Bean){
					NFeICMS60Bean icms60 =(NFeICMS60Bean) bean;
					icms60.setOrig(origem);
					
					//ICMS de Substituicao
					//MLBRNFLineTax icmsSubTax = txs.get("ICMSST");
					if (icmsSubTax==null)
						throw new Exception("Valor do ICMS Substituicao nao calculado");
					
					icms60.setVBCSTRet(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxBaseAmt()));
					icms60.setVICMSSTRet(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxBaseAmt()));
					icmsBean.setIcms(icms60);
				}else if (bean instanceof NFeICMS70Bean){
					if (icmsTax==null)
						throw new Exception("Valor do ICMS nao calculado");
					
					NFeICMS70Bean icms70 =(NFeICMS70Bean) bean;
					icms70.setOrig(origem);
					icms70.setModBC("3"); //TODO Modalidade de determinacao da BC do ICMS
					icms70.setPRedBC(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxBase()));//TODO a verificar
					icms70.setVBC(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxBaseAmt())); // Base de Calculo
					icms70.setPICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxRate()));// Aliquota
					icms70.setVICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxAmt()));//Valor do Imposto
					
					//ICMS de Substituicao
					//MLBRNFLineTax icmsSubTax = txs.get("ICMSST");
					if (icmsSubTax==null)
						throw new Exception("Valor do ICMS Substituicao nao calculado");
					
					icms70.setModBCST("4"); 
				//	if(productConfig.getimposto_icms_pMVAST().compareTo(Env.ZERO)!=0)
				//		icms70.setPMVAST(TextUtil.bigdecimalToString(productConfig.getimposto_icms_pMVAST()));
					if (icmsSubTax.getlbr_TaxBase().signum() != 0)
						icms70.setPRedBCST(TextUtil.bigdecimalToString (icmsSubTax.getlbr_TaxBase()));
					
					icms70.setVBCST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxBaseAmt())); // Base de Calculo
					icms70.setPICMSST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxRate()));// Aliquota
					icms70.setVICMSST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxAmt()));//Valor do Imposto
					icmsBean.setIcms(icms70);
				}else if (bean instanceof NFeICMS90Bean){
					
					if (icmsTax==null)
						throw new Exception("Valor do ICMS nao calculado");
		
					
					NFeICMS90Bean icms90 =(NFeICMS90Bean) bean;
					icms90.setOrig(origem);
					icms90.setModBC("3"); //TODO Modalidade de determinacao da BC do ICMS
					if (icmsTax.getlbr_TaxBase().signum() != 0)
						icms90.setPRedBCST(TextUtil.bigdecimalToString (icmsTax.getlbr_TaxBase()));
					
					icms90.setVBC(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxBaseAmt())); // Base de Calculo
					icms90.setPICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxRate()));// Aliquota
					icms90.setVICMS(TextUtil.bigdecimalToString(icmsTax.getlbr_TaxAmt()));//Valor do Imposto
					
					//ICMS de Substituicao
					//MLBRNFLineTax icmsSubTax = txs.get("ICMSST");
					if (icmsSubTax==null)
						throw new Exception("Valor do ICMS Substituicao nao calculado");
					
					icms90.setModBCST("4");
					//if(productConfig.getimposto_icms_pMVAST().compareTo(Env.ZERO)!=0)
					//	icms90.setPMVAST(TextUtil.bigdecimalToString(productConfig.getimposto_icms_pMVAST()));
					if (icmsSubTax.getlbr_TaxBase().signum() != 0)
						icms90.setPRedBCST(TextUtil.bigdecimalToString (icmsTax.getlbr_TaxBase()));
					
				
					icms90.setVBCST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxBaseAmt())); // Base de Calculo
					icms90.setPICMSST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxRate()));// Aliquota
					icms90.setVICMSST(TextUtil.bigdecimalToString(icmsSubTax.getlbr_TaxAmt()));//Valor do Imposto
					icmsBean.setIcms(icms90);
				}else
					throw new IllegalArgumentException("Codigo CST invalido: " + CST);
			
			
			return icmsBean;
			
		}catch (Exception ex){
			throw new Exception("Grupo de ICMS invalido." + ex.getMessage());
		}

	}
}

