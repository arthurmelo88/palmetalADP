package org.adempierelbr.nfe.v200.converters;

import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.NFeCOFINSBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins.COFINSFactory;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins.INFeCOFINSBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins.NFeCOFINSAliqBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins.NFeCOFINSNTBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins.NFeCOFINSOutrBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins.NFeCOFINSQtdeBean;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Classe responsavel por criar a representacao em XML
 *  seguindo o layout da NF-e do imposto COFINS
 * @author Fernando de O.Moraes (fernando.moraes @ faire)
 *
 */
public class COFINSConverter implements Converter {

	/**
	 * 
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,MarshallingContext context) {
		NFeCOFINSBean bean = (NFeCOFINSBean) value;
		if (bean.getCOFINS() instanceof INFeCOFINSBean){
			INFeCOFINSBean COFINS = (INFeCOFINSBean) bean.getCOFINS();
			if(COFINS!=null){
				if (COFINS.getCST().equals(COFINSFactory.OperacaoTributavel01) 
						|| (COFINS.getCST().equals(COFINSFactory.OperacaoTributavel02)))
				{
					writer.startNode("COFINSAliq");
					context.convertAnother(bean.getCOFINS());
					writer.endNode();	
				}

				else if (COFINS.getCST().equals(COFINSFactory.OperacaoTributavel03))
				{
					writer.startNode("COFINSQtde");
					context.convertAnother(bean.getCOFINS());
					writer.endNode();	
				}	
				else if (COFINS.getCST().equals(COFINSFactory.OperacaoTributavel04) 
						||  COFINS.getCST().equalsIgnoreCase(COFINSFactory.OperacaoTributavel06)
						||  COFINS.getCST().equalsIgnoreCase(COFINSFactory.OperacaoTributavel07)
						||  COFINS.getCST().equalsIgnoreCase(COFINSFactory.SemIncidencia)
						||  COFINS.getCST().equalsIgnoreCase(COFINSFactory.SuspensaoContribuicao)
						)
				{
					writer.startNode("COFINSNT");
					context.convertAnother(bean.getCOFINS());
					writer.endNode();	
				}	
				
				else if (COFINS.getCST().equals(COFINSFactory.Outras))
				{
					writer.startNode("COFINSOutr");
					context.convertAnother(bean.getCOFINS());
					writer.endNode();	
				}	
				
			}
		}
	}

	/**
	 * 
	 */
	public Object unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context) {
		NFeCOFINSBean bean = new NFeCOFINSBean();
 		reader.moveDown();
 		String nodeName = reader.getNodeName();
 		if (nodeName.endsWith("COFINSAliq"))
		{
			NFeCOFINSAliqBean COFINS= (NFeCOFINSAliqBean) context.convertAnother(bean, NFeCOFINSAliqBean.class);
 			bean.setCOFINS(COFINS);
		} else if (nodeName.endsWith("COFINSQtde")){
			
 			NFeCOFINSQtdeBean COFINS= (NFeCOFINSQtdeBean) context.convertAnother(bean, NFeCOFINSQtdeBean.class);
 			bean.setCOFINS(COFINS);
 		}else if (nodeName.endsWith("COFINSNT"))
		{
 			NFeCOFINSNTBean COFINS= (NFeCOFINSNTBean) context.convertAnother(bean, NFeCOFINSNTBean.class);
 			bean.setCOFINS(COFINS);
 		}else if (nodeName.endsWith("COFINSOutr"))
		{
 			NFeCOFINSOutrBean COFINS= (NFeCOFINSOutrBean) context.convertAnother(bean, NFeCOFINSOutrBean.class);
 			bean.setCOFINS(COFINS);
 		}
 		reader.moveUp();

		return bean;
	}
	

	/**
	 * 
	 */
	public boolean canConvert(Class type) {
		return NFeCOFINSBean.class == type;
	}



}
