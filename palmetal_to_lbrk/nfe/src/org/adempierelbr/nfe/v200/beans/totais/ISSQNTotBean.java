package org.adempierelbr.nfe.v200.beans.totais;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ISSQNtot")
public class ISSQNTotBean implements INFeBean {

	private String vServ;
	private String vBC;
	private String vISS;
	private String vPIS;
	private String vCOFINS;
	
	public String getvServ() {
		return vServ;
	}
	public void setvServ(String vServ) {
		this.vServ = vServ;
	}
	public String getvBC() {
		return vBC;
	}
	public void setvBC(String vBC) {
		this.vBC = vBC;
	}
	public String getvISS() {
		return vISS;
	}
	public void setvISS(String vISS) {
		this.vISS = vISS;
	}
	public String getvPIS() {
		return vPIS;
	}
	public void setvPIS(String vPIS) {
		this.vPIS = vPIS;
	}
	public String getvCOFINS() {
		return vCOFINS;
	}
	public void setvCOFINS(String vCOFINS) {
		this.vCOFINS = vCOFINS;
	}
}
