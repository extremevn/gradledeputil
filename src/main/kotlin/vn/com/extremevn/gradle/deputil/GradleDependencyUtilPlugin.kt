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

package vn.com.extremevn.gradle.deputil

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import vn.com.extremevn.gradle.deputil.chatwork.ReportToChatworkTask

/**
 * Plugin implementation class
 * It register gradle tasks and extension for using in project which apply this plugin
 */
class GradleDependencyUtilPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val dependencyUpdatesTask = configDependencyUpdateTask(project)

        val jsonFile =
            "${dependencyUpdatesTask.outputDir}/${dependencyUpdatesTask.reportfileName}.json"
        val outdatedFile =
            "${dependencyUpdatesTask.outputDir}/outdated-${dependencyUpdatesTask.reportfileName}.txt"

        with(project) {
            val ext = registerPluginExtension()

            registerPluginTask(jsonFile, outdatedFile)

            afterEvaluate {
                configTaskProperty(ext)
            }
        }
    }

    /**
     * Read config value from plugin extension [ext] or system environment variable into plugin task properties
     */
    private fun Project.configTaskProperty(ext: GradleDependencyUtil) {
        val chatworkMessage =
            tasks.getByName(CHATWORK_TASK_NAME) as ReportToChatworkTask
        chatworkMessage.token = if (ext.chatworkToken.isBlank()) {
            System.getenv(CHATWORK_TOKEN) ?: ""
        } else {
            ext.chatworkToken
        }
        chatworkMessage.roomId = if (ext.chatworkRoomId == 0) {
            System.getenv(CHATWORK_ROOMID)?.toIntOrNull() ?: 0
        } else {
            ext.chatworkRoomId
        }
        chatworkMessage.message = if (ext.chatworkDefaultMessage.isBlank()) {
            System.getenv(CHATWORK_DEFAULT_MESSAGE) ?: ""
        } else {
            ext.chatworkDefaultMessage
        }
    }
    /**
     * Register plugin task whose input is [jsonFile], output is [outdatedFile] and add into project's gradle tasks
     */
    private fun Project.registerPluginTask(jsonFile: String, outdatedFile: String) {
        tasks.register(CHATWORK_TASK_NAME, ReportToChatworkTask::class.java) {
            dependsOn(GRADLE_PLUGIN_VERSION_TASK_NAME)
            jsonInputPath = jsonFile
            outdatedReportFile = outdatedFile
            group = TASK_GROUP
        }
        logger.info("Finished creating $EXTENSION_NAME tasks")
    }

    /**
     * Register plugin extension for configuration in gradle scripts
     */
    private fun Project.registerPluginExtension(): GradleDependencyUtil {
        val ext = extensions.create(EXTENSION_NAME, GradleDependencyUtil::class.java)
        logger.info("Creating $EXTENSION_NAME tasks...")
        return ext
    }

    /**
     * Create and config 'dependencyUpdates' task for how dependency version is accepted or rejected
     * and then write result to version report file in json format
     */
    private fun configDependencyUpdateTask(project: Project): DependencyUpdatesTask {
        val dependencyUpdatesTask: DependencyUpdatesTask =
            project.tasks.maybeCreate("dependencyUpdates") as DependencyUpdatesTask
        with(dependencyUpdatesTask) {
            outputFormatter = "json"
            revision = "release"
            checkForGradleUpdate = true
            resolutionStrategy {
                componentSelection {
                    all {
                        val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview")
                            .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                            .any { it.matches(candidate.version) }
                        if (rejected) {
                            reject("Release candidate")
                            logger.info("reject release candidate ${candidate.displayName} ${candidate.version}")
                        }
                        val acceptVersion = Regex(pattern = "^[0-9,.v-]+$")
                        rejectVersionIf {
                            val reject = !acceptVersion.matches(candidate.version)
                            logger.info("reject note accept version ${candidate.displayName} ${candidate.version}")
                            reject
                        }
                    }
                }
            }
        }
        return dependencyUpdatesTask
    }

    companion object {
        private const val TASK_GROUP = "dependencyUtil"
        private const val CHATWORK_TASK_NAME = "reportToChatwork"
        private const val GRADLE_PLUGIN_VERSION_TASK_NAME = ":dependencyUpdates"
        const val EXTENSION_NAME = "dependencyUtil"
        const val CHATWORK_TOKEN = "CHATWORK_TOKEN"
        const val CHATWORK_ROOMID = "CHATWORK_ROOMID"
        private const val CHATWORK_DEFAULT_MESSAGE = "CHATWORK_DEFAULT_MESSAGE"
    }
}
