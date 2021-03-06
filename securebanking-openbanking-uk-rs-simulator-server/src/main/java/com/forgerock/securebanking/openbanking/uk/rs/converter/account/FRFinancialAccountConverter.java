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

import com.forgerock.securebanking.openbanking.uk.rs.converter.FRAccountIdentifierConverter;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.payment.FRAccountIdentifier;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.account.FRFinancialAccount;
import uk.org.openbanking.datamodel.account.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FRFinancialAccountConverter {

    // FR to OB
    public static OBAccount1 toOBAccount1(FRFinancialAccount account) {
        return account == null ? null : new OBAccount1()
                .accountId(account.getAccountId())
                .currency(account.getCurrency())
                .nickname(account.getNickname())
                .account(FRAccountIdentifierConverter.toOBCashAccount1(account.getAccounts().get(0)))
                .servicer(FRAccountServicerConverter.toOBBranchAndFinancialInstitutionIdentification2(account.getServicer()));
    }

    public static OBAccount2 toOBAccount2(FRFinancialAccount account) {
        return account == null ? null : new OBAccount2()
                .accountId(account.getAccountId())
                .currency(account.getCurrency())
                .accountType(toOBExternalAccountType1Code(account.getAccountType()))
                .accountSubType(toOBExternalAccountSubType1Code(account.getAccountSubType()))
                .description(account.getDescription())
                .nickname(account.getNickname())
                .account(toOBCashAccount3List(account.getAccounts()))
                .servicer(FRAccountServicerConverter.toOBBranchAndFinancialInstitutionIdentification4(account.getServicer()));

    }

    public static OBAccount3 toOBAccount3(FRFinancialAccount account) {
        return account == null ? null : new OBAccount3()
                .accountId(account.getAccountId())
                .currency(account.getCurrency())
                .accountType(toOBExternalAccountType1Code(account.getAccountType()))
                .accountSubType(toOBExternalAccountSubType1Code(account.getAccountSubType()))
                .description(account.getDescription())
                .nickname(account.getNickname())
                .account(toOBCashAccount5List(account.getAccounts()))
                .servicer(FRAccountServicerConverter.toOBBranchAndFinancialInstitutionIdentification5(account.getServicer()));
    }

    public static OBAccount6 toOBAccount6(FRFinancialAccount account) {
        return account == null ? null : new OBAccount6()
                .accountId(account.getAccountId())
                .status(toOBAccountStatus1Code(account.getStatus()))
                .statusUpdateDateTime(account.getStatusUpdateDateTime())
                .currency(account.getCurrency())
                .accountType(toOBExternalAccountType1Code(account.getAccountType()))
                .accountSubType(toOBExternalAccountSubType1Code(account.getAccountSubType()))
                .description(account.getDescription())
                .nickname(account.getNickname())
                .openingDate(account.getOpeningDate())
                .maturityDate(account.getMaturityDate())
                .account(toOBAccount3AccountList(account.getAccounts()))
                .servicer(FRAccountServicerConverter.toOBBranchAndFinancialInstitutionIdentification50(account.getServicer()));
    }

    public static OBAccountStatus1Code toOBAccountStatus1Code(FRFinancialAccount.FRAccountStatusCode status) {
        return status == null ? null : OBAccountStatus1Code.valueOf(status.name());
    }

    public static OBExternalAccountType1Code toOBExternalAccountType1Code(FRFinancialAccount.FRAccountTypeCode accountType) {
        return accountType == null ? null : OBExternalAccountType1Code.valueOf(accountType.name());
    }

    public static OBExternalAccountSubType1Code toOBExternalAccountSubType1Code(FRFinancialAccount.FRAccountSubTypeCode accountSubType) {
        return accountSubType == null ? null : OBExternalAccountSubType1Code.valueOf(accountSubType.name());
    }

    public static List<OBCashAccount3> toOBCashAccount3List(List<FRAccountIdentifier> accounts) {
        return accounts == null ? null : accounts.stream()
                .map(FRAccountIdentifierConverter::toOBCashAccount3)
                .collect(Collectors.toList());
    }

    public static List<OBCashAccount5> toOBCashAccount5List(List<FRAccountIdentifier> accounts) {
        return accounts == null ? null : accounts.stream()
                .map(FRAccountIdentifierConverter::toOBCashAccount5)
                .collect(Collectors.toList());
    }

    public static List<OBAccount3Account> toOBAccount3AccountList(List<FRAccountIdentifier> accounts) {
        return accounts == null ? null : accounts.stream()
                .map(FRAccountIdentifierConverter::toOBAccount3Account)
                .collect(Collectors.toList());
    }

    // OB to FR
    public static FRFinancialAccount toFRFinancialAccount(OBAccount3 account) {
        return account == null ? null : FRFinancialAccount.builder()
                .accountId(account.getAccountId())
                .currency(account.getCurrency())
                .accountType(toFRAccountTypeCode(account.getAccountType()))
                .accountSubType(toFRAccountSubTypeCode(account.getAccountSubType()))
                .description(account.getDescription())
                .nickname(account.getNickname())
                .accounts(toFRAccountIdentifierList(account.getAccount(), FRAccountIdentifierConverter::toFRAccountIdentifier))
                .servicer(FRAccountServicerConverter.toFRAccountServicer(account.getServicer()))
                .build();
    }

    public static FRFinancialAccount toFRFinancialAccount(OBAccount6 account) {
        return account == null ? null : FRFinancialAccount.builder()
                .accountId(account.getAccountId())
                .status(toFRAccountStatusCode(account.getStatus()))
                .statusUpdateDateTime(account.getStatusUpdateDateTime())
                .currency(account.getCurrency())
                .accountType(toFRAccountTypeCode(account.getAccountType()))
                .accountSubType(toFRAccountSubTypeCode(account.getAccountSubType()))
                .description(account.getDescription())
                .nickname(account.getNickname())
                .openingDate(account.getOpeningDate())
                .maturityDate(account.getMaturityDate())
                .accounts(toFRAccountIdentifierList(account.getAccount(), FRAccountIdentifierConverter::toFRAccountIdentifier))
                .servicer(FRAccountServicerConverter.toFRAccountServicer(account.getServicer()))
                .build();
    }

    public static FRFinancialAccount.FRAccountStatusCode toFRAccountStatusCode(OBAccountStatus1Code status) {
        return status == null ? null : FRFinancialAccount.FRAccountStatusCode.valueOf(status.name());
    }

    public static FRFinancialAccount.FRAccountTypeCode toFRAccountTypeCode(OBExternalAccountType1Code accountType) {
        return accountType == null ? null : FRFinancialAccount.FRAccountTypeCode.valueOf(accountType.name());
    }

    public static FRFinancialAccount.FRAccountSubTypeCode toFRAccountSubTypeCode(OBExternalAccountSubType1Code accountSubType) {
        return accountSubType == null ? null : FRFinancialAccount.FRAccountSubTypeCode.valueOf(accountSubType.name());
    }

    public static <T> List<FRAccountIdentifier> toFRAccountIdentifierList(List<T> accounts, Function<T, FRAccountIdentifier> converter) {
        return accounts == null ? null : accounts.stream()
                .map(converter)
                .collect(Collectors.toList());
    }
}
