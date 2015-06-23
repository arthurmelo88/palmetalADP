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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.Properties;

import org.adempiere.model.POWrapper;
import org.adempierelbr.wrapper.I_W_C_Tax;
import org.compiere.model.MTax;
import org.compiere.util.Env;

/**
 * 		C�lculo do Imposto por Linha
 * 
 * 	@author Ricardo Santana (Kenos, www.kenos.com.br)
 * 			<li> Sponsored by Soliton, www.soliton.com.br
 *	@version $Id: MLBRTaxLine.java, v1.0 2011/04/20 7:49:43 PM, ralexsander Exp $
 */
@SuppressWarnings("rawtypes")
public class MLBRTaxLine extends X_LBR_TaxLine implements Comparator, Comparable
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
	public MLBRTaxLine (Properties ctx, int X_LBR_TaxLine_ID, String trxName)
	{
		super (ctx, X_LBR_TaxLine_ID, trxName);
	}	//	MLBRTaxLine

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *  @param trxName transaction
	 */
	public MLBRTaxLine (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MLBRTaxLine
	
	/**
	 * 	Tax Child for Tax Name
	 * 	@param C_Tax_ID
	 * 	@return tax child or -1 if not found
	 */
	public int getChild_Tax_ID (int C_Tax_ID)
	{
		MTax parent = new MTax (getCtx(), C_Tax_ID, get_TrxName());
		
		if (!parent.isSummary())
			return -1;
		
		for (MTax t : parent.getChildTaxes(true))
		{
			I_W_C_Tax tW = POWrapper.create(t, I_W_C_Tax.class);
			if (tW.getLBR_TaxName_ID() == getLBR_TaxName_ID())
				return tW.getC_Tax_ID();
		}
		return -1;
	}	//	getChild_Tax_ID
	
	/**
	 * 	Order for tax calculation
	 * 
	 * 	@return Order based on TaxType or -1
	 */
	private Integer getOrder ()
	{
		if (X_LBR_TaxName.LBR_TAXTYPE_Service.equals(getLBR_TaxName().getlbr_TaxType()))
			return 0;
		else if (X_LBR_TaxName.LBR_TAXTYPE_Product.equals(getLBR_TaxName().getlbr_TaxType()))
			return 1;
		else if (X_LBR_TaxName.LBR_TAXTYPE_Substitution.equals(getLBR_TaxName().getlbr_TaxType()))
			return 2;
		//
		return -1;
	}	//	getOrder
	
	/**
	 * 	Order for tax calculation	
	 */
	public int compare (Object o1, Object o2)
	{
		if (o1 instanceof MLBRTaxLine
				&& o2 instanceof MLBRTaxLine)
		{
			MLBRTaxLine taxLine = (MLBRTaxLine) o1;
			MLBRTaxLine taxLine2 = (MLBRTaxLine) o2;
			//
			return taxLine.getOrder().compareTo(taxLine2.getOrder());
		}
		//
		return 0;
	}	//	compare

	/**
	 * 		Comparador necess�rio pois os impostos do tipo 
	 * 	Substitui��o Tribut�ria precisam ser calculados por �ltimo.
	 */
	public int compareTo (Object arg0)
	{
		return compare (this, arg0);
	}	//	compareTo
	
	/**
	 * 		Copia os impostos para um novo TL
	 */
	public MLBRTaxLine copy ()
	{
		MLBRTaxLine tl = new MLBRTaxLine (Env.getCtx(), 0, null);
		super.copyValues(this, tl);
		tl.setLBR_Tax_ID(0);
		//
		return tl;
	}	//	copy
	
	/**
	 * 	To String
	 */
	public String toString()
	{
		if (getLBR_TaxName() == null)
			return super.toString();
		//
		return "MLBRTaxLine[ID=" + getLBR_TaxLine_ID() + ", Name=" + ("" + getLBR_TaxName().getName()).trim() + ", Rate=" + getlbr_TaxRate().setScale(4, BigDecimal.ROUND_HALF_UP) + ", IsTaxIncluded=" + isTaxIncluded()  + "]";
	}	//	toString
}	//	MLBRTaxLine