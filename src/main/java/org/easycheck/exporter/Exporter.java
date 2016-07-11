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
package org.easycheck.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.easycheck.lib.Amount;
import org.easycheck.lib.Liability;
import org.easycheck.lib.LiabilityRepair;
import org.easycheck.validator.lib.ValidationEntry;

public class Exporter {

	private static final char DELIMITER = ',';

	public static void exportLiability(List<ValidationEntry<Liability>> validationEntries, File outFile)
			throws IOException {
		export(validationEntries, outFile, (ValidationEntry<Liability> entry, PrintWriter writer) -> {
			exportValidationEntry(entry, writer);
		}, (PrintWriter writer) -> {
			exportValidationEntryHeaders(writer);
		});
	}

	public static void exportLiabilityRepair(List<ValidationEntry<LiabilityRepair>> validationEntries, File outFile)
			throws IOException {
		export(validationEntries, outFile, (ValidationEntry<LiabilityRepair> entry, PrintWriter writer) -> {
			exportValidationEntryRepair(entry, writer);
		}, (PrintWriter writer) -> {
			exportValidationEntryRepairHeaders(writer);
		});
	}

	private static <T> void export(List<ValidationEntry<T>> validationEntries, File outFile,
			BiConsumer<ValidationEntry<T>, PrintWriter> entryConsumer, Consumer<PrintWriter> headersConsumer)
			throws IOException {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)))) {
			headersConsumer.accept(writer);
			validationEntries.stream().filter((ValidationEntry<T> entry) -> {
				return isAcceptable(entry);
			}).forEach((ValidationEntry<T> entry) -> {
				entryConsumer.accept(entry, writer);
			});
		}
	}

	private static void exportValidationEntryRepair(ValidationEntry<LiabilityRepair> entry, PrintWriter writer) {
		exportValidationEntry(entry.getPriority().toString(), writer);
		LiabilityRepair currentLiability = entry.getCurrentLiability();
		if (null == currentLiability) {
			exportValidationEntry(String.valueOf(entry.getNextLiability().getNumber()), writer);
			writer.print(DELIMITER);
		} else {
			exportValidationEntry(String.valueOf(currentLiability.getNumber()), writer);
			exportValidationEntry(currentLiability.getRestAmount(), writer);
		}
		exportValidationEntry(entry.getCurrentPayment(), writer);
		LiabilityRepair nextLiability = entry.getNextLiability();
		if (null == nextLiability) {
			writer.print(DELIMITER);
		} else {
			exportValidationEntry(nextLiability.getRestAmount(), writer);
		}
		exportValidationEntry(entry.getMessage(), writer);
		writer.println();
	}

	private static void exportValidationEntry(ValidationEntry<Liability> entry, PrintWriter writer) {
		exportValidationEntry(entry.getPriority().toString(), writer);
		Liability currentLiability = entry.getCurrentLiability();
		int number = (null == currentLiability) ? entry.getNextLiability().getNumber() : currentLiability.getNumber();
		exportValidationEntry(String.valueOf(number), writer);
		if (null == currentLiability) {
			writer.print(DELIMITER);
		} else {
			exportValidationEntry(currentLiability.getTotalAmount(), writer);
		}
		exportValidationEntry(entry.getCurrentPayment(), writer);
		Liability nextLiability = entry.getNextLiability();
		if (null == nextLiability) {
			writer.print(DELIMITER);
			writer.print(DELIMITER);
		} else {
			exportValidationEntry(nextLiability.getTotalAmount(), writer);
			exportValidationEntry(nextLiability.getOccupantCount().toString(), writer);
		}
		exportValidationEntry(entry.getMessage(), writer);
		writer.println();
	}

	private static void exportValidationEntryHeaders(PrintWriter writer) {
		exportValidationEntry("Priority", writer);
		exportValidationEntry("Number", writer);
		exportValidationEntry("Liability", writer);
		exportValidationEntry("Payment", writer);
		exportValidationEntry("Next Liability", writer);
		exportValidationEntry("Count", writer);
		writer.println("Message");
	}

	private static void exportValidationEntryRepairHeaders(PrintWriter writer) {
		exportValidationEntry("Priority", writer);
		exportValidationEntry("Number", writer);
		exportValidationEntry("Rest", writer);
		exportValidationEntry("Payment", writer);
		exportValidationEntry("Next Rest", writer);
		writer.println("Message");
	}

	private static void exportValidationEntry(Amount amount, PrintWriter writer) {
		exportValidationEntry((null == amount) ? "0" : amount.toString(), writer);
	}

	private static void exportValidationEntry(String value, PrintWriter writer) {
		writer.print(value);
		writer.print(DELIMITER);
	}

	private static <T> boolean isAcceptable(ValidationEntry<T> entry) {
		return ValidationEntry.Priority.INFO != entry.getPriority();
	}
}
