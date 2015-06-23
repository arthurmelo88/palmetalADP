package org.compiere.process;

public class StatusFabricacaoVO {

	private int c_order_id;
	private int c_orderline_id;
	private int pp_order_id;
	private int qtyordered;
	private int qtdassociada;
	private int maximo;
	
	public int getC_order_id() {
		return c_order_id;
	}
	public void setC_order_id(int c_order_id) {
		this.c_order_id = c_order_id;
	}
	public int getPp_order_id() {
		return pp_order_id;
	}
	public void setPp_order_id(int pp_order_id) {
		this.pp_order_id = pp_order_id;
	}
	public int getQtyordered() {
		return qtyordered;
	}
	public void setQtyordered(int qtyordered) {
		this.qtyordered = qtyordered;
	}
	public int getQtdassociada() {
		return qtdassociada;
	}
	public void setQtdassociada(int qtdassociada) {
		this.qtdassociada = qtdassociada;
	}
	public int getMaximo() {
		return maximo;
	}
	public void setMaximo(int maximo) {
		this.maximo = maximo;
	}
	public int getC_orderline_id() {
		return c_orderline_id;
	}
	public void setC_orderline_id(int c_orderline_id) {
		this.c_orderline_id = c_orderline_id;
	}
	
	
}
