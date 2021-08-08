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

package com.github.stefan9110.command.exceptions;

public class CommandAlreadyExistsException extends RuntimeException {
    public CommandAlreadyExistsException(String name, String parentCommand) {
        super("The command " + name.toLowerCase() + " already exists in parent command " + parentCommand + ".");
    }

    public CommandAlreadyExistsException(String parentCommand) {
        super("The parent command " + parentCommand.toLowerCase() + " already exists. ");
    }
}
