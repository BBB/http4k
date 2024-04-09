package org.http4k.contract.jsonschema.v3

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import kotlin.reflect.full.createType

private typealias Bingo = String
@JvmInline
value class Boop(val value: String)

class ConfigurableMetadataRetrievalStrategyTest {
    @Test
    fun `match metadata for an instance of the same type`() {
        class Inner
        class Wrapper(val first: Inner = Inner(), val second: Inner = Inner())

        val retriever = ConfigurableMetadataRetrievalStrategy(
            mapOf(
                Inner::class.createType() to FieldMetadata(
                    "pattern" to "*"
                ),
                Bingo::class.createType() to FieldMetadata(
                    "description" to "hello"
                )
            )
        )
        assertThat(
            retriever(Wrapper(), "first"),
            equalTo(FieldMetadata(extra = mapOf("pattern" to "*")))
        )
        assertThat(
            retriever(Wrapper(), "second"),
            equalTo(FieldMetadata(extra = mapOf("pattern" to "*")))
        )
    }


    @Test
    fun `typealiases aren't a good idea`() {

        class Wrapper(val bingo: Bingo = "", val notExpectedToMatchTypealiasButDoesAsTheyAreCompiledAway: String = "")

        val retriever = ConfigurableMetadataRetrievalStrategy(
            mapOf(
                Bingo::class.createType() to FieldMetadata(
                    "description" to "hello"
                )
            )
        )
        assertThat(
            retriever(Wrapper(), "bingo"),
            equalTo(FieldMetadata(extra = mapOf("description" to "hello")))
        )
        assertThat(
            retriever(Wrapper(), "notExpectedToMatchTypealiasButDoesAsTheyAreCompiledAway"),
            equalTo(FieldMetadata(extra = mapOf("description" to "hello")))
        )
    }

    @Test
    fun `value classes work much better than typealias`() {

        class Wrapper(val expectedToMatch: Boop = Boop(""), val notExpectedToMatch: String = "")

        val retriever = ConfigurableMetadataRetrievalStrategy(
            mapOf(
                Boop::class.createType() to FieldMetadata(
                    "description" to "hello"
                )
            )
        )
        assertThat(
            retriever(Wrapper(), "expectedToMatch"),
            equalTo(FieldMetadata(extra = mapOf("description" to "hello")))
        )
        assertThat(
            retriever(Wrapper(), "notExpectedToMatch"),
            equalTo(FieldMetadata(extra = emptyMap()))
        )
    }

}
