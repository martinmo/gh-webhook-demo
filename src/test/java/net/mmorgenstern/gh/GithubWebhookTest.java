package net.mmorgenstern.gh;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GithubWebhookTest {
    @Mock
    private HttpServletRequest request;
    private GithubWebhook hook;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        hook = new GithubWebhook("secret");
    }

    @Test
    public void testNoSignature() throws IOException {
        when(request.getHeader(anyString())).thenReturn(null);
        ResponseEntity<String> entity = hook.receiveHook(request, "hello");
        assertThat(entity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(entity.getBody(), containsString("No signature"));
    }

    @Test
    public void testPayloadModified() throws IOException {
        when(request.getHeader("X-Hub-Signature"))
                .thenReturn("sha1=5112055c05f944f85755efc5cd8970e194e9f45b");
        ResponseEntity<String> entity = hook.receiveHook(request, "evil hello");
        assertThat(entity.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
        assertThat(entity.getBody(), containsString("Invalid"));
    }

    @Test
    public void testValidSignature() throws IOException {
        when(request.getHeader("X-Hub-Signature"))
                .thenReturn("sha1=5112055c05f944f85755efc5cd8970e194e9f45b");
        ResponseEntity<String> entity = hook.receiveHook(request, "hello");
        assertThat(entity.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(entity.getBody(), equalTo("hello"));
    }
}
