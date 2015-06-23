package org.adempierelbr.nfe.v200.beans.infnfe.transporte;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("lacres")
public class NFeLacreBean implements INFeBean {

	private String nLacre;

	public String getNLacre() {
		return nLacre;
	}

	public void setNLacre(String lacre) {
		if (lacre != null)
			lacre = lacre.trim();
		
		nLacre = lacre;
	}
	
}
