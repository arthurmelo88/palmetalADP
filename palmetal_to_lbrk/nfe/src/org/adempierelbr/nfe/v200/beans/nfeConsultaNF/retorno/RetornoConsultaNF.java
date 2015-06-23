package org.adempierelbr.nfe.v200.beans.nfeConsultaNF.retorno;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("retConsSitNFe")
public class RetornoConsultaNF {

	@XStreamAsAttribute
	private String versao;
	private String tpAmb;
	private String verAplic;
	private String cStat;
	private String xMotivo;
	private String cUF;
	private String chNFe;
	private ProtNFeBean protNFe;
	private RetCancNFeBean retCancNFe;

	public RetornoConsultaNF() {
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String getTpAmb() {
		return tpAmb;
	}

	public void setTpAmb(String tpAmb) {
		this.tpAmb = tpAmb;
	}

	public String getVerAplic() {
		return verAplic;
	}

	public void setVerAplic(String verAplic) {
		this.verAplic = verAplic;
	}

	public String getcStat() {
		return cStat;
	}

	public void setcStat(String cStat) {
		this.cStat = cStat;
	}

	public String getxMotivo() {
		return xMotivo;
	}

	public void setxMotivo(String xMotivo) {
		this.xMotivo = xMotivo;
	}

	public String getcUF() {
		return cUF;
	}

	public void setcUF(String cUF) {
		this.cUF = cUF;
	}

	public String getChNFe() {
		return chNFe;
	}

	public void setChNFe(String chNFe) {
		this.chNFe = chNFe;
	}

	public ProtNFeBean getProtNFe() {
		return protNFe;
	}

	public void setProtNFe(ProtNFeBean protNFe) {
		this.protNFe = protNFe;
	}

	public RetCancNFeBean getRetCancNFe() {
		return retCancNFe;
	}

	public void setRetCancNFe(RetCancNFeBean retCancNFe) {
		this.retCancNFe = retCancNFe;
	}

}
