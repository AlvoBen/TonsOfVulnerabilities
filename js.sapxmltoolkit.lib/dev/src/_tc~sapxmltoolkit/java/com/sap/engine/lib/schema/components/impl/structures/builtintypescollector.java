package com.sap.engine.lib.schema.components.impl.structures;

import com.sap.engine.lib.schema.components.FundamentalFacets;
import com.sap.engine.lib.schema.components.impl.ffacets.*;
import com.sap.engine.lib.schema.Constants;

import java.util.*;

class BuiltInTypesCollector extends Hashtable implements Constants {

  protected BuiltInTypesCollector() {
    super();
    init();
  }

  private void init() {
    initAnyType();
    initType(TYPE_ANY_SIMPLE_TYPE_NAME, TYPE_ANY_TYPE_NAME, new FundamentalFacetsAnySimpleType(), null, false, true, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_DURATION_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsDuration(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_DATE_TIME_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsDateTime(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_TIME_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsTime(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_DATE_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsDate(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_G_YEAR_MONTH_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsGYearMonth(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_G_YEAR_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsGYear(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_G_MONTH_DAY_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsGMonthDay(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_G_DAY_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsGDay(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_G_MONTH_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsGMonth(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_STRING_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsString(), null, true, false, WHITE_SPACE_PRESERVE_NORM_VALUE);
    initType(TYPE_BOOLEAN_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsBoolean(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_BASE_64_BINARY_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsBase64Binary(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_HEX_BINARY_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsHexBinary(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_FLOAT_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsFloat(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_DECIMAL_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsDecimal(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_DOUBLE_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsDouble(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_ANY_URI_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsAnyURI(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_QNAME_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsQName(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_NOTATION_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, new FundamentalFacetsNotation(), null, true, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_NORMALIZED_STRING_NAME, TYPE_STRING_NAME, new FundamentalFacetsNormalizedString(), null, false, false, WHITE_SPACE_PRESERVE_NORM_VALUE);
    initType(TYPE_INTEGER_NAME, TYPE_DECIMAL_NAME, new FundamentalFacetsInteger(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_TOKEN_NAME, TYPE_NORMALIZED_STRING_NAME, new FundamentalFacetsToken(), null, false, false, WHITE_SPACE_PRESERVE_NORM_VALUE);
    initType(TYPE_NON_POSITIVE_INTEGER_NAME, TYPE_INTEGER_NAME, new FundamentalFacetsNonPositiveInteger(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_LONG_NAME, TYPE_INTEGER_NAME, new FundamentalFacetsLong(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_NON_NEGATIVE_INTEGER_NAME, TYPE_INTEGER_NAME, new FundamentalFacetsNonNegativeInteger(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_LANGUAGE_NAME, TYPE_TOKEN_NAME, new FundamentalFacetsLanguage(), null, false, false, WHITE_SPACE_PRESERVE_NORM_VALUE);
    initType(TYPE_NAME_NAME, TYPE_TOKEN_NAME, new FundamentalFacetsName(), null, false, false, WHITE_SPACE_PRESERVE_NORM_VALUE);
    initType(TYPE_NMTOKEN_NAME, TYPE_TOKEN_NAME, new FundamentalFacetsNMTOKEN(), null, false, false, WHITE_SPACE_PRESERVE_NORM_VALUE);
    initType(TYPE_NEGATIVE_INTEGER_NAME, TYPE_NON_POSITIVE_INTEGER_NAME, new FundamentalFacetsNegativeInteger(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_INT_NAME, TYPE_LONG_NAME, new FundamentalFacetsInt(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_UNSIGNED_LONG_NAME, TYPE_NON_NEGATIVE_INTEGER_NAME, new FundamentalFacetsUnsignedLong(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_POSITIVE_INTEGER_NAME, TYPE_NON_NEGATIVE_INTEGER_NAME, new FundamentalFacetsPositiveInteger(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_NCNAME_NAME, TYPE_NAME_NAME, new FundamentalFacetsNCName(), null, false, false, WHITE_SPACE_PRESERVE_NORM_VALUE);
    initType(TYPE_NMTOKENS_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, FundamentalFacetsList.newInstance(), TYPE_NMTOKEN_NAME, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_SHORT_NAME, TYPE_INT_NAME, new FundamentalFacetsShort(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_UNSIGNED_INT_NAME, TYPE_UNSIGNED_LONG_NAME, new FundamentalFacetsUnsignedInt(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_ID_NAME, TYPE_NCNAME_NAME, new FundamentalFacetsID(), null, false, false, WHITE_SPACE_PRESERVE_NORM_VALUE);
    initType(TYPE_IDREF_NAME, TYPE_NCNAME_NAME, new FundamentalFacetsIDREF(), null, false, false, WHITE_SPACE_PRESERVE_NORM_VALUE);
    initType(TYPE_ENTITY_NAME, TYPE_NCNAME_NAME, new FundamentalFacetsENTITY(), null, false, false, WHITE_SPACE_PRESERVE_NORM_VALUE);
    initType(TYPE_BYTE_NAME, TYPE_SHORT_NAME, new FundamentalFacetsByte(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_UNSIGNED_SHORT_NAME, TYPE_UNSIGNED_INT_NAME, new FundamentalFacetsUnsignedShort(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_IDREFS_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, FundamentalFacetsList.newInstance(), TYPE_IDREF_NAME, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_ENTITIES_NAME, TYPE_ANY_SIMPLE_TYPE_NAME, FundamentalFacetsList.newInstance(), TYPE_ENTITY_NAME, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
    initType(TYPE_UNSIGNED_BYTE_NAME, TYPE_UNSIGNED_SHORT_NAME, new FundamentalFacetsUnsignedByte(), null, false, false, WHITE_SPACE_COLLAPSE_NORM_VALUE);
  }

  private void initAnyType() {
    ComplexTypeDefinitionImpl type = new ComplexTypeDefinitionImpl();
    type.isBuiltIn = true;
    type.isLoaded = true;
    type.isLoading = false;
    type.isPreloaded = true;
    type.isPreloading = false;
    type.name = TYPE_ANY_TYPE_NAME;
    type.isUrType = true;
    type.baseTypeDefinition = type;
    type.isDerivationMethodExtension = false;
    type.isMixed = true;
    type.contentTypeContentModel = new ParticleImpl();
    type.contentTypeContentModel.minOccurs = 1;
    type.contentTypeContentModel.maxOccurs = 1;
    type.contentTypeContentModel.isEmptiable = true;
    type.contentTypeContentModel.term = new ModelGroupImpl();
    ((ModelGroupImpl)(type.contentTypeContentModel.term)).compositor = ModelGroupImpl.SEQUENCE;
    ((ModelGroupImpl)(type.contentTypeContentModel.term)).minimumEffectiveTotalRange = 0;
    ((ModelGroupImpl)(type.contentTypeContentModel.term)).maximumEffectiveTotalRange = 0;
    ParticleImpl particle = new ParticleImpl();
    particle.minOccurs = 0;
    particle.maxOccurs = Integer.MAX_VALUE;
    particle.isEmptiable = true;
    particle.term = new WildcardImpl();
    ((WildcardImpl)(particle.term)).anyNamespace = true;
    ((WildcardImpl)(particle.term)).processContents = WildcardImpl.LAX;
    ((ModelGroupImpl)(type.contentTypeContentModel.term)).particles.add(particle);
    type.attributeWildcard = new WildcardImpl();
    type.attributeWildcard.isAttribWildcard = true;
    type.attributeWildcard.anyNamespace = true;
    type.attributeWildcard.processContents = WildcardImpl.LAX;
    put(type.name, type);
  }

  private void initType(String typeName, String baseTypeName, FundamentalFacets fundamentalFacets, String itemTypeName, boolean isPrimitive, boolean isUrType, String whiteSpaceNormalizationValue) {
    SimpleTypeDefinitionImpl type = new SimpleTypeDefinitionImpl();
    type.isBuiltIn = true;
    type.isLoaded = true;
    type.isLoading = false;
    type.name = typeName;
    type.isUrType = isUrType;
    type.whiteSpaceNormalizationValue = whiteSpaceNormalizationValue;
    if(baseTypeName != null) {
      type.baseTypeDefinition = getType(baseTypeName);
    }
    type.fundamentalFacets = fundamentalFacets;
    if(itemTypeName == null) {
      type.variety = SimpleTypeDefinitionImpl.ATOMIC;
    } else {
      type.variety = SimpleTypeDefinitionImpl.LIST;
      type.itemTypeDefinition = (SimpleTypeDefinitionImpl)(getType(itemTypeName));
    }
    type.isPrimitive = isPrimitive;
    if(type.baseTypeDefinition != null && !type.isUrType) {
      type.primitiveTypeDefinition = type.isPrimitive ? type : ((SimpleTypeDefinitionImpl)(type.baseTypeDefinition)).primitiveTypeDefinition;
    }
    put(type.name, type);
  }

  private TypeDefinitionBaseImpl getType(String name) {
    return((TypeDefinitionBaseImpl)(get(name)));
  }
}
