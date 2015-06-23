package org.adempierelbr.nfe.v200.beans.infnfe.compra;

import org.adempierelbr.nfe.v200.beans.INFeBean;

public class NFeCompraBean implements INFeBean {

	private String xNEmp;
	private String xPed;
	private String xCont;

	public String getxNEmp() {
		return xNEmp;
	}

	public void setxNEmp(String xNEmp) {
		this.xNEmp = xNEmp;
	}

	public String getxPed() {
		return xPed;
	}

	public void setxPed(String xPed) {
		this.xPed = xPed;
	}

	public String getxCont() {
		return xCont;
	}

	public void setxCont(String xCont) {
		this.xCont = xCont;
	}

}
