package org.adempierelbr.nfe.v200.beans.inutilizacao.retorno;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("infInut")
public class RetInfInutBean {
	
	@XStreamAsAttribute
	private String Id;
	
	private int tpAmb;
	private String verAplic;
	private int cStat;
	private String xMotivo;
	private int cUF;
	private int ano;
	private String CNPJ;
	private int mod;
	private int serie;
	private long nNFIni;
	private long nNFFin;
	private String dhRecbto;
	private String nProt;

	public RetInfInutBean() {
	}

	public void setId(String id) {
		this.Id = id;
	}

	public String getId() {
		return Id;
	}

	public void setTpAmb(int tpAmb) {
		this.tpAmb = tpAmb;
	}

	public int getTpAmb() {
		return tpAmb;
	}

	public void setVerAplic(String verAplic) {
		this.verAplic = verAplic;
	}

	public String getVerAplic() {
		return verAplic;
	}

	public void setCStat(int cStat) {
		this.cStat = cStat;
	}

	public int getCStat() {
		return cStat;
	}

	public void setXMotivo(String xMotivo) {
		this.xMotivo = xMotivo;
	}

	public String getXMotivo() {
		return xMotivo;
	}

	public void setCUF(int cUF) {
		this.cUF = cUF;
	}

	public int getCUF() {
		return cUF;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getAno() {
		return ano;
	}

	public void setCNPJ(String cNPJ) {
		this.CNPJ = cNPJ;
	}

	public String getCNPJ() {
		return CNPJ;
	}

	public void setMod(int mod) {
		this.mod = mod;
	}

	public int getMod() {
		return mod;
	}

	public void setSerie(int serie) {
		this.serie = serie;
	}

	public int getSerie() {
		return serie;
	}

	public void setNNFIni(long nNFIni) {
		this.nNFIni = nNFIni;
	}

	public long getNNFIni() {
		return nNFIni;
	}

	public void setNNFFin(long nNFFin) {
		this.nNFFin = nNFFin;
	}

	public long getNNFFin() {
		return nNFFin;
	}

	public void setDhRecbto(String dhRecbto) {
		this.dhRecbto = dhRecbto;
	}

	public String getDhRecbto() {
		return dhRecbto;
	}

	public void setNProt(String nProt) {
		this.nProt = nProt;
	}

	public String getNProt() {
		return nProt;
	}

}
