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
package org.adempierelbr.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Properties;

import org.adempierelbr.nfe.SocketFactoryDinamico;
import org.adempierelbr.util.NFeUtil;
import org.adempierelbr.util.TextUtil;
import org.apache.commons.httpclient.protocol.Protocol;
import org.compiere.model.MAttachment;
import org.compiere.model.MOrgInfo;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *	Model for LBR_DigitalCertificate
 *
 *	@author Ricardo Santana (Kenos, www.kenos.com.br)
 *	@contributor Mario Grigioni
 *	@version $Id: MDigitalCertificate.java,v 1.0 2009/08/23 00:51:27 ralexsander Exp $
 */
public class MLBRDigitalCertificate extends X_LBR_DigitalCertificate
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**	Certificado do Cliente		*/
	private static String certTypeOrg 	= "";

	/**	Certificado do WS			*/
	private static String certTypeWS	= "";

	/**************************************************************************
	 *  Default Constructor
	 *  @param Properties ctx
	 *  @param int ID (0 create new)
	 *  @param String trx
	 */
	public MLBRDigitalCertificate(Properties ctx, int ID, String trx){
		super(ctx,ID,trx);
	}

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *  @param trxName transaction
	 */
	public MLBRDigitalCertificate (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	/**
	 * setCertificate
	 * Set all System.property for webservice connection
	 */
	public static void setCertificate(Properties ctx, int AD_Org_ID) throws Exception{

		MOrgInfo oi = MOrgInfo.get(ctx, AD_Org_ID, null);

		Integer certOrg = (Integer) oi.get_Value("LBR_DC_Org_ID");
		Integer certWS = (Integer) oi.get_Value("LBR_DC_WS_ID");
		MLBRDigitalCertificate dcOrg = new MLBRDigitalCertificate(Env.getCtx(), certOrg, null);
		MLBRDigitalCertificate dcWS = new MLBRDigitalCertificate(Env.getCtx(), certWS, null);

		//CERTIFICADO CLIENTE
		if (dcOrg.getlbr_CertType() == null)
			throw new Exception("Certificate Type is NULL");
		else if (dcOrg.getlbr_CertType().equals(MLBRDigitalCertificate.LBR_CERTTYPE_PKCS12))
			certTypeOrg = "PKCS12";
		else if (dcOrg.getlbr_CertType().equals(MLBRDigitalCertificate.LBR_CERTTYPE_JavaKeyStore))
			certTypeOrg = "JKS";
		else
			throw new Exception("Unknow Certificate Type or Not implemented yet");

		File certFileOrg = NFeUtil.getAttachmentEntryFile(dcOrg.getAttachment(true).getEntry(0));

		//CERTIFICADO WS
		if (dcWS.getlbr_CertType() == null)
			throw new Exception("Certificate Type is NULL");
		else if (dcWS.getlbr_CertType().equals(MLBRDigitalCertificate.LBR_CERTTYPE_PKCS12))
			certTypeWS = "PKCS12";
		else if (dcWS.getlbr_CertType().equals(MLBRDigitalCertificate.LBR_CERTTYPE_JavaKeyStore))
			certTypeWS = "JKS";
		else
			throw new Exception("Unknow Certificate Type or Not implemented yet");

		File certFileWS = NFeUtil.getAttachmentEntryFile(dcWS.getAttachment(true).getEntry(0));

		//BEGIN degois @ faire
		String trustStorePath = certFileWS.toString();
		int SSL_PORT = 443; 
		InputStream entrada = new FileInputStream(certFileOrg.getAbsolutePath());
		
		KeyStore ks = KeyStore.getInstance(certTypeOrg);  
		
		try {  
            ks.load(entrada, dcOrg.getPassword().toCharArray());  
        } catch (IOException e) {  
            throw new Exception("Senha do Certificado Digital esta incorreta ou Certificado inválido.");  
        } 
		
		 String alias = "";    
         Enumeration<String> aliasesEnum = ks.aliases();    
         while (aliasesEnum.hasMoreElements()) {    
             alias = (String) aliasesEnum.nextElement();    
             if (ks.isKeyEntry(alias)) break;    
         }  
         
		 X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);  
         PrivateKey privateKey = (PrivateKey) ks.getKey(alias, dcOrg.getPassword().toCharArray());  
         SocketFactoryDinamico socketFactoryDinamico = new SocketFactoryDinamico(certificate, privateKey);  
         socketFactoryDinamico.setFileCacerts(trustStorePath);
         socketFactoryDinamico.setTrustStorePassword(dcWS.getPassword());

         Protocol protocol = new Protocol("https", socketFactoryDinamico, SSL_PORT);    
         Protocol.registerProtocol("https", protocol);
         
		//
         //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
		//Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		//
		//System.setProperty("javax.net.ssl.keyStoreType", certTypeOrg);
		//System.setProperty("javax.net.ssl.keyStore", certFileOrg.toString());
		//System.setProperty("javax.net.ssl.keyStorePassword", dcOrg.getPassword());
		//
		//System.setProperty("javax.net.ssl.trustStoreType", certTypeWS);
		//System.setProperty("javax.net.ssl.trustStore", certFileWS.toString());
		//if(dcWS.getPassword()!=null && !"".equals(dcWS.getPassword()))
		//	System.setProperty("javax.net.ssl.trustStorePassword", dcWS.getPassword());
		// BF - JRE > 1.6.19
		//System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
         
         //END
		

	} //setCertificate
	
	/**
	 * 	Called before Save for Pre-Save Operation
	 * 	@param newRecord new record
	 *	@return true if record can be saved
	 */
	protected boolean beforeSave (boolean newRecord)
	{		
		//	No certificate type
		if (getlbr_CertType() == null)
		{
			log.saveError ("Error", Msg.getElement (getCtx(), "lbr_CertType"));
			return false;
		}
	
		//	Needs an attachment to continue
		if (newRecord)
			return true;
		
		MAttachment att = getAttachment (true);

		//	No attachment
		if ((LBR_CERTTYPE_JavaKeyStore.equals(getlbr_CertType ()) || LBR_CERTTYPE_PKCS12.equals(getlbr_CertType ())) 
				&& (att == null || att.getEntryCount() == 0))
		{
			log.saveError ("Error", Msg.getElement (getCtx(), "AD_Attachment"));
			return false;
		}
		
		try
		{
			String certType = getlbr_CertType();
			String pass = getPassword();
			
			if (LBR_CERTTYPE_PKCS12.equals(getlbr_CertType()))
				certType = "PKCS12";
			else if (LBR_CERTTYPE_ICPTrustStoreJKS.equals(getlbr_CertType()))
				certType = "JKS";
			
			String alias = getAlias();
			
			if (alias == null || alias.length() == 0)
				alias = "nfe";
				
			if (pass == null || pass.length() == 0)
			{
				pass = "changeit";
				setPassword(pass);
			}
			//
			KeyStore ks = KeyStore.getInstance (certType);
			ks.load (att.getEntry(0).getInputStream(), pass.toCharArray());
			X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
			//
			if (certificate == null)
			{
				Enumeration<String> aliases = ks.aliases();
				while (aliases.hasMoreElements()) 
				{
					alias = aliases.nextElement();
					X509Certificate tmp = (X509Certificate) ks.getCertificate (alias);
					
					if (tmp != null && (certificate == null || tmp.getNotAfter().after (Env.getContextAsDate(Env.getCtx(), "#Date"))))
					{
						certificate = tmp;
						setAlias(alias);
						break;
					}	
				}
				
				if (certificate == null)
				{
					log.saveWarning ("Invalid", "N\u00E3o foi encontrado um certificado v\u00E1lido");
					return true;
				}
			}
			setValidFrom (new Timestamp (certificate.getNotBefore().getTime()));
			setValidTo (new Timestamp (certificate.getNotAfter().getTime()));
			//
			if (getValidTo().before(Env.getContextAsDate(Env.getCtx(), "#Date")))
				log.saveWarning ("Invalid", "Certificado expirado em " + TextUtil.timeToString (certificate.getNotAfter(), "dd/MM/yyyy"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//
			log.saveWarning ("Error", "Erro ao validar o certificado. Este certificado n\u00E3o funcionar\u00E1 com a NF-e. Verifique o log do sistema.");
		}
		
		return true;
	}	//	beforeSave
	
	/**
	 * 	Gera um arquivo de configuração para o SmartCard, de acordo com o SO
	 * 	@return configuration file or null for error
	 */
	public String getConfigurationFile ()
	{
		String driverPath 		= null;
		String defaultDriver 	= null;
		String cfgFile 			= System.getProperty("java.io.tmpdir") + File.separator + "Token.cfg";

		byte[] library = null;
		//
		if (Env.isWindows())
		{
			library = getAttachmentData("dll");
			defaultDriver = "C:/Windows/System32/aetpkss1.dll";
		}
		
		else if (Env.isMac())
		{
			library = getAttachmentData("dylib");
			defaultDriver = "/usr/local/lib/libaetpkss.dylib";
		}
		else	//	Is Linux ?
		{
			library = getAttachmentData("so");
			defaultDriver = "/usr/lib/libaetpkss.so";
		}
		
		try 
		{
			//	Check driver - Default path
			if (library == null)
				driverPath = defaultDriver;
	
			//	Load from a file
			else
			{
				driverPath = System.getProperty("java.io.tmpdir") + File.separator + "SmartCard.Driver";
				//
				FileOutputStream fos = new FileOutputStream(driverPath);
				fos.write(library);
				fos.close();
			}
			
			FileWriter f = TextUtil.createFile (cfgFile, false);
			f.write("name= SmartCard");
			f.write("\nlibrary= " + driverPath);
			f.flush();
			f.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return cfgFile;
	}	//	getConfigurationFile



}	//	MDigitalCertificate