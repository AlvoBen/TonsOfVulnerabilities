<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="errorPagesView">
<p:portletPage>

<h:form id="errorPagesForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Error Pages" /></f:facet> 
                    <h:dataTable id="errorPages" value="#{ApplicationSettingsBean.errorPages}" var="errorPage" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.errorPages != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.errorPages != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.errorPages != null}" /></f:facet> 
                           <h:outputText value="#{errorPage.elementName}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.errorPages != null}" >
                           <f:facet name="header"><h:outputText value="Error Code" rendered="#{ApplicationSettingsBean.errorPages != null}" /></f:facet> 
                           <h:outputText value="#{errorPage.errorCode}"/>                           
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.errorPages != null}" >
                           <f:facet name="header"><h:outputText value="Type" rendered="#{ApplicationSettingsBean.errorPages != null}" /></f:facet> 
                           <h:outputText value="#{errorPage.type}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.errorPages != null}" >
                           <f:facet name="header"><h:outputText value="Location" rendered="#{ApplicationSettingsBean.errorPages != null}" /></f:facet> 
                           <h:outputText value="#{errorPage.location}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.errorPages != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.errorPages != null}" /></f:facet> 
                           <h:outputText value="#{errorPage.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>