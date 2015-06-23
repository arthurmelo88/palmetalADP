package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * COFINS
 * @author Fernando de O. Moraes (fernando.moraes @ faire.com.br)
 *
 */

@XStreamAlias("COFINS")
public class NFeCOFINSBean implements INFeBean {
	
	private INFeBean cofins;

	public INFeBean getCOFINS() {
		return cofins;
	}

	public void setCOFINS(INFeBean pis) {
		this.cofins = pis;
	}
}
