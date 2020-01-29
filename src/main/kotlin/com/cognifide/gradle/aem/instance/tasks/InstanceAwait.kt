package com.cognifide.gradle.aem.instance.tasks

import com.cognifide.gradle.aem.AemDefaultTask
import com.cognifide.gradle.aem.common.instance.action.AwaitUpAction
import org.gradle.api.tasks.TaskAction

open class InstanceAwait : AemDefaultTask() {

    private var awaitUpOptions: AwaitUpAction.() -> Unit = {}

    fun awaitUp(options: AwaitUpAction.() -> Unit) {
        this.awaitUpOptions = options
    }

    @TaskAction
    fun await() {
        aem.instanceActions.awaitUp(awaitUpOptions)
    }

    init {
        description = "Await for healthy condition of all AEM instances."
    }

    companion object {
        const val NAME = "instanceAwait"
    }
}
