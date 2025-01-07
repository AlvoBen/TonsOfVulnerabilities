<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="ejbLocalReferencesView">
<p:portletPage>

<h:form id="ejbLocalReferencesForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="EJB Local References" /></f:facet> 
                    <h:dataTable id="ejbLocalReferences" value="#{ApplicationSettingsBean.ejbLocalReferences}" var="ejbLocalReference" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbLocalReference.elementName}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" >
                           <f:facet name="header"><h:outputText value="Name" rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbLocalReference.name}"/>                           
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" >
                           <f:facet name="header"><h:outputText value="Type" rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbLocalReference.type}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" >
                           <f:facet name="header"><h:outputText value="Link" rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbLocalReference.link}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" >
                           <f:facet name="header"><h:outputText value="Local" rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbLocalReference.local}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" >
                           <f:facet name="header"><h:outputText value="JNDI Name" rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbLocalReference.jndiName}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" >
                           <f:facet name="header"><h:outputText value="Local Home" rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbLocalReference.localHome}"/>                    
                        </h:column>                                                                           
                        <h:column rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.ejbLocalReferences != null}" /></f:facet> 
                           <h:outputText value="#{ejbLocalReference.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>