package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Imposto
 * @author Fernando de O. Moraes (fernando.moraes @ faire.com.br)
 *
 */

@XStreamAlias("imposto")
public class NFeImpostoBean implements INFeBean {

	
	private NFeICMSBean ICMS;
	private NFeIPIBean IPI;
	private NFeIIBeans II;
	private NFePISSTBean PISST;
	private NFeCOFINSSTBean COFINSST;
	private NFePISBean PIS;
	private NFeCOFINSBean COFINS;
	//private INFeBean ISSQN;
	
	public NFeICMSBean getICMS() {
		return ICMS;
	}
	public void setICMS(NFeICMSBean iCMS) {
		ICMS = iCMS;
	}
	public NFeIPIBean getIPI() {
		return IPI;
	}
	public void setIPI(NFeIPIBean iPI) {
		IPI = iPI;
	}
	public NFeIIBeans getII() {
		return II;
	}
	public void setII(NFeIIBeans iI) {
		II = iI;
	}
	public NFePISSTBean getPISST() {
		return PISST;
	}
	public void setPISST(NFePISSTBean pISST) {
		PISST = pISST;
	}
	public NFeCOFINSSTBean getCOFINSST() {
		return COFINSST;
	}
	public void setCOFINSST(NFeCOFINSSTBean cOFINSST) {
		COFINSST = cOFINSST;
	}
	public NFePISBean getPIS() {
		return PIS;
	}
	public void setPIS(NFePISBean pIS) {
		PIS = pIS;
	}
	public NFeCOFINSBean getCOFINS() {
		return COFINS;
	}
	public void setCOFINS(NFeCOFINSBean cOFINS) {
		COFINS = cOFINS;
	}
	
	


}
