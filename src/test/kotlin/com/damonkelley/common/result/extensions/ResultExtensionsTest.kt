package com.damonkelley.common.result.extensions

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.kotest.core.spec.style.FunSpec

class ResultExtensionsTest : FunSpec({
    context("flatMap") {
        context("on success") {
            test("it unwraps the result") {
                val result = Result.success("Success")
                    .flatMap { Result.success("Flat Mapped") }

                assertThat(result, equalTo(Result.success("Flat Mapped")))
            }
        }
    }
})
