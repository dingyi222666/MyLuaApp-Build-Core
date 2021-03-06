/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.remote.services;

import org.gradle.internal.id.IdGenerator;
import org.gradle.internal.id.UUIDGenerator;

import org.gradle.internal.remote.internal.inet.InetAddressFactory;


import java.util.UUID;

/**
 * A factory for a set of messaging services. Provides the following services:
 *
 *
 * </ul>
 */
public class MessagingServices {
    private final IdGenerator<UUID> idGenerator = new UUIDGenerator();

    protected InetAddressFactory createInetAddressFactory() {
        return new InetAddressFactory();
    }


}
