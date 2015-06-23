package org.adempierelbr.nfe.v200.beans.infnfe.transporte;

import java.util.ArrayList;
import java.util.List;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("transp")
public class NFeTranspBean implements INFeBean {

	private String modFrete;
	private NFeTransportadorBean transporta;
	private NFeRetTranspBean retTransp;
	private NFeVeicTranspBean veicTransp; //informar o veiculo trator
	
	@XStreamImplicit
	private List<NFeReboqueBean> reboque; //informar os reboques 0-5
	
	@XStreamImplicit
	private List<NFeVolBean> vol;

	@XStreamImplicit
	private List<NFeLacreBean> lacres;

	public String getModFrete() {
		return modFrete;
	}

	public void setModFrete(String modFrete) {
		this.modFrete = modFrete;
	}

	public NFeTransportadorBean getTransporta() {
		return transporta;
	}

	public void setTransporta(NFeTransportadorBean transporta) {
		this.transporta = transporta;
	}

	public NFeRetTranspBean getRetTransp() {
		return retTransp;
	}

	public void setRetTransp(NFeRetTranspBean retTransp) {
		this.retTransp = retTransp;
	}

	public NFeVeicTranspBean getVeicTransp() {
		return veicTransp;
	}

	public void setVeicTransp(NFeVeicTranspBean veicTransp) {
		this.veicTransp = veicTransp;
	}

	public List<NFeReboqueBean> getReboque() {
		if (reboque == null) {
			reboque = new ArrayList<NFeReboqueBean>();
		}
		return this.reboque;
	
	}


	public List<NFeVolBean> getVol() {
		if (vol == null) {
			vol = new ArrayList<NFeVolBean>();
		}
		return this.vol;
	}


	public List<NFeLacreBean> getLacres() {
		if (lacres == null) {
			lacres = new ArrayList<NFeLacreBean>();
		}
		return this.lacres;
	}
	
	
	public void setReboque(List<NFeReboqueBean> reboque) {
		this.reboque = reboque;
	}

	public void setVol(List<NFeVolBean> vol) {
		this.vol = vol;
	}

	public void setLacres(List<NFeLacreBean> lacres) {
		this.lacres = lacres;
	}
	
	

}
