package org.adempierelbr.nfe.v200.converters;

import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.NFeICMSBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.ICMSFactory;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.INFeICMSBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS00Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS10Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS20Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS30Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS40Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS51Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS60Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS70Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMS90Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSPartBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN101Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN102Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN201Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN202Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN500Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSN900Bean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms.NFeICMSSTBean;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Classe responsavel por criar a representacao em XML
 *  seguindo o layout da NF-e do imposto ICMS
 * @author Fernando de O.Moraes (fernando.moraes @ faire)
 *
 */
public class ICMSConverter implements Converter {

	/**
	 * 
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,MarshallingContext context) {
		NFeICMSBean bean = (NFeICMSBean) value;
		if (bean.getIcms() instanceof INFeICMSBean){
			INFeICMSBean icms = (INFeICMSBean) bean.getIcms();
			if(icms!=null){
				if (icms.getCST().equals(ICMSFactory.TribIntegralmente)){
					writer.startNode("ICMS00");
					context.convertAnother(bean.getIcms());
					writer.endNode();
				}
				else if (icms.getCST().equalsIgnoreCase(ICMSFactory.TribEComCobrDoICMSPorSubstTrib))
				{
					writer.startNode("ICMS10");
					context.convertAnother(bean.getIcms());
					writer.endNode();
				}
				else if (icms.getCST().equalsIgnoreCase(ICMSFactory.ComReducaoDeBaseDeCalculo))
				{
					writer.startNode("ICMS20");
					context.convertAnother(bean.getIcms());
					writer.endNode();
				}
				else if (icms.getCST().equalsIgnoreCase(ICMSFactory.IsentaNaoTribComCobrDeICMSPorSubstTrib))
				{
					writer.startNode("ICMS30");
					context.convertAnother(bean.getIcms());
					writer.endNode();
				}
				else if (icms.getCST().equalsIgnoreCase(ICMSFactory.Isenta) 
						||  icms.getCST().equalsIgnoreCase(ICMSFactory.NaoTributada)
						||  icms.getCST().equalsIgnoreCase(ICMSFactory.Suspensao)
						)
				{
					writer.startNode("ICMS40");
					context.convertAnother(bean.getIcms());
					writer.endNode();	
				}	
				else if (icms.getCST().equalsIgnoreCase(ICMSFactory.Deferimento)){
					writer.startNode("ICMS51");
					context.convertAnother(bean.getIcms());
					writer.endNode();
				}	
				else if (icms.getCST().equalsIgnoreCase(ICMSFactory.ICMSCobradoAnteriormentePorSubstTrib)){
					writer.startNode("ICMS60");
					context.convertAnother(bean.getIcms());
					writer.endNode();
				}						
				else if (icms.getCST().equalsIgnoreCase(ICMSFactory.RedDeBCECobICMSSubstTrib))
				{
					writer.startNode("ICMS70");
					context.convertAnother(bean.getIcms());
					writer.endNode();
				}
				else if (icms.getCST().equalsIgnoreCase(ICMSFactory.Outros))
				{
					writer.startNode("ICMS90");
					context.convertAnother(bean.getIcms());
					writer.endNode();
				}
				//-------------------Simples Nacional ------------------//
				else if (icms.getCST().equals(ICMSFactory.IMPOSTO_ICMSSN_CSOSN_102_TributadaPeloSimplesSemPermissaoDeCredito)
						|| icms.getCST().equals(ICMSFactory.IMPOSTO_ICMSSN_CSOSN_103_IsencaoDoICMSNoSimplesParaFaixaDeReceitaBruta)
						|| icms.getCST().equals(ICMSFactory.IMPOSTO_ICMSSN_CSOSN_300_Imune)
						|| icms.getCST().equals(ICMSFactory.IMPOSTO_ICMSSN_CSOSN_400_NaoTributadaPeloSimples)
						)
				{
					writer.startNode("ICMSSN102");
					context.convertAnother(bean.getIcms());
					writer.endNode();
				}
				else if (icms.getCST().equals(ICMSFactory.IMPOSTO_ICMSSN_CSOSN_202_TribSimplesSemPermCredCCobrDoICMSST)
						|| icms.getCST().equals(ICMSFactory.IMPOSTO_ICMSSN_CSOSN_203_IsenICMSSimpFaixaDeRecBrtComCobICMSST)){
					writer.startNode("ICMSSN202");
					context.convertAnother(bean.getIcms());
					writer.endNode();
					
				}else if ( icms.getCST().equals(ICMSFactory.IMPOSTO_ICMSSN_CSOSN_101_TributadaPeloSimplesComPermissaoDeCredito)
						||  icms.getCST().equals(ICMSFactory.IMPOSTO_ICMSSN_CSOSN_201_TribSimplesComPermCredCCobrDoICMSST)
						|| icms.getCST().equals(ICMSFactory.IMPOSTO_ICMSSN_CSOSN_500_ICMSCobAntPorSubstTribSubstituidoOuAntec)
						|| icms.getCST().equals(ICMSFactory.IMPOSTO_ICMSSN_CSOSN_900_Outros))
				{
					writer.startNode("ICMSSN"+icms.getCST());
					context.convertAnother(bean.getIcms());
					writer.endNode();
				}
				
			}
		}
	}

	/**
	 * 
	 */
	public Object unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context) {
 		NFeICMSBean bean = new NFeICMSBean();
 		reader.moveDown();
 		String nodeName = reader.getNodeName();
 		if (nodeName.endsWith("ICMS00")){
 			NFeICMS00Bean imcs00= (NFeICMS00Bean) context.convertAnother(bean, NFeICMS00Bean.class);
 			bean.setIcms(imcs00);
 		} else if (nodeName.endsWith("ICMS10")){
 			NFeICMS10Bean imcs10= (NFeICMS10Bean) context.convertAnother(bean, NFeICMS10Bean.class);
 			bean.setIcms(imcs10);
 		}else if (nodeName.endsWith("ICMS20")){
 			NFeICMS20Bean imcs00= (NFeICMS20Bean) context.convertAnother(bean, NFeICMS20Bean.class);
 			bean.setIcms(imcs00);
 		}
 		else if (nodeName.endsWith("ICMS30")){
 			NFeICMS30Bean imcs00= (NFeICMS30Bean) context.convertAnother(bean, NFeICMS30Bean.class);
 			bean.setIcms(imcs00);
 		}
 		else if (nodeName.endsWith("ICMS40")){
 			NFeICMS40Bean imcs00= (NFeICMS40Bean) context.convertAnother(bean, NFeICMS40Bean.class);
 			bean.setIcms(imcs00);
 		}
 		else if (nodeName.endsWith("ICMS51")){
 			NFeICMS51Bean imcs51= (NFeICMS51Bean) context.convertAnother(bean, NFeICMS51Bean.class);
 			bean.setIcms(imcs51);
 		}
 		else if (nodeName.endsWith("ICMS60")){
 			NFeICMS60Bean imcs00= (NFeICMS60Bean) context.convertAnother(bean, NFeICMS60Bean.class);
 			bean.setIcms(imcs00);
 		}
 		else if (nodeName.endsWith("ICMS70")){
 			NFeICMS70Bean imcs70= (NFeICMS70Bean) context.convertAnother(bean, NFeICMS70Bean.class);
 			bean.setIcms(imcs70);
 		}
 		else if (nodeName.endsWith("ICMS90")){
 			NFeICMS90Bean imcs90= (NFeICMS90Bean) context.convertAnother(bean, NFeICMS90Bean.class);
 			bean.setIcms(imcs90);
 		}
 		else if (nodeName.endsWith("ICMSPart")){
 			NFeICMSPartBean icmsPart= (NFeICMSPartBean) context.convertAnother(bean, NFeICMSPartBean.class);
 			bean.setIcms(icmsPart);
 		}
 		else if (nodeName.endsWith("ICMSSN101")){
 			NFeICMSSN101Bean icmsSN101= (NFeICMSSN101Bean) context.convertAnother(bean, NFeICMSSN101Bean.class);
 			bean.setIcms(icmsSN101);
 		}
 		else if (nodeName.endsWith("ICMSSN102")){
 			NFeICMSSN102Bean icmsSN102= (NFeICMSSN102Bean) context.convertAnother(bean, NFeICMSSN102Bean.class);
 			bean.setIcms(icmsSN102);
 		}
 		else if (nodeName.endsWith("ICMSSN201")){
 			NFeICMSSN201Bean imcsSN201= (NFeICMSSN201Bean) context.convertAnother(bean, NFeICMSSN201Bean.class);
 			bean.setIcms(imcsSN201);
 		}
 		else if (nodeName.endsWith("ICMSSN202")){
 			NFeICMSSN202Bean imcsSN202= (NFeICMSSN202Bean) context.convertAnother(bean, NFeICMSSTBean.class);
 			bean.setIcms(imcsSN202);
 		}
 		else if (nodeName.endsWith("ICMSSN500")){
 			NFeICMSSN500Bean imcsSN500= (NFeICMSSN500Bean) context.convertAnother(bean, NFeICMSSN500Bean.class);
 			bean.setIcms(imcsSN500);
 		}
 		else if (nodeName.endsWith("ICMSSN900")){
 			NFeICMSSN900Bean imcsSN900= (NFeICMSSN900Bean) context.convertAnother(bean, NFeICMSSN900Bean.class);
 			bean.setIcms(imcsSN900);
 		}
 		else if (nodeName.endsWith("ICMSST")){
 			NFeICMSSTBean icmsST= (NFeICMSSTBean) context.convertAnother(bean, NFeICMSSTBean.class);
 			bean.setIcms(icmsST);
 		}
 		reader.moveUp();

		return bean;
	}
	

	/**
	 * 
	 */
	public boolean canConvert(Class type) {
		return NFeICMSBean.class == type;
	}



}
