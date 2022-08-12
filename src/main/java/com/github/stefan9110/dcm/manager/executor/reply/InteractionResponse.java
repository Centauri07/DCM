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

package com.github.stefan9110.dcm.manager.executor.reply;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.github.stefan9110.dcm.manager.executor.reply.InteractionResponse.ResponseType.*;

public class InteractionResponse {
    public enum ResponseType {
        STRING, MESSAGE, EMBED, MODAL, DEFFER
    }

    private String stringResponse;
    private Message messageResponse;
    private List<MessageEmbed> embedResponse;
    private Modal modalResponse;
    private boolean ephemeral = false;

    @NotNull
    private final ResponseType responseType;

    private InteractionResponse(String response) {
        this.stringResponse = response;
        responseType = STRING;
    }

    private InteractionResponse(Message response) {
        messageResponse = response;
        responseType = MESSAGE;
    }

    private InteractionResponse(MessageEmbed... embeds) {
        embedResponse = Arrays.asList(embeds);
        responseType = EMBED;
    }

    private InteractionResponse(Modal modal) {
        modalResponse = modal;
        responseType = MODAL;
    }

    private InteractionResponse() {
        responseType = DEFFER;
    }

    public InteractionResponse setEphemeral() {
        ephemeral = true;
        return this;
    }

    public static InteractionResponse of(String str) {
        return new InteractionResponse(str);
    }

    public static InteractionResponse of(Message message) {
        return new InteractionResponse(message);
    }

    public static InteractionResponse of(MessageEmbed... embeds) {
        return new InteractionResponse(embeds);
    }

    public static InteractionResponse of(Modal modal) {
        return new InteractionResponse(modal);
    }

    public static InteractionResponse deferInteraction() {
        return new InteractionResponse();
    }

    public String getStringResponse() {
        return stringResponse;
    }

    public List<MessageEmbed> getEmbedResponse() {
        return embedResponse;
    }

    public Message getMessageResponse() {
        return messageResponse;
    }

    public Modal getModalResponse() {
        return modalResponse;
    }

    public @NotNull ResponseType getResponseType() {
        return responseType;
    }

    public boolean isEphemeral() {
        return ephemeral;
    }
}
