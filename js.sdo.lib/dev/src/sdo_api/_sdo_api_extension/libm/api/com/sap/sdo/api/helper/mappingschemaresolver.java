package com.sap.sdo.api.helper;


public interface MappingSchemaResolver extends SchemaResolver {

	/**
     * Defines a mapping from an absolute schema location to an internal
     * schema location. The absolute schema location is defined in the import or
     * include in the schema or in the schemaLocation in the xml. Relative
     * schema locations will not work, they have to be translated to absolute
     * schema locations first. The internal schema location must be resolvable
     * at runtime in this environment.
     * @param absoluteSchemaLocation The given absolute schema location.
     * @param internalSchemaLocation The internal schema location.
	 */
    void defineSchemaLocationMapping(String absoluteSchemaLocation, String internalSchemaLocation);

}
