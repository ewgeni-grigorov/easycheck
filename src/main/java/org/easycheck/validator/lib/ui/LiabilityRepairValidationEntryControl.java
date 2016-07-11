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
package org.easycheck.validator.lib.ui;

import org.easycheck.lib.Amount;
import org.easycheck.lib.LiabilityRepair;
import org.easycheck.validator.lib.ValidationEntry;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LiabilityRepairValidationEntryControl {

	private final ValidationEntry<LiabilityRepair> validationEntry;

	public LiabilityRepairValidationEntryControl(ValidationEntry validationEntry) {
		this.validationEntry = validationEntry;
	}

	public String getPriority() {
		return validationEntry.getPriority().toString();
	}

	public StringProperty priorityProperty() {
		return new SimpleStringProperty(getPriority());
	}

	public Integer getNumber() {
		LiabilityRepair liability = validationEntry.getCurrentLiability();
		if (null == liability) {
			liability = validationEntry.getNextLiability();
		}
		return liability.getNumber();
	}

	public IntegerProperty numberProperty() {
		return new SimpleIntegerProperty(getNumber());
	}

	public String getMessage() {
		return validationEntry.getMessage();
	}

	public StringProperty messageProperty() {
		return new SimpleStringProperty(getMessage());
	}

	public Amount getCurrentLiabilityRest() {
		LiabilityRepair currentLiability = validationEntry.getCurrentLiability();
		return (null == currentLiability) ? null : currentLiability.getRestAmount();
	}

	public ObjectProperty<Amount> currentLiabilityRestProperty() {
		return new SimpleObjectProperty<>(getCurrentLiabilityRest());
	}

	public Amount getCurrentPayment() {
		return validationEntry.getCurrentPayment();
	}

	public ObjectProperty<Amount> currentPaymentProperty() {
		return new SimpleObjectProperty<>(getCurrentPayment());
	}

	public Amount getNextLiabilityRest() {
		LiabilityRepair nextLiability = validationEntry.getNextLiability();
		return (null == nextLiability) ? null : nextLiability.getRestAmount();
	}

	public ObjectProperty<Amount> nextLiabilityRestProperty() {
		return new SimpleObjectProperty<>(getNextLiabilityRest());
	}
}
