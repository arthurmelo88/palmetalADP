package org.adempierelbr.nfe.beans;

import org.adempierelbr.model.MLBRNotaFiscal;
import org.adempierelbr.util.AdempiereLBR;
import org.adempierelbr.util.RemoverAcentos;
import org.adempierelbr.util.TextUtil;
import org.compiere.model.MDocType;
import org.compiere.model.MOrgInfo;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

/**
 * 	Inutiliza��o da Numera��o da NF.
 * 
 * 	@author Ricardo Santana
 *	@version $Id: InutilizacaoNF.java, v1.0 2010/08/04 11:45:26 AM, ralexsander Exp $
 */
public class InutilizacaoNF
{
	/**
	 * 	Default Constructor
	 */
	public InutilizacaoNF (MOrgInfo oi, String regionCode)
	{
		setCNPJ(oi.get_ValueAsString("lbr_CNPJ"));
		setTpAmb(oi.get_ValueAsString("lbr_NFeEnv"));
		setcUF(regionCode);
		
	}	//	InutilizacaoNF
	
	/**
	 * 	Constructor
	 * 
	 * @param nf Nota Fiscal
	 */
	public InutilizacaoNF (MLBRNotaFiscal nf, String tpAmb, String regionCode)
	{
		MDocType dt = new MDocType (Env.getCtx(), nf.getC_DocTypeTarget_ID(), null);
		//
		setcUF(regionCode);
		if (nf.getDateDoc() != null)
			setAno(TextUtil.timeToString(nf.getDateDoc(), "yy"));
		setCNPJ(nf.getlbr_CNPJ());
		setMod(dt.get_ValueAsString("lbr_NFModel"));
		setSerie(dt.get_ValueAsString("lbr_NFSerie"));
		setnNFIni(nf.getDocumentNo());
		setnNFFin(nf.getDocumentNo());
		setxJust(nf.get_ValueAsString("lbr_MotivoCancel"));
		//
		setTpAmb(tpAmb);
	}	//	InutilizacaoNF
	
	/**	Campos para compor o XML	*/
	private String Id;
	private String tpAmb;
	private final String xServ = "INUTILIZAR"; 
	private String cUF;
	private String ano;
	private String CNPJ;
	private String mod;
	private String serie;
	private String nNFIni;
	private String nNFFin;
	private String xJust;
	
	/**	Campos auxiliares			*/
	private String msg = "";
	
	/**	Logger						*/
	private static CLogger log = CLogger.getCLogger(InutilizacaoNF.class);
	
	public String getxServ()
	{
		return xServ;
	}	//	getxServ
	public String getID()
	{
		return Id;
	}	//	getID
	public void setID()
	{
		this.Id = "ID"+getcUF()+getAno()+getCNPJ()+getMod()+TextUtil.lPad(getSerie(),3)+
					TextUtil.lPad(getnNFIni(),9)+TextUtil.lPad(getnNFFin(),9);
	}	//	setID
	public String getTpAmb()
	{
		return tpAmb;
	}	//	getTpAmb
	public void setTpAmb(String tpAmb)
	{
		this.tpAmb = tpAmb;
	}	//	setTpAmb
	public String getcUF()
	{
		return cUF;
	}	//	getcUF
	public void setcUF(String cUF)
	{
		this.cUF = cUF;
	}	//	setcUF
	public String getAno()
	{
		return ano;
	}	//	getAno
	public void setAno(String ano)
	{
		this.ano = ano;
	}	//	setAno
	public String getCNPJ()
	{
		return CNPJ;
	}	//	getCNPJ
	public void setCNPJ(String cNPJ)
	{
		CNPJ = TextUtil.toNumeric(cNPJ);
	}	//	setCNPJ
	public String getMod()
	{
		return mod;
	}	//	getMod
	public void setMod(String mod)
	{
		this.mod = mod;
	}	//	setMod
	public String getSerie()
	{
		return serie;
	}	//	getSerie
	public void setSerie(String serie)
	{
		this.serie = serie;
	}	//	setSerie
	public String getnNFIni()
	{
		return nNFIni;
	}	//	getnNFIni
	public void setnNFIni(String nNFIni)
	{
		this.nNFIni = nNFIni;
	}	//	setnNFIni
	public String getnNFFin()
	{
		return nNFFin;
	}	//	getnNFFin
	public void setnNFFin(String nNFFin)
	{
		this.nNFFin = nNFFin;
	}	//	setnNFFin
	public String getxJust()
	{
		return xJust;
	}	//	getxJust
	public void setxJust(String xJust)
	{
		xJust = RemoverAcentos.remover(xJust);
		//
		if (xJust != null && xJust.length() > 255)
		{
			log.warning("Motivo do cancelamento truncado para 255 caracteres");
			xJust = xJust.substring(0, 255);
		}
		//
		this.xJust = xJust;
	}	//	setxJust
	public boolean isValid()
	{
		msg = "";
		Id = "";
		//
		if (getTpAmb() == null || getTpAmb().length() != 1)
			msg = "Tipo de Ambiente inv�lido\n";
		if (getcUF() == null || getcUF().length() != 2)
			msg = "C�digo da UF inv�lido\n";
		if (getAno() == null || getAno().length() != 2)
			msg = "O Ano de inutiliza��o � inv�lido\n";
		if (getCNPJ() == null || getCNPJ().length() != 14)
			msg = "CNPJ inv�lido\n";
		if (getMod() == null || getMod().length() != 2
				|| !AdempiereLBR.isNumber(getMod()))
			msg = "Modelo da NF inv�lido\n";
		if (getSerie() == null || getSerie().length() < 1 || 
				getSerie().length() > 3 || !AdempiereLBR.isNumber(getSerie()))
			msg = "S�rie da NF inv�lida\n";
		if (getnNFIni() == null || getnNFIni().length() < 1 || 
				getnNFIni().length() > 9 || !AdempiereLBR.isNumber(getnNFIni()))
			msg = "N�mero Inicial da NF para inutiliza��o � inv�lido\n";
		if (getnNFFin() == null || getnNFFin().length() < 1 || 
				getnNFFin().length() > 9 || !AdempiereLBR.isNumber(getnNFFin()))
			msg = "N�mero Final da NF para inutiliza��o � inv�lido\n";
		if (getxJust() == null || getxJust().length() < 15)
			msg = "C�digo da UF inv�lido\n";
		//
		if (msg.equals(""))
		{
			setID();
			return true;
		}
		else
			return false;
	}	//	isValid
	public String getMsg()
	{
		return msg;
	}	//	getMsg
}	//	InutilizacaoNF
