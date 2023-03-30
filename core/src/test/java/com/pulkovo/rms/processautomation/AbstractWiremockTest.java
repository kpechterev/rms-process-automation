package com.pulkovo.rms.processautomation;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;


public abstract class AbstractWiremockTest extends ApplicationTest {

    @Autowired
    protected WireMockServer wireMockServer;

    protected void stubFor(@NonNull final String requestBodyPattern, @NonNull  final Resource responseResource) {
        wireMockServer.stubFor(WireMock.post("/api/graphql")
                .withRequestBody(new RegexPattern(requestBodyPattern))
                .withHeader("Content-Type", new EqualToPattern("application/json; charset=UTF-8"))
                .willReturn(new ResponseDefinitionBuilder().withHeader("Content-Type", "application/json")
                        .withBody(ResourceUtils.contentOf(responseResource))));
    }

    protected RequestPatternBuilder apiCall(@NonNull final Resource requestResource) {
        return postRequestedFor(urlEqualTo("/api/graphql")).withRequestBody(
                new EqualToJsonPattern(ResourceUtils.contentOf(requestResource), true, true));
    }
}