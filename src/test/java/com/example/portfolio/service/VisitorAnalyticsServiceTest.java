package com.example.portfolio.service;

import com.example.portfolio.entity.VisitorClassification;
import com.example.portfolio.entity.VisitorClassificationRecord;
import com.example.portfolio.repository.VisitorClassificationRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitorAnalyticsServiceTest {

    @Mock
    private VisitorClassificationRepository repository;

    private VisitorAnalyticsService service;

    @BeforeEach
    void setUp() {
        service = new VisitorAnalyticsService(repository);
    }

    @Test
    void classifiesNormalLookingBrowserAsLikelyHumanAndIssuesAnonymousCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("User-Agent", "Mozilla/5.0 Chrome/126.0 Safari/537.36");
        MockHttpServletResponse response = new MockHttpServletResponse();

        VisitorAnalyticsService.VisitIdentity identity = service.capture(request, response);

        assertEquals(VisitorClassification.HUMAN_LIKELY, identity.classification());
        Cookie cookie = response.getCookie("portfolio_visitor");
        assertNotNull(cookie);
        assertEquals(64, identity.visitorKeyHash().length());
    }

    @Test
    void recognizesCrawlerAndDeduplicatesRepeatedRequestsForTheSameBotOnADay() {
        MockHttpServletRequest first = botRequest();
        VisitorAnalyticsService.VisitIdentity firstVisit =
                service.capture(first, new MockHttpServletResponse());
        VisitorAnalyticsService.VisitIdentity repeatedVisit =
                service.capture(botRequest(), new MockHttpServletResponse());

        assertEquals(VisitorClassification.BOT, firstVisit.classification());
        assertEquals(firstVisit.visitorKeyHash(), repeatedVisit.visitorKeyHash());

        when(repository.findByVisitDateAndVisitorKeyHash(firstVisit.visitDate(), firstVisit.visitorKeyHash()))
                .thenReturn(Optional.empty());
        service.record(firstVisit);
        VisitorClassificationRecord existing = new VisitorClassificationRecord();
        existing.setClassification(VisitorClassification.BOT);
        when(repository.findByVisitDateAndVisitorKeyHash(firstVisit.visitDate(), firstVisit.visitorKeyHash()))
                .thenReturn(Optional.of(existing));
        service.record(repeatedVisit);

        verify(repository, times(1)).save(any(VisitorClassificationRecord.class));
    }

    @Test
    void classifiesMissingUserAgentAsUnknownAndUsesCookieToDeduplicate() {
        MockHttpServletRequest firstRequest = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        VisitorAnalyticsService.VisitIdentity first = service.capture(firstRequest, response);

        MockHttpServletRequest repeatedRequest = new MockHttpServletRequest();
        repeatedRequest.setCookies(response.getCookies());
        VisitorAnalyticsService.VisitIdentity repeated = service.capture(
                repeatedRequest, new MockHttpServletResponse());

        assertEquals(VisitorClassification.UNKNOWN, first.classification());
        assertEquals(first.visitorKeyHash(), repeated.visitorKeyHash());
        assertEquals(LocalDate.now(), first.visitDate());
    }

    @Test
    void genericRobotAiAndPortfolioWordsDoNotTriggerBotClassification() {
        assertEquals(VisitorClassification.HUMAN_LIKELY,
                VisitorAnalyticsService.classify("Mozilla/5.0 Robot Framework portfolio AI assistant"));
    }

    private MockHttpServletRequest botRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.0.2.15");
        request.addHeader("User-Agent", "Googlebot/2.1 (+http://www.google.com/bot.html)");
        return request;
    }
}
