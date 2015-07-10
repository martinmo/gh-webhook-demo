# github-webhook-java

Proof of concept implementation of a GitHub webhook endpoint using [Spring Boot][spring-boot], demonstrating

* the [protection of webhooks][gh-securehooks] (using a secret key and the `X-Hub-Signature` header),
* tooling such as the awesome [Gradle build tool][gradle],
* deployment on a Linux box that runs `systemd`.

## Build and run

```bash
# Option 1: run locally
export SECRET_KEY=secret
gradle -q run

# Option 2: build and deploy on Linux
gradle -q clean build
sudo cp build/libs/github-webhook-java-*.jar /srv/webhook/webhook.jar
sudo cp webhook.service /etc/systemd/system
sudo systemctl enable webhook.service
echo "SECRET_KEY=$(pwgen -s 32)" | sudo tee /etc/default/webhook
sudo systemctl start webhook.service
```

The exact same `SECRET_KEY` generated with `pwgen` must be used to configure the webhook in the Github
repository settings.

## Send POST requests with curl

```bash
# with SECRET_KEY=secret (see above):
curl -X POST \
  -d foo \
  -H 'Content-Type: text/plain' \
  -H 'X-Hub-Signature: sha1=9baed91be7f58b57c824b60da7cb262b2ecafbd2' \
  localhost:8080/github-webhook
# output:
# > Signature OK.
# > Received 3 bytes.
```

## LICENSE

See [LICENSE.txt](LICENSE.txt).

[spring-boot]: http://projects.spring.io/spring-boot/
[gradle]: https://gradle.org/
[gh-securehooks]: https://developer.github.com/webhooks/securing/
