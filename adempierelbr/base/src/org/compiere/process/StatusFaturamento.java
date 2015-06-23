package org.compiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.util.DB;

public class StatusFaturamento extends SvrProcess{
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		try{
			ArrayList<Integer> pedidos = getPedidosEmFabricacao();
			
			for(int x=0;x<pedidos.size();x++){

				if(especiaisFabricados(pedidos.get(x))==true && normaisFabricados(pedidos.get(x))==true) {
					
					mudaSituacao(pedidos.get(x));
					
				}
				
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			log.log (Level.SEVERE, "StatusFaturamento", e);
		}
		return "";
	}


	
	/**
	 * Retorna uma lista com os ids dos pedidos que estão em fabricação
	 * 
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Integer> getPedidosEmFabricacao() throws Exception{
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		String sql = "select c_order_id from c_order where situacao='CL' order by c_order_id";
		
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
	 * Retorna verdadeiro se o pedido não tiver produtos especiais ou se tiver produtos especiais cujas OS já
	 * estejam finalizadas. Retorna falso caso contrario.
	 * 
	 * @param idPedido
	 * @return
	 * @throws Exception
	 */
	private boolean especiaisFabricados(int idPedido) throws Exception{
		boolean retorno=true;
		
		ArrayList<Integer> linhas = getLinhasEspecial(idPedido);
				
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		for(int x=0;x<linhas.size();x++){
			sql="Select * from pp_order where c_orderline_id=" + linhas.get(x);
			
			pstmt = DB.prepareStatement (sql, null);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				if(!rs.getString("docstatus").equalsIgnoreCase("CL")) retorno = false;
			}
			else{
				retorno=false;
			}
			
			DB.close(rs, pstmt);
			rs=null;
			pstmt=null;
		}
		

		
		return retorno;
	}
	
	/**
	 * Retorna uma lista com os ids das linhas do pedido, mas somente das linhas que tenham produto especial
	 * 
	 * @param idPedido
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Integer> getLinhasEspecial(int idPedido) throws Exception{
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		String sql =  "Select * from c_orderline ol where m_product_id in (3000082,3000057,3000168,3000169,3000170) and c_order_id=" + idPedido;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		while(rs.next()){
			
			al.add(rs.getInt("c_orderline_id"));
		}		
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		return al;
	}
	
	/**
	 * Retorna verdadeiro se o pedido não tiver produtos normais ou se tiver produtos normais cujas OS já
	 * estejam finalizadas. Retorna falso caso contrario.
	 * 
	 * @param idPedido
	 * @return
	 * @throws Exception
	 */
	private boolean normaisFabricados(int idPedido) throws Exception{
		boolean retorno=true;
		
		ArrayList<Integer> linhas = getAtendimentoOS(idPedido);
				
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		for(int x=0;x<linhas.size();x++){
			sql="Select * from pp_order where pp_order_id=" + linhas.get(x);
			
			pstmt = DB.prepareStatement (sql, null);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				if(!rs.getString("docstatus").equalsIgnoreCase("CL")) retorno = false;
			}
			else{
				retorno=false;
			}
			
			DB.close(rs, pstmt);
			rs=null;
			pstmt=null;
			
		}
		

		
		return retorno;
	}
	
	/**
	 * Retorna uma lista com os ids das OS que atendem a linhas do pedido
	 * 
	 * @param idPedido
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Integer> getAtendimentoOS(int idPedido) throws Exception{
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		String sql =  "Select * from X_Order_OS where c_order_id=" + idPedido;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		while(rs.next()){
			al.add(rs.getInt("pp_order_id"));
		}				
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		return al;
	}
	
	/**
	 * Muda a situação do pedido para RT (aguardando faturamento)
	 * 
	 * @param idPedido
	 * @throws Exception
	 */
	private void mudaSituacao(int idPedido) throws Exception{
		
		String sql = "update c_order set situacao='RT' where c_order_id="+idPedido;
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());
		
		registraStatus(idPedido,"RT");
		
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
