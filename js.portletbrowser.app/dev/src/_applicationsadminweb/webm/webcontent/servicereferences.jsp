<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="serviceReferencesView">
<p:portletPage>

<h:form id="serviceReferencesForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Service References" /></f:facet> 
                    <h:dataTable id="serviceReferences" value="#{ApplicationSettingsBean.serviceReferences}" var="serviceReference" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.serviceReferences != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.serviceReferences != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.serviceReferences != null}" /></f:facet> 
                           <h:outputText value="#{serviceReference.elementName}"/>                    
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.serviceReferences != null}" >
                           <f:facet name="header"><h:outputText value="Name" rendered="#{ApplicationSettingsBean.serviceReferences != null}" /></f:facet> 
                           <h:outputText value="#{serviceReference.name}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.serviceReferences != null}" >
                           <f:facet name="header"><h:outputText value="JNDI Name" rendered="#{ApplicationSettingsBean.serviceReferences != null}" /></f:facet> 
                           <h:outputText value="#{serviceReference.jndiName}"/>                    
                        </h:column>                        
                        <h:column rendered="#{ApplicationSettingsBean.serviceReferences != null}" >
                           <f:facet name="header"><h:outputText value="Type" rendered="#{ApplicationSettingsBean.serviceReferences != null}" /></f:facet> 
                           <h:outputText value="#{serviceReference.type}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.serviceReferences != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.serviceReferences != null}" /></f:facet> 
                           <h:outputText value="#{serviceReference.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>