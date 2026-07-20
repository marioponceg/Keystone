package io.github.marioponceg.keystone.data

import io.github.marioponceg.conduit.engine.ConduitEngine
import io.github.marioponceg.conduit.http.HttpRequest
import io.github.marioponceg.conduit.http.HttpResponse
import java.io.IOException

/** Engine returning a canned response (or throwing), recording the last request. */
class FakeEngine(
    private val response: HttpResponse? = null,
    private val error: IOException? = null,
) : ConduitEngine {
    var lastRequest: HttpRequest? = null

    override suspend fun execute(request: HttpRequest): HttpResponse {
        lastRequest = request
        error?.let { throw it }
        return checkNotNull(response)
    }
}
