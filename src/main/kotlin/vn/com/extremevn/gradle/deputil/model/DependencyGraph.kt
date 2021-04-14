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

package vn.com.extremevn.gradle.deputil.model

/**
 * Dependency graph model in json data which is generated after dependencyUpdates task completed
 */
data class DependencyGraph(
    val gradle: GradleConfig,
    val current: Dependencies,
    val exceeded: Dependencies,
    val outdated: Dependencies,
    val unresolved: Dependencies,
    val count: Int = 0
)

/**
 * Function return current version and new version
 */
fun Dependency.versionInfo(): String {
    return when {
        available?.milestone.isNullOrBlank().not() ->
            "$group:$name:[$version -> ${available?.milestone}]"

        else ->
            "$group:$name:[$version -> ${available?.release ?: ""}]"
    }
}

/**
 * Dependencies model in json data which is generated after dependencyUpdates task completed
 */
data class Dependencies(
    val dependencies: List<Dependency> = emptyList(),
    val count: Int = 0
) : List<Dependency> by dependencies

/**
 * Dependency model in json data which is generated after dependencyUpdates task completed
 */
data class Dependency(
    val group: String = "",
    val version: String = "",
    val reason: String? = "",
    var latest: String? = "",
    val projectUrl: String? = "",
    val name: String = "",
    val available: AvailableDependency? = null
)

/**
 * Gradle Config model in json data which is generated after dependencyUpdates task completed
 */
data class GradleConfig(
    val current: GradleVersion,
    val nightly: GradleVersion,
    val enabled: Boolean = false,
    val releaseCandidate: GradleVersion,
    val running: GradleVersion
)

/**
 * Gradle Version model in json data which is generated after dependencyUpdates task completed
 */
data class GradleVersion(
    val version: String = "",
    val reason: String = "",
    val isUpdateAvailable: Boolean = false,
    val isFailure: Boolean = false
)

/**
 * Available Dependency model in json data which is generated after dependencyUpdates task completed
 */
data class AvailableDependency(
    val release: String? = "",
    val milestone: String? = "",
    val integration: String? = ""
)
