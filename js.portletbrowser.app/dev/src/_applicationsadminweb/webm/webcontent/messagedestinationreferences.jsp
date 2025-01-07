<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="messageDestinationReferencesView">
<p:portletPage>

<h:form id="messageDestinationReferencesForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Message Destination References" /></f:facet> 
                    <h:dataTable id="messageDestinationReferences" value="#{ApplicationSettingsBean.messageDestinationReferences}" var="messageDestinationReference" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" /></f:facet> 
                           <h:outputText value="#{messageDestinationReference.elementName}"/>                    
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" >
                           <f:facet name="header"><h:outputText value="Name" rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" /></f:facet> 
                           <h:outputText value="#{messageDestinationReference.name}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" >
                           <f:facet name="header"><h:outputText value="JNDI Name" rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" /></f:facet> 
                           <h:outputText value="#{messageDestinationReference.jndiName}"/>                    
                        </h:column>                        
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" >
                           <f:facet name="header"><h:outputText value="Link" rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" /></f:facet> 
                           <h:outputText value="#{messageDestinationReference.link}"/>                           
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" >
                           <f:facet name="header"><h:outputText value="Type" rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" /></f:facet> 
                           <h:outputText value="#{messageDestinationReference.type}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" >
                           <f:facet name="header"><h:outputText value="Usage" rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" /></f:facet> 
                           <h:outputText value="#{messageDestinationReference.usage}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.messageDestinationReferences != null}" /></f:facet> 
                           <h:outputText value="#{messageDestinationReference.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>