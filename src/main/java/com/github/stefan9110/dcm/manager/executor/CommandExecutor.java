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

package com.github.stefan9110.dcm.manager.executor;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class CommandExecutor implements Executor {
    public abstract void execute(Member member, String[] args, MessageReceivedEvent event);

    /* Cast Event -> GuildMessageReceivedEvent
     * CommandExecutor#onCommand() is only called with GuildMessageReceivedEvent as parameter */
    @Override
    public final void onCommand(Member member, String[] args, Event event) {

        if (!((MessageReceivedEvent) event).isFromGuild()) return;

        execute(member, args, (MessageReceivedEvent) event);

    }
}
