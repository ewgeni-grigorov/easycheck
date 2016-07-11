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

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;

public class EasycheckApp extends Application {

	private static final Logger LOG = Logger.getLogger(EasycheckApp.class.getName());

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoaderUtil.load("/easycheck/Easycheck.fxml", "Easycheck", primaryStage, null);
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
