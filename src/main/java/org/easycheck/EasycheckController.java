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
package org.easycheck;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class EasycheckController implements Initializable, StagedController {

	@FXML
	private Button paymentsViewButton;

	@FXML
	private Button liabilitiesViewButton;

	@FXML
	private Button validationButton;

	private static final Logger LOG = Logger.getLogger(EasycheckController.class.getName());

	private Stage stage;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		paymentsViewButton.setOnAction(actionEvent -> {
			try {
				FXMLLoaderUtil.load("/view/PaymentsView.fxml", "Payments View", null, null);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
			}
		});
		liabilitiesViewButton.setOnAction(actionEvent -> {
			try {
				FXMLLoaderUtil.load("/view/LiabilityView.fxml", "Liabilities View", null, null);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
			}
		});
		validationButton.setOnAction(actionEvent -> {
			try {
				FXMLLoaderUtil.load("/validator/Validator.fxml", "Validator", null, null);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
			}
		});
	}

	@Override
	// TODO: clean up
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
