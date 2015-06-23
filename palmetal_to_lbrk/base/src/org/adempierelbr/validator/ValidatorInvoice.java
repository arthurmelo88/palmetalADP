/******************************************************************************
 * Product: ADempiereLBR - ADempiere Localization Brazil                      *
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
package org.adempierelbr.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.adempiere.model.POWrapper;
import org.adempierelbr.model.MLBRBoleto;
import org.adempierelbr.model.MLBRNotaFiscal;
import org.adempierelbr.model.MLBRProductMovementFiller;
import org.adempierelbr.model.MLBRTax;
import org.adempierelbr.util.NewTaxBR;
import org.adempierelbr.wrapper.I_W_C_InvoiceLine;
import org.compiere.apps.search.Info_Column;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.MProduct;
import org.compiere.model.MUOMConversion;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * ValidatorInvoice
 *
 * Validate Invoice (Tax Calculation)
 *
 * [ 1967069 ] LBR_Tax no  excludo quando exclu uma linha, mgrigioni
 * [ 2200626 ] Lista de Preo Brasil, mgrigioni
 *
 * @author Mario Grigioni (Kenos, www.kenos.com.br)
 * @contributor Fernando Lucktemberg (Faire, www.faire.com.br)
 * @version $Id: ValidatorInvoice.java, 04/01/2008 15:56:00 mgrigioni
 *
 *          BF: 1928906 - amontenegro
 */
public class ValidatorInvoice implements ModelValidator
{
	/**
	 * Constructor. The class is instanciated when logging in and client is
	 * selected/known
	 */
	public ValidatorInvoice()
	{
		super();
	} // ValidatorOrder

	/** Logger */
	private static CLogger	log				= CLogger.getCLogger(ValidatorInvoice.class);
	/** Client */
	private int				m_AD_Client_ID	= -1;

	/**
	 *	Initialize Validation
	 *	@param engine validation engine
	 *	@param client client
	 */
	public void initialize (ModelValidationEngine engine, MClient client)
	{
		//client = null for global validator
		if (client != null) {
			m_AD_Client_ID = client.getAD_Client_ID();
			log.info(client.toString());
		}
		else  {
			log.info("Initializing global validator: "+this.toString());
		}

		engine.addModelChange(MInvoice.Table_Name, this);
		engine.addModelChange(MInvoiceLine.Table_Name, this);

		engine.addDocValidate(MInvoice.Table_Name, this);
	}	//	initialize

	/**
	 * Get Client to be monitored
	 *
	 * @return AD_Client_ID client
	 */
	public int getAD_Client_ID()
	{
		return m_AD_Client_ID;
	} // getAD_Client_ID

	/**
	 * User Login. Called when preferences are set
	 *
	 * @param AD_Org_ID
	 *            org
	 * @param AD_Role_ID
	 *            role
	 * @param AD_User_ID
	 *            user
	 * @return error message or null
	 */
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		log.info("AD_User_ID=" + AD_User_ID);
		return null;
	} // login

	/**
	 * Model Change of a monitored Table. Called after
	 * PO.beforeSave/PO.beforeDelete when you called addModelChange for the
	 * table
	 *
	 * @param po
	 *            persistent object
	 * @param type
	 *            TYPE_
	 * @return error message or null
	 * @exception Exception
	 *                if the recipient wishes the change to be not accept.
	 */
	public String modelChange(PO po, int type) throws Exception
	{
		log.info(po.get_TableName() + " Type: "+type);

		/**
		 * 	
		 */
		if (po.get_TableName().equals(MInvoice.Table_Name))
			return modelChange((MInvoice) po);

		/**
		 * 	Apaga os registros dos impostos quando a linha é apagada
		 */
		else if (po.get_TableName().equals(MInvoiceLine.Table_Name))
			return modelChange((MInvoiceLine) po, type);

		return null;
	} // modelChange

	
	// @param MInvoice
	public String modelChange(MInvoice invoice) throws Exception
	{
		int C_Order_ID = invoice.getC_Order_ID();
		if (C_Order_ID <= 0)
			return null;

		MOrder order = new MOrder(invoice.getCtx(), C_Order_ID, invoice.get_TrxName());;

		if (invoice.get_ValueAsString("lbr_TransactionType").equals("")){
			invoice.set_ValueOfColumn("lbr_TransactionType", order.get_Value("lbr_TransactionType"));
		}

		if (invoice.get_ValueAsString("lbr_PaymentRule").equals("")){
			invoice.set_ValueOfColumn("lbr_PaymentRule", order.get_Value("lbr_PaymentRule"));
		}

		if (invoice.get_ValueAsString("lbr_NFEntrada").trim().equals("")){
			invoice.set_ValueOfColumn("lbr_NFEntrada", order.get_ValueAsString("lbr_NFEntrada").trim());
		}

		if (invoice.get_ValueAsString("lbr_BillNote").trim().equals("")){
			invoice.set_ValueOfColumn("lbr_BillNote", order.get_ValueAsString("lbr_BillNote").trim());
		}

		if (invoice.get_ValueAsString("lbr_ShipNote").trim().equals("")){
			invoice.set_ValueOfColumn("lbr_ShipNote", order.get_ValueAsString("lbr_ShipNote").trim());
		}

		if (invoice.get_ValueAsInt("C_BankAccount_ID") == 0) {
			invoice.set_ValueOfColumn("C_BankAccount_ID", order.get_Value("C_BankAccount_ID"));
		}
		
		if (invoice.get_ValueAsString("lbr_NFDescription").trim().equals("")){
			invoice.set_ValueOfColumn("lbr_NFDescription", order.get_ValueAsString("lbr_NFDescription").trim());
		}

		return validatePaymentTerm(invoice);
	}

	/**
	 *
	 * @param 	iLine 	MInvoiceLine
	 * @param 	type	Timing
	 * @return	null or error msg
	 * @throws Exception
	 */
	public String modelChange (MInvoiceLine iLine, int type) throws Exception
	{
		I_W_C_InvoiceLine iLineW = POWrapper.create(iLine, I_W_C_InvoiceLine.class);
		
		//
		if (type == TYPE_BEFORE_DELETE)
		{	
			if (iLineW.getM_InOutLine_ID() > 0)
			{
				return "Não é possível apagar uma linha que já foi expedida.";
			}
			
			if (iLineW.getLBR_Tax_ID() > 0)
			{
				MLBRTax lbrTax = new MLBRTax(iLine.getCtx(), iLineW.getLBR_Tax_ID() , iLine.get_TrxName());
				lbrTax.delete(true, iLine.get_TrxName());
			}
		}
		else if (type == TYPE_BEFORE_NEW && iLineW.getLBR_Tax_ID() == 0)
		{
	
         
			int C_OrderLine_ID = iLine.getC_OrderLine_ID();
			if (C_OrderLine_ID != 0)
			{
				MOrderLine oLine = new MOrderLine(Env.getCtx(), C_OrderLine_ID, iLine.get_TrxName());
				
					//
					// CFOP, Sit. Tributária, Mensagem Legal
					if (iLine.get_ValueAsInt("LBR_CFOP_ID") <= 0)
						iLine.set_ValueOfColumn("LBR_CFOP_ID", oLine.get_Value("LBR_CFOP_ID"));

					if (iLine.get_ValueAsInt("LBR_LegalMessage_ID") <= 0)
						iLine.set_ValueOfColumn("LBR_LegalMessage_ID", oLine.get_Value("LBR_LegalMessage_ID"));

					if (iLine.get_ValueAsString("lbr_TaxStatus").isEmpty())
						iLine.set_ValueOfColumn("lbr_TaxStatus", oLine.get_Value("lbr_TaxStatus"));

					if(iLine.getDescription() == null || iLine.getDescription().equals(""))
						iLine.setDescription(oLine.getDescription());

					//
					int LBR_Tax_ID = oLine.get_ValueAsInt("LBR_Tax_ID");
					if (LBR_Tax_ID != 0)
					{
						MLBRTax oTax = new MLBRTax(Env.getCtx(), LBR_Tax_ID, iLine.get_TrxName());
						MLBRTax newTax = oTax.copyTo();
						//
						iLine.set_ValueOfColumn("LBR_Tax_ID", newTax.getLBR_Tax_ID());
					}
					
					//BEGIN palmetal
					MProduct p = new MProduct(iLine.getCtx(),iLine.getM_Product_ID(),null);
					BigDecimal qtyInvoiced= oLine.getQtyEntered();
					BigDecimal qtyOrdered =iLine.getQtyEntered();
					if (iLine.getC_UOM_ID()!=p.getC_UOM_ID()){
						 qtyInvoiced = MUOMConversion.convertProductQty(iLine.getCtx(),iLine.getM_Product_ID(),iLine.getC_UOM_ID(),p.getC_UOM_ID(),iLine.getQtyEntered());
						 qtyOrdered = MUOMConversion.convertProductQty(oLine.getCtx(),oLine.getM_Product_ID(),oLine.getC_UOM_ID(),p.getC_UOM_ID(),oLine.getQtyEntered());

					}
					if (qtyInvoiced==null || qtyOrdered==null ){
						//TODO - melhorar mensagem de erro aqui dps ;)
						return "Erro ao gerar a fatura. Nao foi possivel fazer a conversao das unidades de medida. ";
					}
			
					if (!qtyInvoiced.equals(qtyOrdered)){
						iLine.set_ValueOfColumn("lbr_RecalculateTax", true);
					}
					//END
				
			}
		} // new

		return null;
	} // modelChange

	/**
	 * Validate Document. Called as first step of DocAction.prepareIt when you
	 * called addDocValidate for the table. Note that totals, etc. may not be
	 * correct.
	 *
	 * @param po
	 *            persistent object
	 * @param timing
	 *            see TIMING_ constants
	 * @return error message or null
	 */
	public String docValidate(PO po, int timing)
	{

		if (po instanceof MInvoice){

			MInvoice invoice = (MInvoice)po;
			Properties ctx = invoice.getCtx();
			String     trx = invoice.get_TrxName();

			if (timing == TIMING_AFTER_PREPARE){

				MDocType docType = new MDocType(ctx,invoice.getC_DocTypeTarget_ID(),trx);
				if (docType.get_ValueAsBoolean("lbr_HasFiscalDocument") && //	Gera Documento Fiscal
					!docType.get_ValueAsBoolean("lbr_IsOwnDocument"))	   //	No  um documento prprio
				{
					if (invoice.get_ValueAsString("lbr_NFEntrada").equals(""))
					{
						if (!invoice.isReversal())
							return "Necessrio preencher campo Referncia do Pedido";
					}
				}
			} //AFTER PREPARE

			else if (timing == TIMING_AFTER_COMPLETE)
			{
				// Fix - Ajustar PaySchedule
				MPaymentTerm pt = new MPaymentTerm(invoice.getCtx(), invoice.getC_PaymentTerm_ID(), null);
				log.fine(pt.toString());
				pt.apply(invoice);

				// Validate Withhold
				//MLBRTax.validateWithhold(invoice);

				MDocType dt = MDocType.get(ctx, invoice.getC_DocTypeTarget_ID());
				boolean hasOpenItems      = dt.get_ValueAsBoolean("lbr_HasOpenItems");
				boolean hasFiscalDocument = dt.get_ValueAsBoolean("lbr_HasFiscalDocument");
				boolean isOwnDocument     = dt.get_ValueAsBoolean("lbr_IsOwnDocument");

				if (!hasOpenItems && !invoice.isReversal())
				{

					invoice.setC_Payment_ID(0);
					invoice.setIsPaid(true);

					// Create Allocation
					MAllocationHdr alloc = new MAllocationHdr(ctx, false, invoice.getDateAcct(), invoice.getC_Currency_ID(), Msg.translate(ctx, "C_Invoice_ID") + ": " + invoice.getDocumentNo() + "/", trx);
					alloc.setAD_Org_ID(invoice.getAD_Org_ID());
					if (alloc.save())
					{
						// Amount
						BigDecimal gt = invoice.getGrandTotal(true);
						if (!invoice.isSOTrx())
							gt = gt.negate();
						// Orig Line
						MAllocationLine aLine = new MAllocationLine(alloc, gt, Env.ZERO, Env.ZERO, Env.ZERO);
						aLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
						aLine.save();
						// Process It
						if (alloc.processIt(DocAction.ACTION_Complete))
							alloc.save();
					}

				} // don't have Open Items - create automatically allocation

				if (hasFiscalDocument && !invoice.isReversal()) {
					if (dt.getDocBaseType().equals(MDocType.DOCBASETYPE_APCreditMemo) || dt.getDocBaseType().equals(MDocType.DOCBASETYPE_ARInvoice)) {
						isOwnDocument = true;
					} // documento de venda (saída)

					MLBRNotaFiscal nf = new MLBRNotaFiscal (Env.getCtx(), 0, invoice.get_TrxName());
					boolean ok = nf.generateNF(invoice, isOwnDocument);
					 ok = nf.save() && ok ;
					if (!ok || nf.getLBR_NotaFiscal_ID()==0)						
						return "Erro na geracao da Nota Fiscal";
					else
						invoice.set_ValueOfColumn("LBR_NotaFiscal_ID", nf.getLBR_NotaFiscal_ID());
				} // geração de Documento Fiscal
				
				
				//FR 3079621 Onhate
				MLBRProductMovementFiller pmf = new MLBRProductMovementFiller();
				pmf.saveThis(invoice);

				// Processo de consignao - Registra referncia na tabela LBR_ProcessLink
				// A referncia ser sempre da fatura de envio, com a fatura de retorno ou venda
				// Antes de fazer qualquer processamento pesado, primeiro deve-se verificar se  uma fatura de
				// consignao
				// FIXME - Necessrio corrigir o processo
				/*
				String lbr_docbasetype = (String)dt.get_Value("LBR_DocBaseType"); //BF: assim se no existe a coluna retorn NULL

				if (lbr_docbasetype != null && (lbr_docbasetype.equalsIgnoreCase("farc") || lbr_docbasetype.equalsIgnoreCase("faec") || lbr_docbasetype.equalsIgnoreCase("fafc")))
				{
					MProcessLink proc = new MProcessLink(ctx, 0, trx);
					Integer lbr_Ref_C_InvoiceLine_ID;

					for(MInvoiceLine iLine : invoice.getLines())
					{
						proc.setM_Product_ID(iLine.getM_Product_ID());
						proc.setM_AttributeSetInstance_ID(iLine.getM_AttributeSetInstance_ID());

						lbr_Ref_C_InvoiceLine_ID = AdempiereLBR.getlbr_Ref_C_InvoiceLine_ID(iLine.getC_OrderLine_ID(),trx);

						if (!invoice.isReversal()) //Se for uma invoice reversa, devemos multiplicar a qty por -1
							proc.setMovementQty(iLine.getQtyInvoiced());
						else
							proc.setMovementQty(iLine.getQtyInvoiced().negate());

						proc.setlbr_Dest_C_InvoiceLine_ID(iLine.getC_InvoiceLine_ID());
						proc.setlbr_Ori_C_InvoiceLine_ID(lbr_Ref_C_InvoiceLine_ID);

						if (lbr_docbasetype.equalsIgnoreCase("farc"))
						{
							proc.setMovementType(MProcessLink.MOVEMENTTYPE_MovementFrom);
						}
						else if (lbr_docbasetype.equalsIgnoreCase("faec"))
						{
							proc.setMovementType(MProcessLink.MOVEMENTTYPE_MovementTo);
							proc.setMovementQty(proc.getMovementQty().negate()); //Enviando itens = Retira do Estoque
						}
						else if (lbr_docbasetype.equalsIgnoreCase("fafc"))
						{
							proc.setMovementType(MProcessLink.MOVEMENTTYPE_CustomerShipment);
							proc.setMovementQty(proc.getMovementQty().negate()); //Enviando itens = Retira do Estoque
						}
						proc.save(trx);
					}
				}
				// Fim Processo de Consginao
				*/
			} //AFTER COMPLETE

			else if ((timing == TIMING_AFTER_REACTIVATE || timing == TIMING_AFTER_VOID || timing == TIMING_AFTER_CLOSE || timing == TIMING_AFTER_REVERSECORRECT)){

				String sql = "UPDATE C_Invoice SET LBR_Withhold_Invoice_ID=NULL " + "WHERE LBR_Withhold_Invoice_ID=" + invoice.getC_Invoice_ID();

				DB.executeUpdate(sql, invoice.get_TrxName());
				// TODO: Continuar fazendo as reverses

				// CANCELA BOLETO E CNAB
				MLBRBoleto.cancelBoleto(invoice.getCtx(), invoice.getC_Invoice_ID(), invoice.get_TrxName());

				//FIXME
				//CANCELA CONSIGNAÌO
				/*
				for(MInvoiceLine iLine : invoice.getLines())
				{
					MOtherNFLine.voidConsignationLine(iLine.getC_InvoiceLine_ID(), invoice.get_TrxName());
				}
				*/
			} //AFTER REACTIVE AND REVERSE

			else if ((timing == TIMING_BEFORE_REACTIVATE || timing == TIMING_BEFORE_VOID || timing == TIMING_BEFORE_CLOSE || timing == TIMING_BEFORE_REVERSECORRECT)) {

				int whInvoice = invoice.get_ValueAsInt("LBR_Withhold_Invoice_ID");
				if (whInvoice != 0 && whInvoice != invoice.getC_Invoice_ID())
					return "No  possvel re-abrir uma Fatura que tem Retenes de outra Fatura.";
			} //BEFORE REACTIVE AND REVERSE

		} //MInvoice

		return null;
	} // docValidate

	/**
	 * Update Info Window Columns. - add new Columns - remove columns - change
	 * dispay sequence
	 *
	 * @param columns
	 *            array of columns
	 * @param sqlFrom
	 *            from clause, can be modified
	 * @param sqlOrder
	 *            order by clause, can me modified
	 * @return true if you updated columns, sequence or sql From clause
	 */
	public boolean updateInfoColumns(ArrayList<Info_Column> columns, StringBuffer sqlFrom, StringBuffer sqlOrder)
	{
		/**
		 * * int AD_Role_ID = Env.getAD_Role_ID (Env.getCtx()); // Can be
		 * Role/User specific String from = sqlFrom.toString(); if
		 * (from.startsWith ("M_Product")) { columns.add (new
		 * Info_Column("Header", "'sql'", String.class).seq(35)); return true;
		 * }/**
		 */
		return false;
	} // updateInfoColumns

	private String validatePaymentTerm(MInvoice invoice)
	{

		/*
		 * Properties ctx = invoice.getCtx(); String trx =
		 * invoice.get_TrxName();
		 *
		 * String docStatus = invoice.getDocStatus(); String error = "";
		 *
		 * if (docStatus.equals(MInvoice.DOCSTATUS_Completed) ||
		 * docStatus.equals(MInvoice.DOCSTATUS_Reversed) ||
		 * docStatus.equals(MInvoice.DOCSTATUS_Closed) ||
		 * docStatus.equals(MInvoice.DOCSTATUS_Voided)){
		 *
		 * MInvoicePaySchedule[] ischedule = POLBR.getInvoicePaySchedule(ctx,
		 * invoice.getC_Invoice_ID(), trx);
		 *
		 * int C_PaymentTerm_ID = invoice.getC_PaymentTerm_ID(); MPaymentTerm
		 * paymentTerm = new MPaymentTerm(ctx,C_PaymentTerm_ID,trx);
		 * MPaySchedule[] schedule = paymentTerm.getSchedule(true);
		 *
		 * if (ischedule.length != schedule.length){ error =
		 * "Condio de Pagamento Inconsistente"; log.log(Level.WARNING, error);
		 * return error; }
		 *
		 * }
		 */

		return null;
	}

	/**
	 * String Representation
	 *
	 * @return info
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer("ValidatorInvoice@AdempiereLBR - Powered by Kenos & Faire");
		return sb.toString();
	} // toString

} // ValidatorInvoice