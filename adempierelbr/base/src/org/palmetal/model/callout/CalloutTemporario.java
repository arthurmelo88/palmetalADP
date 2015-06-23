package org.palmetal.model.callout;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class CalloutTemporario extends CalloutEngine {

	/**
	 * Se o usuário desmarcar o temporário vamos fazer várias validações, caso nem todas sejam satisfeitas
	 * o sistema irá emitir um aviso e voltar o valor compo temporario para verdadeiro.
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	public String validaTemporario (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		String mensagem="";
		
		if(mTab.getValue("Temporario")==null) return "";
		
		if(!(Boolean)mTab.getValue("Temporario")) {
			int idParceiro = (Integer)mTab.getValue("C_BPartner_ID");
			
			String sql="select p.lbr_bptypebr,p.emailNF, pl.phone as telefoneLocalizacao, pl.ddd as dddLocalizacao, pl.isbillto, pl.isshipto, pl.name as nomeLocalizacao,l.address1 as logradouro, l.address2 as numero, l.address3 as bairro, l.address4 as complemento, l.c_city_id as cidade, l.postal as cep, l.c_country_id as pais, l.c_region_id as estado from c_bpartner p, c_bpartner_location pl, c_location l where p.c_bpartner_id=pl.c_bpartner_id and pl.c_location_id=l.c_location_id and p.c_bpartner_id=?";
			
			System.out.println("SQL: " + sql);
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try	{
				
				boolean entrou=false;
				boolean deuErro=false;
				boolean isbillto=false;
				boolean isshipto=false;
				
				pstmt = DB.prepareStatement(sql, null);				
				pstmt.setInt(1, idParceiro);
				rs = pstmt.executeQuery();
								
				mensagem="Para mudar o temporário verifique os seguintes erros:\n";
				
				while (rs.next()){
					entrou=true;
					
					//Verificando o Logradouro
					if(rs.getString("logradouro")==null){
						deuErro=true;
						mensagem=mensagem+"O logradouro da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					else if(rs.getString("logradouro").equalsIgnoreCase("")){
						deuErro=true;
						mensagem=mensagem+"O logradouro da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}

					//Verificando o Numero
					if(rs.getString("numero")==null){
						deuErro=true;
						mensagem=mensagem+"O Número da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					else if(rs.getString("numero").equalsIgnoreCase("") || rs.getString("numero").equalsIgnoreCase("0")){
						deuErro=true;
						mensagem=mensagem+"O Número da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}

					//Verificando o Bairro
					if(rs.getString("bairro")==null){
						deuErro=true;
						mensagem=mensagem+"O Bairro da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					else if(rs.getString("bairro").equalsIgnoreCase("") ){
						deuErro=true;
						mensagem=mensagem+"O Bairro da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					
					//Verificando o Complemento
					if(rs.getString("complemento")==null){
						deuErro=true;
						mensagem=mensagem+"O Complemento da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					else if(rs.getString("complemento").equalsIgnoreCase("") ){
						deuErro=true;
						mensagem=mensagem+"O Complemento da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					
					//Verificando o Cidade
					if(rs.getString("cidade")==null){
						deuErro=true;
						mensagem=mensagem+"A Cidade da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					else if(rs.getString("cidade").equalsIgnoreCase("") || rs.getString("cidade").equalsIgnoreCase("0") ){
						deuErro=true;
						mensagem=mensagem+"A Cidade da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					
					//Verificando o CEP
					if(rs.getString("cep")==null){
						deuErro=true;
						mensagem=mensagem+"O CEP da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					else if(rs.getString("cep").equalsIgnoreCase("") || rs.getString("cep").equalsIgnoreCase("0") || rs.getString("cep").length()!=8 ){
						deuErro=true;
						mensagem=mensagem+"O CEP da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					
					//Verificando o Pais
					if(rs.getString("pais")==null){
						deuErro=true;
						mensagem=mensagem+"O Pais da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					else if(rs.getString("pais").equalsIgnoreCase("") || rs.getString("pais").equalsIgnoreCase("0") ){
						deuErro=true;
						mensagem=mensagem+"O Pais da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					
					//Verificando o Estado
					if(rs.getString("estado")==null){
						deuErro=true;
						mensagem=mensagem+"O Estado da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					else if(rs.getString("estado").equalsIgnoreCase("") || rs.getString("estado").equalsIgnoreCase("0") ){
						deuErro=true;
						mensagem=mensagem+"O Estado da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					
					//Verificando BillTo e ShipTo
					if(rs.getString("isbillto").equalsIgnoreCase("Y")) isbillto=true;
					if(rs.getString("isshipto").equalsIgnoreCase("Y")) isshipto=true;
					
					//Verificando o Telefone da Localizacao
					if(rs.getString("telefoneLocalizacao")==null){
						deuErro=true;
						mensagem=mensagem+"O Telefone da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					else if(rs.getString("telefoneLocalizacao").equalsIgnoreCase("") || rs.getString("telefoneLocalizacao").equalsIgnoreCase("0") || rs.getString("telefoneLocalizacao").length()<8 ){
						deuErro=true;
						mensagem=mensagem+"O Telefone da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					
					//Verificando o ddd da Localizacao
					if(rs.getString("dddLocalizacao")==null){
						deuErro=true;
						mensagem=mensagem+"O DDD da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					else if(rs.getString("dddLocalizacao").equalsIgnoreCase("") || rs.getString("dddLocalizacao").equalsIgnoreCase("0") || rs.getString("dddLocalizacao").length()<2 ){
						deuErro=true;
						mensagem=mensagem+"O DDD da Localização " + rs.getString("nomeLocalizacao") + " não foi preenchido\n";						
					}
					
					//verificando se é PJ e preencheu email da NF eletronica
					if(rs.getString("lbr_bptypebr")!=null){
						if(rs.getString("lbr_bptypebr").equalsIgnoreCase("PJ")){
							if(rs.getString("emailNF")==null){
								deuErro=true;
								mensagem=mensagem+"Parceiro PJ precisa informar Email para receber NF eletrônica\n";						

							}
							else{
								if(rs.getString("emailNF").equalsIgnoreCase("")){
									deuErro=true;
									mensagem=mensagem+"Parceiro PJ precisa informar Email para receber NF eletrônica\n";									
								}
							}
							
						}
						
					}
					
				}
				
				boolean entrou2=false;
				
				sql="select u.name as nomeContato, u.phone as telefoneContato, u.email, u.c_bpartner_location_id as localizacaoContato from c_bpartner p, ad_user u where p.c_bpartner_id=u.c_bpartner_id and p.c_bpartner_id=?";
				
				System.out.println("SQL: " + sql);

				pstmt = DB.prepareStatement(sql, null);				
				pstmt.setInt(1, idParceiro);
				rs = pstmt.executeQuery();

				while (rs.next()){
					entrou2=true;
					
					//Verificando o Telefone do Contato
					if(rs.getString("telefoneContato")==null){
						deuErro=true;
						mensagem=mensagem+"O Telefone do Contato " + rs.getString("nomeContato") + " não foi preenchido\n";						
					}
					else if(rs.getString("telefoneContato").equalsIgnoreCase("") || rs.getString("telefoneContato").equalsIgnoreCase("0") || rs.getString("telefoneContato").length()<8 ){
						deuErro=true;
						mensagem=mensagem+"O Telefone do Contato " + rs.getString("nomeContato") + " não foi preenchido\n";						
					}

					//Verificando o Email do Contato
					if(rs.getString("email")==null){
						deuErro=true;
						mensagem=mensagem+"O Email do Contato " + rs.getString("nomeContato") + " não foi preenchido\n";						
					}
					else if(rs.getString("email").equalsIgnoreCase("")){
						deuErro=true;
						mensagem=mensagem+"O Email do Contato " + rs.getString("nomeContato") + " não foi preenchido\n";						
					}

					//Verificando o Localizacao do Contato
					if(rs.getString("localizacaoContato")==null){
						deuErro=true;
						mensagem=mensagem+"A localização do Contato " + rs.getString("nomeContato") + " não foi preenchida\n";						
					}
					else if(rs.getString("localizacaoContato").equalsIgnoreCase("") || rs.getString("telefoneContato").equalsIgnoreCase("0") ){
						deuErro=true;
						mensagem=mensagem+"A localização do Contato " + rs.getString("nomeContato") + " não foi preenchida\n";						
					}
				}
				
				
				if(entrou && entrou2 && !deuErro) mensagem="";
				
				if(!entrou) {
					mensagem=mensagem+"Parceiro sem Localização, favor verificar\n";
					mTab.setValue ("Temporario",true);
				}
				
				if(!entrou2) {
					mensagem=mensagem+"Parceiro sem Contato, favor verificar\n";
					mTab.setValue ("Temporario",true);
				}
				
				if(entrou && isbillto==false) {
					 mensagem=mensagem+"Parceiro sem endereço de Faturamento\n";
					 deuErro=true;
				}
				
				if(entrou && isshipto==false) {
					 mensagem=mensagem+"Parceiro sem endereço de Entrega\n";
					 deuErro=true;
				}

				if((entrou || entrou2) && deuErro) mTab.setValue ("Temporario",true);
				
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
			
		}
			
		return mensagem;
	}	
}
