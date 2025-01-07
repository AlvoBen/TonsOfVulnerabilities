<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
        <f:subview id="jspPropertiesGroups1">
        <p:portletPage>

<h:form id="jspPropertiesGroups">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="JSP Properties Groups" /></f:facet> 
                    <h:dataTable id="jspPropertiesGroups" value="#{ApplicationSettingsBean.jspPropertiesGroups}" var="jspPropGroup" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" /></f:facet> 
                           <h:outputText value="#{jspPropGroup.elementName}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" >
                           <f:facet name="header"><h:outputText value="Display Name" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" /></f:facet> 
                           <h:outputText value="#{jspPropGroup.displayName}"/>                           
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" >
                           <f:facet name="header"><h:outputText value="Page Encoding" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" /></f:facet> 
                           <h:outputText value="#{jspPropGroup.pageEncoding}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" >
                           <f:facet name="header"><h:outputText value="Patterns" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" /></f:facet> 
                           <h:outputText value="#{jspPropGroup.patterns}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" >
                           <f:facet name="header"><h:outputText value="Include Preludes" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" /></f:facet> 
                           <h:outputText value="#{jspPropGroup.includePreludes}"/>                    
                        </h:column>                                                                           
                        <h:column rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" >
                           <f:facet name="header"><h:outputText value="Include Codas" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" /></f:facet> 
                           <h:outputText value="#{jspPropGroup.includeCodas}"/>                    
                        </h:column>                                                                                                   
                        <h:column rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" >
                           <f:facet name="header"><h:outputText value="Scripting Invalid" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" /></f:facet> 
                           <h:selectBooleanCheckbox value="#{jspPropGroup.scriptingInvalid}" disabled="true" />                    
                        </h:column>  
                        <h:column rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" >
                           <f:facet name="header"><h:outputText value="EL Ignored" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" /></f:facet> 
                           <h:selectBooleanCheckbox value="#{jspPropGroup.elIgnored}" disabled="true" />                    
                        </h:column>                                                  
                        <h:column rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" >
                           <f:facet name="header"><h:outputText value="XML" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" /></f:facet> 

                           <h:selectBooleanCheckbox value="#{jspPropGroup.xml}" disabled="true" />                    
                        </h:column>                                                                          
                        <h:column rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.jspPropertiesGroups != null}" /></f:facet> 
                           <h:outputText value="#{jspPropGroup.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>