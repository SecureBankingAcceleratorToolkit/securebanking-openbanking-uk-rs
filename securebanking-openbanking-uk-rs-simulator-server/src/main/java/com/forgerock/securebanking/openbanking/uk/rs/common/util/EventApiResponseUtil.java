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
package com.forgerock.securebanking.openbanking.uk.rs.common.util;

import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.event.FRCallbackUrlData;
import com.forgerock.securebanking.openbanking.uk.common.api.meta.OBVersion;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.document.event.FRCallbackUrl;
import lombok.extern.slf4j.Slf4j;
import uk.org.openbanking.datamodel.account.Links;
import uk.org.openbanking.datamodel.account.Meta;
import uk.org.openbanking.datamodel.event.OBCallbackUrlResponse1;
import uk.org.openbanking.datamodel.event.OBCallbackUrlResponseData1;
import uk.org.openbanking.datamodel.event.OBCallbackUrlsResponse1;
import uk.org.openbanking.datamodel.event.OBCallbackUrlsResponseData1;

import java.util.List;
import java.util.stream.Collectors;

import static com.forgerock.securebanking.openbanking.uk.common.api.meta.OBVersion.v3_0;

/**
 * Helps build responses for the Event Notification API, according to the "Release Management" rules of the OB API. For
 * example, refer to https://openbankinguk.github.io/read-write-api-site3/v3.1.3/profiles/callback-url-api-profile.html#release-management
 */
@Slf4j
public class EventApiResponseUtil {

    /**
     * Checks if a resource can be accessed from the version of the API that's currently being invoked. The OB spec
     * states that a TPP cannot access a resource from an older API version if the resource was created in a newer
     * version.
     *
     * <p>
     * This method is being used to filter any resources that cannot be accessed via the version of the API version
     * in question.
     *
     * @param apiVersion The version of the API currently being invoked.
     * @param resourceVersion The version of the API that the resource was saved against.
     * @return {@code true} if the resource is allowed to be accessed, otherwise false.
     */
    public static boolean isAccessToResourceAllowed(OBVersion apiVersion, OBVersion resourceVersion) {
        return apiVersion.equals(resourceVersion) ||
                apiVersion.isAfterVersion(resourceVersion);
    }

    /**
     * Provides the required {@link OBCallbackUrlsResponse1} instance, filtered according to the API version rules. Any
     * resources that cannot be accessed via the API version in use are filtered from the response.
     *
     * @param apiVersion The version of the API currently being invoked.
     * @param frCallbackUrls A {@link List} {@link FRCallbackUrl} to be returned in the response.
     * @return The {@link OBCallbackUrlsResponse1}, containing zero or more {@link FRCallbackUrl FRCallbackUrls}.
     */
    public static OBCallbackUrlsResponse1 packageResponse(List<FRCallbackUrl> frCallbackUrls, OBVersion apiVersion) {
        FRCallbackUrl frCallbackUrl = !frCallbackUrls.isEmpty() ? frCallbackUrls.get(0) : null;
        List<OBCallbackUrlResponseData1> filteredUrls = frCallbackUrls.stream()
                .filter(it -> isAccessToResourceAllowed(apiVersion, OBVersion.fromString(it.getCallbackUrl().getVersion())))
                .map(EventApiResponseUtil::toOBCallbackUrlResponseData1)
                .collect(Collectors.toList());
        return new OBCallbackUrlsResponse1()
                .data(new OBCallbackUrlsResponseData1().callbackUrl(filteredUrls))
                .meta(hasMetaSection(apiVersion) ? new Meta() : null)
                .links(hasMetaSection(apiVersion) ? toSelfLink(frCallbackUrl, apiVersion) : null);
    }

    /**
     * Provides the required {@link OBCallbackUrlResponse1} for the callback API, containing "links" and "meta"
     * depending on the API version.
     *
     * @param apiVersion The version of the API currently being invoked.
     * @param frCallbackUrl The {@link FRCallbackUrl} containing the data for the response.
     * @return the populated {@link OBCallbackUrlResponse1} response
     */
    public static OBCallbackUrlResponse1 packageResponse(FRCallbackUrl frCallbackUrl, OBVersion apiVersion) {
        return new OBCallbackUrlResponse1()
                .data(toOBCallbackUrlResponseData1(frCallbackUrl))
                .meta(hasMetaSection(apiVersion) ? new Meta() : null)
                .links(hasMetaSection(apiVersion) ? toSelfLink(frCallbackUrl, apiVersion) : null);
    }

    private static OBCallbackUrlResponseData1 toOBCallbackUrlResponseData1(FRCallbackUrl frCallbackUrl) {
        final FRCallbackUrlData data = frCallbackUrl.getCallbackUrl();
        return new OBCallbackUrlResponseData1()
                .callbackUrlId(frCallbackUrl.getId())
                .url(data.getUrl())
                .version(data.getVersion());
    }

    private static Links toSelfLink(FRCallbackUrl frCallbackUrl, OBVersion obVersion) {
        // TODO - fix this as part of #55
        return new Links();
        //return resourceLinkService.toSelfLink(frCallbackUrl, discovery -> discovery.getVersion(version).getGetCallbackUrls());
    }

    private static boolean hasMetaSection(OBVersion apiVersion) {
        return apiVersion != null && !apiVersion.equals(v3_0);
    }
}
