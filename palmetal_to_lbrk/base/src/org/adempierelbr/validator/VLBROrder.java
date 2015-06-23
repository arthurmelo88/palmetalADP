package org.adempierelbr.validator;

import java.math.BigDecimal;

import org.adempiere.model.POWrapper;
import org.adempierelbr.wrapper.I_W_AD_ClientInfo;
import org.adempierelbr.wrapper.I_W_C_BPartner;
import org.adempierelbr.wrapper.I_W_C_InvoiceLine;
import org.adempierelbr.wrapper.I_W_C_Order;
import org.adempierelbr.wrapper.I_W_C_OrderLine;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * 		Utilizado para efetuar os cálculos dos impostos brasileiros.
 * 
 * 	@author Ricardo Santana (Kenos, www.kenos.com.br)
 *	@version $Id: VLBROrder.java, v1.0 2011/05/13 11:37:23 AM, ralexsander Exp $
 */
public class VLBROrder implements ModelValidator
{
	/**
	 *	Constructor.
	 *	The class is instanciated when logging in and client is selected/known
	 */
	public VLBROrder ()
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

		engine.addDocValidate (MInvoice.Table_Name, this);
		engine.addDocValidate (MOrder.Table_Name, this);
		engine.addModelChange (MInvoiceLine.Table_Name, this);
		engine.addModelChange (MOrder.Table_Name, this);
		engine.addModelChange (MOrderLine.Table_Name, this);
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
		if (po.get_TableName().equals(MOrder.Table_Name))
			return modelChange ((MOrder) po, type);
		
		/**
		 * 	Recalcula o frete caso seja uma nova linha ou caso a linha seja deletada.
		 * 		Também é necessário recalcular quando uma linha é alterada.
		 */
//		else if (po.get_TableName().equals(MOrderLine.Table_Name))
//			return modelChange ((MOrderLine) po, type);
		
		/**
		 * 	Ajusta alguns campos ao gerar a fatura pelo pedido
		 */
		else if (po.get_TableName().equals(MInvoiceLine.Table_Name))
			return modelChange ((MInvoiceLine) po, type);
		
		else if (po.get_TableName().equals(MOrderLine.Table_Name))
			return modelChange ((MOrderLine) po, type);
	
		
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
	public String modelChange (MOrder order, int type) throws Exception
	{
		/**
		 * 	Faz as validações dos valores para evitar erros
		 */
		//if (type == TYPE_BEFORE_NEW || type == TYPE_BEFORE_CHANGE)
		if (type == TYPE_AFTER_NEW || type == TYPE_AFTER_CHANGE)
		{
			//if (MOrder.FREIGHTCOSTRULE_FixPrice.equals(order.getFreightCostRule())
			//		&& Env.ZERO.compareTo(order.getFreightAmt()) >= 0)
			//	return Msg.parseTranslation(Env.getCtx(), "@FillMandatory@ @FreightAmt@");
			
			/**
			 * 	Divide o valor do frete nas linhas
			 */
			if (isChangeAffectFreight (order) 
					&& !MOrder.FREIGHTCOSTRULE_Line.equals(order.getFreightCostRule()))
				recalcuteFreight (order);
		}
		//
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
	public String modelChange (MOrderLine orderLine, int type) throws Exception
	{
		/**
		 * 	Faz as validações dos valores para evitar erros
		 */
		if (type == TYPE_AFTER_NEW || type == TYPE_AFTER_DELETE)
		{
			MOrder order = new MOrder(orderLine.getCtx(),orderLine.getC_Order_ID(),orderLine.get_TrxName());
			order.setFreightCostRule(MOrder.FREIGHTCOSTRULE_FixPrice);
			order.save();
			
			recalcuteFreightOL (order);

		}
		
		/* PALMETAL BEGIN */
		if (type == TYPE_AFTER_CHANGE || type == TYPE_AFTER_NEW)
		{
			MOrder order = orderLine.getParent();
			
			/**
			 * 	Divide o valor do frete nas linhas
			 */
			if (isChangeAffectFreight (orderLine))
				recalcuteFreightOL (order);
		}
		/* PALMETAL END */
		
		//
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
		if (type == TYPE_BEFORE_NEW && iLine.getC_OrderLine_ID() > 0)
		{
			I_W_C_InvoiceLine iLineW = POWrapper.create(iLine, I_W_C_InvoiceLine.class);
			I_W_C_OrderLine oLineW = POWrapper.create(new MOrderLine(Env.getCtx(), iLine.getC_OrderLine_ID(), null), I_W_C_OrderLine.class);
			//
			iLineW.setFreightAmt(oLineW.getFreightAmt());
			iLineW.setlbr_SISCOMEXAmt(oLineW.getlbr_SISCOMEXAmt());
			iLineW.setlbr_InsuranceAmt(oLineW.getlbr_InsuranceAmt());
		}
		return null;
	}	//	modelChange
	
	/**
	 *	Validate Document.
	 *	Called as first step of DocAction.prepareIt
     *	when you called addDocValidate for the table.
     *	Note that totals, etc. may not be correct.
	 *	@param po persistent object
	 *	@param timing see TIMING_ constants
     *	@return error message or null
	 */
	public String docValidate (PO po, int timing)
	{
		if (po.get_TableName().equals(MInvoice.Table_Name) 
				&& timing == TIMING_BEFORE_PREPARE)
		{			
			String result = null;
			//
			result = addCharge ((MInvoice) po, SISCOMEX);
			if (result != null)
				return result;
			
			/* PALMETAL MUDAR AQUI
			result = addCharge ((MInvoice) po, FREIGHT);
			if (result != null)
				return result;
			*/
			
			result = addCharge ((MInvoice) po, INSURANCE);
			if (result != null)
				return result;
			//
			return result;
		}
		else if (po.get_TableName().equals(MOrder.Table_Name) 
				&& timing == TIMING_BEFORE_PREPARE)
		{
			//	Valida Cadastro do PN
			if (MSysConfig.getBooleanValue("LBR_VALIDATE_BP_ON_SO", false, po.getAD_Client_ID()) 
					&& !isBPValid ((MOrder) po))
				return "Cadastro de Parceiro de Negócios Inválido";
		}
		//
		return null;
	}	//	docValidate
	
	/**
	 * 		Verifica se o parceiro de negócios 
	 * 		está com o cadastro válido
	 * 
	 * 	@param order
	 * 	@return	true or false
	 */
	private boolean isBPValid (MOrder order)
	{
		if (MDocType.DOCSUBTYPESO_Proposal.equals(order.getC_DocTypeTarget().getDocSubTypeSO())
				|| MDocType.DOCSUBTYPESO_Quotation.equals(order.getC_DocTypeTarget().getDocSubTypeSO()))
			return true;
		//
		MBPartner bp = new MBPartner (order.getCtx(), order.getC_BPartner_ID(), order.get_TrxName());
		MBPartnerLocation bpL = new MBPartnerLocation (order.getCtx(), order.getC_BPartner_Location_ID(), order.get_TrxName());
		MLocation loc = bpL.getLocation (true);
		//
		if (loc != null && loc.getC_Country_ID() == 139)	//	Brazil
		{
			I_W_C_BPartner bpW = POWrapper.create(bp, I_W_C_BPartner.class);
			//
			return bpW.islbr_BPTypeBRIsValid();
		}
		//
		return true;
	}	//	isBPValid
	
	/**
	 * 	Verify if freight must be recalculated to all lines
	 * 	@param order
	 * 	@return true if must recalculate
	 */
	private boolean isChangeAffectFreight (PO po)
	{
		if (po instanceof MOrder || po instanceof MInvoice)
		{
			/**
			 * 	Campos comuns entre Pedido e Fatura
			 */
			if (po.is_ValueChanged(I_W_C_Order.COLUMNNAME_FreightAmt)
					|| po.is_ValueChanged(I_W_C_Order.COLUMNNAME_FreightCostRule))
				return true;
		}
		else if  (po instanceof MOrderLine || po instanceof MInvoiceLine)
		{
			/**
			 * 	Campos comuns nas Linhas do Pedido e Fatura
			 */
			//PALMETAL: Colocamos a condição po.is_ValueChanged (I_W_C_OrderLine.COLUMNNAME_QtyOrdered)
			if (po.is_ValueChanged (I_W_C_OrderLine.COLUMNNAME_LineNetAmt) || po.is_ValueChanged (I_W_C_OrderLine.COLUMNNAME_QtyOrdered))
				return true;
		}
		//
		return false;
	}	//	isChangeAffectTaxes

	/**
	 *  PALMETAL
	 * 	Refaz os cálculos de Frete por linha
	 * 
	 * 	@param order Pedido
	 */
	private void recalcuteFreightOL (MOrder order)
	{
		BigDecimal freightAmt = order.getFreightAmt();
		int M_ProductFreight_ID = MClientInfo.get(Env.getCtx()).getM_ProductFreight_ID();
		
		//	Total da NF sem considerar o valor do frete
		BigDecimal totalLines = Env.ZERO;
				
		//	Compõe o TotalLines
		int aux = 0;
		
		for (MOrderLine ol : order.getLines())
		{
			if (ol.getM_Product_ID() > 0 
					&& ol.getM_Product_ID() != M_ProductFreight_ID
					&& ol.getM_Product().getProductType().equals(MProduct.PRODUCTTYPE_Item)){
				totalLines = totalLines.add(ol.getLineNetAmt());
				aux++;
			}
		}
		
		BigDecimal qtdeLinhas = new BigDecimal(aux);
		
		//	Rateia o Frete
		for (MOrderLine ol : order.getLines())
		{
			//	Não ratear a linha do frete e para serviços
			if (ol.getM_Product_ID() == 0 
					|| ol.getM_Product_ID() == M_ProductFreight_ID
					|| !ol.getM_Product().getProductType().equals(MProduct.PRODUCTTYPE_Item)
					|| ol.getLineNetAmt().compareTo(Env.ZERO) == 0)
				continue;
			
			//	Faz o rateiro do valor do frete
			//BigDecimal lineAmt 			= ol.getLineNetAmt();
			
			BigDecimal lineFreightAmt = freightAmt.divide(qtdeLinhas, 17, BigDecimal.ROUND_HALF_UP);
			lineFreightAmt = lineFreightAmt.divide(ol.getQtyEntered(), 17, BigDecimal.ROUND_HALF_UP);
			
			if (MOrder.FREIGHTCOSTRULE_Line.equals(order.getFreightCostRule())){
				System.out.println(ol.getPriceActual().doubleValue());
				System.out.println(ol.getLineNetAmt().doubleValue());
				
				ol.setFreightAmt(lineFreightAmt);
				ol.setPriceActual(lineFreightAmt.add(ol.getPriceEntered()));
				System.out.println(ol.getPriceActual().doubleValue());
				ol.setLineNetAmt(lineFreightAmt.add(ol.getPriceEntered()).multiply(ol.getQtyEntered()));
				System.out.println(ol.getLineNetAmt().doubleValue());
			}
			else if (MOrder.FREIGHTCOSTRULE_FixPrice.equals(order.getFreightCostRule())){
				System.out.println(ol.getPriceActual().doubleValue());
				System.out.println(ol.getLineNetAmt().doubleValue());
				
				ol.setFreightAmt(lineFreightAmt);
				ol.setPriceActual(lineFreightAmt.add(ol.getPriceEntered()));
				System.out.println(ol.getPriceActual().doubleValue());
				ol.setLineNetAmt(lineFreightAmt.add(ol.getPriceEntered()).multiply(ol.getQtyEntered()));
				System.out.println(ol.getLineNetAmt().doubleValue());
			}
			else if (MOrder.FREIGHTCOSTRULE_FreightIncluded.equals(order.getFreightCostRule())){
				ol.setFreightAmt(Env.ZERO);
				ol.setPriceActual(ol.getPriceEntered());
				ol.setLineNetAmt(ol.getPriceEntered().multiply(ol.getQtyEntered()));
			}
				
			//
			ol.save();
		}
		/**
		 * 	Após calcular o frete, o valor é distribuido para as linhas
		 * 		e a regra de custo é modificada para LINHA, pois assim
		 * 		caso seja adicionado ou removido uma linha, é necessário
		 * 		redistribuir o frete.
		 */
		if (MOrder.FREIGHTCOSTRULE_FixPrice.equals(order.getFreightCostRule()))
			order.setFreightCostRule(MOrder.FREIGHTCOSTRULE_Line);
	}	//	recalcuteOrder
	
	
	/**
	 * 	Refaz os cálculos de Frete por linha
	 * 
	 * 	@param order Pedido
	 */
	private void recalcuteFreight (MOrder order)
	{
		BigDecimal freightAmt = order.getFreightAmt();
		int M_ProductFreight_ID = MClientInfo.get(Env.getCtx()).getM_ProductFreight_ID();
		
		//	Total da NF sem considerar o valor do frete
		BigDecimal totalLines = Env.ZERO;
		
		//	Compõe o TotalLines
		int aux = 0;
		for (MOrderLine ol : order.getLines())
		{
			if (ol.getM_Product_ID() > 0 
					&& ol.getM_Product_ID() != M_ProductFreight_ID
					&& ol.getM_Product().getProductType().equals(MProduct.PRODUCTTYPE_Item)){
				totalLines = totalLines.add(ol.getLineNetAmt());
				aux++;
			}
		}
		
		BigDecimal qtdeLinhas = new BigDecimal(aux);
		
		//	Rateia o Frete
		for (MOrderLine ol : order.getLines())
		{
			//	Não ratear a linha do frete e para serviços
			if (ol.getM_Product_ID() == 0 
					|| ol.getM_Product_ID() == M_ProductFreight_ID
					|| !ol.getM_Product().getProductType().equals(MProduct.PRODUCTTYPE_Item)
					|| ol.getLineNetAmt().compareTo(Env.ZERO) == 0)
				continue;
			
			//	Faz o rateiro do valor do frete
			//BigDecimal lineAmt 			= ol.getLineNetAmt();
			//BigDecimal lineFreightAmt 	= lineAmt.multiply(freightAmt).divide(totalLines, 17, BigDecimal.ROUND_HALF_UP).divide(ol.getQtyEntered(), 17, BigDecimal.ROUND_HALF_UP);
			
			BigDecimal lineFreightAmt = freightAmt.divide(qtdeLinhas, 17, BigDecimal.ROUND_HALF_UP);
			lineFreightAmt = lineFreightAmt.divide(ol.getQtyEntered(), 17, BigDecimal.ROUND_HALF_UP);
			
			if (MOrder.FREIGHTCOSTRULE_FixPrice.equals(order.getFreightCostRule())){
				System.out.println(ol.getPriceActual().doubleValue());
				System.out.println(ol.getLineNetAmt().doubleValue());
				
				ol.setFreightAmt(lineFreightAmt);
				ol.setPriceActual(lineFreightAmt.add(ol.getPriceEntered()));
				System.out.println(ol.getPriceActual().doubleValue());
				ol.setLineNetAmt(lineFreightAmt.add(ol.getPriceEntered()).multiply(ol.getQtyEntered()));
				System.out.println(ol.getLineNetAmt().doubleValue());			}
			else if (MOrder.FREIGHTCOSTRULE_FixPrice.equals(order.getFreightCostRule())){
				System.out.println(ol.getPriceActual().doubleValue());
				System.out.println(ol.getLineNetAmt().doubleValue());
				
				ol.setFreightAmt(lineFreightAmt);
				ol.setPriceActual(lineFreightAmt.add(ol.getPriceEntered()));
				System.out.println(ol.getPriceActual().doubleValue());
				ol.setLineNetAmt(lineFreightAmt.add(ol.getPriceEntered()).multiply(ol.getQtyEntered()));
				System.out.println(ol.getLineNetAmt().doubleValue());
			}	
			else if (MOrder.FREIGHTCOSTRULE_FreightIncluded.equals(order.getFreightCostRule())){
				ol.setFreightAmt(Env.ZERO);
				ol.setPriceActual(ol.getPriceEntered());
				ol.setLineNetAmt(ol.getPriceEntered().multiply(ol.getQtyEntered()));
			}
			//
			ol.save();
		}

		
		/**
		 * 	Após calcular o frete, o valor é distribuido para as linhas
		 * 		e a regra de custo é modificada para LINHA, pois assim
		 * 		caso seja adicionado ou removido uma linha, é necessário
		 * 		redistribuir o frete.
		 */
		if (MOrder.FREIGHTCOSTRULE_FixPrice.equals(order.getFreightCostRule()))
			order.setFreightCostRule(MOrder.FREIGHTCOSTRULE_Line);
	}	//	recalcuteOrder
	
	/**
	 * 	Adiciona o valor do Seguro
	 * 	@param order
	 */
	private String addCharge (MInvoice invoice, int chargeType)
	{
		MClientInfo cInfo = MClientInfo.get (invoice.getCtx(), invoice.getAD_Client_ID());
		I_W_AD_ClientInfo cInfoW = POWrapper.create(cInfo, I_W_AD_ClientInfo.class);
		MInvoiceLine chgLine = null;
		
		//	Verifica se a despesa está cadastrada na empresa
		if ((cInfoW.getLBR_ProductSISCOMEX_ID() <= 0 && chargeType == SISCOMEX)
				|| (cInfoW.getM_ProductFreight_ID() <= 0 && chargeType == FREIGHT)
				|| (cInfoW.getLBR_ProductInsurance_ID() <= 0 && chargeType == INSURANCE))
			return null;
		
		int M_Product_ID = 0;
		
		if (chargeType 		== SISCOMEX)
			M_Product_ID = cInfoW.getLBR_ProductSISCOMEX_ID();
		else if (chargeType == FREIGHT)
			M_Product_ID = cInfoW.getM_ProductFreight_ID();
		else if (chargeType == INSURANCE)
			M_Product_ID = cInfoW.getLBR_ProductInsurance_ID();
		
		BigDecimal amount = getChargeAmt (invoice, chargeType);

		if (amount == null 
				|| amount.compareTo(Env.ZERO) == 0)
			return null;
		
		for (MInvoiceLine ol : invoice.getLines())
		{
			if (ol.getM_Product_ID() == M_Product_ID)
			{
				chgLine = ol;
				break;
			}
		}
		
		if (chgLine == null)
		{
			chgLine = new MInvoiceLine(invoice);
			chgLine.setQty(Env.ONE);
			chgLine.setDescription("Inserido Automáticamente");
			chgLine.setM_Product_ID(M_Product_ID);
		}		
		//
		chgLine.setPrice(amount);
		chgLine.setPriceList(amount);
		chgLine.save();
		//
		return null;
	}	//	addInsurance
	
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
				//PALMETAL
				//.add (getChargeAmt (po, FREIGHT))
				//END PALMETAL
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
						//PALMETAL
						//amount = amount.add(olW.getFreightAmt());
						amount = amount;
						//END PALMETAL
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
						//PALMETAL
						//amount = amount.add(olW.getFreightAmt());
						amount = amount;
						//END PALMETAL
					else if (chargeType == INSURANCE)
						amount = amount.add(olW.getlbr_InsuranceAmt());
				}
			}
		//
		return amount;
	}	//	getInsuranceAmt
}	//	VLBROrder