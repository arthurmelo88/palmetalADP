package org.adempierelbr.nfe.v200.beans.infnfe.det.prod;

import java.util.ArrayList;
import java.util.List;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Classe que representa o produto do XML  NFe 2.0
 * @author Fernando de O.Moraes (fernando.moraes @ faire)
 */

@XStreamAlias("prod")
public class NFeProdBean implements INFeBean {
	private String cProd;
	private String cEAN;
	private String xProd;
	private String NCM;
	private String EXTIPI;
	private String CFOP;
	private String uCom;
	private String qCom;
	private String vUnCom;
	private String vProd;
	private String cEANTrib;
	private String uTrib;
	private String qTrib;
	private String vUnTrib;
	private String vFrete;

	private String vSeg;
	private String vDesc;
	private String vOutro;
	private String indTot;

	private String xPed;
	private String nItemPed;

	@XStreamImplicit
	private List<NFeDIBean> DI;

	public NFeProdBean() {
	}

	public String getCProd() {
		return cProd;
	}

	public void setCProd(String prod) {
		cProd = prod;
	}

	public String getCEAN() {
		return cEAN;
	}

	public void setCEAN(String cean) {
		cEAN = cean;
	}

	public String getXProd() {
		return xProd;
	}

	public void setXProd(String prod) {
		xProd = prod;
	}

	public String getNCM() {
		return NCM;
	}

	public void setNCM(String ncm) {
		NCM = ncm;
	}

	public String getEXTIPI() {
		return EXTIPI;
	}

	public void setEXTIPI(String extipi) {
		EXTIPI = extipi;
	}

	public String getCFOP() {
		return CFOP;
	}

	public void setCFOP(String cfop) {
		CFOP = cfop;
	}

	public String getUCom() {
		return uCom;
	}

	public void setUCom(String com) {
		uCom = com;
	}

	public String getQCom() {
		return qCom;
	}

	public void setQCom(String com) {
		qCom = com;
	}

	public String getVUnCom() {
		return vUnCom;
	}

	public void setVUnCom(String unCom) {
		vUnCom = unCom;
	}

	public String getVProd() {
		return vProd;
	}

	public void setVProd(String prod) {
		vProd = prod;
	}

	public String getCEANTrib() {
		return cEANTrib;
	}

	public void setCEANTrib(String trib) {
		cEANTrib = trib;
	}

	public String getUTrib() {
		return uTrib;
	}

	public void setUTrib(String trib) {
		uTrib = trib;
	}

	public String getQTrib() {
		return qTrib;
	}

	public void setQTrib(String trib) {
		qTrib = trib;
	}

	public String getVUnTrib() {
		return vUnTrib;
	}

	public void setVUnTrib(String unTrib) {
		vUnTrib = unTrib;
	}

	public String getVFrete() {
		return vFrete;
	}

	public void setVFrete(String frete) {
		vFrete = frete;
	}

	public String getVSeg() {
		return vSeg;
	}

	public void setVSeg(String seg) {
		vSeg = seg;
	}

	public String getVDesc() {
		return vDesc;
	}

	public void setVDesc(String desc) {
		vDesc = desc;
	}

	public String getVOutro() {
		return vOutro;
	}

	public void setVOutro(String outro) {
		vOutro = outro;
	}

	public String getIndTot() {
		return indTot;
	}

	public void setIndTot(String indTot) {
		this.indTot = indTot;
	}

	public String getXPed() {
		return xPed;
	}

	public void setXPed(String ped) {
		xPed = ped;
	}

	public String getNItemPed() {
		return nItemPed;
	}

	public void setNItemPed(String itemPed) {
		nItemPed = itemPed;
	}

	public List<NFeDIBean> getDI() {
		if (DI == null) {
			DI = new ArrayList<NFeDIBean>();
		}
		return this.DI;
	}

}
