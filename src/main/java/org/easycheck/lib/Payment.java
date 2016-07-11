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

public class Payment {

	private final String id;
	private final int number;
	private final Amount amount;
	private final long time;
	private final int paymentWay;

	public Payment(String id, int number, Amount amount, long time, int paymentWay) {
		this.id = id;
		this.number = number;
		this.amount = amount;
		this.time = time;
		this.paymentWay = paymentWay;
	}

	public String getId() {
		return id;
	}

	public int getNumber() {
		return number;
	}

	public Amount getAmount() {
		return amount;
	}

	public long getTime() {
		return time;
	}

	public int getPaymentWay() {
		return paymentWay;
	}

	@Override
	public String toString() {
		return "Payment{" + "id=" + id + ", number=" + number + ", amount=" + amount + ", time=" + time
				+ ", paymentWay=" + paymentWay + '}';
	}

}
