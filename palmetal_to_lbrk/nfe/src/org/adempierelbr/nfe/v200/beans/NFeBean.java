package org.adempierelbr.nfe.v200.beans;

import org.adempierelbr.nfe.v200.beans.infnfe.NFeInfNFeBean;
import org.adempierelbr.nfe.v200.beans.signature.NFeSignature;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("NFe")
public class NFeBean implements INFeBean {

	private NFeInfNFeBean infNFe;
	
	@XStreamAlias("Signature")
	private NFeSignature signature;

	public NFeBean() {
	}

	public void setInfNFe(NFeInfNFeBean infNFe) {
		this.infNFe = infNFe;
	}

	public NFeInfNFeBean getInfNFe() {
		return infNFe;
	}

	public void setSignature(NFeSignature signature) {
		this.signature = signature;
	}

	public NFeSignature getSignature() {
		return signature;
	}

}
