package com.sap.portlet.jdbc;

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.event.ActionEvent;
import static com.sap.portlet.jdbc.LogContext.log;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;


public class JDBCResourceManagedBean{
	private String mode;
	private String selectedResourceName;
	private JDBCDataResource[] jdbc_resources;
	private JDBCDataResourceAlias[] jdbc_resource_aliases;
	private JDBCDataResource selectedJDBCDataSource;
        private String updatedDataSourceAlias;
        private String redirectToDataSource;
        private SelectItem[] dataSourceItemList;
	
	
	private JMXBeansAdapter beans = new JMXBeansAdapter();
	
	
	
	
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	

	public String getSelectedResourceName() {
		//log.debugT("JDBCResourceManagedBean getSelectedResourceName:"+selectedResourceName);
		return selectedResourceName;
	}

	public void setSelectedResourceName(String selectedResourceName) {
		log.debugT("JDBCResourceManagedBean setSelectedResourceName:"+selectedResourceName);
		this.selectedResourceName = selectedResourceName;
	}

	public JDBCDataResource[] getJdbc_resources() {
		if (jdbc_resources == null){
			jdbc_resources = beans.getAllJDBCResources();
		}
		if (jdbc_resources != null){
			log.debugT("JDBCResourceManagedBean getJdbc_resources:"+jdbc_resources.length);
		}	
		return jdbc_resources;
	}
	
	public JDBCDataResourceAlias[] getJdbc_resource_aliases() {
		if (jdbc_resource_aliases == null){
			jdbc_resource_aliases = beans.getAllJDBCResourceAliases();
		}
		if (jdbc_resource_aliases != null){
			log.debugT("JDBCResourceManagedBean getJdbc_resource_aliases:"+jdbc_resource_aliases.length);
		}
		return jdbc_resource_aliases;
	}
	
	
	
	public void selectedds(ActionEvent event){
	    Object obj = ((HtmlCommandLink) event.getComponent()).getValue();
	    selectedResourceName = obj.toString();
	    setSelectedResource(selectedResourceName);
	    log.debugT("JDBCResourceManagedBean selectedds:"+selectedResourceName);
	}

	private void setSelectedResource(String name) {
		selectedJDBCDataSource = null;
		jdbc_resources = beans.getAllJDBCResources();
		for (JDBCDataResource jdbc: jdbc_resources){
			if (jdbc.getName().equals(name)){
				selectedJDBCDataSource = jdbc;
				log.debugT("JDBCResourceManagedBean setSelectedResource: found");
				break;
			}
		}
	}
	
	 

	public JDBCDataResource getSelectedJDBCDataSource(){
		if (selectedJDBCDataSource != null){
			//log.debugT("JDBCResourceManagedBean getSelectedJDBCDataSource:"+selectedJDBCDataSource.getName());
			if (selectedJDBCDataSource.getAliases() != null){
				//log.debugT("JDBCResourceManagedBean getSelectedJDBCDataSource Aliases:"+selectedJDBCDataSource.getAliases().length);
			}
		}else{
			//log.debugT("JDBCResourceManagedBean getSelectedJDBCDataSource: null");
		}
		return selectedJDBCDataSource;
	}

    public void setUpdatedDataSourceAlias(String alias) {
        log.debugT("JDBCResourceManagedBean setUpdateSourceAlias: " +alias);
        updatedDataSourceAlias = alias;
    }

    public String getUpdatedDataSourceAlias() {
        return "";
    }
    

    
    public void setRedirectToDataSource(String to){
        log.debugT("JDBCResourceManagedBean setRedirectToDataSource: " +to);
        redirectToDataSource = to;
    }

    public String getRedirectToDataSource() {
        return "";
    }
    

    
    public List<SelectItem> getDataSourceItemList(){
        JDBCDataResource[] dss = getJdbc_resources();
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (JDBCDataResource ds: dss){
            list.add(new SelectItem(ds.getName()));
        }
        return list;
        
    }
	
    public String createDataSourceAlias(){
        log.debugT("createDataSourceAlias: "+updatedDataSourceAlias); 
        beans.addAlias(selectedJDBCDataSource.getName(),updatedDataSourceAlias);
        setSelectedResource(selectedResourceName);
        return "";
    }
    
     public String redirectDataSourceAlias(){
         if (selectedJDBCDataSource == null || selectedJDBCDataSource.getAliases() == null){
             return "";
         }
             log.debugT("redirectDataSourceAlias to:"+redirectToDataSource);   
             for (JDBCDataResourceAlias alias: selectedJDBCDataSource.getAliases()){
                if (alias.isSelected()){
                        log.debugT("redirectDataSourceAlias: "+alias.getName());   
                        beans.redirectAlias(alias,redirectToDataSource);
                }
            }
            setSelectedResource(selectedResourceName);   
        return "";
    }
     public String removeDataSourceAlias(){
         if (selectedJDBCDataSource == null || selectedJDBCDataSource.getAliases() == null){
             return "";
         }
            for (JDBCDataResourceAlias alias: selectedJDBCDataSource.getAliases()){
                if (alias.isSelected()){
                        log.debugT("removeDataSourceAlias: "+alias.getName());   
                        beans.removeAlias(alias.getCompositeData());
                }
            }
            setSelectedResource(selectedResourceName);
        return "";
    }
     
    public String createDataSource(){
       log.debugT("createDataSource");
       return "";
    }	
   
    public String removeDataSource(){
       log.debugT("removeDataSource");
       for (JDBCDataResource ds : jdbc_resources){
           if (ds.isSelected()){
               beans.removeDataSource(ds.getName());
           }
       }
       return "";
    }
    
    public void changeJdbc(ValueChangeEvent event){
         Object obj = ((HtmlSelectOneMenu) event.getComponent()).getValue();
         selectedJDBCDataSource.setJdbcVersion(Integer.parseInt(obj.toString()));
    }
    
    
     public void changeDSType(ValueChangeEvent event){
         Object obj = ((HtmlSelectOneMenu) event.getComponent()).getValue();
         selectedJDBCDataSource.setDsType(Integer.parseInt(obj.toString()));
    }
    
    
    
    
    
	
}