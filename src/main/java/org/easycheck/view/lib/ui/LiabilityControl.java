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
package org.easycheck.view.lib.ui;

import java.math.BigDecimal;

import org.easycheck.lib.Amount;
import org.easycheck.lib.Liability;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LiabilityControl {

	private final Liability liability;

	public LiabilityControl(Liability liability) {
		this.liability = liability;
	}

	public IntegerProperty numberProperty() {
		return new SimpleIntegerProperty(liability.getNumber());
	}

	public Integer getNumber() {
		return liability.getNumber();
	}

	public BigDecimal getOccupantCount() {
		return liability.getOccupantCount();
	}

	public Amount getTotalAmount() {
		return liability.getTotalAmount();
	}

	public StringProperty ownerProperty() {
		return new SimpleStringProperty(liability.getOwner());
	}

	public SimpleObjectProperty<BigDecimal> countProperty() {
		return new SimpleObjectProperty(liability.getOccupantCount());
	}

	public ObjectProperty<Amount> oldProperty() {
		return new SimpleObjectProperty<>(liability.getOldAmount());
	}

	public ObjectProperty<Amount> currentProperty() {
		return new SimpleObjectProperty<>(liability.getCurrentAmount());
	}

	public ObjectProperty<Amount> totalProperty() {
		return new SimpleObjectProperty<>(liability.getTotalAmount());
	}

	public StringProperty toProperty() {
		return new SimpleStringProperty(ControlUtil.convertDateToString(liability.getEndTime()));
	}
}
