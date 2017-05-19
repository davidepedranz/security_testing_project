# Schoolmate - Security Test Cases
This folder contains the Security Test Cases for Static Taint Analysis run with [Pixy](https://github.com/oliverklee/pixy) on the PHP application [SchoolMate](https://sourceforge.net/projects/schoolmate/).
The test cases are written using [JWebUnit](https://jwebunit.github.io/jwebunit/) and grouped in a standalone Gradle project.

## Run the tests
In order to run the test cases, you first need to run the SchoolMate application:
* start a MySQL database and run the provided SQL scripts to populate the database
* start the SchoolMate application
* run the test cases
```bash
./gradlew check
```

The test cases suite assumes the application to be running at http://localhost:2001/.
However, you can override this value with an environment variable:
```bash
BASE_URL=http://localhost:10000/ ./gradlew check
```

## License
The security test cases source code is licences under the MIT license.
A copy of the license is available in the [LICENSE](LICENSE) file.
