package com.cognifide.gradle.aem.common.instance.action

import com.cognifide.gradle.aem.AemExtension
import com.cognifide.gradle.aem.common.instance.check.*
import com.cognifide.gradle.aem.common.instance.names
import java.util.concurrent.TimeUnit

/**
 * Awaits for unavailable local instances.
 */
class AwaitDownAction(aem: AemExtension) : LocalInstanceAction(aem) {

    private var timeoutOptions: TimeoutCheck.() -> Unit = {
        stateTime = aem.prop.long("instance.awaitDown.timeout.stateTime")
                ?: TimeUnit.MINUTES.toMillis(2)
        constantTime = aem.prop.long("instance.awaitDown.timeout.constantTime")
                ?: TimeUnit.MINUTES.toMillis(10)
    }

    fun timeout(options: TimeoutCheck.() -> Unit) {
        timeoutOptions = options
    }

    private var unavailableOptions: UnavailableCheck.() -> Unit = {
        utilisationTime = aem.prop.long("instance.awaitDown.unavailable.utilizationTime")
                ?: TimeUnit.SECONDS.toMillis(10)
    }

    fun unavailable(options: UnavailableCheck.() -> Unit) {
        unavailableOptions = options
    }

    private var unchangedOptions: UnchangedCheck.() -> Unit = {
        awaitTime = aem.prop.long("instance.awaitDown.unchanged.awaitTime")
                ?: TimeUnit.SECONDS.toMillis(3)
    }

    fun unchanged(options: UnchangedCheck.() -> Unit) {
        unchangedOptions = options
    }

    private val runner = CheckRunner(aem).apply {
        delay = aem.prop.long("instance.awaitDown.delay") ?: TimeUnit.SECONDS.toMillis(1)
        verbose = aem.prop.boolean("instance.awaitDown.verbose") ?: true

        checks {
            listOf(
                    timeout(timeoutOptions),
                    unavailable(unavailableOptions),
                    unchanged(unchangedOptions)
            )
        }
    }

    override fun perform() {
        if (!enabled) {
            return
        }

        if (instances.isEmpty()) {
            logger.info("No instances to await down.")
            return
        }

        logger.info("Awaiting instance(s) down: ${instances.names}")

        runner.check(instances)
    }
}
