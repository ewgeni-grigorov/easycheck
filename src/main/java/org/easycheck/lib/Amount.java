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

import java.math.BigDecimal;
import java.util.Comparator;

public class Amount {

	public static final Comparator<Amount> COMPARATOR;

	public static final Amount ZERO = new Amount();

	private final int amount;

	private BigDecimal amountAsBigDecimal;

	static {
		COMPARATOR = (firstAmount, secondAmount) -> {
			return Integer.compare(firstAmount.amount, secondAmount.amount);
		};
	}

	public Amount() {
		this(0);
	}

	private Amount(int amount) {
		this.amount = amount;
	}

	public Amount(String amount) {
		if (0 == amount.length()) {
			this.amount = 0;
			return;
		}
		int dotIndex = amount.indexOf('.');
		if (-1 == dotIndex) {
			// it's integer
			this.amount = Integer.parseInt(amount) * 100;
			return;
		}
		String integerStr = amount.substring(0, dotIndex);
		int integer = (integerStr.length() == 0) ? 0 : Integer.parseInt(integerStr);
		integer *= 100;
		if (dotIndex < amount.length() - 1) {
			String fractionStr = amount.substring(dotIndex + 1);
			if (fractionStr.length() > 2) {
				throw new IllegalArgumentException("Fraction is not correct: " + amount);
			}
			int fraction = Integer.parseInt(fractionStr);
			if ((fraction < 10) && (fractionStr.charAt(0) != '0')) {
				fraction *= 10;
			}
			integer += fraction;
		}
		this.amount = integer;
	}

	public Amount add(Amount other) {
		return new Amount(this.amount + other.amount);
	}

	public Amount subtract(Amount other) {
		return new Amount(this.amount - other.amount);
	}

	public Amount multiply(BigDecimal multiplier) {
		if (null == amountAsBigDecimal) {
			amountAsBigDecimal = new BigDecimal(amount);
		}
		return new Amount(amountAsBigDecimal.multiply(multiplier).intValue());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + this.amount;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Amount other = (Amount) obj;
		if (this.amount != other.amount) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		int suffix = (amount % 100);
		return Integer.toString(amount / 100) + '.' + ((suffix < 10) ? "0" + suffix : Integer.toString(suffix));
	}
}
