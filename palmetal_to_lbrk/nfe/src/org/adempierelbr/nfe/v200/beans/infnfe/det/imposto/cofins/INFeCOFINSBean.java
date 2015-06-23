package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins;

import org.adempierelbr.nfe.v200.beans.INFeBean;

/**
 * Interface, todo e qualquer tipo de PIS deve implementar essa classe
 * @author Fernando de O Moraes (fernando.moraes @ faire.com.br)
 *
 */
public interface INFeCOFINSBean extends INFeBean{

	public String getCST();
	public void setCST(String CST);
}
