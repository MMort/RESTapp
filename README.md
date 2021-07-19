# REST app for ImageBiopsyLab
by Michael Mörtenhuber

## Goal of the project
App for sending RESTful requests to a database according to IBLabs assessment.
## Getting started

### Prerequisites

* gradle
* openjdk 11
* Springboot > 2.5.2
* Postman desktop client (to be able to test on localhost level)

### Application overview
There are eight different HTTP requests provided.

| command | path                    | request parameters                                  | type of rparam |
| --------|:-----------------------:| ---------------------------------------------------:| --------------:|
| POST    | `/`                     | `{"name":"Obi Wan", "expirationdate": "01.01.3277"}`| json           |
| GET     | `/modules`              |                                                     |                |
| GET     | `/<module>`             |                                                     |                |
| GET     | `/IsExpired/<module>`   |                                                     |                |
| GET     | `IsExpired`             |                                                     |                |
| DELETE  | `/<module>`             |                                                     |                |
| PUT     | `/<module>`             | expirationdate (format "dd.MM.yyyy" required)       | String         |
| GET     | `/echo`                 | echo                                                | String         |

The `<module>` is an ID that is automatically assigned as primary key to a module object. See more in [Known Issues]() section.

### Database
The database used is an H2 Database provided by SpringBoot. It is an embedded, in-memory relational DB and
can be easily configured to be used for demo purposes.

## Run the application

Since there are no build files provided via Git, the java application has to be built before.
``` bash
    # BUILD
    cd <pathToRepository>/RESTapp
    ./gradlew build
    
    # RUN
    cd build/libs
    java -jar RESTapp-1.0.0.jar
```

## Test the application

You can test all available HTTP calls via postman.
Here is a [postman test workspace](https://www.getpostman.com/collections/bb37c31f361d73fd217c) for
all HTTP requests. 
You can import the workspace to your postman desktop suite by
1. going to your workspace
2. Find and press the `Import` button (right beside your workspace title)
3. add link `https://www.getpostman.com/collections/bb37c31f361d73fd217c` and import it.

OR google on how to import workspaces by link.

Unfortunately, the POST message is not working via postman (it is with curl tho). Thus, the app preloads one module
`{"name":"Obi Wan", "expirationdate": "01.01.3277"}` to be able to test the other requests.

The test set is very simple and there is no mocking. If you run the tests once, the preloaded module will be deleted.
You can either restart the app or POST modules via `curl` (for example).

Manual requests can be done with `curl` via:
```bash
curl -v -X POST localhost:8080/ -H 'Content-Type:application/json' -d '{"name": "Han Solo", "expirationdate": "01.01.2022"}'
curl -v -X GET localhost:8080/modules
curl -v -X GET localhost:8080/<module>
curl -X PUT localhost:8080/<module>?newExpirationDate=01.12.1999
curl -v -X GET localhost:8080/isExpired/
curl -v -X GET localhost:8080/isExpired/<module>
curl -X DELETE localhost:8080/<module>
curl -v -X GET localhost:8080/echo?echo=HelloThere
```
**The POST command works with `curl`.**

## Known issues

Be aware that ***< module >*** stands for a module's **ID**. The module class was built with ID
as primary key, but the assessment requirements needed <module> to be the path variable.
The reason is that the module object hash itself might not be a client friendly way to deal with in requests.
Also, the name of the module might not be usable due to non uniqueness. The ID is automatically created and iterated when POSTing a new module.

**Testing** is VERY basic. Definitely there has to be put more work into postman and JS to be able to automatically test
all requirements sufficiently.
