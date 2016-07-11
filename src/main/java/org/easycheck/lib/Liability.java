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

public class Liability extends LiabilityOwner {

	private final BigDecimal occupantCount;
	private final long startTime;
	private final long endTime;
	private final Amount oldAmount;
	private final Amount currentAmount;
	private final Amount totalAmount;

	public Liability(int number, String owner, String address, BigDecimal occupantCount, long startTime, long endTime,
			Amount oldAmount, Amount currentAmount, Amount totalAmount) {
		super(number, owner, address);
		this.occupantCount = occupantCount;
		this.startTime = startTime;
		this.endTime = endTime;
		this.oldAmount = oldAmount;
		this.currentAmount = currentAmount;
		this.totalAmount = totalAmount;
	}

	public BigDecimal getOccupantCount() {
		return occupantCount;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public Amount getOldAmount() {
		return oldAmount;
	}

	public Amount getCurrentAmount() {
		return currentAmount;
	}

	public Amount getTotalAmount() {
		return totalAmount;
	}

	@Override
	public String toString() {
		return super.toString() + "\nLiability{" + "occupantCount=" + occupantCount + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", oldAmount=" + oldAmount + ", currentAmount=" + currentAmount
				+ ", totalAmount=" + totalAmount + '}';
	}
}
