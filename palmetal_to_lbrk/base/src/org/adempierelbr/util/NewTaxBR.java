package org.adempierelbr.util;

import java.util.HashMap;
import java.util.Map;

import org.adempiere.model.POWrapper;
import org.adempierelbr.model.MLBRTax;
import org.adempierelbr.model.MLBRTaxLine;
import org.adempierelbr.wrapper.I_W_AD_OrgInfo;
import org.adempierelbr.wrapper.I_W_C_BPartner;
import org.adempierelbr.wrapper.I_W_C_Invoice;
import org.adempierelbr.wrapper.I_W_C_Order;
import org.adempierelbr.wrapper.I_W_M_Product;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MClientInfo;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MProduct;
import org.compiere.util.Env;

public class NewTaxBR {

	public static Map<String, Integer> getTaxes(Integer M_Product_ID, Integer C_Order_ID ,Integer C_OrderLine_ID, Integer AD_Org_ID, Integer C_BPartner_ID,String trxName){
		
		Map<String,Integer> map = new HashMap<String,Integer>();
		
		if (M_Product_ID == null || M_Product_ID == 0)
			return null;
		
		if (C_Order_ID == null)
			C_Order_ID = 0;
		
		if (C_OrderLine_ID == null)
			C_OrderLine_ID = 0;
		
		if (AD_Org_ID == null)
			AD_Org_ID = 0;
		
		if (C_BPartner_ID == null)
			C_BPartner_ID = 0;
		
		I_W_C_Order o = POWrapper.create(new MOrder (Env.getCtx(), C_Order_ID, trxName), I_W_C_Order.class);
		I_W_M_Product p = POWrapper.create(new MProduct (Env.getCtx(), M_Product_ID, null), I_W_M_Product.class);
		I_W_AD_OrgInfo oi = POWrapper.create(MOrgInfo.get(Env.getCtx(), AD_Org_ID, null), I_W_AD_OrgInfo.class);
		I_W_C_BPartner bp = POWrapper.create(new MBPartner (Env.getCtx(), o.getC_BPartner_ID(), null), I_W_C_BPartner.class);
		MBPartnerLocation bpLoc = new MBPartnerLocation (Env.getCtx(), o.getBill_Location_ID(), null); 

		/**
		 * 	0 = Taxes
		 * 	1 = Legal Message
		 * 	2 = CFOP
		 * 	3 = CST
		 */
		Object[] taxation = MLBRTax.getTaxes (o.getC_DocTypeTarget_ID(), o.isSOTrx(), o.getlbr_TransactionType(), p, oi, bp, bpLoc, o.getDateAcct());
		//
		if (taxation == null)
			return null;
		//
		Map<Integer, MLBRTaxLine> taxes = (Map<Integer, MLBRTaxLine>) taxation[0];
		
		if (((Integer) taxation[1]) > 0)
			map.put("LBR_LegalMessage_ID", ((Integer) taxation[1]));
		
		if (((Integer) taxation[2]) > 0)
			map.put("LBR_CFOP_ID", ((Integer) taxation[2]));
		
//		if (((String) taxation[3]) != null && ((String) taxation[3]).length() > 0)
//			mTab.setValue("lbr_TaxStatus", p.getlbr_ProductSource() + ((String) taxation[3]));
		//
		if (taxes.size() > 0)
		{
			MLBRTax tax = new MLBRTax (Env.getCtx(), 0, null);
			tax.save();
			//
			for (Integer key : taxes.keySet())
			{
				MLBRTaxLine tl = taxes.get(key);
				tl.setLBR_Tax_ID(tax.getLBR_Tax_ID());
				tl.save();
			}
			//
			tax.setDescription();
			tax.save();
			//
			map.put("LBR_Tax_ID", tax.getLBR_Tax_ID());

		}

		return map;
	}
	
	public static Map<String, Integer> getInvoiceTaxes(Integer M_Product_ID, Integer C_Invoice_ID ,Integer C_InvoiceLine_ID, Integer AD_Org_ID, Integer C_BPartner_ID,String trxName){
		
		Map<String,Integer> map = new HashMap<String,Integer>();
		
		
		if (M_Product_ID == null || M_Product_ID == 0)
			return null;
		
		int M_ProductFreight_ID = MClientInfo.get(Env.getCtx()).getM_ProductFreight_ID();
		if (M_Product_ID==M_ProductFreight_ID)
			return null;

				
		if (C_Invoice_ID == null)
			C_Invoice_ID = 0;
		
		if (C_InvoiceLine_ID == null)
			C_InvoiceLine_ID = 0;
		
		if (AD_Org_ID == null)
			AD_Org_ID = 0;

		
		I_W_C_Invoice i = POWrapper.create(new MInvoice (Env.getCtx(), C_Invoice_ID, trxName), I_W_C_Invoice.class);
		
		
		I_W_M_Product p = POWrapper.create(new MProduct (Env.getCtx(), M_Product_ID, null), I_W_M_Product.class);
		I_W_AD_OrgInfo oi = POWrapper.create(MOrgInfo.get(Env.getCtx(), AD_Org_ID, null), I_W_AD_OrgInfo.class);
		I_W_C_BPartner bp = POWrapper.create(new MBPartner (Env.getCtx(), i.getC_BPartner_ID(), null), I_W_C_BPartner.class);
		MBPartnerLocation bpLoc = new MBPartnerLocation (Env.getCtx(), i.getC_BPartner_Location_ID(), null); 

		/**
		 * 	0 = Taxes
		 * 	1 = Legal Message
		 * 	2 = CFOP
		 * 	3 = CST
		 */
		Object[] taxation = MLBRTax.getTaxes (i.getC_DocTypeTarget_ID(), i.isSOTrx(), i.getlbr_TransactionType(), p, oi, bp, bpLoc, i.getDateAcct());
		//
		if (taxation == null)
			return null;
		//
		Map<Integer, MLBRTaxLine> taxes = (Map<Integer, MLBRTaxLine>) taxation[0];
		
		if (((Integer) taxation[1]) > 0)
			map.put("LBR_LegalMessage_ID", ((Integer) taxation[1]));
		
		if (((Integer) taxation[2]) > 0)
			map.put("LBR_CFOP_ID", ((Integer) taxation[2]));
		
//		if (((String) taxation[3]) != null && ((String) taxation[3]).length() > 0)
//			mTab.setValue("lbr_TaxStatus", p.getlbr_ProductSource() + ((String) taxation[3]));
		//
		if (taxes.size() > 0)
		{
			MLBRTax tax = new MLBRTax (Env.getCtx(), 0, null);
			tax.save();
			//
			for (Integer key : taxes.keySet())
			{
				MLBRTaxLine tl = taxes.get(key);
				tl.setLBR_Tax_ID(tax.getLBR_Tax_ID());
				tl.save();
			}
			//
			tax.setDescription();
			tax.save();
			//
			map.put("LBR_Tax_ID", tax.getLBR_Tax_ID());
		}

		return map;
	}
}
