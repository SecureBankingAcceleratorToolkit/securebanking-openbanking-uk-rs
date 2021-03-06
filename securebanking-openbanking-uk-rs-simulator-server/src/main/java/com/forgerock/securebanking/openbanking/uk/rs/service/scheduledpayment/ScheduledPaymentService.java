/**
 * Copyright © 2020 ForgeRock AS (obst@forgerock.com)
 *
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
package com.forgerock.securebanking.openbanking.uk.rs.service.scheduledpayment;

import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.account.FRScheduledPaymentData;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.document.account.FRScheduledPayment;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.accounts.scheduledpayments.FRScheduledPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.forgerock.securebanking.openbanking.uk.rs.persistence.document.account.FRScheduledPayment.ScheduledPaymentStatus.PENDING;

/**
 * Service for saving, retrieving and updating {@link FRScheduledPayment FRScheduledPayments} for the Accounts API.
 */
@Service
@Slf4j
public class ScheduledPaymentService {

    private final FRScheduledPaymentRepository scheduledPaymentRepository;

    public ScheduledPaymentService(FRScheduledPaymentRepository scheduledPaymentRepository) {
        this.scheduledPaymentRepository = scheduledPaymentRepository;
    }

    /**
     * Saves a {@link FRScheduledPayment} within the repository.
     *
     * @param scheduledPaymentData The {@link FRScheduledPaymentData} containing the required payment information.
     * @return The persisted {@link FRScheduledPayment}.
     */
    public FRScheduledPayment createScheduledPayment(FRScheduledPaymentData scheduledPaymentData) {
        log.debug("Create a scheduled payment in the repository: {}", scheduledPaymentData);

        FRScheduledPayment frScheduledPayment = FRScheduledPayment.builder()
                .id(UUID.randomUUID().toString())
                .scheduledPayment(scheduledPaymentData)
                .accountId(scheduledPaymentData.getAccountId())
                .status(PENDING)
                // TODO - do we need to persist the pispId?
                //.pispId(pispId)
                .build();
        return scheduledPaymentRepository.save(frScheduledPayment);
    }
}
