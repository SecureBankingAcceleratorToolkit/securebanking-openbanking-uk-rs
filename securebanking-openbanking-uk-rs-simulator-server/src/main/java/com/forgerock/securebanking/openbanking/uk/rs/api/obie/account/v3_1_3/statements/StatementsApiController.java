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
package com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_1_3.statements;

import com.forgerock.securebanking.openbanking.uk.rs.common.util.AccountDataInternalIdFilter;
import com.forgerock.securebanking.openbanking.uk.rs.common.util.PaginationUtil;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.document.account.FRStatement;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.accounts.statements.FRStatementRepository;
import com.forgerock.securebanking.openbanking.uk.rs.service.statement.StatementPDFService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code;
import uk.org.openbanking.datamodel.account.OBReadStatement2;
import uk.org.openbanking.datamodel.account.OBReadStatement2Data;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.forgerock.securebanking.openbanking.uk.rs.converter.account.FRExternalPermissionsCodeConverter.toFRExternalPermissionsCodeList;
import static com.forgerock.securebanking.openbanking.uk.rs.converter.account.FRStatementConverter.toOBStatement2;

@Controller("StatementsApiV3.1.3")
@Slf4j
public class StatementsApiController implements StatementsApi {

    private final int pageLimitStatements;

    private final FRStatementRepository frStatementRepository;

    private final AccountDataInternalIdFilter accountDataInternalIdFilter;

    private final StatementPDFService statementPDFService;

    public StatementsApiController(@Value("${rs.page.default.statement.size:10}") int pageLimitStatements,
                                   FRStatementRepository frStatementRepository,
                                   AccountDataInternalIdFilter accountDataInternalIdFilter,
                                   StatementPDFService statementPDFService) {
        this.pageLimitStatements = pageLimitStatements;
        this.frStatementRepository = frStatementRepository;
        this.accountDataInternalIdFilter = accountDataInternalIdFilter;
        this.statementPDFService = statementPDFService;
    }

    @Override
    public ResponseEntity<OBReadStatement2> getAccountStatement(String statementId,
                                                                String accountId,
                                                                int page,
                                                                String authorization,
                                                                DateTime xFapiAuthDate,
                                                                String xFapiCustomerIpAddress,
                                                                String xFapiInteractionId,
                                                                String xCustomerUserAgent,
                                                                List<OBExternalPermissions1Code> permissions,
                                                                String httpUrl) {
        log.info("Read statements for account {} with minimumPermissions {}", accountId, permissions);

        List<FRStatement> statements = frStatementRepository.byAccountIdAndStatementIdWithPermissions(accountId, statementId, toFRExternalPermissionsCodeList(permissions));
        int totalPages = 1;
        return packageResponse(page, httpUrl, statements, totalPages);
    }

    @Override
    public ResponseEntity<Resource> getAccountStatementFile(String statementId,
                                                            String accountId,
                                                            int page,
                                                            String authorization,
                                                            DateTime xFapiAuthDate,
                                                            String xFapiCustomerIpAddress,
                                                            String xFapiInteractionId,
                                                            String accept) {
        log.info("Received a statement file download request for account: {} (Accept: {}). Interaction Id: {}", accountId, accept, xFapiInteractionId);
        if (!accept.contains(MediaType.APPLICATION_PDF_VALUE)) {
            // Mo other file type is implemented apart from PDF
            return new ResponseEntity<Resource>(HttpStatus.NOT_IMPLEMENTED);
        }

        // Check if this cusotmer has a statement file
        Optional<Resource> statement = statementPDFService.getPdfStatement();
        if (statement.isPresent()) {
            return ResponseEntity.ok()
                    .contentLength(getContentLength(statement.get()))
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(statement.get());
        }
        return new ResponseEntity<Resource>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<OBReadStatement2> getAccountStatements(String accountId,
                                                                 int page,
                                                                 String authorization,
                                                                 DateTime xFapiAuthDate,
                                                                 DateTime fromStatementDateTime,
                                                                 DateTime toStatementDateTime,
                                                                 String xFapiCustomerIpAddress,
                                                                 String xFapiInteractionId,
                                                                 String xCustomerUserAgent,
                                                                 List<OBExternalPermissions1Code> permissions,
                                                                 String httpUrl) {
        log.info("Read statements for account {} with minimumPermissions {}", accountId, permissions);

        Page<FRStatement> statements = frStatementRepository.byAccountIdWithPermissions(accountId,
                fromStatementDateTime, toStatementDateTime, toFRExternalPermissionsCodeList(permissions),
                PageRequest.of(page, pageLimitStatements, Sort.Direction.ASC, "startDateTime"));

        int totalPages = statements.getTotalPages();
        return packageResponse(page, httpUrl, statements.getContent(), totalPages);
    }

    @Override
    public ResponseEntity<OBReadStatement2> getStatements(int page,
                                                          String authorization,
                                                          DateTime xFapiAuthDate,
                                                          DateTime fromStatementDateTime,
                                                          DateTime toStatementDateTime,
                                                          String xFapiCustomerIpAddress,
                                                          String xFapiInteractionId,
                                                          String xCustomerUserAgent,
                                                          List<String> accountIds,
                                                          List<OBExternalPermissions1Code> permissions,
                                                          String httpUrl) {
        log.info("Reading statements from account ids {}", accountIds);

        Page<FRStatement> statements = frStatementRepository.findByAccountIdIn(accountIds, PageRequest.of(page, pageLimitStatements, Sort.Direction.ASC, "startDateTime"));
        int totalPages = statements.getTotalPages();
        return packageResponse(page, httpUrl, statements.getContent(), totalPages);
    }

    private Integer getContentLength(Resource resource) {
        String data = "";
        try {
            byte[] bdata = FileCopyUtils.copyToByteArray(resource.getInputStream());
            return bdata.length;
        } catch (IOException e) {
            log.warn("We found a statement PDF file '{}' for ASPSP but could no get content-length with error", resource.getFilename(), e);
            return null;
        }
    }

    private ResponseEntity<OBReadStatement2> packageResponse(int page, String httpUrl, List<FRStatement> statements, int totalPages) {
        return ResponseEntity.ok(new OBReadStatement2().data(new OBReadStatement2Data().statement(
                statements
                        .stream()
                        .map(st -> toOBStatement2(st.getStatement()))
                        .map(st -> accountDataInternalIdFilter.apply(st))
                        .collect(Collectors.toList())))
                .links(PaginationUtil.generateLinks(httpUrl, page, totalPages))
                .meta(PaginationUtil.generateMetaData(totalPages)));
    }

}
