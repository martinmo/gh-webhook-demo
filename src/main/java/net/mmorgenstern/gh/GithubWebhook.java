package net.mmorgenstern.gh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class GithubWebhook {
    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "Hello!";
    }

    public static void main(String[] args) {
        SpringApplication.run(GithubWebhook.class, args);
    }
}
