package com.sap.i18n.itsam.compositedata;

public class SAP_ITSAMSAPCountryFormat {

	private String m_Land=null;
	
	private String m_XDEZP=null;
	
	private String m_DATFM=null;
	
	private String m_TIMEFM=null;
		
	public SAP_ITSAMSAPCountryFormat(){

	}

	public SAP_ITSAMSAPCountryFormat(String land,String xdezp, String datfm, String timefm){

	this.m_Land = land;
		
	this.m_XDEZP = xdezp;
		
	this.m_DATFM = datfm;
	
	this.m_TIMEFM = timefm;
		
	}

	/*
	Description Missing
	@return String
	*/
	public String getCountry() 
	{
	return this.m_Land;
	}

	/*
	Description Missing
	@return String
	*/
	public String getDecimalNotation() 
	{
	return this.m_XDEZP;
	}

	/*
	Description Missing
	@return String
	*/
	public String getDateFormat() 
	{
	return this.m_DATFM;
	}
	
	/*
	Description Missing
	@return String
	*/
	public String getTimeFormat() 
	{
	return this.m_TIMEFM;
	}

}
