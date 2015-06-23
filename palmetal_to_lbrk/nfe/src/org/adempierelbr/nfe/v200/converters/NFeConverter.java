package org.adempierelbr.nfe.v200.converters;

import org.adempierelbr.nfe.v200.beans.NFeBean;
import org.adempierelbr.nfe.v200.beans.infnfe.NFeInfNFeBean;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author Fernando de O.Moraes (fernando.moraes @ faire)
 *
 */
public class NFeConverter implements Converter {

	/**
	 * 
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,MarshallingContext context) {
		NFeBean bean = (NFeBean) value;
		context.convertAnother(bean);
	}

	/**
	 * 
	 */
	public Object unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context) {
		NFeBean nfeBean = new NFeBean();
 		reader.moveDown();
 		NFeInfNFeBean infNFe= (NFeInfNFeBean) context.convertAnother(nfeBean, NFeInfNFeBean.class);
 		nfeBean.setInfNFe(infNFe);
 		reader.moveUp();
 
		return nfeBean;
	}
	

	/**
	 * 
	 */
	public boolean canConvert(Class type) {
		return NFeBean.class == type;
	}



}
