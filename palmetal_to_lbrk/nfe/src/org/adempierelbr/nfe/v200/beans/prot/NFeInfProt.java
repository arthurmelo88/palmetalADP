package org.adempierelbr.nfe.v200.beans.prot;

import org.adempierelbr.nfe.v200.beans.INFeBean;
import org.adempierelbr.nfe.v200.beans.signature.NFeSignature;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Informações do Protocolo de resposta. TAG a ser assinada
 * @author Fernando
 *
 */


@XStreamAlias("infProt")
public class NFeInfProt implements INFeBean {

	@XStreamAlias("Id")
	private String id;
	
	@XStreamAlias("tpAmb")
	private String tipoAmbiente;

	private String verAplic;
	private String chNFe;
	private String dhRecbto;
	private String nProt;
	private String digVal;
	private String cStat;
	private String xMotivo;
	
	@XStreamAlias("Signature")
	private NFeSignature signature;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTipoAmbiente() {
		return tipoAmbiente;
	}

	public void setTipoAmbiente(String tipoAmbiente) {
		this.tipoAmbiente = tipoAmbiente;
	}

	public String getVerAplic() {
		return verAplic;
	}

	public void setVerAplic(String verAplic) {
		this.verAplic = verAplic;
	}

	public String getChNFe() {
		return chNFe;
	}

	public void setChNFe(String chNFe) {
		this.chNFe = chNFe;
	}

	public String getDhRecbto() {
		return dhRecbto;
	}

	public void setDhRecbto(String dhRecbto) {
		this.dhRecbto = dhRecbto;
	}

	public String getnProt() {
		return nProt;
	}

	public void setnProt(String nProt) {
		this.nProt = nProt;
	}

	public String getDigVal() {
		return digVal;
	}

	public void setDigVal(String digVal) {
		this.digVal = digVal;
	}

	public String getcStat() {
		return cStat;
	}

	public void setcStat(String cStat) {
		this.cStat = cStat;
	}

	public String getxMotivo() {
		return xMotivo;
	}

	public void setxMotivo(String xMotivo) {
		this.xMotivo = xMotivo;
	}

	public NFeSignature getSignature() {
		return signature;
	}

	public void setSignature(NFeSignature signature) {
		this.signature = signature;
	}
	
	
}
