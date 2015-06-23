package org.palmetal.model.callout;

import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import org.compiere.util.DB;
import org.compiere.util.Env;


public class CalloutCodigo extends CalloutEngine {

	/**
	 * Este método deve ser usado no callout de codigo da tabela m_product_category_id. Ele será 
	 * executado toda vez que o formulario (aba) de uma nova familia ou subfamilia for aberto, ou 
	 * seja, quando o usuário clicar no botão de criar nova familia ou subfamilia.
	 * Basicamente o que ele faz é preencher automaticamente o campo código e no caso de
	 * subfamilia também preenche o campo m_product_category_parent_id com o id do pai.
	 *  
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	public String getNextCodigo (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		Integer pai = (Integer)mTab.getParentTab().getValue("M_Product_Category_ID");
		Integer tipoCategoria = (Integer)mTab.getValue("x_tipocategoria_ID");
		
		System.out.println("Tipo Categoria: " + tipoCategoria);
		System.out.println("PAI: " + pai);
		
		if(pai==null) return processaFamilia(ctx, WindowNo, mTab, mField, value, tipoCategoria);
		else return processaSubFamilia(ctx, WindowNo, mTab, mField, value, pai);
			
	}		

	public String getNextCodigoProduto(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer subfamilia = (Integer)mTab.getValue("M_Product_Category_ID");
		
		return processaProduto(ctx, WindowNo, mTab, mField, value, subfamilia);
	}

	private String processaProduto(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Integer subfamilia){
		
		System.out.println("Criando novo Produto");
		
		int newid = 1; 
		int codf = subfamilia;

		String codigof="";

		String sql3 = "SELECT codigo from M_Product_Category WHERE codigo<>'' and M_Product_Category_ID=" + codf;
		PreparedStatement pstmt2 = DB.prepareStatement(sql3, null);
		try {
			ResultSet rs2 = pstmt2.executeQuery();
			if (rs2.next()) {
				codigof = rs2.getString("codigo");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sql2 = "select substring(value from 10 for 3) as cod from m_product where m_product_category_id=" + codf + " and ad_client_id=2000000 and value<>'' order by substring(value from 10 for 3) desc";
		PreparedStatement pstmt = DB.prepareStatement(sql2, null);
		ResultSet rs=null;
		try {
			rs = pstmt.executeQuery();
			if (rs.next()) {
				newid = rs.getInt("cod")+1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

		String newcod = "";
		if (newid<10)
			newcod = "00" + Integer.toString(newid);
		else if (newid>=10 && newid<100)
			newcod = "0" + Integer.toString(newid);
		else	
			newcod = Integer.toString(newid);

		Env.setContext(ctx, WindowNo, "value", codigof + "." + newcod);
		mTab.setValue("value", codigof + "." + newcod);

		sql2="select description from M_Product_Category where m_product_category_id=" + codf;
		pstmt = DB.prepareStatement(sql2, null);
		rs=null;
		
		String descricao="";
		
		try {
			rs = pstmt.executeQuery();
			if (rs.next()) {
				descricao=rs.getString("description");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Env.setContext(ctx, WindowNo, "description", descricao);
		mTab.setValue("description", descricao);
		
		DB.close(rs, pstmt);
		rs = null;
		pstmt = null;
		
		return "";

	}
	
	private String processaFamilia(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Integer tipoCategoria){
		System.out.println("Criando nova Familia");		
		
		String sql="select substring(codigo from 4 for 2) as cod from m_product_category where m_product_category_parent_id is null and ad_client_id=2000000 and codigo<>'' and x_tipocategoria_id=? order by substring(codigo from 4 for 2) desc";
		System.out.println("SQL: " + sql);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try	{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, tipoCategoria);
			rs = pstmt.executeQuery();
			
			
			if (rs.next()){
				int cod = rs.getInt("cod")+1;
				
     			String newcod;
				if (cod<10) newcod = "0" + Integer.toString(cod);
				else newcod = Integer.toString(cod);

				String idc = String.valueOf(tipoCategoria);
				int tam = idc.length();
				idc = idc.substring(tam-2, tam);
				newcod=idc + "." + newcod ;
				
				Env.setContext(ctx, WindowNo, "codigo", newcod);
				mTab.setValue("codigo", newcod);
			}

			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		catch (SQLException e){
			log.log(Level.SEVERE, sql, e);
			return e.getLocalizedMessage();
		}
		finally	{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return "";
		
		
		/*
		String sql="select * from m_product_category where codigo is not null and m_product_category_parent_id is null and x_tipocategoria_id=? and m_product_category_id<>3000060 order by codigo desc";
		
		System.out.println("SQL: " + sql);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try	{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, tipoCategoria);
			rs = pstmt.executeQuery();
			
			
			if (rs.next()){
				String cod = rs.getString("codigo");
				
				String[] array = cod.split("\\.");
				
				System.out.println("zero: " + array[0]);
				System.out.println("um: " + array[1]);
				
				int novo = Integer.parseInt(array[1]);
				novo++;
				
				String novoCodigo = array[0] + "." + novo;
				
				Env.setContext(ctx, WindowNo, "codigo", novoCodigo);
				mTab.setValue("codigo", novoCodigo);
			}

			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		catch (SQLException e){
			log.log(Level.SEVERE, sql, e);
			return e.getLocalizedMessage();
		}
		finally	{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return "";
		*/
	}
	
	private String processaSubFamilia(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Integer pai){
		System.out.println("Criando nova SubFamilia");
		
		int newid = 1; 
		int codf = pai;

		String codigof="";

		String sql3 = "SELECT codigo from M_Product_Category WHERE codigo<>'' and M_Product_Category_ID=" + codf;
		PreparedStatement pstmt2 = DB.prepareStatement(sql3, null);
		try {
			ResultSet rs2 = pstmt2.executeQuery();
			if (rs2.next()) {
				codigof = rs2.getString("codigo");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sql2 = "select substring(codigo from 7 for 2) as cod from m_product_category where m_product_category_parent_id=" + pai + " and ad_client_id=2000000 and codigo<>'' order by substring(codigo from 7 for 2) desc";
		PreparedStatement pstmt = DB.prepareStatement(sql2, null);
		ResultSet rs=null;
		try {
			rs = pstmt.executeQuery();
			if (rs.next()) {
				newid = rs.getInt("cod")+1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		DB.close(rs, pstmt);
		rs = null;
		pstmt = null;

		String newcod = "";
		if (newid<10)
			newcod = "0" + Integer.toString(newid);
		else
			newcod = Integer.toString(newid);

		Env.setContext(ctx, WindowNo, "codigo", codigof + "." + newcod);
		mTab.setValue("codigo", codigof + "." + newcod);
		
		return "";

		
		
		/*
		Env.setContext(ctx, WindowNo, "M_Product_Category_Parent_ID", pai);
		mTab.setValue("M_Product_Category_Parent_ID", pai);
		
		String sql="select * from m_product_category where codigo is not null and m_product_category_parent_id=? order by codigo desc";
		
		System.out.println("SQL: " + sql);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try	{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, pai);
			rs = pstmt.executeQuery();
			
			
			if (rs.next()){
				String cod = rs.getString("codigo");
				
				String[] array = cod.split("\\.");
				
				System.out.println("zero: " + array[0]);
				System.out.println("um: " + array[1]);
				System.out.println("dois: " + array[2]);
				
				int novo = Integer.parseInt(array[2]);
				novo++;
				
				String novoCodigo = array[0] + "." + array[1] + "." + novo;
				
				Env.setContext(ctx, WindowNo, "codigo", novoCodigo);
				mTab.setValue("codigo", novoCodigo);
			}
			else{
				String codigoPai = (String)mTab.getParentTab().getValue("codigo");
				
				Env.setContext(ctx, WindowNo, "codigo", codigoPai + ".1");
				mTab.setValue("codigo", codigoPai + ".1");
				
			}

			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		catch (SQLException e){
			log.log(Level.SEVERE, sql, e);
			return e.getLocalizedMessage();
		}
		finally	{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return "";
		
		*/
		
	}
	
}
