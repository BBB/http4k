package org.http4k.lens

data class Meta(val required: Boolean, val location: String, val paramMeta: ParamMeta, val name: String, val description: String? = null, val bag: Map<String, Any> = emptyMap())
