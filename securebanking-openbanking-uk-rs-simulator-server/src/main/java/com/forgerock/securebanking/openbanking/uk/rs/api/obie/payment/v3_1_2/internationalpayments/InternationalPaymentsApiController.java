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
package com.forgerock.securebanking.openbanking.uk.rs.api.obie.payment.v3_1_2.internationalpayments;

import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.payments.InternationalPaymentSubmissionRepository;
import com.forgerock.securebanking.openbanking.uk.rs.validator.PaymentSubmissionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller("InternationalPaymentsApiV3.1.2")
@Slf4j
public class InternationalPaymentsApiController extends com.forgerock.securebanking.openbanking.uk.rs.api.obie.payment.v3_1_1.internationalpayments.InternationalPaymentsApiController implements InternationalPaymentsApi {

    public InternationalPaymentsApiController(InternationalPaymentSubmissionRepository paymentSubmissionRepository,
                                              PaymentSubmissionValidator paymentSubmissionValidator) {
        super(paymentSubmissionRepository, paymentSubmissionValidator);
    }
}
