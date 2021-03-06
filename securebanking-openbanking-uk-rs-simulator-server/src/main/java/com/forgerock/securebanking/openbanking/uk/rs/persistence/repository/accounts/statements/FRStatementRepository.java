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
package com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.accounts.statements;

import com.forgerock.securebanking.openbanking.uk.rs.persistence.document.account.FRStatement;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;


public interface FRStatementRepository extends MongoRepository<FRStatement, String>, FRStatementRepositoryCustom {

    Page<FRStatement> findByAccountIdAndStartDateTimeBetweenAndEndDateTimeBetween(
            String accountId,
            DateTime fromStartDateTime,
            DateTime toStartDateTime,
            DateTime fromEndDateTime,
            DateTime toEndDateTime,
            Pageable pageable
    );

    Page<FRStatement> findByAccountId(
            String accountId,
            Pageable pageable
    );

    List<FRStatement> findByAccountIdAndId(
            String accountId,
            String id
    );

    Page<FRStatement> findByAccountIdIn(
            List<String> accountIds,
            Pageable pageable
    );

    Long deleteFRStatementByAccountId(
            String accountId
    );

    Long countByAccountIdIn(Set<String> accountIds);
}
