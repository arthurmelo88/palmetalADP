package org.adempierelbr.nfe.v200.beans.totais;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("total")
public class NFeTotaisBean implements INFeBean {

	private NFeICMSTotBean ICMSTot;
	private ISSQNTotBean ISSQNtot;
	private NFeRetTribBean retTrib;

	public NFeICMSTotBean getICMSTot() {
		return ICMSTot;
	}

	public void setICMSTot(NFeICMSTotBean iCMSTot) {
		ICMSTot = iCMSTot;
	}

	public ISSQNTotBean getISSQNtot() {
		return ISSQNtot;
	}

	public void setISSQNtot(ISSQNTotBean iSSQNtot) {
		ISSQNtot = iSSQNtot;
	}

	public NFeRetTribBean getRetTrib() {
		return retTrib;
	}

	public void setRetTrib(NFeRetTribBean retTrib) {
		this.retTrib = retTrib;
	}

}
