package org.adempierelbr.nfe.v200.beans.infnfe.det.prod;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Classe que representa informacoes adicionais de produto do XML  NFe 2.0
 * @author Fernando de O.Moraes (fernando.moraes @ faire)
 *
 */

@XStreamAlias("adi")
public class NFeAdiBean implements INFeBean{
	private String nAdicao;
	private String nSeqAdic;
	private String cFabricante;
	private String vDescDI;

	public String getNAdicao() {
		return nAdicao;
	}

	public void setNAdicao(String adicao) {
		nAdicao = adicao;
	}

	public String getNSeqAdic() {
		return nSeqAdic;
	}

	public void setNSeqAdic(String seqAdic) {
		nSeqAdic = seqAdic;
	}

	public String getCFabricante() {
		return cFabricante;
	}

	public void setCFabricante(String fabricante) {
		cFabricante = fabricante;
	}

	public String getVDescDI() {
		return vDescDI;
	}

	public void setVDescDI(String descDI) {
		vDescDI = descDI;
	}

}
