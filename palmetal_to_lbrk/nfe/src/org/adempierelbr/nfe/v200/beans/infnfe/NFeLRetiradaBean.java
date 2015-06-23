package org.adempierelbr.nfe.v200.beans.infnfe;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("retirada")
public class NFeLRetiradaBean implements INFeBean {

	private String CPF;
	private String CNPJ;
	private String xLgr;
	private String nro;
	private String xCpl;
	private String xBairro;
	private String cMun;
	private String xMun;
	private String UF;

	public String getCPF() {
		return CPF;
	}

	public void setCPF(String cPF) {
		CPF = cPF;
	}

	public String getCNPJ() {
		return CNPJ;
	}

	public void setCNPJ(String cNPJ) {
		CNPJ = cNPJ;
	}

	public String getxLgr() {
		return xLgr;
	}

	public void setxLgr(String xLgr) {
		this.xLgr = xLgr;
	}

	public String getNro() {
		return nro;
	}

	public void setNro(String nro) {
		this.nro = nro;
	}

	public String getxCpl() {
		return xCpl;
	}

	public void setxCpl(String xCpl) {
		this.xCpl = xCpl;
	}

	public String getxBairro() {
		return xBairro;
	}

	public void setxBairro(String xBairro) {
		this.xBairro = xBairro;
	}

	public String getcMun() {
		return cMun;
	}

	public void setcMun(String cMun) {
		this.cMun = cMun;
	}

	public String getxMun() {
		return xMun;
	}

	public void setxMun(String xMun) {
		this.xMun = xMun;
	}

	public String getUF() {
		return UF;
	}

	public void setUF(String uF) {
		UF = uF;
	}

}
