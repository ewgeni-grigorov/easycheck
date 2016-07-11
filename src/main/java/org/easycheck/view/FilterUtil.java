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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class FilterUtil {

	private static final char WILDCARD = '*';

	public static Set<String> prepareFilters(String filters) {
		if (null == filters) {
			return null;
		}
		StringTokenizer filterTokens = new StringTokenizer(filters);
		Set<String> result = new HashSet<>();
		while (filterTokens.hasMoreTokens()) {
			result.add(filterTokens.nextToken());
		}
		return result;
	}

	public static boolean match(Set<String> filters, String value) {
		if ((null == filters) || (filters.isEmpty())) {
			return true; // no filter, match everything
		}
		for (Iterator<String> filterIter = filters.iterator(); filterIter.hasNext();) {
			if (match(filterIter.next(), value)) {
				return true;
			}
		}
		return false;
	}

	private static boolean match(String filter, String value) {
		int filterLength = filter.length();
		char firstChar = filter.charAt(0);
		if ((WILDCARD == firstChar) && (1 == filterLength)) {
			return true; // match everything
		}
		if (firstChar == WILDCARD) {
			filter = filter.substring(1);
			return value.endsWith(filter);
		}
		if (filter.charAt(filterLength - 1) == WILDCARD) {
			filter = filter.substring(0, filterLength - 1);
			return value.startsWith(filter);
		}
		return -1 != value.indexOf(filter);
	}
}
