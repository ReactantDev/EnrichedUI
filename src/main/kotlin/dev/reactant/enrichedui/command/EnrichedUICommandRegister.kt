package dev.reactant.enrichedui.command

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook


import dev.reactant.reactant.extra.command.PicocliCommandService
import dev.reactant.reactant.service.spec.dsl.register
import dev.reactant.reactant.ui.ReactantUIService


@Component
class EnrichedUICommandRegister(
        private val commandService: PicocliCommandService,
        private val uiService: ReactantUIService
) : LifeCycleHook {
    override fun onEnable() {
        register(commandService) {
            command(::EnrichedUICommand) {
                command(::EnrichedUIListFontCommand)
                command({ EnrichedUIVersionInfoCommand(uiService) })
            }
        }
    }
}
