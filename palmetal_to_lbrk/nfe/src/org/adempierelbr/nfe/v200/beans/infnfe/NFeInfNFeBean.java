package org.adempierelbr.nfe.v200.beans.infnfe;

import java.util.ArrayList;
import java.util.List;

import org.adempierelbr.nfe.v200.beans.INFeBean;
import org.adempierelbr.nfe.v200.beans.infnfe.cobranca.NFeCobrBean;
import org.adempierelbr.nfe.v200.beans.infnfe.compra.NFeCompraBean;
import org.adempierelbr.nfe.v200.beans.infnfe.dest.NFeDestBean;
import org.adempierelbr.nfe.v200.beans.infnfe.emit.NFeEmitBean;
import org.adempierelbr.nfe.v200.beans.infnfe.infadic.NFeInfAdicBean;
import org.adempierelbr.nfe.v200.beans.infnfe.transporte.NFeTranspBean;
import org.adempierelbr.nfe.v200.beans.totais.NFeTotaisBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("infNFe")
public class NFeInfNFeBean implements INFeBean {

	@XStreamAsAttribute
	private String versao;

	@XStreamAsAttribute
	@XStreamAlias("Id")
	private String id;

	private NFeIdeBean ide;
	private NFeEmitBean emit;
	private NFeDestBean dest;
	private NFeLRetiradaBean retirada;
	private NFeLEntregaBean entrega;
	
	@XStreamImplicit
	private List<NFeDetBean> det;
	
    private NFeTotaisBean total;
    private NFeTranspBean transp;
    private NFeCobrBean cobr;
    private NFeInfAdicBean infAdic;
    
    // private NFeAvulsaBean avulsa; - identificacao do fisco emitente
	// private NFeExportaBean exporta;
	 private NFeCompraBean compra;
    // private NFeCanaBean compra;

	public NFeInfAdicBean getInfAdic() {
		return infAdic;
	}

	public void setInfAdic(NFeInfAdicBean infAdic) {
		this.infAdic = infAdic;
	}

	public NFeCobrBean getCobr() {
		return cobr;
	}

	public void setCobr(NFeCobrBean cobr) {
		this.cobr = cobr;
	}

	public NFeTranspBean getTransp() {
		return transp;
	}

	public void setTransp(NFeTranspBean transp) {
		this.transp = transp;
	}

	public NFeTotaisBean getTotal() {
		return total;
	}

	public void setTotal(NFeTotaisBean total) {
		this.total = total;
	}

	public NFeLEntregaBean getEntrega() {
		return entrega;
	}

	public void setEntrega(NFeLEntregaBean entrega) {
		this.entrega = entrega;
	}

	public NFeLRetiradaBean getRetirada() {
		return retirada;
	}

	public void setRetirada(NFeLRetiradaBean retirada) {
		this.retirada = retirada;
	}

	public NFeDestBean getDest() {
		return dest;
	}

	public void setDest(NFeDestBean dest) {
		this.dest = dest;
	}

	public NFeEmitBean getEmit() {
		return emit;
	}

	public void setEmit(NFeEmitBean emit) {
		this.emit = emit;
	}

	public NFeIdeBean getIde() {
		return ide;
	}

	public void setIde(NFeIdeBean ide) {
		this.ide = ide;
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<NFeDetBean> getDet() {
		if (det == null) {
			det = new ArrayList<NFeDetBean>();
		}
		return this.det;
	}

	public void setDet(List<NFeDetBean> det) {
		this.det = det;
	}

	public NFeCompraBean getCompra() {
		return compra;
	}

	public void setCompra(NFeCompraBean compra) {
		this.compra = compra;
	}

	

}
