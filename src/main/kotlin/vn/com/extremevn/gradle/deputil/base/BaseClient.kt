/*
MIT License
Copyright (c) [2020] Extreme Viet Nam

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package vn.com.extremevn.gradle.deputil.base

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

/**
 * Base http client wraps api call input, output
 */
interface BaseClient : Runnable {

    companion object {
        val client = OkHttpClient()
        private val requestBuilder = Request.Builder()
    }

    /**
     * Make call to [url] which uses POST method with [headers] and [requestBody]
     */
    fun doPost(
        headers: Map<String, String>,
        url: String,
        requestBody: RequestBody
    ): Pair<Response?, Int> {
        headers.forEach {
            requestBuilder.addHeader(it.key, it.value)
        }
        val request = requestBuilder.url(url)
            .post(requestBody)
            .build()
        var response: Response? = null
        val status: Int

        try {
            response = client.newCall(request).execute()
            status = response.code
        } finally {
            response?.close()
        }
        return Pair(response, status)
    }
}
