<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="ejbRemoteReferencesView">
<p:portletPage>

<h:form id="ejbRemoteReferencesForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="EJB Remote References" /></f:facet> 
                    <h:dataTable id="ejbRemoteReferences" value="#{ApplicationSettingsBean.ejbRemoteReferences}" var="ejbRemoteReference" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbRemoteReference.elementName}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" >
                           <f:facet name="header"><h:outputText value="Name" rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbRemoteReference.name}"/>                           
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" >
                           <f:facet name="header"><h:outputText value="Type" rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbRemoteReference.type}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" >
                           <f:facet name="header"><h:outputText value="Link" rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbRemoteReference.link}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" >
                           <f:facet name="header"><h:outputText value="Remote" rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbRemoteReference.remote}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" >
                           <f:facet name="header"><h:outputText value="JNDI Name" rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbRemoteReference.jndiName}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" >
                           <f:facet name="header"><h:outputText value="Remote Home" rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbRemoteReference.remoteHome}"/>                    
                        </h:column>                                                                           
                        <h:column rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.ejbRemoteReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbRemoteReference.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>