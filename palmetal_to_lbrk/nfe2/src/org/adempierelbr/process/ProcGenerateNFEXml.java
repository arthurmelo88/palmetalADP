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
package org.adempierelbr.process;

import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;

import org.adempierelbr.model.MLBRNFeLot;
import org.adempierelbr.model.MLBRNotaFiscal;
import org.adempierelbr.nfe.NFeXMLGenerator;
import org.adempierelbr.util.TextUtil;
import org.compiere.model.MAttachment;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;

/**
 * 	Processo para geraÃ§Ã£o do XML
 * 
 * @author Ricardo Santana (Kenos, www.kenos.com.br)
 * @contributor Mario Grigioni
 */
public class ProcGenerateNFEXml extends SvrProcess 
{
	/** Nota Fiscal               */
	private static Integer p_LBR_NotaFiscal_ID = 0;
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(ProcGenerateNFEXml.class);

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("LBR_NotaFiscal_ID"))
				p_LBR_NotaFiscal_ID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
		p_LBR_NotaFiscal_ID = getRecord_ID();
	}	//	prepare

	/**
	 * 	DoIt
	 */
	protected String doIt() throws Exception 
	{
		MLBRNotaFiscal nf = new MLBRNotaFiscal(getCtx(), p_LBR_NotaFiscal_ID, null);
		//
		if (nf.get_Value("lbr_NFeProt") != null || nf.isProcessed())
		{
			return "NF jÃ¡ processada.";
		}
		
		String nfeDesc = "";
		
		if (nf.getLBR_NFeLot_ID() > 0)
		{
			//Se lote nÃ£o foi enviado apaga o lote ou processado
			MLBRNFeLot lot = new MLBRNFeLot(getCtx(),nf.getLBR_NFeLot_ID(),null);
			if (lot.islbr_LotSent() && !lot.isProcessed())
				return "Lote jÃ¡ enviado. Processar retorno para verificar erros.";
			
			if (!lot.isProcessed()){
				log.log(Level.WARNING, "Lote excluÃ­do: " + lot.getDocumentNo());
				lot.delete(true); //Apaga Lote
			}
			
			// Remove do Lote
			nf.setLBR_NFeLot_ID(0);
			nfeDesc += "["+TextUtil.timeToString(getTime(), "yyyy-MM-dd HH:mm:ss")+"] NF removida do lote anterior\n";
		}
		
		
		if (nf.getlbr_NFeID() != null && !nf.getlbr_NFeID().equals("")){
			MAttachment attach = nf.getAttachment(true);
			if (attach != null){
				if (attach.delete(true)) // Apaga o XML antigo
					nfeDesc = "["+TextUtil.timeToString(getTime(), "yyyy-MM-dd HH:mm:ss")+"] XML antigo deletado\n";
			}
		}
		
		if (nf.getlbr_NFeDesc() == null)
			nf.setlbr_NFeDesc(nfeDesc);
		else
			nf.setlbr_NFeDesc(nf.getlbr_NFeDesc() + nfeDesc);

		nf.save();
		//
		 new NFeXMLGenerator(nf).doit();
		//if (!result.equals(""))
		//	return result;
		//
		
		return "Processo finalizado";
	}	//	doIt
	
	private Timestamp getTime(){
		return new Timestamp(new Date().getTime());
	}
	
}	//	ProcGenerateNFEXml