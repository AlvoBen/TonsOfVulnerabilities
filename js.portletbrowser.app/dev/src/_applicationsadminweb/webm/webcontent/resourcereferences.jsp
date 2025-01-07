<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="resourceReferencesView">
<p:portletPage>

<h:form id="resourceReferencesForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Resource References" /></f:facet> 
                    <h:dataTable id="resourceReferences" value="#{ApplicationSettingsBean.resourceReferences}" var="resourceReference" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.resourceReferences != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.resourceReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceReference.elementName}"/>                    
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.resourceReferences != null}" >
                           <f:facet name="header"><h:outputText value="Name" rendered="#{ApplicationSettingsBean.resourceReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceReference.name}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.resourceReferences != null}" >
                           <f:facet name="header"><h:outputText value="JNDI Name" rendered="#{ApplicationSettingsBean.resourceReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceReference.jndiName}"/>                    
                        </h:column>                        
                        <h:column rendered="#{ApplicationSettingsBean.resourceReferences != null}" >
                           <f:facet name="header"><h:outputText value="Type" rendered="#{ApplicationSettingsBean.resourceReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceReference.type}"/>                    
                        </h:column>       
                        <h:column rendered="#{ApplicationSettingsBean.resourceReferences != null}" >
                           <f:facet name="header"><h:outputText value="Auth Type" rendered="#{ApplicationSettingsBean.resourceReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceReference.authType}"/>                    
                        </h:column>  
                        <h:column rendered="#{ApplicationSettingsBean.resourceReferences != null}" >
                           <f:facet name="header"><h:outputText value="Sharing Scope" rendered="#{ApplicationSettingsBean.resourceReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceReference.sharingScope}"/>                    
                        </h:column>          
                        <h:column rendered="#{ApplicationSettingsBean.resourceReferences != null}" >
                           <f:facet name="header"><h:outputText value="transactional" rendered="#{ApplicationSettingsBean.resourceReferences != null}" /></f:facet> 
                           <h:selectBooleanCheckbox value="#{resourceReference.transactional}" disabled="true" />                    
                        </h:column>                                                                                
                        <h:column rendered="#{ApplicationSettingsBean.resourceReferences != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.resourceReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceReference.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>