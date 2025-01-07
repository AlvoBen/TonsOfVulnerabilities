<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="messageDestinationsView">
<p:portletPage>

<h:form id="messageDestinationsForm">          
          <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Message Destinations"/></f:facet> 
                    <h:dataTable id="messageDestinations" value="#{ApplicationSettingsBean.messageDestinations}" var="messageDestination" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.messageDestinations != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinations != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.messageDestinations != null}" /></f:facet> 
                           <h:outputText value="#{messageDestination.elementName}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinations != null}" >
                           <f:facet name="header"><h:outputText value="Name" rendered="#{ApplicationSettingsBean.messageDestinations != null}" /></f:facet> 
                           <h:outputText value="#{messageDestination.name}"/>                           
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinations != null}" >
                           <f:facet name="header"><h:outputText value="JNDI Name" rendered="#{ApplicationSettingsBean.messageDestinations != null}" /></f:facet> 
                           <h:outputText value="#{messageDestination.jndiName}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.messageDestinations != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.messageDestinations != null}" /></f:facet> 
                           <h:outputText value="#{messageDestination.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>	

</h:form>
</p:portletPage>
</f:subview>