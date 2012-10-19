/**
 * Copyright 2012 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasySendException extends RuntimeException {

    private static Logger log = LoggerFactory.getLogger(EasySendException.class);

    public EasySendException() {
    }

    public EasySendException(String s) {
        super(s);
        log.error(s);
    }

    public EasySendException(String s, Throwable throwable) {
        super(s, throwable);
        log.error(s);
    }

    public EasySendException(Throwable throwable) {
        super(throwable);
        log.error("Error on EasySend", throwable);
    }
}