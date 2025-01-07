<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="resourceEnvironmentReferencesView">
<p:portletPage>

<h:form id="resourceEnvironmentReferencesForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Resource Environment References" /></f:facet> 
                    <h:dataTable id="resourceEnvironmentReferences" value="#{ApplicationSettingsBean.resourceEnvironmentReferences}" var="resourceEnvironmentReference" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceEnvironmentReference.elementName}"/>                    
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}" >
                           <f:facet name="header"><h:outputText value="Name" rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceEnvironmentReference.name}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}" >
                           <f:facet name="header"><h:outputText value="JNDI Name" rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceEnvironmentReference.jndiName}"/>                    
                        </h:column>                        
                        <h:column rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}" >
                           <f:facet name="header"><h:outputText value="Type" rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceEnvironmentReference.type}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.resourceEnvironmentReferences != null}" /></f:facet> 
                           <h:outputText value="#{resourceEnvironmentReference.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>