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

public class LiabilityOwner {

	private final int number;
	private final String owner;
	private final String address;

	public LiabilityOwner(int number, String owner, String address) {
		this.number = number;
		this.owner = owner;
		this.address = address;
	}

	public int getNumber() {
		return number;
	}

	public String getOwner() {
		return owner;
	}

	public String getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "LiabilityOwner{" + "number=" + number + ", owner=" + owner + ", address=" + address + '}';
	}
}
