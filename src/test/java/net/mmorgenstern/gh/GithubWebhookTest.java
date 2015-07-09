package net.mmorgenstern.gh;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GithubWebhookTest {
    private GithubWebhook hook;

    @Before
    public void setUp() {
        hook = new GithubWebhook("secret");
    }

    @Test
    public void testNoSignature() throws IOException {
        ResponseEntity<String> entity = hook.handle(null, "hello");
        assertThat(entity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(entity.getBody(), containsString("No signature"));
    }

    @Test
    public void testPayloadModified() throws IOException {
        ResponseEntity<String> entity = hook
                .handle("sha1=5112055c05f944f85755efc5cd8970e194e9f45b", "evil hello");
        assertThat(entity.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
        assertThat(entity.getBody(), containsString("Invalid"));
    }

    @Test
    public void testValidSignature() throws IOException {
        ResponseEntity<String> entity = hook
                .handle("sha1=5112055c05f944f85755efc5cd8970e194e9f45b", "hello");
        assertThat(entity.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(entity.getBody(), containsString("Signature ok"));
    }
}
