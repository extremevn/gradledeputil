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

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.buffer
import okio.source
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import vn.com.extremevn.gradle.deputil.model.Dependency
import vn.com.extremevn.gradle.deputil.model.DependencyGraph
import vn.com.extremevn.gradle.deputil.model.versionInfo
import java.io.File

/**
 * Define base gradle task which defines common functions for processing dependency graph
 */
abstract class BaseDefaultTask : DefaultTask() {

    companion object {
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    @Input
    var jsonInputPath = "build/dependencyUpdates/report.json"

    @TaskAction
    fun taskAction() {
        val jsonInput = project.file(jsonInputPath)
        val dependencyGraph = readGraphFromJsonFile(jsonInput)
        processDependency(dependencyGraph)
    }

    protected abstract fun processDependency(dependencyGraph: DependencyGraph)

    protected inline fun <reified T> adapter(): JsonAdapter<T> = moshi.adapter(T::class.java)

    /**
     * Parse gradle version information in [graph] and return it as String
     */
    protected fun parseGradleGraph(graph: DependencyGraph): String {
        val gradle = graph.gradle
        return when {
            gradle.current.version > gradle.running.version ->
                "[${gradle.running.version} -> ${gradle.current.version}]"

            gradle.releaseCandidate.version > gradle.running.version ->
                "[${gradle.running.version} -> ${gradle.releaseCandidate.version}]"

            else -> ""
        }
    }

    /**
     * Filter outdated dependencies in [graph] and return it
     */
    protected fun filterOutdatedDependency(graph: DependencyGraph): List<Dependency> {
        val dependencies: List<Dependency> = graph.outdated
        return dependencies.sortedDependencies()
    }

    /**
     * Read and parse all dependency from [jsonInput] file
     */
    private fun readGraphFromJsonFile(jsonInput: File): DependencyGraph {
        return adapter<DependencyGraph>().fromJson(jsonInput.source().buffer())!!
    }

    /**
     * Sort dependencies by it version info
     */
    private fun List<Dependency>.sortedDependencies(): List<Dependency> {
        return this.sortedBy { it.versionInfo() }
    }
}
