[![Build Status](https://travis-ci.org/martinmo/gh-webhook-demo.svg?branch=master)](https://travis-ci.org/martinmo/gh-webhook-demo)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Proof of concept implementation of a GitHub webhook endpoint using [Spring Boot][spring-boot],
demonstrating

* the [protection of webhooks][gh-securehooks] using a secret key and the `X-Hub-Signature` header,
* tooling such as the awesome [Gradle build tool][gradle],
* deployment on a Linux box that runs `systemd`.

This is a good starting point if you want to build a real endpoint and care about protecting it
against unauthorized access.

The code also demonstrates a way to include version information into the application using the
`processResources` capability of Gradle. Oh, and of course the HMAC signatures are compared using
[constant time comparison][constant-time].


## Quickstart

Run locally with:

    SECRET_KEY=secret gradle -q run

Now you can test the endpoint using `curl` and an example message `foo`, for which the HMAC-SHA1
is `9baed91be7f58b57c824b60da7cb262b2ecafbd2`:

    curl -i -d foo \
      -H 'Content-Type: text/plain' \
      -H 'X-Hub-Signature: sha1=9baed91be7f58b57c824b60da7cb262b2ecafbd2' \
      localhost:8080/github-webhook

The output should be:

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    X-Webhook-Version: 0.1.0-dev/16feb2a
    Content-Type: text/plain;charset=UTF-8
    Content-Length: 32
    Date: Mon, 20 Jun 2016 10:45:47 GMT

    Signature OK.
    Received 3 bytes.

Note the header `X-Webhook-Version`, which contains the Gradle `project.version` and the Git
commit id. This can be very helpful during debugging!


## Build and deploy to Linux using systemd and a reverse proxy

Build an executable JAR, deploy it and install supporting files:

    gradle -q clean build
    sudo cp build/libs/github-webhook-java-*.jar /srv/webhook/webhook.jar
    sudo cp webhook.service /etc/systemd/system
    sudo systemctl enable webhook.service
    echo "SECRET_KEY=$(pwgen -s 32)" | sudo tee /etc/default/webhook
    sudo systemctl start webhook.service

Please note:

* The embedded Tomcat is configured to listen only on `127.0.0.1`, so you will need a reverse
  proxy (e.g., nginx) in front of it.
* The exact same `SECRET_KEY` generated with `pwgen` must be used to configure the webhook in the
  Github repository settings.


## LICENSE

Licensed under the Apache 2.0 license, see [LICENSE.txt](LICENSE.txt).

[spring-boot]: http://projects.spring.io/spring-boot/
[gradle]: https://gradle.org/
[gh-securehooks]: https://developer.github.com/webhooks/securing/
[constant-time]: https://codahale.com/a-lesson-in-timing-attacks/
