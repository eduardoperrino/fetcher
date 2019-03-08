package me.perrino.fetcher.domain.trovit;

import me.perrino.fetcher.domain.Advert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.core.io.FileUrlResource;

import java.util.Collection;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrovitFetcherServiceIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TrovitFetcherService trovitFetcherService;

    private MockRestServiceServer mockServer;

    @Value("${trovit.feed.url}")
    private String feed_url;

    @Before
    public void setUp() {
        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);
        mockServer = MockRestServiceServer.createServer(gateway);
    }

    @Test
    public void givenFeedRemoteError_thenZeroAdsAreReturned() {
        mockServer.expect(once(), requestTo(feed_url))
                .andRespond(withServerError());
        Collection<Advert> ads = trovitFetcherService.fetchAds();
        assertThat(ads, notNullValue());
        assertThat(ads.size(), is(0));
    }

    @Test
    public void givenEmptyFeed_thenZeroAdsAreReturned() {
        String fileName = "__files/empty.xml";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        mockServer.expect(once(), requestTo(feed_url))
                .andRespond(withSuccess(new FileUrlResource(classLoader.getResource(fileName)), MediaType.APPLICATION_XML));
        Collection<Advert> ads = trovitFetcherService.fetchAds();
        assertThat(ads, notNullValue());
        assertThat(ads.size(), is(0));
    }

    @Test
    public void givenWrongFeedFormat_thenZeroAdsAreReturned() {
        String fileName = "__files/wrong_format.xml";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        mockServer.expect(once(), requestTo(feed_url))
                .andRespond(withSuccess(new FileUrlResource(classLoader.getResource(fileName)), MediaType.APPLICATION_XML));
        Collection<Advert> ads = trovitFetcherService.fetchAds();
        assertThat(ads, notNullValue());
        assertThat(ads.size(), is(0));
    }

    @Test
    public void givenFeedResponse_thenAdsAreReturned() {
        String fileName = "__files/completed.xml";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        mockServer.expect(once(), requestTo(feed_url))
                .andRespond(withSuccess(new FileUrlResource(classLoader.getResource(fileName)), MediaType.APPLICATION_XML));
        Collection<Advert> ads = trovitFetcherService.fetchAds();
        assertThat(ads, notNullValue());
        assertThat(ads.size(), is(791));
    }

    @Test
    public void givenFeedResponseWithoutTitle_thenAdsAreReturned() {
        String fileName = "__files/without_title.xml";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        mockServer.expect(once(), requestTo(feed_url))
                .andRespond(withSuccess(new FileUrlResource(classLoader.getResource(fileName)), MediaType.APPLICATION_XML));
        Collection<Advert> ads = trovitFetcherService.fetchAds();
        assertThat(ads, notNullValue());
        assertThat(ads.size(), is(1));
        Advert ad = ads.iterator().next();
        assertThat(ad.getTitle(), is(""));
    }

    @Test
    public void givenFeedResponseWithoutPictures_thenAdsAreReturned() {
        String fileName = "__files/without_title.xml";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        mockServer.expect(once(), requestTo(feed_url))
                .andRespond(withSuccess(new FileUrlResource(classLoader.getResource(fileName)), MediaType.APPLICATION_XML));
        Collection<Advert> ads = trovitFetcherService.fetchAds();
        assertThat(ads, notNullValue());
        assertThat(ads.size(), is(1));
        Advert ad = ads.iterator().next();
        assertThat(ad.getMainPicture(), notNullValue());
    }


}