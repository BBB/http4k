package org.http4k.contract.openapi.v3

import argo.jdom.JsonNode
import org.http4k.contract.ContractRendererContract
import org.http4k.contract.HttpMessageMeta
import org.http4k.contract.RequestMeta
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.contract.meta
import org.http4k.contract.openapi.AddSimpleFieldToRootNode
import org.http4k.contract.openapi.ApiInfo
import org.http4k.core.Body
import org.http4k.core.HttpMessage
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.with
import org.http4k.format.Argo
import org.http4k.format.Argo.asJsonValue
import org.http4k.format.Moshi.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.Header
import org.http4k.lens.Lens
import org.http4k.lens.LensExtractor
import org.http4k.testing.Approver
import org.junit.jupiter.api.Test

val lens = Header.map(Boop.Companion::of).required("f", "mappedHeader").bag(mapOf("pattern" to "ollie"))
val rendererToUse = OpenApi3(
    ApiInfo("title", "1.2", "module description"),
    Argo,
    listOf(AddSimpleFieldToRootNode),
    servers = listOf(ApiServer(Uri.of("https://localhost:8000"))),
    lensToSchema = emptyMap()
)

class OpenApi3Test : ContractRendererContract<JsonNode>(Argo, rendererToUse) {
    @Test
    fun `wip`(approver: Approver) {
        val router = "/basepath" bind contract {
            renderer = rendererToUse
            routes += "/headers" meta {
                headers += lens
                receiving(Body.auto<Boop>().toLens() to Boop("abc"))
            } bindContract Method.POST to { _ -> Response(OK).body("hello") }
        }
        val httpMessage = router(Request(Method.GET, "/basepath?the_api_key=somevalue"))
        approver.assertApproved(httpMessage)
    }
}

private infix fun <T> Pair<BiDiBodyLens<T>, T>.meta(meta: Map<String, String>): HttpMessageMeta<Request> {
    return RequestMeta(
        Request(Method.POST, "").with(this.first of this.second),
        "definitionId",
        this.second,
        "schemaPrefix"
    )

}



private infix fun <IN : HttpMessage, FINAL> Lens<IN, FINAL>.meta(meta: Map<String, String>): Pair<LensExtractor<IN, FINAL>, Map<String, String>> {
    return this to meta
}


class Boop(val value: String) {
    companion object {
        fun of(inner: String) = Boop(inner)
    }
}
