package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Grupo do Imposto de importacao
 * 
 * @author fernandom
 * 
 */

@XStreamAlias("II")
public class NFeIIBeans implements INFeBean {

	private String vBC;
	private String vDespAdu;
	private String vII;
	private String vIOF;

	public String getVBC() {
		return vBC;
	}

	public void setVBC(String vbc) {
		vBC = vbc;
	}

	public String getVDespAdu() {
		return vDespAdu;
	}

	public void setVDespAdu(String despAdu) {
		vDespAdu = despAdu;
	}

	public String getVII() {
		return vII;
	}

	public void setVII(String vii) {
		vII = vii;
	}

	public String getVIOF() {
		return vIOF;
	}

	public void setVIOF(String viof) {
		vIOF = viof;
	}

}
