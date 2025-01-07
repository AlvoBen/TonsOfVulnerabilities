<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="environmentEntriesView">
<p:portletPage>

<h:form id="environmentEntriesForm">          
          <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Environment Entries"/></f:facet> 
                    <h:dataTable id="environmentEntries" value="#{ApplicationSettingsBean.environmentEntries}" var="environmentEntry" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.environmentEntries != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.environmentEntries != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.environmentEntries != null}" /></f:facet> 
                           <h:outputText value="#{environmentEntry.elementName}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.environmentEntries != null}" >
                           <f:facet name="header"><h:outputText value="Name" rendered="#{ApplicationSettingsBean.environmentEntries != null}" /></f:facet> 
                           <h:outputText value="#{environmentEntry.name}"/>                           
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.environmentEntries != null}" >
                           <f:facet name="header"><h:outputText value="Type" rendered="#{ApplicationSettingsBean.environmentEntries != null}" /></f:facet> 
                           <h:outputText value="#{environmentEntry.type}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.environmentEntries != null}" >
                           <f:facet name="header"><h:outputText value="Value" rendered="#{ApplicationSettingsBean.environmentEntries != null}" /></f:facet> 
                           <h:outputText value="#{environmentEntry.value}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.environmentEntries != null}" >
                           <f:facet name="header"><h:outputText value="JNDI Name" rendered="#{ApplicationSettingsBean.environmentEntries != null}" /></f:facet> 
                           <h:outputText value="#{environmentEntry.jndiName}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.environmentEntries != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.environmentEntries != null}" /></f:facet> 
                           <h:outputText value="#{environmentEntry.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>	

</h:form>
</p:portletPage>
</f:subview>