package org.adempierelbr.nfe.v200.converters;

import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.NFePISBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis.INFePISBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis.NFePISAliqBean;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis.NFePISNT;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis.NFePISOutr;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis.NFePISQtde;
import org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis.PISFactory;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Classe responsavel por criar a representacao em XML
 *  seguindo o layout da NF-e do imposto PIS
 * @author Fernando de O.Moraes (fernando.moraes @ faire)
 *
 */
public class PISConverter implements Converter {

	/**
	 * 
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,MarshallingContext context) {
		NFePISBean bean = (NFePISBean) value;
		if (bean.getPis() instanceof INFePISBean){
			INFePISBean pis = (INFePISBean) bean.getPis();
			if(pis!=null){
				if (pis.getCST().equals(PISFactory.OperacaoTributavel01) 
						|| (pis.getCST().equals(PISFactory.OperacaoTributavel02)))
				{
					writer.startNode("PISAliq");
					context.convertAnother(bean.getPis());
					writer.endNode();	
				}

				else if (pis.getCST().equals(PISFactory.OperacaoTributavel03))
				{
					writer.startNode("PISQtde");
					context.convertAnother(bean.getPis());
					writer.endNode();	
				}	
				else if (pis.getCST().equals(PISFactory.OperacaoTributavel04) 
						||  pis.getCST().equalsIgnoreCase(PISFactory.OperacaoTributavel06)
						||  pis.getCST().equalsIgnoreCase(PISFactory.OperacaoTributavel07)
						||  pis.getCST().equalsIgnoreCase(PISFactory.SemIncidencia)
						||  pis.getCST().equalsIgnoreCase(PISFactory.SuspensaoContribuicao)
						)
				{
					writer.startNode("PISNT");
					context.convertAnother(bean.getPis());
					writer.endNode();	
				}	
				
				else if (pis.getCST().equals(PISFactory.Outras))
				{
					writer.startNode("PISOutr");
					context.convertAnother(bean.getPis());
					writer.endNode();	
				}	
				
			}
		}
	}

	/**
	 * 
	 */
	public Object unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context) {
		NFePISBean bean = new NFePISBean();
 		reader.moveDown();
 		String nodeName = reader.getNodeName();
		if (nodeName.endsWith("PISAliq"))
		{
			NFePISAliqBean pis= (NFePISAliqBean) context.convertAnother(bean, NFePISAliqBean.class);
 			bean.setPis(pis);
 		} else if (nodeName.endsWith("PISQtde"))
		{
 			NFePISQtde pis= (NFePISQtde) context.convertAnother(bean, NFePISQtde.class);
 			bean.setPis(pis);
 		}else if (nodeName.endsWith("PISNT"))
		{
 			NFePISNT pis= (NFePISNT) context.convertAnother(bean, NFePISNT.class);
 			bean.setPis(pis);
 		}else if (nodeName.endsWith("PISOutr"))
		{
 			NFePISOutr pis= (NFePISOutr) context.convertAnother(bean, NFePISOutr.class);
 			bean.setPis(pis);
 		}
		reader.moveUp();

		return bean;
	}
	

	/**
	 * 
	 */
	public boolean canConvert(Class type) {
		return NFePISBean.class == type;
	}



}
