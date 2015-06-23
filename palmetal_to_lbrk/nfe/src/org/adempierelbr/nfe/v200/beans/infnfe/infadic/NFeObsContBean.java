package org.adempierelbr.nfe.v200.beans.infnfe.infadic;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("obsCont")
public class NFeObsContBean implements INFeBean {

	private String xCampo;
	private String xTexto;

	public String getxCampo() {
		return xCampo;
	}

	public void setxCampo(String xCampo) {
		this.xCampo = xCampo;
	}

	public String getxTexto() {
		return xTexto;
	}

	public void setxTexto(String xTexto) {
		this.xTexto = xTexto;
	}

}
