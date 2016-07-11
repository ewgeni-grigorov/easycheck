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

import org.easycheck.lib.Amount;
import org.easycheck.lib.LiabilityRepair;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LiabilityRepairControl {

	private final LiabilityRepair liabilityRepair;

	public LiabilityRepairControl(LiabilityRepair liabilityRepair) {
		this.liabilityRepair = liabilityRepair;
	}

	public IntegerProperty numberProperty() {
		return new SimpleIntegerProperty(liabilityRepair.getNumber());
	}

	public Integer getNumber() {
		return liabilityRepair.getNumber();
	}

	public StringProperty ownerProperty() {
		return new SimpleStringProperty(liabilityRepair.getOwner());
	}

	public ObjectProperty<Amount> totalProperty() {
		return new SimpleObjectProperty<>(liabilityRepair.getTotalAmount());
	}

	public ObjectProperty<Amount> paidProperty() {
		return new SimpleObjectProperty<>(liabilityRepair.getPaidAmount());
	}

	public ObjectProperty<Amount> restProperty() {
		return new SimpleObjectProperty<>(liabilityRepair.getRestAmount());
	}

	public StringProperty fromProperty() {
		return new SimpleStringProperty(ControlUtil.convertDateToString(liabilityRepair.getStartTime()));
	}

	public StringProperty toProperty() {
		return new SimpleStringProperty(ControlUtil.convertDateToString(liabilityRepair.getEndTime()));
	}
}
