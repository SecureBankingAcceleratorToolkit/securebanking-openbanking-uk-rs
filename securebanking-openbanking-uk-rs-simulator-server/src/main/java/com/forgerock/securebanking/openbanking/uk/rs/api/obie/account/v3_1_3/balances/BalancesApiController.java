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
package com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_1_3.balances;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code;
import uk.org.openbanking.datamodel.account.OBReadBalance1;

import java.util.List;

import static com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.AccountsUtil.DUMMY_FINANCIAL_ID;

@Controller("BalancesApiV3.1.3")
public class BalancesApiController implements BalancesApi {

    private final com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_1_2.balances.BalancesApiController previousVersionController;

    public BalancesApiController(@Qualifier("BalancesApiV3.1.2") com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_1_2.balances.BalancesApiController previousVersionController) {
        this.previousVersionController = previousVersionController;
    }

    @Override
    public ResponseEntity<OBReadBalance1> getAccountBalances(String accountId,
                                                             int page,
                                                             String authorization,
                                                             DateTime xFapiAuthDate,
                                                             String xFapiCustomerIpAddress,
                                                             String xFapiInteractionId,
                                                             String xCustomerUserAgent,
                                                             List<OBExternalPermissions1Code> permissions,
                                                             String httpUrl) {
        return previousVersionController.getAccountBalances(
                accountId,
                page,
                DUMMY_FINANCIAL_ID,
                authorization,
                xFapiAuthDate,
                xFapiCustomerIpAddress,
                xFapiInteractionId,
                xCustomerUserAgent,
                permissions, httpUrl);
    }

    @Override
    public ResponseEntity<OBReadBalance1> getBalances(int page,
                                                      String authorization,
                                                      DateTime xFapiAuthDate,
                                                      String xFapiCustomerIpAddress,
                                                      String xFapiInteractionId,
                                                      String xCustomerUserAgent,
                                                      List<String> accountIds,
                                                      List<OBExternalPermissions1Code> permissions,
                                                      String httpUrl) {

        return previousVersionController.getBalances(
                DUMMY_FINANCIAL_ID,
                page,
                authorization,
                xFapiAuthDate,
                xFapiCustomerIpAddress,
                xFapiInteractionId,
                xCustomerUserAgent,
                accountIds,
                permissions,
                httpUrl
        );
    }

}
