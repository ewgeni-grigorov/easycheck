/*
 * Copyright 2015 Ewgeni Grigorov
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
package org.easycheck.lib;

public class AmountConstants {
	private AmountConstants() {
		// prevent object instantiation
	}

	// TODO: move them to a constants and add an option for configuration
	public static final Amount AMOUNT_PER_MAN = new Amount("7.00");
	public static final Amount AMOUNT_PER_ROOM = new Amount("10.00");
	public static final Amount AMOUNT_PER_DOG = new Amount("5.00");
	public static final Amount AMOUNT_PER_DOGS = new Amount("10.00");
}
