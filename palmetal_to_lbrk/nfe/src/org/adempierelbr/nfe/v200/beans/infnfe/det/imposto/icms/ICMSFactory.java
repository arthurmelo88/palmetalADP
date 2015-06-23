package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;


/**
 * Classe responsavel por determinar qual as informacoes estarao contidas
 *  no XML a respeito do ICMS (NFe 2.0)
 * @author Fernando de O. Moraes (fernando.moraes @ faire.com.br)
 *
 */
public class ICMSFactory {

	public static final String TribIntegralmente = "00";
	public static final String TribEComCobrDoICMSPorSubstTrib = "10";
	public static final String ComReducaoDeBaseDeCalculo = "20";
	public static final String IsentaNaoTribComCobrDeICMSPorSubstTrib = "30";
	public static final String Isenta = "40";
	public static final String NaoTributada = "41";
	public static final String Suspensao = "50";
	public static final String Deferimento = "51";
	public static final String ICMSCobradoAnteriormentePorSubstTrib = "60";
	public static final String RedDeBCECobICMSSubstTrib = "70";
	public static final String Outros = "90";
	public static final String IMPOSTO_ICMSSN_CSOSN_102_TributadaPeloSimplesSemPermissaoDeCredito = "102";
	public static final String IMPOSTO_ICMSSN_CSOSN_103_IsencaoDoICMSNoSimplesParaFaixaDeReceitaBruta = "103";
	public static final String IMPOSTO_ICMSSN_CSOSN_300_Imune = "300";
	public static final String IMPOSTO_ICMSSN_CSOSN_400_NaoTributadaPeloSimples = "400";
	public static final String IMPOSTO_ICMSSN_CSOSN_202_TribSimplesSemPermCredCCobrDoICMSST = "202";
	public static final String IMPOSTO_ICMSSN_CSOSN_203_IsenICMSSimpFaixaDeRecBrtComCobICMSST = "203";
	public static final Object IMPOSTO_ICMSSN_CSOSN_101_TributadaPeloSimplesComPermissaoDeCredito = "101";
	public static final Object IMPOSTO_ICMSSN_CSOSN_201_TribSimplesComPermCredCCobrDoICMSST = "201";
	public static final Object IMPOSTO_ICMSSN_CSOSN_500_ICMSCobAntPorSubstTribSubstituidoOuAntec = "500";
	public static final Object IMPOSTO_ICMSSN_CSOSN_900_Outros = "900";

	/**
	 * 
	 * @param CST
	 * @return
	 */
	public static INFeICMSBean getNFeBeanICMS(String CST){
		if (CST.equalsIgnoreCase(TribIntegralmente))
			return new NFeICMS00Bean();
		else if (CST.equalsIgnoreCase(TribEComCobrDoICMSPorSubstTrib))
			return new NFeICMS10Bean();
		else if (CST.equalsIgnoreCase(ComReducaoDeBaseDeCalculo))
			return new NFeICMS20Bean();	
		else if (CST.equalsIgnoreCase(IsentaNaoTribComCobrDeICMSPorSubstTrib))
			return new NFeICMS30Bean();	
		else if (CST.equalsIgnoreCase(Isenta) 
				||  CST.equalsIgnoreCase(NaoTributada)
				||  CST.equalsIgnoreCase(Suspensao)
				)
			return new NFeICMS40Bean();		
		else if (CST.equalsIgnoreCase(Deferimento))
			return new NFeICMS51Bean();	
		else if (CST.equalsIgnoreCase(ICMSCobradoAnteriormentePorSubstTrib))
			return new NFeICMS60Bean();						
		else if (CST.equalsIgnoreCase(RedDeBCECobICMSSubstTrib))
			return new NFeICMS70Bean();							
		else if (CST.equalsIgnoreCase(Outros))
			return new NFeICMS90Bean();
		else throw new IllegalArgumentException("Código CST inválido: " + CST);
	}
}
