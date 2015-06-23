package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis;

/**
 * Classe responsavel por determinar qual as informacoes estarao contidas
 *  no XML a respeito do PIS (NFe 2.0)
 * @author Fernando de O. Moraes (fernando.moraes @ faire.com.br)
 *
 */
public class PISFactory {
	
	//(base de cálculo = valor da operação alíquota normal	(cumulativo/não cumulativo));
	public static final String OperacaoTributavel01 = "01"; 
	
	//(base de cálculo = valor da operação	(alíquota diferenciada));
	public static final String OperacaoTributavel02 = "02"; 
	
	//(base de cálculo = quantidade vendida x alíquota por unidade	de produto);
	public static final String OperacaoTributavel03 = "03";

	//04 - Operação Tributável (tributação monofásica	(alíquota zero));
	public static final String OperacaoTributavel04 = "04";
	
	//06 - Operação Tributável 	(alíquota zero);
	public static final String OperacaoTributavel06 = "06";
	
	//	07 - Operação Isenta da Contribuição;
	public static final String OperacaoTributavel07 = "07";
	
	//08 - Operação Sem Incidência 	da Contribuição;
	public static final String SemIncidencia = "08";
	
	//09 - Operação com Suspensão 	da Contribuição;
	public static final String SuspensaoContribuicao = "09";
	
	//99 - Outras
	public static final String Outras = "99";
	/**
	 * 
	 * @param CST
	 * @return
	 */
	public static INFePISBean getNFeBeanPIS(String CST){
		if (CST.equalsIgnoreCase(OperacaoTributavel01) 
				|| (CST.equalsIgnoreCase(OperacaoTributavel02)))
			return new NFePISAliqBean();
		else if (CST.equalsIgnoreCase(OperacaoTributavel03))
			return new NFePISQtde();	
		else if (CST.equalsIgnoreCase(OperacaoTributavel04) 
				||  CST.equalsIgnoreCase(OperacaoTributavel06)
				||  CST.equalsIgnoreCase(OperacaoTributavel07)
				||  CST.equalsIgnoreCase(SemIncidencia)
				||  CST.equalsIgnoreCase(SuspensaoContribuicao)
				)
			return new NFePISNT();	
		
		else if (CST.equalsIgnoreCase(Outras))
			return new NFePISOutr();	
					

		else throw new IllegalArgumentException("C�digo CST inv�lido: " + CST);
	}
}
