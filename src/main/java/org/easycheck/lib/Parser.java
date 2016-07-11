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
package org.easycheck.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {

	private static final Logger LOG = Logger.getLogger(Parser.class.getName());

	private static final String PAYMENT_DELIMITERS = " :";
	private static final String LIABILITY_REGEX = ",";
	private static final SimpleDateFormat PAYMENT_DATE_FORMATTER;
	private static final SimpleDateFormat LIABILITY_DATE_FORMATTER;
	private static final SimpleDateFormat LIABILITY_DATE_FORMATTER_1;

	static {
		// TODO: use common formatters
		PAYMENT_DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");
		PAYMENT_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("EET"));
		LIABILITY_DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");
		LIABILITY_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("EET"));
		LIABILITY_DATE_FORMATTER_1 = new SimpleDateFormat("MM/dd/yyyy");
		LIABILITY_DATE_FORMATTER_1.setTimeZone(TimeZone.getTimeZone("EET"));
	}

	// <number>:<amount>:<date time>:<id>:<payment way>
	public static Set<Payment> parsePayments(File paymentsFile) throws IOException {
		Set<Payment> result = parse(paymentsFile, paymentStr -> {
			StringTokenizer paymentTokenizer = new StringTokenizer(paymentStr, PAYMENT_DELIMITERS);
			int number = Integer.parseInt(paymentTokenizer.nextToken());
			Amount amount = new Amount(paymentTokenizer.nextToken());
			long time = PAYMENT_DATE_FORMATTER.parse(paymentTokenizer.nextToken()).getTime();
			String id = paymentTokenizer.nextToken();
			int paymentWay = Integer.parseInt(paymentTokenizer.nextToken());
			return new Payment(id, number, amount, time, paymentWay);
		});
		result.removeIf(payment -> {
			return !NumberUtil.isValidNumber(payment.getNumber());
		});
		return result;
	}

	// expected date format: 01.06.2014г.
	// bad one: 01/06/2014
	public static LiabilityReport parseLiabilities(File liabilityFile) throws IOException {
		Set<?> result = parse(liabilityFile, liabilityStr -> {
			String[] entries = liabilityStr.split(LIABILITY_REGEX);
			if (entries.length < 9) {
				throw new IllegalArgumentException("Some entries are missing in: " + liabilityStr);
			}
			int number = Integer.parseInt(entries[0]);
			String owner = entries[1];
			String address = entries[2];
			BigDecimal occupantCount = new BigDecimal(entries[3]);
			long startTime = parseLiabilityDate(entries[4]);
			long endTime = parseLiabilityDate(entries[7]);
			if (NumberUtil.isRepairNumber(number)) {
				Amount totalAmount = new Amount(entries[5]);
				Amount paidAmount = new Amount(entries[6]);
				Amount restAmount = new Amount(entries[8]);
				return new LiabilityRepair(number, owner, address, startTime, endTime, totalAmount, paidAmount,
						restAmount);
			} else {
				Amount oldAmount = new Amount(entries[5]);
				Amount currentAmount = new Amount(entries[6]);
				Amount totalAmount = new Amount(entries[8]);
				return new Liability(number, owner, address, occupantCount, startTime, endTime, oldAmount,
						currentAmount, totalAmount);
			}
		});
		Set<Liability> liabilities = new HashSet<>();
		Set<LiabilityRepair> liabilityRepairs = new HashSet<>();
		result.removeIf(liability -> {
			return !NumberUtil.isValidNumber(((LiabilityOwner) liability).getNumber());
		});
		result.forEach(liability -> {
			if (liability instanceof Liability) {
				liabilities.add((Liability) liability);
			} else {
				liabilityRepairs.add((LiabilityRepair) liability);
			}
		});
		return new LiabilityReport(liabilities, liabilityRepairs);
	}

	private static long parseLiabilityDate(String dateStr) throws ParseException {
		try {
			return LIABILITY_DATE_FORMATTER.parse(dateStr).getTime();
		} catch (ParseException pe) {
			return LIABILITY_DATE_FORMATTER_1.parse(dateStr).getTime();
		}
	}

	private static <R> Set<R> parse(File file, LineParser<R> parser) throws IOException {
		Set<R> results = new HashSet<>();
		try (BufferedReader reader = openReader(file, null)) {
			for (String currentLine = reader.readLine(); null != currentLine; currentLine = reader.readLine()) {
				try {
					R result = parser.parse(currentLine);
					results.add(result);
				} catch (NoSuchElementException | IllegalArgumentException | ParseException e) {
					LOG.log(Level.SEVERE, "Parser error for: " + currentLine, e);
				}
			}
		}
		return results;
	}

	private static BufferedReader openReader(File file, String charsetName)
			throws FileNotFoundException, UnsupportedEncodingException {
		FileInputStream fileIn = new FileInputStream(file);
		InputStreamReader inReader;
		if (null == charsetName) {
			inReader = new InputStreamReader(fileIn);
		} else {
			inReader = new InputStreamReader(fileIn, charsetName);
		}
		return new BufferedReader(inReader);
	}
}
