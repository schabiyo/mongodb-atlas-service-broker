/*
 * Copyright 2002-2018 the original author or authors.
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

package com.syolab.broker.atlasbroker.atlas;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UserGenerator {
	private static final String PASSWORD_CHARS =
			"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final int PASSWORD_LENGTH = 12;
	private static final SecureRandom RANDOM = new SecureRandom();



	//Must be an email
	public String generateUsername() {
		return "@syolab.io";
	}

	public String generatePassword() {
		StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
		for (int i = 0; i < PASSWORD_LENGTH; i++) {
			sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
		}
		return sb.toString();
	}
}
