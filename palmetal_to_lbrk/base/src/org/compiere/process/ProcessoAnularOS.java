package org.compiere.process;

public class ProcessoAnularOS extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String doIt() throws Exception {
		String data_entrega ="";
		String idpcp_os ="";
		String idpcpDT ="";
		
		String sql2="select data_entrega from pcp_os_data_entrega where pcp_os_id="+ idpcp_os +" and pcp_os_data_entrega_id=" + idpcpDT;
		String sql3 = "Update pcp_os set data_entrega="+ sql2 +" where pcp_os_id="+ idpcp_os;

		String sql ="";
		String sql1 ="";
		sql = "Update pvp_os set data_entrega=" + data_entrega + " where pcp_os_id="+ idpcp_os;
		sql1 = "Update pvp_os_data_entrega set situacao='N' where pcp_os_data_entrega_id <> " + idpcpDT;
		// TODO Auto-generated method stub
		return "Hello Word";
	}

}
