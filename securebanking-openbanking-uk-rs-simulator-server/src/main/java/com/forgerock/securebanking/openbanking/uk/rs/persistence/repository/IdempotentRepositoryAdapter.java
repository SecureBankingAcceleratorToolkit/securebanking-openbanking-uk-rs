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
package com.forgerock.securebanking.openbanking.uk.rs.persistence.repository;

import com.forgerock.securebanking.openbanking.uk.error.OBErrorResponseException;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.document.payment.PaymentSubmission;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.payments.PaymentSubmissionRepository;
import com.forgerock.securebanking.openbanking.uk.rs.validator.IdempotencyValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * This class allows reuse of idempotent save logic for payment submissions.
 * <p>
 * It wraps the relevant repository but adds logic to check if this is a valid idempotent request.
 * <ul>
 * <li/> If valid and repeated -> return existing object and do nothing
 * <li/> If valid and not repeated -> save and return new object
 * <li/> If invalid -> throw OBErrorResponseException
 * </ul>
 *
 * @param <T> The type of the {@link PaymentSubmission} (e.g. FRDomesticPaymentSubmission).
 */
@Slf4j
public class IdempotentRepositoryAdapter<T extends PaymentSubmission> {

    private final PaymentSubmissionRepository<T> repository;

    public IdempotentRepositoryAdapter(PaymentSubmissionRepository<T> repository) {
        this.repository = repository;
    }

    public T idempotentSave(T paymentSubmission) throws OBErrorResponseException {
        Optional<T> isPaymentSubmission = repository.findByConsentId(paymentSubmission.getConsentId());
        if (isPaymentSubmission.isPresent()) {
            log.info("A payment with this consent id '{}' was already found. Checking idempotency key.", isPaymentSubmission.get().getConsentId());
            IdempotencyValidator.validateIdempotencyRequest(paymentSubmission, isPaymentSubmission.get());
            log.info("Idempotent request is valid. Returning [201 CREATED] but take no further action.");
            return isPaymentSubmission.get();
        } else {
            log.info("No payment with this consent id '{}' exists. Proceed to create it.", paymentSubmission.getConsentId());
            log.debug("Saving new payment submission: {}", paymentSubmission);
            paymentSubmission = repository.save(paymentSubmission);
            log.info("Created new Payment Submission: {}", paymentSubmission.getId());
            return paymentSubmission;
        }
    }
}
