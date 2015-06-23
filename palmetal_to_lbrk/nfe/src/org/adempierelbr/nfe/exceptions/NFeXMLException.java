package org.adempierelbr.nfe.exceptions;




public class NFeXMLException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String errorMsg;
	private String identificador;
	
	public NFeXMLException(){
		super();
	}
	
	public NFeXMLException(String identificador, String errorMsg){
		super();
		this.errorMsg=errorMsg;
		this.identificador=identificador;
		
	}
	
	public String getMessage(){
		String msg = identificador +" " + errorMsg;
		return msg;
	}

}
