package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * PIS
 * @author Fernando de O. Moraes (fernando.moraes @ faire.com.br)
 *
 */

@XStreamAlias("PIS")
public class NFePISBean implements INFeBean {
	
	private INFeBean pis;

	public INFeBean getPis() {
		return pis;
	}

	public void setPis(INFeBean pis) {
		this.pis = pis;
	}


}
