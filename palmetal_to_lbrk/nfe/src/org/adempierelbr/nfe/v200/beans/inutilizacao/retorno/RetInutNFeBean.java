package org.adempierelbr.nfe.v200.beans.inutilizacao.retorno;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("retInutNFe")
public class RetInutNFeBean
{
   @XStreamAsAttribute
   private String versao;
   
   private RetInfInutBean infInut;
   
   public RetInutNFeBean()
   {
   }


   public void setVersao(String versao)
   {
      this.versao = versao;
   }


   public String getVersao()
   {
      return versao;
   }


   public void setInfInut(RetInfInutBean infInutVo)
   {
      this.infInut = infInutVo;
   }


   public RetInfInutBean getInfInut()
   {
      return infInut;
   }
}
