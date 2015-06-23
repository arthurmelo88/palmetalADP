package org.compiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.util.DB;

public class DesalocaOS extends SvrProcess{

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		try{
			ArrayList<Integer> al = getPedidosDesativados();
			desalocaOS(al);

		}
		catch(Exception e){
			e.printStackTrace();
			log.log (Level.SEVERE, "StatusPagamento", e);
		}
		return "";
	}


	/**
	 * Retorna uma lista com os pedidos que foram desativados e que tinham produtos alocados em OS
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Integer> getPedidosDesativados() throws Exception{
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		String sql = "select distinct o.c_order_id from c_order o, X_Order_OS x where o.situacao='DE' and x.c_order_id=o.c_order_id";
		
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
	 * Esse método vai desalocar os produtos das OS
	 * 
	 * @param al
	 * @throws Exception
	 */
	private void desalocaOS(ArrayList<Integer> al) throws Exception{
		
		if(al.size()==0) return;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int qtde;
		int idOS;
		
		for(int x=0;x<al.size();x++){
			sql = "select p.pp_order_id, p.qtdassociada from X_Order_OS x, pp_order p where x.pp_order_id=p.pp_order_id and x.c_order_id=" + al.get(x);
			
			pstmt = DB.prepareStatement (sql, null);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				
				qtde=rs.getInt("qtdassociada");
				idOS=rs.getInt("pp_order_id"); 
				
				atualizaOS(idOS,qtde,al.get(x));
				
			}		

			DB.close(rs, pstmt);
			rs=null;
			pstmt=null;
		}
		
	}
	

	/**
	 * Executa o update na tabela das OS e o Delete na X_Order_OS
	 * 
	 * @param idOS
	 * @param qtde
	 * @param idPedido
	 * @throws Exception
	 */
	private void atualizaOS(int idOS, int qtde, int idPedido) throws Exception{

		qtde--;
		
		String sql = "update pp_order set qtdassociada=" + qtde + " where pp_order_id=" + idOS;
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());
		
		sql = "delete from X_Order_OS where pp_order_id=" + idOS + " and c_order_id=" + idPedido;
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());
		
	}

	
	
}
