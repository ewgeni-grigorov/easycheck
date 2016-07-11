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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ControlUtil {

	private static final String DATE_FORMAT = "yyyy.MM.dd";
	private static final String TIME_FORMAT = "HH:mm:ss";
	private static final String TIME_ZONE_EET = "EET";

	private static final SimpleDateFormat DATE_FORMATTER;
	private static final SimpleDateFormat TIME_FORMATTER;

	static {
		TIME_FORMATTER = new SimpleDateFormat(DATE_FORMAT + ' ' + TIME_FORMAT);
		TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_EET));
		DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
		DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_EET));
	}

	public static String convertTimeToString(long time) {
		return TIME_FORMATTER.format(new Date(time));
	}

	public static String convertDateToString(long date) {
		return DATE_FORMATTER.format(new Date(date));
	}

	private ControlUtil() {
	}
}
