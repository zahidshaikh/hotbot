package me.aberrantfox.aegeus.commandframework.commands

import me.aberrantfox.aegeus.commandframework.ArgumentType
import me.aberrantfox.aegeus.commandframework.Command
import me.aberrantfox.aegeus.commandframework.CommandEvent
import me.aberrantfox.aegeus.extensions.sendPrivateMessage
import me.aberrantfox.aegeus.services.CommandDescriptor
import me.aberrantfox.aegeus.services.Configuration
import me.aberrantfox.aegeus.services.HelpConf
import me.aberrantfox.aegeus.services.SelectionArgument
import net.dv8tion.jda.core.EmbedBuilder
import java.awt.Color
import java.time.LocalDateTime

@Command(ArgumentType.Manual)
fun help(event: CommandEvent) {
    val (args, config, _, _, author) = event

    if(args.isEmpty()) {
        author.sendPrivateMessage(getZeroArgMessage(config))
    } else if (args.size == 1) {
        val selection = args[0] as String
        val argType = HelpConf.fetchArgumentType(selection)

        when (argType) {
            SelectionArgument.CommandName -> {
                val descriptor = HelpConf.fetchCommandDescriptor(selection) ?: return
                author.sendPrivateMessage(buildCommandHelpMessage(config, descriptor))
            }
            SelectionArgument.CategoryName -> {
                val categories = HelpConf.fetchCommandsInCategory(selection)
                author.sendPrivateMessage(buildCategoryDescription(selection.toLowerCase(), categories))
            }
            else -> author.sendPrivateMessage("Not a command or category... maybe try the default help command?")
        }
    } else {
        author.sendPrivateMessage("Uhh... this command takes either 0 or 1 arguments.")
    }
}

private fun buildCommandHelpMessage(config: Configuration, descriptor: CommandDescriptor) =
        EmbedBuilder().setTitle("${descriptor.category} - ${descriptor.name}")
                .setDescription(descriptor.description)
                .setColor(Color.CYAN)
                .addField("Argument structure",
                        if(descriptor.structure.isNullOrBlank()) "This command takes no arguments" else descriptor.structure,
                        false)
                .addField("Example Command",
                        "${config.prefix}${descriptor.name} ${descriptor.example}",
                        false)
                .build()

private fun buildCategoryDescription(name: String, commands: String) =
        EmbedBuilder().setTitle("Category $name Overview")
                .setColor(Color.cyan)
                .setDescription(HelpConf.configuration.categoryDescriptions[name])
                .addField("Commands In this Category", commands, false)
                .build()

private fun getZeroArgMessage(config: Configuration) =
        EmbedBuilder().setTitle("HotBot Help Menu")
                .setColor(Color.MAGENTA)
                .setDescription("This is the help menu. Use ${config.prefix}help <Command | Category>." +
                        "If you want to know what commands you have access to, use ${config.prefix}listAvailable")
                .setFooter("Bot by Fox, made with Kotlin", "http://i.imgur.com/SJPggeJ.png")
                .setThumbnail("http://i.imgur.com/DFoaG7k.png")
                .setTimestamp(LocalDateTime.now())
                .addField("Currently Available Categories",
                        HelpConf.fetchCategories(),
                        false)
                .build()