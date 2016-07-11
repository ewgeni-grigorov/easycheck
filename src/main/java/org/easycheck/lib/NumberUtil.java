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

public class NumberUtil {

	private NumberUtil() {
	}

	public static boolean isValidNumber(int number) {
		return isInRange(number, 600000, 699999);
	}

	public static boolean isRoomNumber(int number) {
		return isInRange(number, 620000, 620999);
	}

	public static boolean isManNumber(int number) {
		return isInRange(number, 600000, 600999);
	}

	public static boolean isRepairNumber(int number) {
		return !isRoomNumber(number) && !(isManNumber(number));
	}

	private static boolean isInRange(int number, int min, int max) {
		return (number >= min) && (number <= max);
	}
}
