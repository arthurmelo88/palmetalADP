package org.adempierelbr.validator;

import java.math.BigDecimal;

import org.adempiere.model.POWrapper;
import org.adempierelbr.wrapper.I_W_C_InvoiceLine;
import org.adempierelbr.wrapper.I_W_C_Order;
import org.adempierelbr.wrapper.I_W_C_OrderLine;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * 		Utilizado para efetuar os cálculos dos impostos brasileiros.
 * 

 */
public class VLBRInvoice implements ModelValidator
{
	/**
	 *	Constructor.
	 *	The class is instanciated when logging in and client is selected/known
	 */
	public VLBRInvoice ()
	{
		super ();
	}	//	VLBRTax
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(VLBROrder.class);
	/** Client			*/
	private int		m_AD_Client_ID = -1;
	/**	Charge Type		*/
	public final static int	SISCOMEX 	= 0;
	public final static int	FREIGHT 	= 1;
	public final static int	INSURANCE 	= 2;

	/**
	 *	Initialize Validation
	 *	@param engine validation engine
	 *	@param client client
	 */
	public void initialize (ModelValidationEngine engine, MClient client)
	{
		//	Global Validator
		if (client != null) 
		{
			m_AD_Client_ID = client.getAD_Client_ID();
			log.info(client.toString());
		}
		else 
			log.info("Initializing global validator: "+this.toString());

		engine.addModelChange (MInvoiceLine.Table_Name, this);

	}	//	initialize

	/**
	 *	Get Client to be monitored
	 *	@return AD_Client_ID client
	 */
	public int getAD_Client_ID ()
	{
		return m_AD_Client_ID;
	}	//	getAD_Client_ID

	/**
	 *	User Login.
	 *	Called when preferences are set
	 *	@param AD_Org_ID org
	 *	@param AD_Role_ID role
	 *	@param AD_User_ID user
	 *	@return error message or null
	 */
	public String login (int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		log.info ("AD_User_ID=" + AD_User_ID);
		return null;
	}	//	login

	
	public String docValidate(PO po, int timing) {
		// TODO Auto-generated method stub
		return null;
	}
	
    /**
     *	Model Change of a monitored Table.
     *	Called after PO.beforeSave/PO.beforeDelete
     *	when you called addModelChange for the table
     *	@param po persistent object
     *	@param type TYPE_
     *	@return error message or null
     *	@exception Exception if the recipient wishes the change to be not accept.
     */
	public String modelChange (PO po, int type) throws Exception
	{
		/**
		 * 	Replica o valor do frete lançado no cabeçalho para uma linha
		 */
		if (po.get_TableName().equals(MInvoiceLine.Table_Name))
			return modelChange ((MInvoiceLine) po, type);
		
		
		return null;
	}	//	modelChange
	
	/**
	 *	Model Change of a monitored Table.
	 *	Called after PO.beforeSave/PO.beforeDelete
	 *	when you called addModelChange for the table
	 *	@param po persistent object
	 *	@param type TYPE_
	 *	@return error message or null
	 *	@exception Exception if the recipient wishes the change to be not accept.
	 */
	public String modelChange (MInvoiceLine iLine, int type) throws Exception
	{
		/**
		 * 	Divide o valor do frete nas linhas
		 */
		if (isChangeAffectFreight (iLine,type)){
			MInvoice inv = new MInvoice (iLine.getCtx(),iLine.getC_Invoice_ID(),iLine.get_TrxName());
			recalcuteFreight (inv);
		}
		
		return null;
	}	//	modelChange



	/**
	 * 	Verify if freight must be recalculated to all lines
	 * 	@param order
	 * 	@return true if must recalculate
	 */
	private boolean isChangeAffectFreight (MInvoiceLine iLine, int type)
	{
		int M_ProductFreight_ID = MClientInfo.get(Env.getCtx()).getM_ProductFreight_ID();
		if (iLine.getM_Product_ID() > 0		&& (iLine.getM_Product_ID() == M_ProductFreight_ID)){
			
			if (type == TYPE_AFTER_NEW || type == TYPE_AFTER_CHANGE){
				if (type == TYPE_AFTER_CHANGE && iLine.is_ValueChanged (I_W_C_InvoiceLine.COLUMNNAME_LineNetAmt))
					return true;
				else if (type == TYPE_AFTER_NEW)
					return true;
			}else if (type==TYPE_AFTER_DELETE){
				return true;
			}
			
		}
	
		//
		return false;
	}	//	isChangeAffectTaxes

	/**
	 * 	Refaz os cálculos de Frete por linha
	 * 
	 * 	@param order Pedido
	 */
	private void recalcuteFreight (MInvoice inv)
	{
		BigDecimal freightAmt = Env.ZERO;
		
		int M_ProductFreight_ID = MClientInfo.get(Env.getCtx()).getM_ProductFreight_ID();
		
		//	Total da Fatura sem considerar o valor do frete
		BigDecimal totalLines = Env.ZERO;
		
		//	Compõe o TotalLines
		for (MInvoiceLine l : inv.getLines())
		{
			if (l.getM_Product_ID() > 0 
					&& l.getM_Product_ID() != M_ProductFreight_ID
					&& l.getM_Product().getProductType().equals(MProduct.PRODUCTTYPE_Item))
				totalLines = totalLines.add(l.getLineNetAmt());
			
			else if ((l.getM_Product_ID() > 0 && l.getM_Product_ID() == M_ProductFreight_ID)){
				freightAmt= freightAmt.add(l.getLineNetAmt());
			}
		}
		
		//	Rateia o Frete
		for (MInvoiceLine l : inv.getLines())
		{
			//	Não ratear a linha do frete e para serviços
			if (l.getM_Product_ID() == 0 
					|| l.getM_Product_ID() == M_ProductFreight_ID
					|| !l.getM_Product().getProductType().equals(MProduct.PRODUCTTYPE_Item)
					|| l.getLineNetAmt().compareTo(Env.ZERO) == 0)
				continue;
			
			//	Faz o rateiro do valor do frete
			BigDecimal lineAmt 			= l.getLineNetAmt();
			BigDecimal lineFreightAmt 	= lineAmt.multiply(freightAmt).divide(totalLines, 17, BigDecimal.ROUND_HALF_UP);
			
			I_W_C_InvoiceLine iLineW = POWrapper.create(l, I_W_C_InvoiceLine.class);

			//
			iLineW.setFreightAmt(lineFreightAmt);
			//
			l.save();
		}
		

	}	//	recalcuteInvoice
	

	
	/**
	 * 		Retorna o Valor de Todas as Despesas
	 * 
	 * 	@param po	-	Documento
	 * 	@param chargeType 	-	Tipo de Valor
	 * 	@return	Valor
	 */
	public static BigDecimal getChargeAmt (PO po)
	{
		return 		  getChargeAmt (po, SISCOMEX)
				.add (getChargeAmt (po, FREIGHT))
				.add (getChargeAmt (po, INSURANCE));
	}	//	getChargeAmt
	
	/**
	 * 		Retorna o Valor da Despesa especificada
	 * 
	 * 	@param po	-	Documento
	 * 	@param chargeType 	-	Tipo de Valor
	 * 	@return	Valor
	 */
	public static BigDecimal getChargeAmt (PO po, int chargeType)
	{
		//	Total da NF sem considerar o valor do seguro
		BigDecimal amount = Env.ZERO;
		
		//	Compõe o Total do Seguro para o Pedido
		if (MOrder.Table_Name.equals(po.get_TableName()))
			for (MOrderLine ol : ((MOrder) po).getLines())
			{
				I_W_C_OrderLine olW = POWrapper.create(ol, I_W_C_OrderLine.class);
				//
				if (ol.getM_Product_ID() > 0 
						&& ol.getM_Product().getProductType().equals(MProduct.PRODUCTTYPE_Item))
				{
					if (chargeType == SISCOMEX)
						amount = amount.add(olW.getlbr_SISCOMEXAmt());
					else if (chargeType == FREIGHT)
						amount = amount.add(olW.getFreightAmt());
					else if (chargeType == INSURANCE)
						amount = amount.add(olW.getlbr_InsuranceAmt());
				}
			}
		
		//	Compõe o Total do Seguro para a Fatura
		else if (MInvoice.Table_Name.equals(po.get_TableName()))
			for (MInvoiceLine il : ((MInvoice) po).getLines())
			{
				I_W_C_InvoiceLine olW = POWrapper.create(il, I_W_C_InvoiceLine.class);
				//
				if (il.getM_Product_ID() > 0 
						&& il.getM_Product().getProductType().equals(MProduct.PRODUCTTYPE_Item))
				{
					if (chargeType == SISCOMEX)
						amount = amount.add(olW.getlbr_SISCOMEXAmt());
					else if (chargeType == FREIGHT)
						amount = amount.add(olW.getFreightAmt());
					else if (chargeType == INSURANCE)
						amount = amount.add(olW.getlbr_InsuranceAmt());
				}
			}
		//
		return amount;
	}	//	getInsuranceAmt


}	//	VLBROrder
