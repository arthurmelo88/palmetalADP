package org.adempierelbr.nfe.v200.beans.infnfe.det.prod;

import java.util.ArrayList;
import java.util.List;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Informar dados da importação
 * @author Fernando
 *
 */
@XStreamAlias("DI")
public class NFeDIBean implements INFeBean
{
   private String nDI;
   private String dDI;
   private String xLocDesemb;
   private String UFDesemb;
   private String dDesemb;
   private String cExportador;
   
   @XStreamImplicit
   private List<NFeAdiBean> adi;

   
   public void setNDI(String nDI)
   {
      this.nDI = nDI;
   }


   public String getNDI()
   {
      return nDI;
   }


   public void setDDI(String dDI)
   {
      this.dDI = dDI;
   }


   public String getDDI()
   {
      return dDI;
   }


   public void setXLocDesemb(String xLocDesemb)
   {
      this.xLocDesemb = xLocDesemb;
   }


   public String getXLocDesemb()
   {
      return xLocDesemb;
   }


   public void setUFDesemb(String uFDesemb)
   {
      this.UFDesemb = uFDesemb;
   }


   public String getUFDesemb()
   {
      return UFDesemb;
   }


   public void setDDesemb(String dDesemb)
   {
      this.dDesemb = dDesemb;
   }


   public String getDDesemb()
   {
      return dDesemb;
   }


   public void setCExportador(String cExportador)
   {
      this.cExportador = cExportador;
   }


   public String getCExportador()
   {
      return cExportador;
   }
   
   public List<NFeAdiBean> getAdi()
   {
      if (adi == null)
      {
         adi = new ArrayList<NFeAdiBean>();
      }
      return this.adi;
   }

}