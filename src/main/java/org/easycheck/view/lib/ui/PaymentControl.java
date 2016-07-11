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
import org.easycheck.lib.Payment;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PaymentControl {

	private final Payment payment;

	public PaymentControl(Payment payment) {
		this.payment = payment;
	}

	public StringProperty idProperty() {
		return new SimpleStringProperty(payment.getId());
	}

	public Integer getNumber() {
		return payment.getNumber();
	}

	public IntegerProperty numberProperty() {
		return new SimpleIntegerProperty(payment.getNumber());
	}

	public Amount getAmount() {
		return payment.getAmount();
	}

	public ObjectProperty<Amount> amountProperty() {
		return new SimpleObjectProperty<>(payment.getAmount());
	}

	public StringProperty timeProperty() {
		return new SimpleStringProperty(ControlUtil.convertTimeToString(payment.getTime()));
	}

	public IntegerProperty paymentWayProperty() {
		return new SimpleIntegerProperty(payment.getPaymentWay());
	}
}
