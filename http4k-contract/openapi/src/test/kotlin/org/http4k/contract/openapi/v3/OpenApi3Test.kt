package org.http4k.contract.openapi.v3

import argo.jdom.JsonNode
import com.ubertob.kondor.json.parser.string
import org.http4k.contract.ContractRendererContract
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.contract.meta
import org.http4k.contract.openapi.AddSimpleFieldToRootNode
import org.http4k.contract.openapi.ApiInfo
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.format.Argo
import org.http4k.format.Argo.asJsonValue
import org.http4k.lens.Header
import org.http4k.testing.Approver
import org.junit.jupiter.api.Test

val lens = Header.map(Boop.Companion::of).required("f", "mappedHeader")
val rendererToUse = OpenApi3(
    ApiInfo("title", "1.2", "module description"),
    Argo,
    listOf(AddSimpleFieldToRootNode),
    servers = listOf(ApiServer(Uri.of("https://localhost:8000"))),
    lensToSchema = mapOf(lens to mapOf("type" to "string".asJsonValue(), "pattern" to "test".asJsonValue()))
)

class OpenApi3Test : ContractRendererContract<JsonNode>(Argo, rendererToUse) {
    @Test
    fun `wip`(approver: Approver) {
        val router = "/basepath" bind contract {
            renderer = rendererToUse
            routes += "/headers" meta {
                headers += lens
            } bindContract Method.POST to { _ -> Response(OK).body("hello") }
        }
        val httpMessage = router(Request(Method.GET, "/basepath?the_api_key=somevalue"))
        approver.assertApproved(httpMessage)
    }
}


class Boop(val value: String) {
    companion object {
        fun of(inner: String) = Boop(inner)
    }
}
