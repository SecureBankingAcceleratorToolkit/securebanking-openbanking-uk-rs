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

import uk.org.openbanking.datamodel.account.OBExternalProductType1Code;
import uk.org.openbanking.datamodel.account.OBProduct1;
import uk.org.openbanking.datamodel.account.OBReadProduct2DataProduct;

public class FRProductConverter {

    public static OBProduct1 toOBProduct1(OBReadProduct2DataProduct product) {
        OBProduct1 product1 = new OBProduct1()
                .accountId(product.getAccountId())
                .productName(product.getProductName())
                .productIdentifier(product.getProductId())
                .secondaryProductIdentifier(product.getSecondaryProductId());

        switch (product.getProductType()) {
            case BUSINESSCURRENTACCOUNT:
                product1.setProductType(OBExternalProductType1Code.BCA);
                break;
            case PERSONALCURRENTACCOUNT:
                product1.setProductType(OBExternalProductType1Code.PCA);
        }
        return product1;
    }

}
