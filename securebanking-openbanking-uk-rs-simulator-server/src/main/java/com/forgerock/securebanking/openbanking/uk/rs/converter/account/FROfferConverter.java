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
package com.forgerock.securebanking.openbanking.uk.rs.converter.account;

import com.forgerock.securebanking.openbanking.uk.rs.converter.FRAmountConverter;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.account.FROfferData;
import uk.org.openbanking.datamodel.account.OBExternalOfferType1Code;
import uk.org.openbanking.datamodel.account.OBOffer1;

import static com.forgerock.securebanking.openbanking.uk.rs.converter.FRAmountConverter.toFRAmount;

public class FROfferConverter {

    // FR to OB
    public static OBOffer1 toOBOffer1(FROfferData offerData) {
        return offerData == null ? null : new OBOffer1()
                .accountId(offerData.getAccountId())
                .offerId(offerData.getOfferId())
                .offerType(toOBExternalOfferType1Code(offerData.getOfferType()))
                .description(offerData.getDescription())
                .startDateTime(offerData.getStartDateTime())
                .endDateTime(offerData.getEndDateTime())
                .rate(offerData.getRate())
                .value(offerData.getValue())
                .term(offerData.getTerm())
                .URL(offerData.getURL())
                .amount(FRAmountConverter.toOBActiveOrHistoricCurrencyAndAmount(offerData.getAmount()))
                .fee(FRAmountConverter.toOBActiveOrHistoricCurrencyAndAmount(offerData.getFee()));
    }

    public static OBExternalOfferType1Code toOBExternalOfferType1Code(FROfferData.FROfferType offerType) {
        return offerType == null ? null : OBExternalOfferType1Code.valueOf(offerType.name());
    }

    // OB to FR
    public static FROfferData toFROfferData(OBOffer1 obOffer) {
        return obOffer == null ? null : FROfferData.builder()
                .accountId(obOffer.getAccountId())
                .offerId(obOffer.getOfferId())
                .offerType(toFROfferType(obOffer.getOfferType()))
                .description(obOffer.getDescription())
                .startDateTime(obOffer.getStartDateTime())
                .endDateTime(obOffer.getEndDateTime())
                .rate(obOffer.getRate())
                .value(obOffer.getValue())
                .term(obOffer.getTerm())
                .URL(obOffer.getURL())
                .amount(FRAmountConverter.toFRAmount(obOffer.getAmount()))
                .fee(FRAmountConverter.toFRAmount(obOffer.getFee()))
                .build();
    }

    public static FROfferData.FROfferType toFROfferType(OBExternalOfferType1Code offerType) {
        return offerType == null ? null : FROfferData.FROfferType.valueOf(offerType.name());
    }
}
