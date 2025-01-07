package com.sap.portlet.jdbc;

import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCDataSourceAliasSettings;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCDataSourceAliasWrapper;
import javax.management.openmbean.CompositeData;

public class JDBCDataResourceAlias {
	private String name = "";
	private String dataSourceName="";
	private SAP_ITSAMJ2eeJDBCDataSourceAliasSettings settings;
	private String deployer;
	private boolean norm = false;
        private CompositeData compositeData;
	
	private boolean selected = false;
	
	public JDBCDataResourceAlias(CompositeData _cd){
                this.compositeData = _cd;
		this.settings = SAP_ITSAMJ2eeJDBCDataSourceAliasWrapper.getSAP_ITSAMJ2eeJDBCDataSourceAliasSettingsForCData(_cd);
                
	}
	
	
	
	public String getName(){
		return settings.getName();
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String getDataSourceName(){
		return settings.getDataSourceName();
	}
	
	public String getDeployer(){
		if (!norm){
			deployer = normalize(settings.getDeployer());
		}
		return deployer;
	}



	private String normalize(String _tmp) {
		String result = _tmp.replace('~', '/');
		this.norm = true;
		
		return result;
	}
	
        public SAP_ITSAMJ2eeJDBCDataSourceAliasSettings getSetings(){
            return settings;
        }
        
         public CompositeData getCompositeData(){
            return compositeData;
        }
	
}
