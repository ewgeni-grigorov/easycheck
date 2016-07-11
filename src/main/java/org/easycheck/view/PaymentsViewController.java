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
package org.easycheck.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.easycheck.ResourceController;
import org.easycheck.StagedController;
import org.easycheck.lib.Amount;
import org.easycheck.lib.Parser;
import org.easycheck.lib.Payment;
import org.easycheck.lib.ui.UIUtil;
import org.easycheck.view.lib.ui.PaymentControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class PaymentsViewController implements Initializable, StagedController, ResourceController {

	@FXML
	private TextField filterTextField;
	@FXML
	private Button openButton;
	@FXML
	private Button clearButton;
	@FXML
	private Label totalAmountLabel;
	@FXML
	private TableView<PaymentControl> tableViewPayments;
	@FXML
	private TableColumn<PaymentControl, String> columnId;
	@FXML
	private TableColumn<PaymentControl, Number> columnNumber;
	@FXML
	private TableColumn<PaymentControl, Amount> columnAmount;
	@FXML
	private TableColumn<PaymentControl, String> columnTime;
	@FXML
	private TableColumn<PaymentControl, Number> columnPaymentWay;

	private static final Logger LOG = Logger.getLogger(PaymentsViewController.class.getName());

	private final FileChooser paymentsFileChooser;

	private Stage stage;
	private String defaultTitle;

	private final ObservableList<PaymentControl> payments = FXCollections.observableArrayList();
	private final FilteredList<PaymentControl> filteredPayments = new FilteredList<>(payments, null);

	private Amount total;

	public PaymentsViewController() {
		paymentsFileChooser = new FileChooser();
		paymentsFileChooser.setTitle("Select Payments File");
		paymentsFileChooser.setSelectedExtensionFilter(new ExtensionFilter("Payments Files", "txt"));
	}

	public void setStage(Stage stage) {
		this.stage = stage;
		this.defaultTitle = stage.getTitle();
	}

	@Override
	public void setResource(File resource) {
		open((null == resource) ? null : Arrays.asList(resource));
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		openButton.setOnAction((ActionEvent event) -> {
			List<File> paymentsFiles = paymentsFileChooser.showOpenMultipleDialog(stage);
			open(paymentsFiles);
		});

		clearButton.setOnAction((ActionEvent event) -> {
			clear();
		});

		filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			initTotal();
			Set<String> filters = FilterUtil.prepareFilters(newValue);
			filteredPayments.setPredicate(paymentControl -> {
				boolean result = FilterUtil.match(filters, paymentControl.getNumber().toString());
				if (result) {
					total = total.add(paymentControl.getAmount());
				}
				return result;
			});
			flushTotal();
		});

		SortedList<PaymentControl> sortedPayments = new SortedList<>(filteredPayments);
		sortedPayments.comparatorProperty().bind(tableViewPayments.comparatorProperty());
		tableViewPayments.setItems(sortedPayments);

		columnId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		columnNumber.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		columnAmount.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
		// TODO:rename
		columnAmount.setComparator(Amount.COMPARATOR);
		columnTime.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
		columnPaymentWay.setCellValueFactory(cellData -> cellData.getValue().paymentWayProperty());
	}

	private void open(List<File> paymentsFiles) {
		if (null == paymentsFiles) {
			return;
		}
		clear();
		initTotal();
		StringBuilder title = new StringBuilder();
		paymentsFiles.forEach((File paymentsFile) -> {
			try {
				Set<Payment> currentPayments = Parser.parsePayments(paymentsFile);
				currentPayments.forEach((Payment payment) -> {
					payments.add(new PaymentControl(payment));
					total = total.add(payment.getAmount());
				});
				if (title.length() > 0) {
					title.append(", ");
				}
				title.append(paymentsFile.getName());
			} catch (IOException ioe) {
				LOG.log(Level.WARNING, "The payments files are not correct.", ioe);
			}
		});
		flushTotal();
		stage.setTitle(title.toString());
		UIUtil.sortAscending(tableViewPayments, columnNumber);
	}

	private void clear() {
		payments.clear();
		filterTextField.clear();
		filteredPayments.setPredicate(null); // clear the predicate after the
												// filter text
		totalAmountLabel.setText(null);
		total = null;
		stage.setTitle(defaultTitle);
	}

	private void flushTotal() {
		totalAmountLabel.setText(total == Amount.ZERO ? null : total.toString());
		total = null;
	}

	private void initTotal() {
		total = Amount.ZERO;
	}
}
