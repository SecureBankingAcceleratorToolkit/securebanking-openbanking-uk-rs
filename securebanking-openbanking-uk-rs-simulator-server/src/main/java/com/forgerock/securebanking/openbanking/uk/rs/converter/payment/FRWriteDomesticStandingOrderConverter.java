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
package com.forgerock.securebanking.openbanking.uk.rs.converter.payment;

import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.payment.FRWriteDataDomesticStandingOrder;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.payment.FRWriteDomesticStandingOrder;
import uk.org.openbanking.datamodel.payment.*;

public class FRWriteDomesticStandingOrderConverter {

    public static FRWriteDomesticStandingOrder toFRWriteDomesticStandingOrder(OBWriteDomesticStandingOrder1 obWriteDomesticStandingOrder1) {
        return obWriteDomesticStandingOrder1 == null ? null : FRWriteDomesticStandingOrder.builder()
                .data(toFRWriteDataDomesticStandingOrder(obWriteDomesticStandingOrder1.getData()))
                .risk(FRPaymentRiskConverter.toFRRisk(obWriteDomesticStandingOrder1.getRisk()))
                .build();
    }

    public static FRWriteDomesticStandingOrder toFRWriteDomesticStandingOrder(OBWriteDomesticStandingOrder2 obWriteDomesticStandingOrder2) {
        return obWriteDomesticStandingOrder2 == null ? null : FRWriteDomesticStandingOrder.builder()
                .data(toFRWriteDataDomesticStandingOrder(obWriteDomesticStandingOrder2.getData()))
                .risk(FRPaymentRiskConverter.toFRRisk(obWriteDomesticStandingOrder2.getRisk()))
                .build();
    }

    public static FRWriteDomesticStandingOrder toFRWriteDomesticStandingOrder(OBWriteDomesticStandingOrder3 obWriteDomesticStandingOrder3) {
        return obWriteDomesticStandingOrder3 == null ? null : FRWriteDomesticStandingOrder.builder()
                .data(toFRWriteDataDomesticStandingOrder(obWriteDomesticStandingOrder3.getData()))
                .risk(FRPaymentRiskConverter.toFRRisk(obWriteDomesticStandingOrder3.getRisk()))
                .build();
    }

    public static FRWriteDataDomesticStandingOrder toFRWriteDataDomesticStandingOrder(OBWriteDataDomesticStandingOrder1 data) {
        return data == null ? null : FRWriteDataDomesticStandingOrder.builder()
                .consentId(data.getConsentId())
                .initiation(FRWriteDomesticStandingOrderConsentConverter.toFRWriteDomesticStandingOrderDataInitiation(data.getInitiation()))
                .build();
    }

    public static FRWriteDataDomesticStandingOrder toFRWriteDataDomesticStandingOrder(OBWriteDataDomesticStandingOrder2 data) {
        return data == null ? null : FRWriteDataDomesticStandingOrder.builder()
                .consentId(data.getConsentId())
                .initiation(FRWriteDomesticStandingOrderConsentConverter.toFRWriteDomesticStandingOrderDataInitiation(data.getInitiation()))
                .build();
    }

    public static FRWriteDataDomesticStandingOrder toFRWriteDataDomesticStandingOrder(OBWriteDataDomesticStandingOrder3 data) {
        return data == null ? null : FRWriteDataDomesticStandingOrder.builder()
                .consentId(data.getConsentId())
                .initiation(FRWriteDomesticStandingOrderConsentConverter.toFRWriteDomesticStandingOrderDataInitiation(data.getInitiation()))
                .build();
    }
}
