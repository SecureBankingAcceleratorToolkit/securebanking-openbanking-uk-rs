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
package com.forgerock.securebanking.openbanking.uk.rs.api.obie.payment.v3_1.domesticscheduledpayments;

import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.account.FRScheduledPaymentData;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.payment.FRWriteDataDomesticScheduled;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.payment.FRWriteDomesticScheduled;
import com.forgerock.securebanking.openbanking.uk.error.OBErrorResponseException;
import com.forgerock.securebanking.openbanking.uk.rs.common.util.VersionPathExtractor;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.document.payment.FRDomesticScheduledPaymentSubmission;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.IdempotentRepositoryAdapter;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.payments.DomesticScheduledPaymentSubmissionRepository;
import com.forgerock.securebanking.openbanking.uk.rs.service.scheduledpayment.ScheduledPaymentService;
import com.forgerock.securebanking.openbanking.uk.rs.validator.PaymentSubmissionValidator;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.org.openbanking.datamodel.account.Meta;
import uk.org.openbanking.datamodel.payment.OBWriteDataDomesticScheduledResponse2;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduled2;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduledResponse2;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static com.forgerock.securebanking.openbanking.uk.rs.api.obie.payment.FRScheduledPaymentDataFactory.createFRScheduledPaymentData;
import static com.forgerock.securebanking.openbanking.uk.rs.api.obie.payment.LinksHelper.createDomesticScheduledPaymentLink;
import static com.forgerock.securebanking.openbanking.uk.rs.converter.payment.FRSubmissionStatusConverter.toOBExternalStatus1Code;
import static com.forgerock.securebanking.openbanking.uk.rs.converter.payment.FRWriteDomesticScheduledConsentConverter.toOBDomesticScheduled2;
import static com.forgerock.securebanking.openbanking.uk.rs.converter.payment.FRWriteDomesticScheduledConverter.toFRWriteDomesticScheduled;
import static com.forgerock.securebanking.openbanking.uk.rs.persistence.document.payment.FRSubmissionStatus.INITIATIONPENDING;

@Controller("DomesticScheduledPaymentsApiV3.1")
@Slf4j
public class DomesticScheduledPaymentsApiController implements DomesticScheduledPaymentsApi {

    private final DomesticScheduledPaymentSubmissionRepository scheduledPaymentSubmissionRepository;
    private final PaymentSubmissionValidator paymentSubmissionValidator;
    private final ScheduledPaymentService scheduledPaymentService;

    public DomesticScheduledPaymentsApiController(
            DomesticScheduledPaymentSubmissionRepository scheduledPaymentSubmissionRepository,
            PaymentSubmissionValidator paymentSubmissionValidator,
            ScheduledPaymentService scheduledPaymentService) {
        this.scheduledPaymentSubmissionRepository = scheduledPaymentSubmissionRepository;
        this.paymentSubmissionValidator = paymentSubmissionValidator;
        this.scheduledPaymentService = scheduledPaymentService;
    }

    @Override
    public ResponseEntity<OBWriteDomesticScheduledResponse2> createDomesticScheduledPayments(
            @Valid OBWriteDomesticScheduled2 obWriteDomesticScheduled2,
            String xFapiFinancialId,
            String authorization,
            String xIdempotencyKey,
            String xJwsSignature,
            String xAccountId,
            DateTime xFapiCustomerLastLoggedTime,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            HttpServletRequest request,
            Principal principal
    ) throws OBErrorResponseException {
        log.debug("Received payment submission: '{}'", obWriteDomesticScheduled2);

        paymentSubmissionValidator.validateIdempotencyKeyAndRisk(xIdempotencyKey, obWriteDomesticScheduled2.getRisk());

        FRWriteDomesticScheduled frScheduledPayment = toFRWriteDomesticScheduled(obWriteDomesticScheduled2);
        log.trace("Converted to: '{}'", frScheduledPayment);

        FRDomesticScheduledPaymentSubmission frPaymentSubmission = FRDomesticScheduledPaymentSubmission.builder()
                .id(UUID.randomUUID().toString())
                .scheduledPayment(frScheduledPayment)
                .status(INITIATIONPENDING)
                .created(new DateTime())
                .updated(new DateTime())
                .idempotencyKey(xIdempotencyKey)
                .obVersion(VersionPathExtractor.getVersionFromPath(request))
                .build();

        // Save the scheduled payment
        frPaymentSubmission = new IdempotentRepositoryAdapter<>(scheduledPaymentSubmissionRepository)
                .idempotentSave(frPaymentSubmission);

        // Save the scheduled payment data for the Accounts API
        FRScheduledPaymentData scheduledPaymentData = createFRScheduledPaymentData(frScheduledPayment.getData().getInitiation(), xAccountId);
        scheduledPaymentService.createScheduledPayment(scheduledPaymentData);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseEntity(frPaymentSubmission));
    }

    @Override
    public ResponseEntity getDomesticScheduledPaymentsDomesticScheduledPaymentId(
            String domesticScheduledPaymentId,
            String xFapiFinancialId,
            String authorization,
            DateTime xFapiCustomerLastLoggedTime,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            HttpServletRequest request,
            Principal principal
    ) {
        Optional<FRDomesticScheduledPaymentSubmission> isPaymentSubmission = scheduledPaymentSubmissionRepository.findById(domesticScheduledPaymentId);
        if (!isPaymentSubmission.isPresent()) {
            // OB specifies a 400 when the id does not match an existing consent
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment submission '" + domesticScheduledPaymentId + "' can't be found");
        }
        FRDomesticScheduledPaymentSubmission frPaymentSubmission = isPaymentSubmission.get();

        return ResponseEntity.ok(responseEntity(frPaymentSubmission));
    }

    private OBWriteDomesticScheduledResponse2 responseEntity(FRDomesticScheduledPaymentSubmission frPaymentSubmission) {
        FRWriteDataDomesticScheduled data = frPaymentSubmission.getScheduledPayment().getData();
        return new OBWriteDomesticScheduledResponse2()
                .data(new OBWriteDataDomesticScheduledResponse2()
                        .domesticScheduledPaymentId(frPaymentSubmission.getId())
                        .initiation(toOBDomesticScheduled2(data.getInitiation()))
                        .creationDateTime(frPaymentSubmission.getCreated())
                        .statusUpdateDateTime(frPaymentSubmission.getUpdated())
                        .status(toOBExternalStatus1Code(frPaymentSubmission.getStatus()))
                        .consentId(data.getConsentId()))
                .links(createDomesticScheduledPaymentLink(this.getClass(), frPaymentSubmission.getId()))
                .meta(new Meta());
    }
}
