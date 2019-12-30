package dev.reactant.enrichedui.command

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook


import dev.reactant.reactant.extra.command.PicocliCommandService
import dev.reactant.reactant.service.spec.dsl.register


@Component
class EnrichedUICommandRegister(
        private val commandService: PicocliCommandService
) : LifeCycleHook {
    override fun onEnable() {
        register(commandService) {
            command(::EnrichedUICommand) {
                command(::EnrichedUIListFontCommand)
            }
        }
    }
}
