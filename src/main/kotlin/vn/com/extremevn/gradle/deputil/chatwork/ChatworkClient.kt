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

package vn.com.extremevn.gradle.deputil.chatwork

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import vn.com.extremevn.gradle.deputil.base.BaseClient
import java.io.File

/**
 * A Chatwork client which implements [BaseClient] upload outdated dependencies information [file] with [message]
 * using [token] to [roomId]
 */
class ChatworkClient(
    private val token: String,
    private val roomId: Int,
    private val file: File,
    private val message: String = ""
) : BaseClient {

    override fun run() {
        val (response, status) = uploadFileWithMessage()
        val isSuccessful = response?.isSuccessful ?: return
        if (!isSuccessful) {
            error("Could not send 'chatwork' message: $status ${response.message}\n")
        }
    }

    /**
     * Upload outdated dependencies information [file] with [message] using [token] to [roomId]
     */
    private fun uploadFileWithMessage(): Pair<Response?, Int> {
        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                FIELD_FILE, file.name,
                file.asRequestBody(MEDIA_TYPE_TEXT.toMediaTypeOrNull())
            )
            .addFormDataPart(FIELD_MESSAGE, message)
            .build()
        val headers = mapOf(
            HEADER_X_CHATWORK_TOKEN to token,
            HEADER_CONTENT_TYPE to HEADER_CONTENT_TYPE_MUTIPART
        )
        return doPost(
            headers = headers,
            url = "https://api.chatwork.com/v2/rooms/$roomId/files",
            requestBody = requestBody
        )
    }

    companion object {
        const val HEADER_X_CHATWORK_TOKEN = "X-ChatWorkToken"
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val HEADER_CONTENT_TYPE_MUTIPART = "multipart/form-data"
        const val FIELD_MESSAGE = "message"
        const val FIELD_FILE = FIELD_MESSAGE
        const val MEDIA_TYPE_TEXT = "text/plain"
    }
}
