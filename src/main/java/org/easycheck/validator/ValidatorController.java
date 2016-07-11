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
package org.easycheck.validator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.easycheck.FXMLLoaderUtil;
import org.easycheck.StagedController;
import org.easycheck.exporter.Exporter;
import org.easycheck.lib.Amount;
import org.easycheck.lib.Liability;
import org.easycheck.lib.LiabilityRepair;
import org.easycheck.lib.LiabilityReport;
import org.easycheck.lib.Parser;
import org.easycheck.lib.Payment;
import org.easycheck.lib.ui.UIUtil;
import org.easycheck.validator.lib.ValidationEntry;
import org.easycheck.validator.lib.Validator;
import org.easycheck.validator.lib.ui.LiabilityRepairValidationEntryControl;
import org.easycheck.validator.lib.ui.LiabilityValidationEntryControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class ValidatorController implements Initializable, StagedController {

	@FXML
	private Button clearButton;
	@FXML
	private Button validateButton;
	@FXML
	private Button saveButton;
	@FXML
	private Button currentPaymentsButton;
	@FXML
	private Button currentPaymentsViewButton;
	@FXML
	private Label currentPaymentsLabel;
	@FXML
	private Button currentLiabilitiesButton;
	@FXML
	private Button currentLiabilitiesViewButton;
	@FXML
	private Label currentLiabilitiesLabel;
	@FXML
	private Button nextLiabilitiesButton;
	@FXML
	private Button nextLiabilitiesViewButton;
	@FXML
	private Label nextLiabilitiesLabel;
	@FXML
	private TabPane tabPane;
	@FXML
	private Tab liabilitiesTab;
	@FXML
	private Tab repairsTab;
	@FXML
	private TableView<LiabilityValidationEntryControl> validationTableView;
	@FXML
	private TableColumn<LiabilityValidationEntryControl, String> priorityColumn;
	@FXML
	private TableColumn<LiabilityValidationEntryControl, Number> numberColumn;
	@FXML
	private TableColumn<LiabilityValidationEntryControl, String> messageColumn;
	@FXML
	private TableColumn<LiabilityValidationEntryControl, Amount> currentLiabilityColumn;
	@FXML
	private TableColumn<LiabilityValidationEntryControl, Amount> currentPaymentColumn;
	@FXML
	private TableColumn<LiabilityValidationEntryControl, BigDecimal> countColumn;
	@FXML
	private TableColumn<LiabilityValidationEntryControl, Amount> nextLiabilityColumn;

	@FXML
	private TableView<LiabilityRepairValidationEntryControl> validationRepairTableView;
	@FXML
	private TableColumn<LiabilityRepairValidationEntryControl, String> priorityRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairValidationEntryControl, Number> numberRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairValidationEntryControl, Amount> restRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairValidationEntryControl, Amount> paymentRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairValidationEntryControl, Amount> nextRestRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairValidationEntryControl, String> messageRepairColumn;

	private final ObservableList<LiabilityValidationEntryControl> entries = FXCollections.observableArrayList();
	private final FilteredList<LiabilityValidationEntryControl> filteredEntries = new FilteredList<>(entries, null);

	private final ObservableList<LiabilityRepairValidationEntryControl> repairEntries = FXCollections
			.observableArrayList();
	private final FilteredList<LiabilityRepairValidationEntryControl> repairFilteredEntries = new FilteredList<>(
			repairEntries, null);

	private final FileChooser fileChooser;

	private Stage stage;
	private File currentPaymentsFile;
	private File currentLiabilitiesFile;
	private File nextLiabilitiesFile;

	private List<ValidationEntry<LiabilityRepair>> entryRepairList;
	private List<ValidationEntry<Liability>> entryList;

	public ValidatorController() {
		fileChooser = new FileChooser();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		currentPaymentsButton.setOnAction((ActionEvent event) -> {
			fileChooser.setTitle("Current Payments");
			fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Payments File", "txt"));
			File file = fileChooser.showOpenDialog(stage);
			if (null == file) {
				return;
			}
			currentPaymentsLabel.setText(file.getName());
			currentPaymentsFile = file;
			currentPaymentsViewButton.disableProperty().set(false);
			enableValidation();
		});

		currentPaymentsViewButton.setOnAction(actionEvent -> {
			try {
				FXMLLoaderUtil.load("/view/PaymentsView.fxml", "Current Payments View", null, currentPaymentsFile);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
			}
		});
		currentPaymentsViewButton.disableProperty().set(true);

		currentLiabilitiesButton.setOnAction((ActionEvent event) -> {
			fileChooser.setTitle("Current Liabilities");
			// TODO
			// fileChooser.setSelectedExtensionFilter(new
			// ExtensionFilter("Payments File", "txt"));
			File file = fileChooser.showOpenDialog(stage);
			if (null == file) {
				return;
			}
			currentLiabilitiesLabel.setText(file.getName());
			currentLiabilitiesFile = file;
			currentLiabilitiesViewButton.disableProperty().set(false);
			enableValidation();
		});

		currentLiabilitiesViewButton.setOnAction(actionEvent -> {
			try {
				FXMLLoaderUtil.load("/view/LiabilityView.fxml", "Current Liabilities View", null,
						currentLiabilitiesFile);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
			}
		});
		currentLiabilitiesViewButton.disableProperty().set(true);

		nextLiabilitiesButton.setOnAction((ActionEvent event) -> {
			fileChooser.setTitle("Next Liabilities");
			// TODO
			// fileChooser.setSelectedExtensionFilter(new
			// ExtensionFilter("Payments File", "txt"));
			File file = fileChooser.showOpenDialog(stage);
			if (null == file) {
				return;
			}
			nextLiabilitiesLabel.setText(file.getName());
			nextLiabilitiesFile = file;
			nextLiabilitiesViewButton.disableProperty().set(false);
			enableValidation();
		});
		nextLiabilitiesViewButton.setOnAction(actionEvent -> {
			try {
				FXMLLoaderUtil.load("/view/LiabilityView.fxml", "Next Liabilities View", null, nextLiabilitiesFile);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
			}
		});
		nextLiabilitiesViewButton.disableProperty().set(true);

		validateButton.setOnAction((ActionEvent event) -> {
			clearTable();
			try {
				Set<Payment> payments = Parser.parsePayments(currentPaymentsFile);
				LiabilityReport currentLiabilityReport = Parser.parseLiabilities(currentLiabilitiesFile);
				Set<Liability> currentLiabilities = currentLiabilityReport.getLiabilities();
				LiabilityReport nextLiabilityReport = Parser.parseLiabilities(nextLiabilitiesFile);
				Set<Liability> nextLiabilities = nextLiabilityReport.getLiabilities();
				entryList = Validator.validate(payments, currentLiabilities, nextLiabilities);
				entryList.forEach((validationEntry) -> {
					entries.add(new LiabilityValidationEntryControl(validationEntry));
				});

				Set<LiabilityRepair> currentRepairLiabilities = currentLiabilityReport.getLiabilityRepairs();
				Set<LiabilityRepair> nextRepairLiabilities = nextLiabilityReport.getLiabilityRepairs();
				entryRepairList = Validator.validateRepairs(payments, currentRepairLiabilities, nextRepairLiabilities);
				entryRepairList.forEach((validationEntry) -> {
					repairEntries.add(new LiabilityRepairValidationEntryControl(validationEntry));
				});
				UIUtil.sortAscending(validationTableView, priorityColumn);
				UIUtil.sortAscending(validationTableView, numberColumn);
				UIUtil.sortAscending(validationRepairTableView, priorityRepairColumn);
				UIUtil.sortAscending(validationRepairTableView, numberRepairColumn);
				saveButton.disableProperty().set(false);
			} catch (IOException ioe) {
				LOG.log(Level.SEVERE, null, ioe);
				clearTable();
			}
		});

		clearButton.setOnAction((ActionEvent event) -> {
			clearTable();
			currentLiabilitiesLabel.setText("no file");
			currentLiabilitiesFile = null;
			currentLiabilitiesViewButton.disableProperty().set(true);
			currentPaymentsLabel.setText("no file");
			currentPaymentsFile = null;
			currentPaymentsViewButton.disableProperty().set(true);
			nextLiabilitiesLabel.setText("no file");
			nextLiabilitiesFile = null;
			nextLiabilitiesViewButton.disableProperty().set(true);
			enableValidation();
		});

		saveButton.setOnAction((ActionEvent event) -> {
			fileChooser.setTitle("Save validation.");
			fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Payments File", "csv"));
			File file = fileChooser.showSaveDialog(stage);
			if (null == file) {
				return;
			}
			try {
				if (liabilitiesTab == tabPane.getSelectionModel().getSelectedItem()) {
					Exporter.exportLiability(entryList, file);
				} else if (repairsTab == tabPane.getSelectionModel().getSelectedItem()) {
					Exporter.exportLiabilityRepair(entryRepairList, file);
				}
			} catch (IOException ioe) {
				LOG.log(Level.SEVERE, null, ioe);
			}
		});
		saveButton.disableProperty().set(true);

		SortedList<LiabilityValidationEntryControl> sortedEntries = new SortedList<>(filteredEntries);
		sortedEntries.comparatorProperty().bind(validationTableView.comparatorProperty());
		validationTableView.setItems(sortedEntries);

		SortedList<LiabilityRepairValidationEntryControl> repairSortedEntries = new SortedList<>(repairFilteredEntries);
		repairSortedEntries.comparatorProperty().bind(validationRepairTableView.comparatorProperty());
		validationRepairTableView.setItems(repairSortedEntries);

		priorityColumn.setCellValueFactory(cellData -> cellData.getValue().priorityProperty());
		numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		messageColumn.setCellValueFactory(cellData -> cellData.getValue().messageProperty());
		currentLiabilityColumn.setCellValueFactory(cellData -> cellData.getValue().currentLiabilityProperty());
		currentLiabilityColumn.setComparator(Amount.COMPARATOR);
		currentPaymentColumn.setCellValueFactory(cellData -> cellData.getValue().currentPaymentProperty());
		currentPaymentColumn.setComparator(Amount.COMPARATOR);
		countColumn.setCellValueFactory(cellData -> cellData.getValue().nextLiabilityOccupantCountPrperty());
		nextLiabilityColumn.setCellValueFactory(cellData -> cellData.getValue().nextLiabilityProperty());
		nextLiabilityColumn.setComparator(Amount.COMPARATOR);

		priorityRepairColumn.setCellValueFactory(cellData -> cellData.getValue().priorityProperty());
		numberRepairColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		restRepairColumn.setCellValueFactory(cellData -> cellData.getValue().currentLiabilityRestProperty());
		restRepairColumn.setComparator(Amount.COMPARATOR);
		paymentRepairColumn.setCellValueFactory(cellData -> cellData.getValue().currentPaymentProperty());
		paymentRepairColumn.setComparator(Amount.COMPARATOR);
		nextRestRepairColumn.setCellValueFactory(cellData -> cellData.getValue().nextLiabilityRestProperty());
		paymentRepairColumn.setComparator(Amount.COMPARATOR);
		messageRepairColumn.setCellValueFactory(cellData -> cellData.getValue().messageProperty());

		enableValidation();
	}

	private static final Logger LOG = Logger.getLogger(ValidatorController.class.getName());

	private void clearTable() {
		entryRepairList = null;
		entryList = null;
		entries.clear();
		repairEntries.clear();
		saveButton.disableProperty().set(true);
	}

	private void enableValidation() {
		boolean disableValidation = (null == currentLiabilitiesFile) || (null == currentPaymentsFile)
				|| (null == nextLiabilitiesFile);
		validateButton.disableProperty().set(disableValidation);
	}
}
