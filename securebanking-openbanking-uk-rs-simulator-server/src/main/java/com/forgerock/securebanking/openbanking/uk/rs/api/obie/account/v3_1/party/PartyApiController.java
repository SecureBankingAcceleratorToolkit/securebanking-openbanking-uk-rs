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
package com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_1.party;

import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.accounts.party.FRPartyRepository;
import org.springframework.stereotype.Controller;

@Controller("PartyApiV3.1")
public class PartyApiController extends com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_0.party.PartyApiController implements PartyApi {

    public PartyApiController(FRPartyRepository frPartyRepository) {
        super(frPartyRepository);
    }
}
