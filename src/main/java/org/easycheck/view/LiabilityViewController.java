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
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.easycheck.ResourceController;
import org.easycheck.StagedController;
import org.easycheck.lib.Amount;
import org.easycheck.lib.LiabilityReport;
import org.easycheck.lib.Parser;
import org.easycheck.lib.ui.UIUtil;
import org.easycheck.view.lib.ui.LiabilityControl;
import org.easycheck.view.lib.ui.LiabilityRepairControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class LiabilityViewController implements Initializable, StagedController, ResourceController {

	@FXML
	private Button openButton;
	@FXML
	private Button clearButton;
	@FXML
	private TextField filterTextField;
	@FXML
	private Label totalCountLabel;
	@FXML
	private Label totalLiabilityLabel;

	@FXML
	private TableView<LiabilityControl> liabilitiesTableView;
	@FXML
	private TableColumn<LiabilityControl, Number> numberLiabilityColumn;
	@FXML
	private TableColumn<LiabilityControl, String> ownerLiabilityColumn;
	@FXML
	private TableColumn<LiabilityControl, BigDecimal> countLiabilityColumn;
	@FXML
	private TableColumn<LiabilityControl, Amount> oldLiabilityColumn;
	@FXML
	private TableColumn<LiabilityControl, Amount> currentLiabilityColumn;
	@FXML
	private TableColumn<LiabilityControl, Amount> totalLiabilityColumn;
	@FXML
	private TableColumn<LiabilityControl, String> toLiabilityColumn;

	@FXML
	private TableView<LiabilityRepairControl> liabilityRepairsTableView;
	@FXML
	private TableColumn<LiabilityRepairControl, Number> numberLiabilityRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairControl, String> ownerLiabilityRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairControl, Amount> totalLiabilityRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairControl, Amount> paidLiabilityRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairControl, Amount> restLiabilityRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairControl, String> fromLiabilityRepairColumn;
	@FXML
	private TableColumn<LiabilityRepairControl, String> toLiabilityRepairColumn;

	private static final Logger LOG = Logger.getLogger(LiabilityViewController.class.getName());

	private final FileChooser liabilitiesFileChooser;

	private final ObservableList<LiabilityControl> liabilities = FXCollections.observableArrayList();
	private final FilteredList<LiabilityControl> filteredLiabilities = new FilteredList<>(liabilities, null);

	private final ObservableList<LiabilityRepairControl> liabilityRepairs = FXCollections.observableArrayList();
	private final FilteredList<LiabilityRepairControl> filteredLiabilityRepairs = new FilteredList<>(liabilityRepairs,
			null);

	private Stage stage;
	private String defaultTitle;
	private BigDecimal totalOccupantCount;
	private Amount totalLiability;

	public LiabilityViewController() {
		liabilitiesFileChooser = new FileChooser();
		liabilitiesFileChooser.setTitle("Select Liability File");
		liabilitiesFileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Liability File", "cvs"));
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
		this.defaultTitle = stage.getTitle();
	}

	@Override
	public void setResource(File resource) {
		open(resource);
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		openButton.setOnAction(actionEvent -> {
			File liabilitiesFile = liabilitiesFileChooser.showOpenDialog(stage);
			open(liabilitiesFile);
		});

		clearButton.setOnAction(actionEvent -> {
			clear();
		});

		filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			initTotals();
			Set<String> filters = FilterUtil.prepareFilters(newValue);
			filteredLiabilities.setPredicate(liabilityControl -> {
				if (FilterUtil.match(filters, liabilityControl.getNumber().toString())) {
					totalOccupantCount = totalOccupantCount.add(liabilityControl.getOccupantCount());
					totalLiability = totalLiability.add(liabilityControl.getTotalAmount());
					return true;
				}
				return false;
			});
			filteredLiabilityRepairs.setPredicate(liabilityRepairControl -> {
				return FilterUtil.match(filters, liabilityRepairControl.getNumber().toString());
			});
			flushTotal();
		});

		// TODO: move to utility
		SortedList<LiabilityControl> sortedLiabilities = new SortedList<>(filteredLiabilities);
		sortedLiabilities.comparatorProperty().bind(liabilitiesTableView.comparatorProperty());
		liabilitiesTableView.setItems(sortedLiabilities);

		SortedList<LiabilityRepairControl> sortedLiabilityRepairs = new SortedList<>(filteredLiabilityRepairs);
		sortedLiabilityRepairs.comparatorProperty().bind(liabilityRepairsTableView.comparatorProperty());
		liabilityRepairsTableView.setItems(sortedLiabilityRepairs);

		numberLiabilityColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		ownerLiabilityColumn.setCellValueFactory(cellData -> cellData.getValue().ownerProperty());
		countLiabilityColumn.setCellValueFactory(cellData -> cellData.getValue().countProperty());
		oldLiabilityColumn.setCellValueFactory(cellData -> cellData.getValue().oldProperty());
		// TODO: move to utility
		oldLiabilityColumn.setComparator(Amount.COMPARATOR);
		currentLiabilityColumn.setCellValueFactory(cellData -> cellData.getValue().currentProperty());
		currentLiabilityColumn.setComparator(Amount.COMPARATOR);
		totalLiabilityColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
		totalLiabilityColumn.setComparator(Amount.COMPARATOR);
		toLiabilityColumn.setCellValueFactory(cellData -> cellData.getValue().toProperty());

		numberLiabilityRepairColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		ownerLiabilityRepairColumn.setCellValueFactory(cellData -> cellData.getValue().ownerProperty());
		totalLiabilityRepairColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
		totalLiabilityRepairColumn.setComparator(Amount.COMPARATOR);
		paidLiabilityRepairColumn.setCellValueFactory(cellData -> cellData.getValue().paidProperty());
		paidLiabilityRepairColumn.setComparator(Amount.COMPARATOR);
		restLiabilityRepairColumn.setCellValueFactory(cellData -> cellData.getValue().restProperty());
		restLiabilityRepairColumn.setComparator(Amount.COMPARATOR);
		fromLiabilityRepairColumn.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
		toLiabilityRepairColumn.setCellValueFactory(cellData -> cellData.getValue().toProperty());
	}

	private void open(File liabilitiesFile) {
		if (null == liabilitiesFile) {
			return;
		}
		clear();
		initTotals();
		try {
			LiabilityReport liabilityReport = Parser.parseLiabilities(liabilitiesFile);
			liabilityReport.getLiabilities().forEach(liability -> {
				liabilities.add(new LiabilityControl(liability));
				totalOccupantCount = totalOccupantCount.add(liability.getOccupantCount());
				totalLiability = totalLiability.add(liability.getTotalAmount());
			});
			liabilityReport.getLiabilityRepairs().forEach(liabilityRepair -> {
				liabilityRepairs.add(new LiabilityRepairControl(liabilityRepair));
			});
			stage.setTitle(liabilitiesFile.getName());
			UIUtil.sortAscending(liabilitiesTableView, numberLiabilityColumn);
			UIUtil.sortAscending(liabilityRepairsTableView, numberLiabilityRepairColumn);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
		flushTotal();
	}

	private void clear() {
		liabilities.clear();
		liabilityRepairs.clear();
		filterTextField.clear();
		filteredLiabilities.setPredicate(null);
		filteredLiabilityRepairs.setPredicate(null);
		stage.setTitle(defaultTitle);
		totalOccupantCount = null;
		totalLiability = null;
		flushTotal();
	}

	private void initTotals() {
		totalOccupantCount = BigDecimal.ZERO;
		totalLiability = Amount.ZERO;
	}

	private void flushTotal() {
		totalCountLabel.setText((null == totalOccupantCount) ? null : totalOccupantCount.toString());
		totalLiabilityLabel.setText((null == totalLiability) ? null : totalLiability.toString());
	}
}
