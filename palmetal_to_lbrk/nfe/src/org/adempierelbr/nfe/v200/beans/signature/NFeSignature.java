package org.adempierelbr.nfe.v200.beans.signature;

public class NFeSignature {
	
	  private NFeSignedInfoBean signedInfo;

	  public void setSignedInfo(NFeSignedInfoBean signedInfo)
	  {
	    this.signedInfo = signedInfo;
	  }

	  public NFeSignedInfoBean getSignedInfo()
	  {
	    return signedInfo;
	  }
}
