package org.http4k.contract.jsonschema.v3

import kotlin.reflect.KParameter
import kotlin.reflect.KType

typealias MetadataByType = Map<KType, FieldMetadata>

class ConfigurableMetadataRetrievalStrategy(private val metadataByType: MetadataByType) :
    FieldMetadataRetrievalStrategy {

    override fun invoke(target: Any, fieldName: String): FieldMetadata =
        target.javaClass.kotlin.constructors.firstOrNull()
            ?.parameters
            ?.firstOrNull { p -> p.kind == KParameter.Kind.VALUE && p.name == fieldName }?.type
            ?.let { p -> metadataByType[p] } ?: FieldMetadata()

}
