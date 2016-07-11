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
package org.easycheck.validator.lib;

import org.easycheck.lib.Amount;

public class ValidationEntry<T> {

	public enum Priority {
		CRITICAL, IMPORTANT, INFO
	}

	private final Priority priority;
	private final String message;
	private final T currentLiability;
	private final T nextLiability;
	private final Amount currentPayment;

	public ValidationEntry(Priority priority, String message, T currentLiability, T nextLiability,
			Amount currentPayment) {
		this.priority = priority;
		this.message = message;
		this.currentLiability = currentLiability;
		this.nextLiability = nextLiability;
		this.currentPayment = currentPayment;
	}

	public Priority getPriority() {
		return priority;
	}

	public String getMessage() {
		return message;
	}

	public T getCurrentLiability() {
		return currentLiability;
	}

	public T getNextLiability() {
		return nextLiability;
	}

	public Amount getCurrentPayment() {
		return currentPayment;
	}
}
