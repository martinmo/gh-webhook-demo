package net.mmorgenstern.gh;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class GithubWebhook {
    private final String secret;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public GithubWebhook() {
        this(System.getenv("SECRET_KEY"));
    }

    public GithubWebhook(String secret) {
        Objects.requireNonNull(secret);
        this.secret = secret;
    }

    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "up and running!\n";
    }

    @RequestMapping(value = "/github-webhook", method = RequestMethod.POST)
    public ResponseEntity<String> receiveHook(HttpServletRequest request,
            @RequestBody String payload) throws IOException {
        String signature = request.getHeader("X-Hub-Signature");

        if (signature == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No signature given.\n");
        }

        String computed = String.format("sha1=%s", HmacUtils.hmacSha1Hex(secret, payload));

        logger.info("signature: {}", signature);
        logger.info("computed: {})", computed);

        if (!StringUtils.constantTimeCompare(signature, computed)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature.\n");
        }

        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(payload);
    }

    public static void main(String[] args) {
        SpringApplication.run(GithubWebhook.class, args);
    }
}
