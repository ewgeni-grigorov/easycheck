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

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXMLLoaderUtil {

	public static void load(String fxmlResource, String title, Stage primaryStage, File resource) throws IOException {
		FXMLLoader loader = new FXMLLoader(EasycheckApp.class.getResource(fxmlResource));
		Parent page = (Parent) loader.load();
		if (null == primaryStage) {
			primaryStage = new Stage();
		}
		Scene scene = new Scene(page);
		primaryStage.setScene(scene);
		primaryStage.setTitle(title);
		Object controller = loader.getController();
		if (controller instanceof StagedController) {
			((StagedController) controller).setStage(primaryStage);
		}
		if (controller instanceof ResourceController) {
			((ResourceController) controller).setResource(resource);
		}
		primaryStage.show();
	}

}
