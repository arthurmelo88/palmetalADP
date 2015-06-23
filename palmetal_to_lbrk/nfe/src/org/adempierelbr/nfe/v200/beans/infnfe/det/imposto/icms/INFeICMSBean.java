package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import org.adempierelbr.nfe.v200.beans.INFeBean;

/**
 * Interface, todo e qualquer tipo de ICMS deve implementar essa classe
 * @author Fernando de O Moraes (fernando.moraes @ faire.com.br)
 *
 */
public interface INFeICMSBean extends INFeBean{

	public String getCST();
	public void setCST(String CST);
}
