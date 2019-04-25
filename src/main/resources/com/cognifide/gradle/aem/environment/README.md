
# Local AEM Environment Setup with Docker

This setup consists of a dispatcher connected by default with AEM publish and AEM author deployed via [gradle-aem-plugin](https://github.com/Cognifide/gradle-aem-plugin). Dispatcher runs on docker on a [official httpd image](https://hub.docker.com/_/httpd).

AEM author and publish instances need to be up.

## Environment in details
1. AEM author at [http://localhost:4502](http://localhost:4502)
2. AEM publish at [http://localhost:4503](http://localhost:4503)
3. Dispatcher available under three different domains:
  * http://example.com -> which maps to `/content/example/live` content root on publish
  * http://demo.example.com -> which maps to `/content/example/demo` content root on publish
  * http://author.example.com -> which is proxy to the author instance

## Configuration
Most of the configuration steps are automated. However, there are three manual steps to make this setup fully operating:

1. [Install docker](https://docs.docker.com/install/)
2. Setup hosts on your local machine (admin rights are required to access `/etc/hosts` or `C:\Windows\System32\drivers\etc\hosts` file): 
    * Windows: 
        * Start PowerShell with "Run asadministrator"
        * Execute: `.\gradlew.bat aemEnvHosts --no-daemon`
    * Unix: 
        * Execute: `sudo ./gradlew aemEnvHosts --no-daemon`
3. Configure AEM dispatcher distribution TAR URL. In `gradle.properties` set `aem.environment.dispatcher.distUrl` property to URL pointing to your distribution of AEM dispatcher (apache2.4, linux-x86_64, no ssl), e.g.:
    * `file:///Users/user.name/adobe/distributions/dispatcher-apache2.4-linux-x86_64-4.3.2.tar.gz`
    * `http://download.macromedia.com/dispatcher/download/dispatcher-apache2.4-linux-x86_64-4.3.2.tar.gz`
    
## Starting

Run `gradlew aemEnvSetup` to start both dispatcher and AEM instances and deploy the app.

Run `gradlew aemEnvUp` to start dispatcher when AEM instances are up and application is deployed.

**Windows only:**

Because dispatchers configuration is provided using Dockers bind mount, on Windows first start requires additional user confirmation to share those files.

### Service health checks
In case of the dispatcher it takes few seconds to start. Default service heath check can be described in the following configuration. By default it will wait for all three domains to be available:

```kotlin
environment {
    healthChecks {
        "http://example.com/en-us.html" respondsWith {
            status = 200
            text = "English"
        }
        "http://demo.example.com/en-us.html" respondsWith {
            status = 200
            text = "English"
        }
        "http://author.example.com/libs/granite/core/content/login.html?resource=%2F&\$\$login\$\$=%24%24login%24%24&j_reason=unknown&j_reason_code=unknown" respondsWith {
            status = 200
            text = "AEM Sign In"
        }
    }
}
```

You can override this configuration in your build script or check docker services status using `docker service ls`:
```
ID                  NAME                    MODE                REPLICAS            IMAGE               PORTS
z2j4spwxb0ky        aem-local-setup_httpd   replicated          1/1                 httpd:2.4.39        *:80->80/tcp
```

## Stopping

Run `gradlew aemEnvDown` to stop it.

## Developing httpd/dispatcher configuration

If you need to make changes to httpd/dispatcher configuration:

**NOTE** On Windows accept Docker to access files from your machine, as dispatcher configuration is directly bind mounted from the project

1. Run `gradlew aemDispatcherDev`
2. update your service health checks as required - yes, TDD :-)
3. edit files in the [dispatcher/conf](dispatcher/conf) directory 
4. look your tests passing/failing
5. (optional) check httpd/dispatcher logs in [build/logs](build/logs) 
6. (optional) check dispatchers cache in [build/cache](build/cache)
7. drink a coffee and feel the flow 

## Using Docker Stack directly 
1. First of all init Docker Swarm:
`docker swarm init`

2. Then setup the local environment:
`docker stack deploy -c docker-compose.yml aem-local-setup` 

3. To stop it run:
`docker stack rm aem-local-setup`