package net.mmorgenstern.gh;

import java.util.Objects;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class GithubWebhook {
    private static final String EOL = "\n";
    private static final int SIGNATURE_LENGTH = 45;
    private final String secret;

    public GithubWebhook() {
        this(System.getenv("SECRET_KEY"));
    }

    public GithubWebhook(String secret) {
        Objects.requireNonNull(secret, "No secret given.");
        this.secret = secret;
    }

    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "up and running!\n";
    }

    @RequestMapping(value = "/github-webhook", method = RequestMethod.POST)
    public ResponseEntity<String> handle(@RequestHeader("X-Hub-Signature") String signature,
            @RequestBody String payload) {
        if (signature == null) {
            return ResponseEntity.badRequest().body("No signature given." + EOL);
        }

        String computed = String.format("sha1=%s", HmacUtils.hmacSha1Hex(secret, payload));
        boolean invalidLength = signature.length() != SIGNATURE_LENGTH;

        if (invalidLength || !StringUtils.constantTimeCompare(signature, computed)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature." + EOL);
        }

        int bytes = payload.getBytes().length;
        StringBuilder message = new StringBuilder();
        message.append("Signature ok.").append(EOL);
        message.append(String.format("Received %d bytes.", bytes)).append(EOL);
        return ResponseEntity.ok().body(message.toString());
    }

    public static void main(String[] args) {
        SpringApplication.run(GithubWebhook.class, args);
    }
}
