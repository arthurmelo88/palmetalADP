package org.adempierelbr.nfe.v200.beans.infnfe.cobranca;

import java.util.ArrayList;
import java.util.List;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("cobr")
public class NFeCobrBean implements INFeBean {

	private NFeFatBean fat;

	@XStreamImplicit
	private List<NFeDupBean> dup;

	public NFeFatBean getFat() {
		return fat;
	}

	public void setFat(NFeFatBean fat) {
		this.fat = fat;
	}

	public List<NFeDupBean> getDup() {
		if (dup == null) {
			dup = new ArrayList<NFeDupBean>();
		}
		return this.dup;
	}

	
	
}
