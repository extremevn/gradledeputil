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

import org.gradle.api.tasks.Input
import vn.com.extremevn.gradle.deputil.GradleDependencyUtilPlugin.Companion.CHATWORK_ROOMID
import vn.com.extremevn.gradle.deputil.GradleDependencyUtilPlugin.Companion.CHATWORK_TOKEN
import vn.com.extremevn.gradle.deputil.GradleDependencyUtilPlugin.Companion.EXTENSION_NAME
import vn.com.extremevn.gradle.deputil.base.BaseDefaultTask
import vn.com.extremevn.gradle.deputil.model.Dependency
import vn.com.extremevn.gradle.deputil.model.DependencyGraph
import vn.com.extremevn.gradle.deputil.model.versionInfo
import java.io.File
import java.lang.StringBuilder

/**
 * Gradle task process dependencies and send outdated dependencies notification by uploading file and message to chatwork
 */
open class ReportToChatworkTask : BaseDefaultTask() {

    @Input
    lateinit var token: String

    @Input
    var message: String = "Dependencies has new version update"

    @Input
    var roomId: Int = 0

    @Input
    var outdatedReportFile = "build/dependencyUpdates/outdated-report.txt"

    init {
        description = "Create a chatwork message from the outdated dependencies report."
    }

    /**
     * Process [dependencyGraph] and send outdated dependencies notification by uploading file and message to chatwork
     */
    override fun processDependency(dependencyGraph: DependencyGraph) {
        if (token.isBlank() || roomId == 0) {
            error(
                """ChatWork token and roomId not found!
                |Current roomId: $roomId , token: $token
                |Set it by using $EXTENSION_NAME's project extension or System environment variable: $CHATWORK_TOKEN, $CHATWORK_ROOMID""".trimMargin()
            )
        }
        // Build outdated dependencies content
        val dependencies: List<Dependency> = filterOutdatedDependency(dependencyGraph)
        val outDatedDependencies = StringBuilder()

        dependencies.forEach {
            outDatedDependencies.append("- ").appendln(it.versionInfo())
        }
        val gradleVersion = parseGradleGraph(dependencyGraph)
        if (gradleVersion.isNotBlank()) {
            outDatedDependencies.appendln().append("*Gradle updates:* ").appendln().appendln().appendln(gradleVersion)
        }
        if (outDatedDependencies.isBlank()) {
            return
        }
        // Write to file
        File(outdatedReportFile).writeText(outDatedDependencies.toString())
        // Run client upload and send message to chatwork
        ChatworkClient(
            token,
            roomId,
            project.file(outdatedReportFile),
            message
        ).run()
    }
}
