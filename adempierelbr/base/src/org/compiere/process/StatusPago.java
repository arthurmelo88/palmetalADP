package org.compiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.util.DB;

public class StatusPago extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		try{
			ArrayList<Integer> al = getPedidosAguardandoPagamento();
			al = getPedidosPagos(al);
			atualizaStatus(al);
		}
		catch(Exception e){
			e.printStackTrace();
			log.log (Level.SEVERE, "StatusPagamento", e);
		}
		return "";
	}


	/**
	 * Monta uma lista com c_order_id de todos os pedidos com situacao WP (aguardando pagamento)
	 * 
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Integer> getPedidosAguardandoPagamento() throws Exception{
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		String sql = "Select * from c_order where situacao='WP'";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		while(rs.next()){
			
			al.add(rs.getInt("c_order_id"));
		}		
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		return al;
	}
	
	/**
	 * A partir da lista de pedidos aguardando pagamento vai listar aqueles que tem pagamentos
	 * associados no valor total da fatura
	 * 
	 * @param al
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Integer> getPedidosPagos(ArrayList<Integer> al) throws Exception{
		ArrayList<Integer> retorno = new ArrayList<Integer>();
		
		if(al.size()==0) return retorno;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		double valorNF, valorPagamentos;

		for(int x=0;x<al.size();x++){
			sql = "Select i.grandtotal, a.amount from c_allocationline a, c_invoice i where a.c_invoice_id=i.c_invoice_id and a.c_order_id=" + al.get(x);
			
			pstmt = DB.prepareStatement (sql, null);
			rs = pstmt.executeQuery();
			
			valorNF=0;
			valorPagamentos=0;
			
			while(rs.next()){
				valorNF = rs.getDouble("grandtotal");
				valorPagamentos = valorPagamentos + rs.getDouble("amount");
			}		

			if(valorNF!=0 && (valorNF==valorPagamentos)) retorno.add(al.get(x));
			
			DB.close(rs, pstmt);
			rs=null;
			pstmt=null;
		}

		return retorno;		
		
	}
	
	/**
	 * Muda a situação do pedido para VO (Pago) 
	 * 
	 * @param al
	 * @throws Exception
	 */
	private void atualizaStatus(ArrayList<Integer> al) throws Exception{

		if(al.size()==0) return;
		
		StringBuffer sb = new StringBuffer();
		
		for(int x=0;x<al.size();x++){
			if(x==0) sb.append(al.get(x));
			else sb.append("," + al.get(x));
		}
		
		String sql = "update c_order set situacao='VO' where c_order_id in (" + sb.toString() + ")";
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());
		
		for(int w=0;w<al.size();w++){
			registraStatus(al.get(w),"VO");	
		}
		
	}
	
	/**
	 * Registra o historico de mudança de situação do pedido. Vai usar como empresa a Palmetal e 
	 * usuário o System.
	 * 
	 * @param idPedido
	 * @param status
	 * @throws Exception
	 */
	private void registraStatus(int idPedido, String status) throws Exception{
		String idCliente="2000000"; //Palmetal
		String idUsuario="100"; //System
		String idOrg="2000000";	//Palmetal	
		
		String sql = "Insert into x_statuspedido (isactive,createdby,updatedby,ad_client_id,ad_org_id,c_order_id,status) values ('Y'," + idUsuario + "," + idCliente + ","  + idCliente + "," + idOrg + "," + idPedido + ",'" + status + "')";				
		
		DB.executeUpdate(sql, get_TrxName());
		
		DB.commit(true, get_TrxName());
	
	}


}
