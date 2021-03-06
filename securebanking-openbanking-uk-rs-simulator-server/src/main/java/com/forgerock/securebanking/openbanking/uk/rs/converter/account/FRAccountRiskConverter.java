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

import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.account.FRAccountRisk;
import uk.org.openbanking.datamodel.account.OBRisk2;

public class FRAccountRiskConverter {

    // OB to FR
    public static FRAccountRisk toFRAccountRisk(OBRisk2 obRisk) {
        return obRisk == null ? null : FRAccountRisk.builder()
                .data(obRisk.getData())
                .build();
    }

    // FR to OB
    public static OBRisk2 toOBRisk2(FRAccountRisk frRisk) {
        if (frRisk == null) {
            return null;
        }
        OBRisk2 obRisk = new OBRisk2();
        obRisk.setData(frRisk.getData());
        return obRisk;
    }
}
