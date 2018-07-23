package com.cognifide.gradle.aem.instance

import com.cognifide.gradle.aem.api.AemDefaultTask
import com.cognifide.gradle.aem.instance.action.DownAction
import org.gradle.api.tasks.TaskAction

open class DownTask : AemDefaultTask() {

    companion object {
        val NAME = "aemDown"
    }

    init {
        description = "Turns off local AEM instance(s)."
    }

    @TaskAction
    fun down() {
        val instances = Instance.filter(project)

        DownAction(project, instances).perform()
    }

}