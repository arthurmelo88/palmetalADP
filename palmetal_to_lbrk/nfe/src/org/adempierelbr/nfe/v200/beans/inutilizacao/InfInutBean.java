package org.adempierelbr.nfe.v200.beans.inutilizacao;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("infInut")
public class InfInutBean 
{
   @XStreamAsAttribute
   private String Id;
   
   private int tpAmb;
   private String xServ;
   private int cUF;
   private String ano;
   private String CNPJ;
   private int mod;
   private int serie;
   private long nNFIni;
   private long nNFFin;
   private String xJust;

   public InfInutBean()
   {
   }


   public void setId(String id)
   {
      this.Id = id;
   }


   public String getId()
   {
      return Id;
   }


   public void setTpAmb(int tpAmb)
   {
      this.tpAmb = tpAmb;
   }


   public int getTpAmb()
   {
      return tpAmb;
   }

   public void setCUF(int cUF)
   {
      this.cUF = cUF;
   }


   public int getCUF()
   {
      return cUF;
   }


   public void setAno(String ano)
   {
      this.ano = ano;
   }


   public String getAno()
   {
      return ano;
   }


   public void setCNPJ(String cNPJ)
   {
      this.CNPJ = cNPJ;
   }


   public String getCNPJ()
   {
      return CNPJ;
   }


   public void setMod(int mod)
   {
      this.mod = mod;
   }


   public int getMod()
   {
      return mod;
   }


   public void setSerie(int serie)
   {
      this.serie = serie;
   }


   public int getSerie()
   {
      return serie;
   }


   public void setNNFIni(long nNFIni)
   {
      this.nNFIni = nNFIni;
   }


   public long getNNFIni()
   {
      return nNFIni;
   }


   public void setNNFFin(long nNFFin)
   {
      this.nNFFin = nNFFin;
   }


   public long getNNFFin()
   {
      return nNFFin;
   }

   public void setXServ(String xServ)
   {
      this.xServ = xServ;
   }


   public String getXServ()
   {
      return xServ;
   }


   public void setXJust(String xJust)
   {
      this.xJust = xJust;
   }


   public String getXJust()
   {
      return xJust;
   }
}
