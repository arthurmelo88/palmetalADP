package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.ipi;

import org.adempierelbr.nfe.v200.beans.INFeBean;

/**
 * Classe responsavel por determinar qual as informacoes estarao contidas
 *  no XML a respeito do IPI (NFe 2.0)
 * @author Fernando de O. Moraes (fernando.moraes @ faire.com.br)
 *
 */
public class IPIFactory {
	
	public static final String EntradaComRecuperacaoDeCredito = "00";
	public static final String EntradaTributadaComAliquotaZero = "01";
	public static final String EntradaIsenta = "02";
	public static final String EntradaNao_Tributada = "03";
	public static final String EntradaImune = "04";
	public static final String EntradaComSuspensao = "05";
	public static final String OutrasEntradas = "49";
	public static final String SaidaTributada = "50";
	public static final String SaidaTributadaComAliquotaZero = "51";
	public static final String SaidaIsenta = "52";
	public static final String SaidaNao_Tributada = "53";
	public static final String SaidaImune = "54";
	public static final String SaidaComSuspensao = "55";
	public static final String OutrasSaidas = "99";
	
	/**
	 * 
	 * @param CST
	 * @return
	 */
	public static INFeBean getNFeBeanIPI(String CST){
		if (CST.equals(EntradaComRecuperacaoDeCredito)
				|| CST.equals(OutrasEntradas)
				|| CST.equals(SaidaTributada)
				|| CST.equals(OutrasSaidas)
				)
		{
			return new NFeIPITribBean();
		}
		else if(
				CST.equals(EntradaTributadaComAliquotaZero)
				|| CST.equals(EntradaIsenta)
				|| CST.equals(EntradaNao_Tributada)
				|| CST.equals(EntradaImune)
				|| CST.equals(EntradaComSuspensao)
				|| CST.equals(SaidaTributadaComAliquotaZero)
				|| CST.equals(SaidaIsenta)
				|| CST.equals(SaidaNao_Tributada)
				|| CST.equals(SaidaImune)
				|| CST.equals(SaidaComSuspensao)
		)
		{
			return new NFeIPINTBean();
		}
			
		else throw new IllegalArgumentException("Codigo CST invalido: " + CST);
	}

}
