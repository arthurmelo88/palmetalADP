/******************************************************************************
 * Copyright (C) 2011 Kenos Assessoria e Consultoria de Sistemas Ltda         *
 * Copyright (C) 2011 Ricardo Santana                                         *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.adempierelbr.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * 		F�rmulas relacionadas no cadastro dos impostos
 * 
 * 	@author Ricardo Santana (Kenos, www.kenos.com.br)
 * 			<li> Sponsored by Soliton, www.soliton.com.br
 *	@version $Id: MLBRTaxFormula.java, v1.0 2011/04/20 7:45:45 PM, ralexsander Exp $
 */
public class MLBRTaxFormula extends X_LBR_TaxFormula
{
	/**
	 * 	Serial
	 */
	private static final long serialVersionUID = -3454360861235382468L;

	/**************************************************************************
	 *  Default Constructor
	 *  @param Properties ctx
	 *  @param int ID (0 create new)
	 *  @param String trx
	 */
	public MLBRTaxFormula (Properties ctx, int X_LBR_TaxFormula_ID, String trxName)
	{
		super (ctx, X_LBR_TaxFormula_ID, trxName);
	}	//	MLBRTaxFormula

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *  @param trxName transaction
	 */
	public MLBRTaxFormula (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MLBRTaxFormula
	
	/**
	 * 		Retorna a formula para c�lculo do fator. 
	 * 	Este fator multiplicado pelo valor ir� gerar a base de c�lculo para c�lculo do imposto.
	 * 
	 * 	@param isTaxIncluded
	 * 	@return formula
	 */
	public String getFormula (boolean isTaxIncluded)
	{
		if (isTaxIncluded)
			return getFormula ();
		else
			return getFormulaNetWorth ();
	}	//	getFormula
		
	/**
	 * 	Load the formula
	 * 	@return formula
	 */
	public String getFormula ()
	{
		if (getLBR_Formula_ID() > 0)
			return getLBR_Formula().getlbr_Formula();
		else
			return getlbr_Formula();
	}	//	getFormula
	
	/**
	 * 	Load the formula
	 * 	@return formula
	 */
	public String getFormulaNetWorth ()
	{
		if (getLBR_FormulaNet_ID() > 0)
			return getLBR_FormulaNet().getlbr_Formula();
		else
			return getlbr_FormulaNetWorth();
	}	//	getFormulaNetWorth
	
	/**
	 * 	To String
	 */
	public String toString ()
	{
		if (getLBR_Formula() == null)
			return super.toString();
		//
		return "MLBRTaxFormula [IsTaxIncluded=" + isTaxIncluded() + ", Name=" + getLBR_Formula().getName() + ", Formula=" +getLBR_Formula().getlbr_Formula() + "]";
	}	//	toString
}	//	MLBRTaxFormula