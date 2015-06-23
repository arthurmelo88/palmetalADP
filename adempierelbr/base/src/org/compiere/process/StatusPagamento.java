package org.compiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.util.DB;

public class StatusPagamento extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		try{
			ArrayList<Integer> al = getPedidosComRemessa();
			atualizaStatus(al);
		}
		catch(Exception e){
			e.printStackTrace();
			log.log (Level.SEVERE, "StatusPagamento", e);
		}
		return "";
	}

	/**
	 * Este método retorna uma lista com os c_order_id de todos os pedidos que 
	 * estão na situacao 'WR' (aguardando remessa) e tem uma remessa fechada com o seu id
	 * e também já tem fatura e todos os itens das linhas do pedido foram entregues
	 * 
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Integer> getPedidosComRemessa() throws Exception{
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		String sql = "select o.c_order_id from M_InOut m, C_Order o, c_invoice i where m.docstatus='CO' and o.situacao='WR' and m.c_order_id=o.c_order_id and m.c_doctype_id=2000011 and i.c_order_id=o.c_order_id and i.docstatus='CO' and o.situacao in ('WR','RT') and o.c_order_id not in (select distinct o.c_order_id from c_order o, c_orderline ol where o.c_order_id=ol.c_order_id and ol.qtyordered>ol.qtydelivered)";
		System.out.print(sql);
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
	 * Atualiza a situação para WP (aguardando pagamento)
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
		
		String sql = "update c_order set situacao='WP' where c_order_id in (" + sb.toString() + ")";
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());		
		
		for(int w=0;w<al.size();w++){
			registraStatus(al.get(w),"WP");	
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
