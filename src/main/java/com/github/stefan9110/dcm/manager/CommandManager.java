/*
 * Copyright 2021 Stefan9110
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.stefan9110.dcm.manager;

import com.github.stefan9110.dcm.command.Command;
import com.github.stefan9110.dcm.command.ParentCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
    private Guild registeredGuild;
    private final String commandPrefix;

    public CommandManager(JDA jda, String commandPrefix) {
        jda.addEventListener(this);
        this.commandPrefix = commandPrefix;
    }

    /* Message method of calling a command through the commandPrefix String */
    @SubscribeEvent
    public void onMessageReceived(@Nonnull MessageReceivedEvent e) {
        // If the command is not from the guild we don't want to run the command
        if (!e.isFromGuild()) return;
        // If the command is not called in the registered guild we don't want to run the command.
        if (registeredGuild != null && !registeredGuild.getId().equals(e.getGuild().getId())) return;
        // If the member is null (mostly WebHook cases) or the member is a bot we don't want to run the command.
        if (e.getMember() == null || e.getMember().getUser().isBot()) return;

        // Checking if the call message starts with the command prefix in order to differentiate between normal message and command calls.
        if (!e.getMessage().getContentDisplay().toLowerCase().startsWith(commandPrefix)) return;

        // Building the command hierarchy from the initial message
        String message = e.getMessage().getContentDisplay().substring(commandPrefix.length());

        // Special case: if the call message only contained the command prefix we do not validate the call.
        if (message.equals("")) return;

        String[] messageFormatted = message.split(" ");

        // Calling the Executor of the command
        ParentCommand cmd = ParentCommand.getParentIncludingAliases(messageFormatted[0]);
        /*
            Make sure that the command with the name identifier given exists and checking if the command is not a SlashCommand type.
            We are treating message-called commands and slash-commands separately for the time being, it is possible that in the future
            we will make all slash-commands accessible through legacy message calls.
         */
        if (cmd != null && !cmd.isSlashCommand())
            cmd.execute(e.getMember(), (messageFormatted.length == 1 ? new String[0] : Arrays.copyOfRange(messageFormatted, 1, messageFormatted.length)), e);

    }

    /* SlashCommand implementation method of calling a command */
    @SubscribeEvent
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent e) {
        // If the command is not called in the registered guild we don't want to run the command.
        if (e.getGuild() == null || (registeredGuild != null && !registeredGuild.getId().equals(e.getGuild().getId())))
            return;

        // Registering all the arguments from the SlashCommand implementation
        List<String> args = new ArrayList<>();
        args.add(e.getSubcommandGroup());
        args.add(e.getSubcommandName());
        e.getOptions().forEach(option -> args.add(option.getAsString()));

        // Calling the top of the hierarchy ParentCommand found at the SlashCommand name with the build arguments.
        ParentCommand.getParentCommand(e.getName().toLowerCase()).execute(e.getMember(),
                !args.isEmpty() ? args.toArray(new String[0]) : new String[0], e);

    }

    // Method used to obtain the SlashCommand implementation data from a given ParentCommand
    private static SlashCommandData getCommandData(ParentCommand parent) {
        SlashCommandData cmdData = Commands.slash(parent.getName(), parent.getDescription() == null ? parent.getName() : parent.getDescription());

        cmdData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(parent.getRequiredPermission()));

        // If the command doesn't have any sub-commands we will add its own CommandArgument data in the SlashCommand implementation
        if (parent.getSubCommands().isEmpty()) {
            parent.getArguments().forEach(arg -> cmdData.addOption(arg.getType(), arg.getName(), arg.getDescription(), arg.isRequired()));
            return cmdData;
        }

        // Obtaining the SlashCommand implementation data from the sub-commands of the ParentCommand given.
        List<SubcommandData> subCommandData = new ArrayList<>();
        List<SubcommandGroupData> subcommandGroupData = new ArrayList<>();
        for (Command sb : parent.getSubCommands().values()) {
            if (sb instanceof ParentCommand) {
                SubcommandGroupData sbgData = new SubcommandGroupData(sb.getName(), sb.getDescription());

                ((ParentCommand) sb).getSubCommands().forEach((name, command) -> {
                    SubcommandData sbData = new SubcommandData(command.getName(), command.getDescription());
                    command.getArguments().forEach(arg -> sbData.addOption(arg.getType(), arg.getName(), arg.getDescription(), arg.isRequired()));
                    sbgData.addSubcommands(sbData);
                });

                subcommandGroupData.add(sbgData);
            } else {
                SubcommandData sbData = new SubcommandData(sb.getName(), sb.getDescription());
                sb.getArguments().forEach(arg -> sbData.addOption(arg.getType(), arg.getName(), arg.getDescription(), arg.isRequired()));
                subCommandData.add(sbData);
            }
        }

        // Adding all the sub-command data to the main CommandData block.
        cmdData.addSubcommands(subCommandData);
        cmdData.addSubcommandGroups(subcommandGroupData);

        return cmdData;
    }

    public void setRegisteredGuild(Guild guild) {
        registeredGuild = guild;
    }

    /**
     * Method used to obtain all the SlashCommand implementation CommandData of the
     * top of the hierarchy ParentCommands registered in the cache.
     *
     * @return List of CommandData to be sent to the Discord API through JDA.
     */
    public List<CommandData> getSlashCommands() {
        List<CommandData> slashCommandsList = new ArrayList<>();
        ParentCommand.getParentCommands().forEach(cmd -> {
            if (cmd.isSlashCommand()) slashCommandsList.add(getCommandData(cmd));
        });
        return slashCommandsList;
    }
}
