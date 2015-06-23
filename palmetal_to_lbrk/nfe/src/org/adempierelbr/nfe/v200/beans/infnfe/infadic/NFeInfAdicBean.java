package org.adempierelbr.nfe.v200.beans.infnfe.infadic;

import java.util.ArrayList;
import java.util.List;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("infAdic")
public class NFeInfAdicBean implements INFeBean {

	private String infAdFisco;
	private String infCpl;

	@XStreamImplicit
	private List<NFeObsContBean> obsCont;

	public String getInfAdFisco() {
		return infAdFisco;
	}

	public void setInfAdFisco(String infAdFisco) {
		this.infAdFisco = infAdFisco;
	}

	public String getInfCpl() {
		return infCpl;
	}

	public void setInfCpl(String infCpl) {
		this.infCpl = infCpl;
	}

	public List<NFeObsContBean> getDet() {
		if (obsCont == null) {
			obsCont = new ArrayList<NFeObsContBean>();
		}
		return this.obsCont;
	}

	public void setDet(List<NFeObsContBean> det) {
		this.obsCont = obsCont;
	}
	//
	// @XStreamImplicit
	// private List<NFeObsFiscoBean> obsFisco;
	//
	// @XStreamImplicit
	// private List<NFeProcRefBean> procRef;
}
