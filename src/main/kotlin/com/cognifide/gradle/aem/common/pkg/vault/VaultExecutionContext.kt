package com.cognifide.gradle.aem.common.pkg.vault

import org.apache.commons.cli2.CommandLine
import org.apache.jackrabbit.vault.cli.VltExecutionContext
import org.apache.jackrabbit.vault.util.console.CliCommand

class VaultExecutionContext(app: VaultApp) : VltExecutionContext(app) {

    @Suppress("TooGenericExceptionCaught")
    override fun execute(commandLine: CommandLine): Boolean {
        commands.filterIsInstance<CliCommand>().forEach { command ->
            try {
                if (doExecute(command, commandLine)) {
                    return true
                }
            } catch (e: Exception) {
                throw VaultException("Error while executing command: $command")
            }
        }

        return false
    }
}
