package org.adempierelbr.nfe.v200.beans.prot;

import org.adempierelbr.nfe.v200.beans.INFeBean;
import org.adempierelbr.nfe.v200.beans.NFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Leiaute da Distribuicao: NF-e
 * @author Fernando
 *
 */
@XStreamAlias("nfeProc")
public class NFeProcBean implements INFeBean {

	@XStreamAsAttribute
	private String versao;
	
	@XStreamAlias("NFe")
	private NFeBean nfe;
	
	private NFeProtBean protNFe;

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public NFeBean getNfe() {
		return nfe;
	}

	public void setNfe(NFeBean nfe) {
		this.nfe = nfe;
	}

	public NFeProtBean getProtNFe() {
		return protNFe;
	}

	public void setProtNFe(NFeProtBean protNFe) {
		this.protNFe = protNFe;
	}


	
	
}
