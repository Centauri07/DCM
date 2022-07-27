package com.github.stefan9110;

import com.github.stefan9110.dcm.CommandManagerAPI;
import com.github.stefan9110.dcm.builder.CommandBuilder;
import com.github.stefan9110.dcm.manager.executor.SlashExecutor;
import com.github.stefan9110.dcm.manager.executor.reply.InteractionResponse;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Objects;

/**
 * @author Centauri07
 */
public class Application {

    private static JDA jda;
    private static CommandManagerAPI commandManagerAPI;

    public static void main(String[] args) {

        try {
            jda = JDABuilder.createDefault("OTQ0MTYwNTI2NDUwMTY3ODA4.G7f9rV.aqBGPhp4pF4M6wwN3WKJJ8mm6AnIGCtOMKvGdw")
                    .setEnabledIntents(EnumSet.allOf(GatewayIntent.class)).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        commandManagerAPI = CommandManagerAPI.registerAPI(jda, "!");

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        commandManagerAPI.setRequiredGuild(Objects.requireNonNull(jda.getGuildById(897117221418045501L)));

        commandManagerAPI.registerCommand(
                CommandBuilder
                        .create("command")
                        .setDescription("some command")
                        .setCommandExecutor(
                                new SlashExecutor() {
                                    @Override
                                    public @NotNull InteractionResponse reply(Member member, String[] args, SlashCommandInteractionEvent event) {
                                        return InteractionResponse.of("Hello there!");
                                    }
                                }
                        ).build(true)
        ).updateSlashCommands(Objects.requireNonNull(jda.getGuildById(897117221418045501L)));

    }

}
