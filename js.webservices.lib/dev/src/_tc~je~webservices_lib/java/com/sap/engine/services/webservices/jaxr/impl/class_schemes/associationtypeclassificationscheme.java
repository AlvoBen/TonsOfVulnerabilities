/*
 * Copyright (c) 2004 by SAP Labs Bulgaria.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.services.webservices.jaxr.impl.class_schemes;

import java.util.Locale;

import com.sap.engine.services.webservices.jaxr.infomodel.ClassificationSchemeImpl;
import com.sap.engine.services.webservices.jaxr.infomodel.InternationalStringImpl;
import com.sap.engine.services.webservices.jaxr.infomodel.LocalizedStringImpl;

import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.Connection;
import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.LocalizedString;

/**
 * @author Vladimir Videlov
 * @version 6.30
 */
public class AssociationTypeClassificationScheme extends ClassificationSchemeImpl implements PredefinedClassificationScheme {
  public AssociationTypeClassificationScheme(Connection connection) throws JAXRException {
    super(connection, "AssociationType");
  }

  public void init() throws JAXRException {
    BusinessLifeCycleManager blcm = getConnection().getRegistryService().getBusinessLifeCycleManager();

    this.getChildrenConcepts().clear();
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("RelatedTo"), "RelatedTo"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("HasChild"), "HasChild"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("HasMember"), "HasMember"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("HasParent"), "HasParent"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("ExternallyLinks"), "ExternallyLinks"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("Contains"), "Contains"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("EquivalentTo"), "EquivalentTo"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("Extends"), "Extends"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("Implements"), "Implements"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("InstanceOf"), "InstanceOf"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("Supersedes"), "Supersedes"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("Uses"), "Uses"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("Replaces"), "Replaces"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("ResponsibleFor"), "ResponsibleFor"));
    this.addChildConcept(blcm.createConcept(this, crtInternationalStringENUSLocale("SubmitterOf"), "SubmitterOf"));
  }
  
  static InternationalString crtInternationalStringENUSLocale(String s) throws JAXRException {
    LocalizedString localizedName = new LocalizedStringImpl();
    localizedName.setLocale(ClassificationSchemeImpl.EN_US_LOCALE);
    localizedName.setValue(s);
    InternationalString name = new InternationalStringImpl();
    name.addLocalizedString(localizedName);
    LocalizedString defaultLocalizedName = new LocalizedStringImpl();
    defaultLocalizedName.setLocale(Locale.getDefault());
    defaultLocalizedName.setValue(s);
    name.addLocalizedString(defaultLocalizedName);
    return name;
  }
}
