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
package org.easycheck.validator.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easycheck.lib.Amount;
import org.easycheck.lib.AmountConstants;
import org.easycheck.lib.Liability;
import org.easycheck.lib.LiabilityOwner;
import org.easycheck.lib.LiabilityRepair;
import org.easycheck.lib.NumberUtil;
import org.easycheck.lib.Payment;

public final class Validator {

	private static final Amount CRITICAL_AMOUNT = new Amount("50");

	private Validator() {
		// prevent object instantiation
	}

	public static List<ValidationEntry<LiabilityRepair>> validateRepairs(Set<Payment> payments,
			Set<LiabilityRepair> currentLiabilityRepairs, Set<LiabilityRepair> nextLiabilityRepairs) {
		Map<Integer, LiabilityRepair> currentLiabilityMap = mapToNumber(currentLiabilityRepairs);
		Map<Integer, LiabilityRepair> nextLiabilityMap = mapToNumber(nextLiabilityRepairs);
		Map<Integer, Amount> paymentsSum = sum(payments);
		List<ValidationEntry<LiabilityRepair>> result = new ArrayList<>();
		currentLiabilityMap.forEach((Integer currentNumber, LiabilityRepair currentLiabilityRepair) -> {
			Amount paymentAmount = paymentsSum.get(currentNumber);
			LiabilityRepair nextLiabilityRepair = nextLiabilityMap.get(currentNumber);
			Amount expectedLiabilityAmount = currentLiabilityRepair.getRestAmount();
			if (null == paymentAmount) {
				if (expectedLiabilityAmount.equals(Amount.ZERO)) {
					result.add(new ValidationEntry<>(ValidationEntry.Priority.INFO, "Redundant liability. It's paid.",
							currentLiabilityRepair, nextLiabilityRepair, null));
				} else {
					result.add(new ValidationEntry<>(ValidationEntry.Priority.INFO, "No repair payment.",
							currentLiabilityRepair, nextLiabilityRepair, null));
				}
			} else {
				expectedLiabilityAmount = expectedLiabilityAmount.subtract(paymentAmount);
			}
			if (null == nextLiabilityRepair) {
				if (!expectedLiabilityAmount.equals(Amount.ZERO)) {
					result.add(new ValidationEntry<>(ValidationEntry.Priority.CRITICAL, "No next repair liability.",
							currentLiabilityRepair, null, paymentAmount));
				}
			} else {
				if (!expectedLiabilityAmount.equals(nextLiabilityRepair.getRestAmount())) {
					result.add(new ValidationEntry<>(ValidationEntry.Priority.CRITICAL,
							"The next repair liability is not correct.", currentLiabilityRepair, nextLiabilityRepair,
							paymentAmount));
				}
				nextLiabilityMap.remove(currentNumber);
			}
		});
		if (!nextLiabilityMap.isEmpty()) {
			nextLiabilityMap.forEach((Integer number, LiabilityRepair liability) -> {
				result.add(new ValidationEntry(ValidationEntry.Priority.INFO, "New repair liability.", null, liability,
						null));
			});
		}
		return result;
	}

	public static List<ValidationEntry<Liability>> validate(Set<Payment> payments, Set<Liability> currentLiabilities,
			Set<Liability> nextLiabilities) {
		Map<Integer, Liability> currentLiabilityMap = mapToNumber(currentLiabilities);
		Map<Integer, Liability> nextLiabilityMap = mapToNumber(nextLiabilities);
		Map<Integer, Amount> paymentsSum = sum(payments);
		List<ValidationEntry<Liability>> result = new ArrayList<>();
		currentLiabilityMap.forEach((Integer currentNumber, Liability currentLiability) -> {
			Amount paymentAmount = paymentsSum.get(currentNumber);
			Amount expectedLiabilityAmount = currentLiability.getTotalAmount();
			Liability nextLiability = nextLiabilityMap.get(currentNumber);
			if (null != paymentAmount) {
				expectedLiabilityAmount = expectedLiabilityAmount.subtract(paymentAmount);
			} else {
				if (isCriticalLiability(nextLiability)) {
					result.add(new ValidationEntry(ValidationEntry.Priority.IMPORTANT,
							"No payment and the next liability is >= " + CRITICAL_AMOUNT, currentLiability,
							nextLiability, null));
				} else {
					result.add(new ValidationEntry(ValidationEntry.Priority.INFO, "No payment.", currentLiability,
							nextLiability, null));
				}
			}
			if (null == nextLiability) {
				result.add(new ValidationEntry(ValidationEntry.Priority.IMPORTANT, "No next liability.",
						currentLiability, nextLiability, paymentAmount));
			} else {
				expectedLiabilityAmount = expectedLiabilityAmount.add(getNextExpectedLiabilityAmount(nextLiability));
				expectedLiabilityAmount = fixDog(expectedLiabilityAmount, nextLiability.getOwner());
				if (!expectedLiabilityAmount.equals(nextLiability.getTotalAmount())) {
					Amount missingAmount = expectedLiabilityAmount.subtract(nextLiability.getTotalAmount());
					result.add(new ValidationEntry(ValidationEntry.Priority.CRITICAL,
							"The next liability is not correct. Missing: " + missingAmount, currentLiability,
							nextLiability, paymentAmount));
				}
				nextLiabilityMap.remove(currentNumber);
			}
		});
		if (!nextLiabilityMap.isEmpty()) {
			nextLiabilityMap.forEach((Integer number, Liability liability) -> {
				result.add(new ValidationEntry(ValidationEntry.Priority.INFO, "New liability.", null, liability, null));
			});
		}
		return result;
	}

	private static <T extends LiabilityOwner> Map<Integer, T> mapToNumber(Set<T> liabilities) {
		Map<Integer, T> result = new HashMap<>(liabilities.size(), 1F);
		liabilities.forEach((T liabilityOwner) -> {
			if (null != result.put(liabilityOwner.getNumber(), liabilityOwner)) {
				throw new IllegalArgumentException("Duplicated liability: " + liabilityOwner);
			}
		});
		return result;
	}

	private static Map<Integer, Amount> sum(Set<Payment> payments) {
		Map<Integer, Amount> result = new HashMap<>(payments.size(), 1F);
		payments.forEach((Payment payment) -> {
			Integer paymentNumber = payment.getNumber();
			Amount paymentAmount = result.get(paymentNumber);
			paymentAmount = (null == paymentAmount) ? payment.getAmount() : paymentAmount.add(payment.getAmount());
			result.put(paymentNumber, paymentAmount);
		});
		return result;
	}

	private static Amount getNextExpectedLiabilityAmount(Liability nextLiability) {
		int liabilityNumber = nextLiability.getNumber();
		if (NumberUtil.isManNumber(liabilityNumber)) {
			return AmountConstants.AMOUNT_PER_MAN.multiply(nextLiability.getOccupantCount());
		}
		if (NumberUtil.isRoomNumber(liabilityNumber)) {
			// TODO: fix when the report is fixed
			return AmountConstants.AMOUNT_PER_ROOM;
		}
		return Amount.ZERO;
	}

	private static Amount fixDog(Amount expectedLiabilityAmount, String owner) {
		if (owner.contains("кучета")) {
			return expectedLiabilityAmount.add(AmountConstants.AMOUNT_PER_DOGS);
		}
		if (owner.contains("куче")) {
			return expectedLiabilityAmount.add(AmountConstants.AMOUNT_PER_DOG);
		}
		return expectedLiabilityAmount;
	}

	private static boolean isCriticalLiability(Liability liability) {
		if (null == liability) {
			return false;
		}
		return Amount.COMPARATOR.compare(liability.getTotalAmount(), CRITICAL_AMOUNT) >= 0;
	}
}
