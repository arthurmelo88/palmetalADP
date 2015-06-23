package org.compiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.util.DB;

public class StatusFabricacao extends SvrProcess{

	ArrayList<StatusFabricacaoVO> listaOSUtilizadas = new ArrayList<StatusFabricacaoVO>();
	ArrayList<StatusFabricacaoVO> listaOSUtilizadasTemp = new ArrayList<StatusFabricacaoVO>();
	
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		try{
			verificaAtendimentoPeloEstoque(getPedidosPCP());
			
			mudaStatusTudoFab();
			
			//primeiro vou buscar as ordens com produtos especiais
			ArrayList<StatusFabricacaoVO> al = getOrdersSobEncomenda();
			if(al.size()>0) mudaStatusDesenho(al);
			
			//agora vou buscar as ordens com produtos comuns e que tiveram
			//suas OS criadas pelo MRP (ou seja, o vinculo sera pelo produto)
			ArrayList<StatusFabricacaoVO> al2 = getOrdersSemEstoque();
			if(al2.size()>0) mudaStatusSemEstoque(al2);

		}
		catch(Exception e){
			e.printStackTrace();
			log.log (Level.SEVERE, "StatusFabricacao", e);
		}
		return "";
	}

	private void verificaAtendimentoPeloEstoque(ArrayList<Integer> pedidos) throws Exception{
		
		ArrayList<Integer> produtos = new ArrayList<Integer>();
		ArrayList<ProdutoVO> listaProd = new ArrayList<ProdutoVO>();
		
		String ids = "";
		boolean inicio=true;
		for(int x=0; x<pedidos.size();x++){
			if(inicio){
				ids = String.valueOf(pedidos.get(x));
				inicio = false;
			}
			else{
				ids = ids + "," + String.valueOf(pedidos.get(x));
			}
			
		}
		
		String sql = "select distinct m_product_id from c_orderline where c_order_id in (" + ids + ") and m_product_id not in (3000082,3000057,3000168,3000169,3000170)";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		while(rs.next()){
			
			produtos.add(rs.getInt("m_product_id"));
		}		
		
		for(int y=0;y<produtos.size();y++){
			sql="select disponivel from vw_resumo_storage where m_product_id=" + produtos.get(y);
			
			pstmt = DB.prepareStatement (sql, null);
			rs = pstmt.executeQuery();
			int tot=0;
			if(rs.next()){				
				ProdutoVO vo = new ProdutoVO();
				
				vo.setM_product_id(produtos.get(y));
				
				if(rs.getInt("disponivel")<0) tot=0;
				else tot=rs.getInt("disponivel");		
				
				vo.setQuantidade(tot);
				
				listaProd.add(vo);
			}	
			else{
				ProdutoVO vo = new ProdutoVO();
				vo.setM_product_id(produtos.get(y));
				vo.setQuantidade(0);
				
			}
			
		}
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		String teste="";
		for(int w=0;w<listaProd.size();w++){
			teste=teste+"|"+listaProd.get(w).getM_product_id()+";"+listaProd.get(w).getQuantidade(); 
		}	
		
		//insereAUX(1,teste);
		
		listaProd = getQtdeNaoReservada(listaProd);
			
		setQtdeAtendidaReserva(listaProd);
		
		marcaEmFabricacao();
		
	}
	
	private void marcaEmFabricacao() throws Exception{
		String sql = "select ol.c_orderline_id from c_order o, c_orderline ol where o.c_order_id=ol.c_order_id and o.situacao in ('PC','PL') and ol.atendidopeloestoque=ol.qtyordered and fabricacao='N'";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		while(rs.next()){
			
			atualizaFabLinha(rs.getInt("c_orderline_id"));
		}	
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
	}
	
	
	private void atualizaFabLinha(int c_orderline_id) throws Exception{
		
		String sql = "update c_orderline set fabricacao='Y' where c_orderline_id="+c_orderline_id;
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());
		
	}
	
	private ArrayList<ProdutoVO> getQtdeNaoReservada(ArrayList<ProdutoVO> lista ) throws Exception{
				
		if(lista==null) return lista;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		for(int x=0;x<lista.size();x++){
			sql = "select sum(ol.qtyordered) as total from c_order o, c_orderline ol where ol.c_order_id=o.c_order_id and o.situacao in ('RT','WR','CL','AC') and ol.m_product_id=" + lista.get(x).getM_product_id();
		
			pstmt = DB.prepareStatement (sql, null);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				lista.get(x).setQuantidade(lista.get(x).getQuantidade()-rs.getInt("total"));
			}
		}
		
		if(pstmt!=null){
			DB.close(rs, pstmt);
			rs=null;
			pstmt=null;
		}
		
		String teste="";
		for(int w=0;w<lista.size();w++){
			teste=teste+"|"+lista.get(w).getM_product_id()+";"+lista.get(w).getQuantidade(); 
		}	
		
		//insereAUX(2,teste);
		
		return lista;
	}
	
	private void setQtdeAtendidaReserva(ArrayList<ProdutoVO> lista) throws Exception{
		//insereAUX(3,"Entrou");
		if(lista==null) return;
				
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		for(int x=0; x<lista.size();x++){
			
			if(lista.get(x).getQuantidade()>0){				
				
				sql = "select ol.c_orderline_id, ol.qtyordered from c_orderline ol, c_order o where o.c_order_id=ol.c_order_id and o.situacao in ('PC','PL') and m_product_id=" + lista.get(x).getM_product_id() + " order by o.created";
				
				System.out.println(sql);
				
				pstmt = DB.prepareStatement (sql, null);
				rs = pstmt.executeQuery();
				
				boolean esgotou=false;
				int tot = lista.get(x).getQuantidade();
				
				while(rs.next() && !esgotou){
					int dif=0;
					int ordered = rs.getInt("qtyordered");
					
					if(ordered<=tot){
						dif=ordered;
						tot=tot-ordered;						
					}
					else{
						dif=tot;
						tot=0;
					}
					
					//insereAUX(5,rs.getInt("c_orderline_id")+"|" + dif + "|" + tot + "|" + ordered);
					atualizaAttLinha(dif,rs.getInt("c_orderline_id"));
					
					if(tot==0) esgotou=true;
				}			
			}
		}
		
		if(pstmt!=null){
			DB.close(rs, pstmt);
			rs=null;
			pstmt=null;
		}
		
	}
	
	private void atualizaAttLinha(int tot, int c_orderline_id) throws Exception{
		
		String sql = "update c_orderline set atendidopeloestoque=" + tot + " where c_orderline_id="+c_orderline_id;
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());
		
	}
	
	
	private void insereAUX(int id, String value) throws Exception{
		
		String sql = "insert into aux (id,value) values (" + id + ",'" + value + "')";
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());
		
	}
	
	/**
	 * Este método busca todos os pedidos que tem produtos que são feitos sob encomenda (produtos
	 * especiais) e que já tiveram o desenho aprovado pela equipe de design. Vai retornar um
	 * ArrayList com os c_order_id dos pedidos de venda.
	 * 
	 * @return
	 * @throws Exception
	 */
	private ArrayList<StatusFabricacaoVO> getOrdersSobEncomenda() throws Exception{
		ArrayList<StatusFabricacaoVO> al = new ArrayList<StatusFabricacaoVO>();
		
		//Os produtos especiais tem a OS criada automaticamente quando o pedido de venda é fechado
		//e neste caso a OS ganha o id da linha do pedido a qual é referente
		String sql = "Select o.c_order_id,ol.c_orderline_id from c_order o, c_orderline ol, pp_order pp where o.c_order_id=ol.c_order_id and ol.c_orderline_id=pp.c_orderline_id and o.situacao='ED' and pp.isdesignapproved='Y' order by o.documentno";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		while(rs.next()){
			StatusFabricacaoVO vo = new StatusFabricacaoVO();
			
			vo.setC_order_id(rs.getInt("c_order_id"));
			vo.setC_orderline_id(rs.getInt("c_orderline_id"));
			
			al.add(vo);
		}		
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		return al;
	}
	
	/**
	 * Muda a situacao das ordens cujo o id foi passado como parametro no ArrayList.
	 * A nova situacao será CL (Em Fabricação)
	 * 
	 * @param al
	 * @throws Exception
	 */
	private void mudaStatusDesenho(ArrayList<StatusFabricacaoVO> al) throws Exception{
		
		if(al.size()<=0) return;
		
		//montando a lista de ids que será usada na query
		int idPedido, idLinha, total, totalFab;
		
		for(int x=0;x<al.size();x++){
			idPedido = al.get(x).getC_order_id();
			idLinha = al.get(x).getC_orderline_id();
			
			atualizaLinha(idLinha);
			
			total = qtdLinhas(idPedido);
			totalFab = qtdLinhasComOS(idPedido);
			
			if(total==totalFab){
				String sql = "update c_order set situacao='CL' where c_order_id=" + idPedido;
				
				DB.executeUpdate(sql, get_TrxName());
				
				DB.commit(true, get_TrxName());
				
				registraStatus(idPedido,"CL");
			}
			else{
				//preciso verificar se tem mais alguma linha com produto especial sem OS
				//se tiver mantenho o estado em Desenho
				//se as linhas sem OS não forem de produto especial, então vou mudar o pedido
				//para parcialmente em fabricação PL
				if(outrosProdEspSemOS(idPedido)==false) {
					String sql = "update c_order set situacao='PL' where c_order_id=" + idPedido;					
					
					DB.executeUpdate(sql, get_TrxName());
					
					DB.commit(true, get_TrxName());
					
					registraStatus(idPedido,"PL");
					
				}				
			}
				
		}
		
	}
	
	/**
	 * Este método retorna uma lista com os pedidos em PCP relacionados com suas possiveis OS. Por enquanto 
	 * a precedencia é pela idade do pedido de vendas, ou seja, os mais velhos são atendidos primeiro.
	 * 
	 * @return
	 * @throws Exception
	 */
	private ArrayList<StatusFabricacaoVO> getOrdersSemEstoque() throws Exception{
		ArrayList<StatusFabricacaoVO> al = new ArrayList<StatusFabricacaoVO>();
		
		//String sql = "Select o.c_order_id,ol.c_orderline_id,pp.pp_order_id,ol.qtyordered,pp.qtdassociada,pp.qtyordered as maximo from c_order o, c_orderline ol, pp_order pp where o.c_order_id=ol.c_order_id and ol.m_product_id=pp.m_product_id and pp.created>o.created and o.situacao in ('PC','PL') and ol.fabricacao='N' and pp.qtyordered>pp.qtdassociada and ol.m_product_id<>3000082 order by o.prioridade_fabricacao,o.documentno";
		String sql = "Select o.c_order_id,ol.c_orderline_id,ol.atendidopeloestoque,pp.pp_order_id,ol.qtyordered,pp.qtdassociada,pp.qtyordered as maximo from c_order o, c_orderline ol, pp_order pp where o.c_order_id=ol.c_order_id and ol.m_product_id=pp.m_product_id and o.situacao in ('PC','PL') and ol.fabricacao='N' and pp.qtyordered>pp.qtdassociada and ol.m_product_id not in (3000082,3000057,3000168,3000169,3000170) and pp.docstatus='IP' and pp.isactive='Y' order by o.prioridade_fabricacao,o.documentno";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();

		StatusFabricacaoVO vo;
		
		while(rs.next()){
			vo = new StatusFabricacaoVO();
			vo.setC_order_id(rs.getInt("c_order_id"));
			vo.setC_orderline_id(rs.getInt("c_orderline_id"));
			vo.setMaximo(rs.getInt("maximo"));
			vo.setPp_order_id(rs.getInt("pp_order_id"));
			vo.setQtdassociada(rs.getInt("qtdassociada"));
			vo.setQtyordered(rs.getInt("qtyordered")-rs.getInt("atendidopeloestoque"));
			
			al.add(vo);
		}		
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		return al;
	}
	
	/**
	 * Este método elenca os pedidos e suas OS e realiza a mudança da situação no pedido e as qtdes associadas por OS. Por enquanto 
	 * a precedencia é pela idade do pedido de vendas, ou seja, os mais velhos são atendidos primeiro. Este método
	 * atende a 4 cenários primários:
	 * 1. Um pedido tem uma quantidade de produtos que é totalmente atendido por uma única OS -> muda para em Fabricação
	 * 2. Um pedido tem uma quantidade de produtos que é totalmente atendido por várias OS -> muda para em Fabricação
	 * 3. Um pedido tem uma quantidade de produtos que é parcialmente atendido por uma ou várias OS -> não muda situação
	 * 4. Um pedido não é atendido por OS alguma pois outros pedidos de maior precedencia já associaram as OS -> não muda situação
	 *  
	 * @param al
	 * @throws Exception
	 */
	private void mudaStatusSemEstoque(ArrayList<StatusFabricacaoVO> al) throws Exception{
		ArrayList<StatusFabricacaoVO> listaMudarStatus = new ArrayList<StatusFabricacaoVO>();	
		ArrayList<StatusFabricacaoVO> listaOSBloqueadas = new ArrayList<StatusFabricacaoVO>();
		
		StatusFabricacaoVO vo;
		
		int disponibilidade = 0; //a quantidade de produtos que pode ser atendido pela OS atual
		int pedidoPendente = 0; //o pedido que não pode ser atendido por uma única OS
		int associarPendente = 0; //quantidade do produto que falta ser associado
		
		
		for(int x=0;x<al.size();x++){
			vo = al.get(x);			
			
			if(estaNaLista(listaMudarStatus,vo.getC_order_id(),vo.getC_orderline_id())==false && disponibilidadeOS(vo.getPp_order_id())!=0 && estaNaListaBloqueada(listaOSBloqueadas,vo.getPp_order_id())==false && estaNaListaTemporaria(vo.getPp_order_id())==false){
				
				//se entrar nesse if é porque o pedido anterior não foi atendido completamente pelas OS
				if(pedidoPendente!=vo.getC_order_id()){
					pedidoPendente=0;
					associarPendente = 0;
					
					//marca as OS como bloqueadas
					listaOSBloqueadas = atualizaListaBloqueados(listaOSBloqueadas,listaOSUtilizadasTemp);
					
					//limpa a lista de OS temporaria
					listaOSUtilizadasTemp = new ArrayList<StatusFabricacaoVO>();
				}
				else{
					vo.setQtyordered(associarPendente);
					
				}
				
				if(disponibilidadeOS(vo.getPp_order_id())>0) disponibilidade = disponibilidadeOS(vo.getPp_order_id());
				else disponibilidade = vo.getMaximo() - vo.getQtdassociada();
				
				//neste caso o pedido pode ser atendida completamente por esta OS
				if(disponibilidade>=vo.getQtyordered()){
					
					if(pedidoPendente!=0){
						//colocar o id do pedido na lista
						StatusFabricacaoVO vo1 = new StatusFabricacaoVO();
						vo1.setC_order_id(vo.getC_order_id());
						vo1.setC_orderline_id(vo.getC_orderline_id());						
						listaMudarStatus.add(vo1);
						
						//vai passar para a listaOSUtilizadas todos os valores e entradas de listaOSUtilizadasTemp
						sincronizaListaOS();
						
						//limpa a lista de OS temporaria
						listaOSUtilizadasTemp = new ArrayList<StatusFabricacaoVO>();
					
						//limpa as variáveis
						pedidoPendente=0;
						associarPendente = 0;
						
					}
					else{
						//colocar o id do pedido na lista						
						StatusFabricacaoVO vo1 = new StatusFabricacaoVO();
						vo1.setC_order_id(vo.getC_order_id());
						vo1.setC_orderline_id(vo.getC_orderline_id());						
						listaMudarStatus.add(vo1);
						
						//atualizar lista de OS
						atualizaListaOS(vo);
					}					
				}
				else{
					if(pedidoPendente!=0){

						//quantidade de produtos que faltam para que o pedido seja totalmente atendido
						associarPendente = associarPendente - atualizaListaOSPendente(vo);
					}
					else{
						//marcar o pedido como pendente de novas OS para ser atendido
						pedidoPendente = vo.getC_order_id();
						
						associarPendente = vo.getQtyordered();
						
						//quantidade de produtos que faltam para que o pedido seja totalmente atendido
						associarPendente = associarPendente - atualizaListaOSPendente(vo);						
						
					}
					
				}
				
			}			
			
		}
		
		//mudar a situacao para Em Fabricação dos pedidos
		mudaStatusFabricacao(listaMudarStatus);
		
		//atualizar nas OS as quantidades associadas
		mudaAssociadas(listaOSUtilizadas);
		
	}
	
	/**
	 *  Verifica se o pedido já foi atendido e está na lista de atendidos
	 * 
	 * @param al
	 * @param idPedido
	 * @param idLinha
	 * @return
	 */
	private boolean estaNaLista(ArrayList<StatusFabricacaoVO> al, int idPedido, int idLinha){
		boolean retorno = false;
		
		for(int x=0;x<al.size();x++){
			StatusFabricacaoVO vo = al.get(x);
			if(vo.getC_order_id()==idPedido && vo.getC_orderline_id()==idLinha) retorno = true;
		}
		
		return retorno;
	}
	
	/**
	 * Verifica se a OS está na lista de OS bloqueadas. Esta lista marca as OS que não podem atender a outros 
	 * pedidos pois já apareceu um pedido mais prioritário que não foi atendido completamente pelas OS existentes
	 * 
	 * @param al
	 * @param idOS
	 * @return
	 */
	private boolean estaNaListaBloqueada(ArrayList<StatusFabricacaoVO> al, int idOS){
		boolean retorno = false;
		
		for(int x=0;x<al.size();x++){
			StatusFabricacaoVO vo = al.get(x);
			if(vo.getPp_order_id()==idOS) retorno = true;
		}
		
		return retorno;
	}
	
	/**
	 * Retorna verdadeiro se a OS estiver na lista temporaria
	 * 
	 * @param idOS
	 * @return
	 */
	private boolean estaNaListaTemporaria(int idOS){
		boolean retorno = false;
		
		for(int x=0;x<listaOSUtilizadasTemp.size();x++){
			StatusFabricacaoVO vo = listaOSUtilizadasTemp.get(x);
			if(vo.getPp_order_id()==idOS) retorno = true;
		}
		
		return retorno;
	}
	
	/**
	 * Verifica a quantidade de produtos ainda disponiveis nesta OS
	 * 
	 * @param id
	 * @return
	 */
	private int disponibilidadeOS(int id){
		//se retornar -1 é porque a OS não foi incluida na lista
		int retorno = -1;
		
		for(int x=0;x<listaOSUtilizadas.size();x++){
			if(listaOSUtilizadas.get(x).getPp_order_id() == id) {
				retorno = listaOSUtilizadas.get(x).getMaximo()-listaOSUtilizadas.get(x).getQtdassociada();
			}
		}
		
		return retorno;
	}
	
	/**
	 * Atualiza a lista de OS que estão atendendo aos pedidos
	 * 
	 * @param vo
	 * @throws Exception
	 */
	private void atualizaListaOS(StatusFabricacaoVO vo) throws Exception{
		boolean encontrou = false;
		
		for(int x=0;x<listaOSUtilizadas.size();x++){
			//se encontrei na lista vou atualizar a qtdassociada
			if(listaOSUtilizadas.get(x).getPp_order_id() == vo.getPp_order_id()) {
				encontrou=true;
				listaOSUtilizadas.get(x).setQtdassociada(listaOSUtilizadas.get(x).getQtdassociada()+vo.getQtyordered());
			}
		}
		
		//se ainda não estava na lista vou adicionar
		if(!encontrou){
			StatusFabricacaoVO aux = new StatusFabricacaoVO();
			
			aux.setPp_order_id(vo.getPp_order_id());
			aux.setMaximo(vo.getMaximo());
			aux.setQtdassociada(vo.getQtyordered());
			aux.setC_order_id(vo.getC_order_id());
			
			listaOSUtilizadas.add(aux);
			
		}
		
	}
	
	/**
	 * Atualiza a lista de OS temporaria que guarda as OS que estão atendendo parcialmente ao pedido
	 * 
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	private int atualizaListaOSPendente(StatusFabricacaoVO vo) throws Exception{

		int associar = 0;
		int disp = disponibilidadeOS(vo.getPp_order_id());
		
		if(disp < 0) associar = vo.getMaximo();
		else associar = vo.getMaximo() - disp;
		
		StatusFabricacaoVO aux = new StatusFabricacaoVO();
		
		aux.setPp_order_id(vo.getPp_order_id());
		aux.setMaximo(vo.getMaximo());
		aux.setQtdassociada(associar);
		aux.setC_order_id(vo.getC_order_id());
		
		listaOSUtilizadasTemp.add(aux);
		
		return associar;
		
	}
	
	/**
	 * Se um pedido passa de atendido parcialmente para completamente atendido então sincronizo a 
	 * lista principal com as informações da lista temporária
	 */
	private void sincronizaListaOS(){
		StatusFabricacaoVO vo = null;
		StatusFabricacaoVO voTemp = null;
		boolean achou = false;
		
		ArrayList<StatusFabricacaoVO> al = new ArrayList<StatusFabricacaoVO>();
		
		for(int x=0;x<listaOSUtilizadasTemp.size();x++){
			voTemp = listaOSUtilizadasTemp.get(x);
			
			for(int y=0;y<listaOSUtilizadas.size();y++){
				vo = listaOSUtilizadas.get(y);
				
				if(vo.getPp_order_id() == voTemp.getPp_order_id()){
					achou=true;
					//atualizando o valor na lista principal
					listaOSUtilizadas.get(y).setQtdassociada(listaOSUtilizadas.get(y).getQtdassociada() + listaOSUtilizadasTemp.get(x).getQtdassociada());
				}
			}
			
			if(!achou){
				al.add(voTemp);
			}
			else{
				achou = false;
			}
		}
		
		//vou carregar na lista principal as OS que só estavam na lista temporaria
		for(int w=0;w<al.size();w++){
			listaOSUtilizadas.add(al.get(w));
		}
	}

	/**
	 * Atualiza no banco de dados as quantidades de produtos já associados com as OS
	 * @param al
	 * @throws Exception
	 */
	private void mudaAssociadas(ArrayList<StatusFabricacaoVO> al) throws Exception{
		
		if(al.size()<=0) return;
		
		String sql = "";
		
		for(int x=0;x<al.size();x++){
			sql = "update pp_order set qtdassociada=" + al.get(x).getQtdassociada() + ", description='Pedido: " + numPedido(al.get(x).getC_order_id()) + " " + getDescricao(al.get(x).getPp_order_id()) +  "' where pp_order_id =" + al.get(x).getPp_order_id();
			
			DB.executeUpdate(sql, get_TrxName());
			
			DB.commit(true, get_TrxName());

		}
		
		for(int x=0;x<al.size();x++){
			sql = "insert into X_Order_OS (C_Order_ID,PP_Order_ID) values (" + al.get(x).getC_order_id() + "," + al.get(x).getPp_order_id() + ")";
			
			DB.executeUpdate(sql, get_TrxName());
			
			DB.commit(true, get_TrxName());

		}
		
		
	}
	
	/**
	 * 
	 * Retorna o valor do campo Description da OS com o pp_order_id informado
	 * 
	 * @param pp_order_id
	 * @return
	 * @throws Exception
	 */
	private String getDescricao(int pp_order_id) throws Exception{
		String retorno = "";
		
		//Os produtos especiais tem a OS criada automaticamente quando o pedido de venda é fechado
		//e neste caso a OS ganha o id da linha do pedido a qual é referente
		String sql = "Select description from pp_order where pp_order_id=" + pp_order_id;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		if(rs.next()){
			retorno = rs.getString("description");
		}		
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		return retorno;		
		
	}
	
	
	/**
	 * Retorna a quantidade de linha de um pedido
	 * 
	 * @param idPedido
	 * @return
	 * @throws Exception
	 */
	private int qtdLinhas(int idPedido) throws Exception{
		int retorno = 0;
		
		//Os produtos especiais tem a OS criada automaticamente quando o pedido de venda é fechado
		//e neste caso a OS ganha o id da linha do pedido a qual é referente
		String sql = "Select count(*) as total from c_orderline where c_order_id=" + idPedido;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		if(rs.next()){
			retorno = rs.getInt("total");
		}		
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		return retorno;
	}
	
	/**
	 * Retorna a quantidade de linhas do pedido que já tenham OS
	 * 
	 * @param idPedido
	 * @return
	 * @throws Exception
	 */
	private int qtdLinhasComOS(int idPedido) throws Exception{
		int retorno = 0;
		
		//Os produtos especiais tem a OS criada automaticamente quando o pedido de venda é fechado
		//e neste caso a OS ganha o id da linha do pedido a qual é referente
		String sql = "Select count(*) as total from c_orderline where fabricacao='Y' and c_order_id=" + idPedido;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		if(rs.next()){
			retorno = rs.getInt("total");
		}		
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		return retorno;
	}
	
	
	/**
	 * Atualiza a flag fabricacao da linha do pedido para o valor Y
	 * 
	 * @param idLinha
	 * @throws Exception
	 */
	private void atualizaLinha(int idLinha) throws Exception{
	
		String sql = "update c_orderline set fabricacao='Y' where c_orderline_id="+idLinha;
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());		
		
	}
	
	/**
	 * Retorna verdadeiro se no pedido houver alguma linha com produto especial sem OS
	 * e falso se contrário
	 * 
	 * @param idPedido
	 * @return
	 * @throws Exception
	 */
	private boolean outrosProdEspSemOS(int idPedido) throws Exception{
		boolean retorno = false;
		
		//Os produtos especiais tem a OS criada automaticamente quando o pedido de venda é fechado
		//e neste caso a OS ganha o id da linha do pedido a qual é referente
		String sql = "Select * from c_orderline where fabricacao='N' and m_product_id in (3000082,3000057,3000168,3000169,3000170) and c_order_id=" + idPedido;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		if(rs.next()){
			retorno=true;
		}		
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		return retorno;
	}
	
	/**
	 * Muda a situacao das ordens cujo o id foi passado como parametro no ArrayList.
	 * A nova situacao será CL (Em Fabricação) ou PL (Parcialmente em Fabricação)
	 * 
	 * @param al
	 * @throws Exception
	 */
	private void mudaStatusFabricacao(ArrayList<StatusFabricacaoVO> al) throws Exception{
		
		if(al.size()<=0) return;
		
		//montando a lista de ids que será usada na query
		int idPedido, idLinha, total, totalFab;
		
		for(int x=0;x<al.size();x++){
			idPedido = al.get(x).getC_order_id();
			idLinha = al.get(x).getC_orderline_id();
			
			atualizaLinha(idLinha);
			
			total = qtdLinhas(idPedido);
			totalFab = qtdLinhasComOS(idPedido);
			
			if(total==totalFab){
				//Em Fabricação
				String sql = "update c_order set situacao='CL' where c_order_id=" + idPedido;
				
				DB.executeUpdate(sql, get_TrxName());
				
				DB.commit(true, get_TrxName());
				
				registraStatus(idPedido,"CL");
			}
			else{
				//Parcialmente Em Fabricação
				String sql = "update c_order set situacao='PL' where c_order_id=" + idPedido;				
				
				DB.executeUpdate(sql, get_TrxName());
				
				DB.commit(true, get_TrxName());
				
				registraStatus(idPedido,"PL");
			
			}
				
		}
		
	}
	
	/**
	 * Este método passa para a lista de bloqueados as OS da lista de temporarios
	 * 
	 * @param listaBloqueados
	 * @param listaOSTemporaria
	 * @return
	 * @throws Exception
	 */
	private ArrayList<StatusFabricacaoVO> atualizaListaBloqueados(ArrayList<StatusFabricacaoVO> listaBloqueados, ArrayList<StatusFabricacaoVO> listaOSTemporaria) throws Exception{
		
		for(int x=0;x<listaOSTemporaria.size();x++){
			listaBloqueados.add(listaOSTemporaria.get(x));
		}
		
		return listaBloqueados;
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
	
	/**
	 * Dado o id do pedido retorna o numero do documento.
	 * @param idPedido
	 * @return
	 * @throws Exception
	 */
	private String numPedido(int idPedido) throws Exception{
		String retorno = "";
		
		//Os produtos especiais tem a OS criada automaticamente quando o pedido de venda é fechado
		//e neste caso a OS ganha o id da linha do pedido a qual é referente
		String sql = "Select * from c_order where c_order_id=" + idPedido;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();
		
		if(rs.next()){
			retorno=rs.getString("documentno");
		}
		else{
			retorno=String.valueOf(idPedido);
		}
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
		return retorno;
	}
	
	private ArrayList<Integer> getPedidosPCP() throws Exception{
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		String sql = "select c_order_id from c_order where situacao in ('PC','PL') order by created";
		
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

	
	private void mudaStatusTudoFab() throws Exception{
		
		String sql = "select o.c_order_id,ol.fabricacao from c_orderline ol, c_order o where o.c_order_id=ol.c_order_id and o.situacao in ('PC','PL')";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		pstmt = DB.prepareStatement (sql, null);
		rs = pstmt.executeQuery();

		String fab="Y";
		int c_order_id=0;
		
		while(rs.next()){
			
			if(c_order_id==0){
				if(rs.getString("fabricacao").equalsIgnoreCase("N")) fab="N";
				c_order_id=rs.getInt("c_order_id");
			}
			else{
				if(c_order_id!=rs.getInt("c_order_id")){
					
					if(fab.equalsIgnoreCase("Y")) atualizaPedido(c_order_id);
					
					fab="Y";
				}
				if(rs.getString("fabricacao").equalsIgnoreCase("N")) fab="N";
				
				c_order_id=rs.getInt("c_order_id");
			}
			
		}		
		
		if(fab.equalsIgnoreCase("Y")) atualizaPedido(c_order_id);
		
		DB.close(rs, pstmt);
		rs=null;
		pstmt=null;
		
	}
	
	private void atualizaPedido(int c_order_id) throws Exception{
		registraStatus(c_order_id,"CL");
		
		String sql = "update c_order set situacao='CL' where c_order_id="+c_order_id;
		
		DB.executeUpdate(sql, get_TrxName());
	
		DB.commit(true, get_TrxName());
		
	}
	
}
