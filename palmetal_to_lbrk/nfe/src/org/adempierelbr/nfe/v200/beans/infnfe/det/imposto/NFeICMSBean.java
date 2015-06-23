package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * ICMS
 * @author Fernando de O. Moraes (fernando.moraes @ faire.com.br)
 *
 */

@XStreamAlias("ICMS")
public class NFeICMSBean implements INFeBean {
	
	private INFeBean icms;

	public INFeBean getIcms() {
		return icms;
	}

	public void setIcms(INFeBean icms) {
		this.icms = icms;
	}
}
