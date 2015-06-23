package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto;

import org.adempierelbr.nfe.v200.beans.INFeBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.ipi.NFeIPINTBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.ipi.NFeIPITribBean;

/**
 * IPI
 * @author Fernando de O. Moraes (fernando.moraes @ faire.com.br)
 *
 */

public class NFeIPIBean implements INFeBean {
	
	private String clEnq;
	private String CNPJProd;
	private String cSelo;
	private String qSelo;
	private String cEnq;
	private NFeIPITribBean IPITrib;
	private NFeIPINTBean IPINT;

	public String getClEnq() {
		return clEnq;
	}

	public void setClEnq(String clEnq) {
		this.clEnq = clEnq;
	}

	public String getCNPJProd() {
		return CNPJProd;
	}

	public void setCNPJProd(String prod) {
		CNPJProd = prod;
	}

	public String getCSelo() {
		return cSelo;
	}

	public void setCSelo(String selo) {
		cSelo = selo;
	}

	public String getQSelo() {
		return qSelo;
	}

	public void setQSelo(String selo) {
		qSelo = selo;
	}

	public String getCEnq() {
		return cEnq;
	}

	public void setCEnq(String enq) {
		cEnq = enq;
	}

	public NFeIPITribBean getIPITrib() {
		return IPITrib;
	}

	public void setIPITrib(NFeIPITribBean trib) {
		IPITrib = trib;
	}

	public NFeIPINTBean getIPINT() {
		return IPINT;
	}

	public void setIPINT(NFeIPINTBean ipint) {
		IPINT = ipint;
	}

}
