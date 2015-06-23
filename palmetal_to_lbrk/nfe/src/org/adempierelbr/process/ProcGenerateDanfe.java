package org.adempierelbr.process;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRReportFont;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.adempierelbr.model.MLBRNotaFiscal;
import org.compiere.model.MAttachment;
import org.compiere.model.MAttachmentEntry;
import org.compiere.model.MImage;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MProcess;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.report.JRViewerProvider;
import org.compiere.report.ReportStarter;
import org.compiere.util.CLogger;

public class ProcGenerateDanfe extends SvrProcess{

	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(ProcGenerateDanfe.class);
	
	/** Nota Fiscal               */
	private Integer p_LBR_NotaFiscal_ID = 0;
	private Integer p_LBR_NFeLote_ID = 0;

	//private MLBRNotaFiscal doc;
	
	
	/** Pattern				*/
	String pattern = "[0-9]{44}\\-nfe\\.xml";
	
	/** XML DataSource **/
	private JRXmlDataSource dataSource;
	/**XPATH */ 
	private String expressao="//NFe";
	
	/** JASPER */
	private InputStream danfe;
	private Map params = new HashMap();
	
	/** Process Name*/
	private String PROCESSVALUE="LBR_GERARDANFE";
	/** Jasper Name */
	private String NOMEARQUIVO="ImpressaoDanfeRetratoA4Report.jasper";
	
	
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
			else if (para[i].getParameterName().equals("LBR_NotaFiscal_ID"))
				p_LBR_NotaFiscal_ID = para[i].getParameterAsInt();
			else if (para[i].getParameterName().equals("LBR_NFeLot_ID"))
				p_LBR_NFeLote_ID = para[i].getParameterAsInt();
			
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
	}	//	prepare
	
	
	/**
	 *  Perform process.
	 *  @return Message (variables are parsed)
	 *  @throws Exception if not successful e.g.
	 *  throw new AdempiereUserError ("@FillMandatory@  @C_BankAccount_ID@");
	 */
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		if (p_LBR_NotaFiscal_ID==0 && p_LBR_NFeLote_ID==0)
			throw new Exception("LBR_NotaFiscal_ID=0 ou p_LBR_Lote_ID==0");
		
		if (p_LBR_NotaFiscal_ID > 0){
			JasperPrint jasperPrint = getJasperPrint(p_LBR_NotaFiscal_ID);
			gerarDANFE(jasperPrint);
		}else{
			String whereClause = "LBR_NFeLot_ID=?";
			//
			MTable table = MTable.get(getCtx(), MLBRNotaFiscal.Table_Name);
			Query query =  new Query(getCtx(), table, whereClause, null);
		 		  query.setParameters(new Object[]{p_LBR_NFeLote_ID});
		 		  query.setOrderBy("DocumentNo, LBR_NotaFiscal_ID");
			//
		 	List<MLBRNotaFiscal> list = query.list();
		 	//
		 	List reports = new ArrayList();
		 	for (MLBRNotaFiscal nf : list)
		 	{
		 		JasperPrint jasperPrint = getJasperPrint(nf.getLBR_NotaFiscal_ID());
		 		reports.add(jasperPrint);

		 	}
		 	gerarDANFE(getCombinedReport(reports));
		}

		
		return null;
		
	}
	
	public JasperPrint getJasperPrint(int p_LBR_DocFiscal_ID) throws Exception{
		MLBRNotaFiscal doc = new MLBRNotaFiscal(getCtx(),p_LBR_DocFiscal_ID,null);
		String protocoloAutorizacao = doc.getlbr_NFeProt();
		//if (doc.getlbr_NFeStatus().equals("101") || !(protocoloAutorizacao!=null && protocoloAutorizacao.length() > 0))
		//	throw new Exception("Nao eh permitido imprimir o DANFE");
		
		
		if (protocoloAutorizacao!=null){
			Timestamp ts = (Timestamp) doc.getDateTrx();
			String dadosProtocolo = protocoloAutorizacao + "  " +
				new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss").format(ts);
			
			params.put( "protocoloDataAutorizacao", dadosProtocolo);

		}
		params.put( "logotipo", getLogoTipoasInputStream(doc.getAD_Org_ID()) );

		//END
		
		getDataSource(doc);
		getReport();
		return getJasperPrint();
		
	}
	
	/**
	 * @author marcped-faire 19/07/2011
	 * @return
	 */
	private ByteArrayInputStream getLogoTipoasInputStream(int AD_Org_ID){
		MOrgInfo orginfo = MOrgInfo.get(getCtx(), AD_Org_ID,null);
		int imageID  = orginfo.getLogo_ID();
		if (imageID > 0){
			MImage image = MImage.get(getCtx(), imageID);
			byte[] data = image.getBinaryData();
			ByteArrayInputStream aux =  new ByteArrayInputStream(data);
			
			return aux;
		}else
			return null;

	}
	
	
	/**
	 * Obtem o XML da Nota Fiscal que sera usado como datasource
	 * @throws Exception
	 */
	private void getDataSource(MLBRNotaFiscal doc) throws Exception
	{
		if (doc==null)
			return;
		
		MAttachment attachLotNFe = doc.createAttachment();
		MAttachmentEntry[] anexos = attachLotNFe.getEntries();
		int pos=0;
		MAttachmentEntry xmlNFE =null;
		for (; pos <  anexos.length ; pos ++){
			MAttachmentEntry anexo = anexos[pos];
			xmlNFE=anexo ;
			break;
			
			
		}
		
		if (xmlNFE!=null)
			dataSource = new JRXmlDataSource(xmlNFE.getInputStream(),expressao);
		
	}	
	
	/** 
	 * Obtem o(s) Jasper(s) que representam o DANFE
	 */
	private void getReport(){
		int process_ID = getProcessInfo().getAD_Process_ID();
		MProcess proc = new MProcess(getCtx(),process_ID,null);
		MAttachment anexos = proc.getAttachment();
		MAttachmentEntry[] entries = anexos.getEntries();

		for(int i = 0; i < entries.length; i++) {
			String name = entries[i].getName();
			if (!name.equals(NOMEARQUIVO) ) {
				InputStream reportFile = entries[i].getInputStream();
				int pos = name.lastIndexOf('.');
				if (reportFile != null){
					params.put(entries[i].getName().substring(0, pos),reportFile);
				}
			}else if(entries[i].getName().equals(NOMEARQUIVO))
				danfe = entries[i].getInputStream();
		}

		
	}

	private JasperPrint getJasperPrint() throws JRException {
		// TODO Auto-generated method stub
		JasperReport jasperReport =  (JasperReport)JRLoader.loadObject(danfe);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,params, dataSource);
		
		return jasperPrint;
	}
	
	/**
	 * Gerar DANFE
	 * @throws JRException
	 */
	private void gerarDANFE(JasperPrint jasperPrint) throws JRException {

	
		log.info( "ReportStarter.startProcess run report -"+jasperPrint.getName());
        JRViewerProvider viewerLauncher = ReportStarter.getReportViewerProvider();
        viewerLauncher.openViewer(jasperPrint,"DANFE");
	}
	

	
	public JasperPrint getCombinedReport(List reports) throws JRException {

		JasperPrint combinedReport = new JasperPrint();

		// temp variable
		JasperPrint print=null;
		for (int i = 0; i < reports.size(); i++) {
			print = (JasperPrint) reports.get(i);

			copyPages(combinedReport, print);
			copyFonts(combinedReport, print);
		}
		copyProperties(combinedReport, print);
		return combinedReport;
	}

	private void copyFonts(final JasperPrint combinedReport,
			JasperPrint filledReport) throws JRException {
		List fonts = filledReport.getFontsList();

		if (fonts == null) {
			return;
		}

		for (Iterator iter = fonts.iterator(); iter.hasNext();) {
			JRReportFont font = (JRReportFont) iter.next();
			if (!combinedReport.getFontsMap().containsKey(font.getName())) {
				combinedReport.addFont(font);
			}
		}
	}

	private void copyPages(final JasperPrint combinedReport,
			JasperPrint filledReport) {
		List pages = filledReport.getPages();

		if (pages == null) {
			return;
		}

		for (Iterator iter = pages.iterator(); iter.hasNext();) {
			JRPrintPage page = (JRPrintPage) iter.next();
			combinedReport.addPage(page);
		}
	}

	/**
	 * Set the properties of the target report to the properties of the source.
	 * You might want to change this up.
	 * 
	 * @param source
	 * @param target
	 */
	private void copyProperties(final JasperPrint target, JasperPrint source) {
		target.setDefaultFont(source.getDefaultFont());
		target.setName(source.getName());
		target.setOrientation(source.getOrientation());
		target.setPageHeight(source.getPageHeight());
		target.setPageWidth(source.getPageWidth());
	}


}
