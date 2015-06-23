package org.adempierelbr.nfe.v200.beans.infnfe.ide;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("NFref")
public class NFeNFRefBean implements INFeBean {

	private String refNFe;
	private NFeInfoNFRef refNF;

	public String getRefNFe() {
		return refNFe;
	}

	public void setRefNFe(String refNFe) {
		this.refNFe = refNFe;
	}

	public NFeInfoNFRef getRefNF() {
		return refNF;
	}

	public void setRefNF(NFeInfoNFRef refNF) {
		this.refNF = refNF;
	}

}
