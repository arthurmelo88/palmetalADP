package org.adempierelbr.nfe.v200.beans.infnfe;

import org.adempierelbr.nfe.v200.beans.INFeBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.NFeImpostoBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.prod.NFeProdBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("det")
public class NFeDetBean implements INFeBean {
	
	@XStreamAsAttribute
	private String nItem;
	
	private NFeProdBean prod;
	private NFeImpostoBean imposto;
	private String infAdProd;

	public String getNItem() {
		return nItem;
	}

	public void setNItem(String item) {
		nItem = item;
	}

	public NFeProdBean getProd() {
		return prod;
	}

	public void setProd(NFeProdBean prod) {
		this.prod = prod;
	}

	public NFeImpostoBean getImposto() {
		return imposto;
	}

	public void setImposto(NFeImpostoBean imposto) {
		this.imposto = imposto;
	}

	public String getInfAdProd() {
		return infAdProd;
	}

	public void setInfAdProd(String infAdProd) {
		this.infAdProd = infAdProd;
	}

}
