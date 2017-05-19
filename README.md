# Security Testing - Static Taint Analysis of SchoolMate
Project of [Security Testing](https://sites.google.com/site/sectestunitn/home), Fall Semester 2017, University of Trento.

The project consist in the Static Taint Analysis for XSS vulnerabilities of the PHP application [SchoolMate](https://sourceforge.net/projects/schoolmate/) using [Pixy](https://github.com/oliverklee/pixy).
In particular, the following tasks were performed:
* Static Taint Analysis of the vulnerable application
* Categorization of the reported vulnerabilities
* Proof-of-Concept attacks for the true positive vulnerabilities
* Fix of all reported vulnerabilities
* Report of the analysis results

## Repository Structure
The repository structure reflects the performed activies:

Folder                                     | Description
-------------------------------------------|---------------------------------------------------------
[taint_analysis](taint_analysis)           | Results of the Static Taint Analysis.
[schoolmate](schoolmate)                   | Code of the original and fixed application, as well as the SQL scripts needed to set it up.
[security_test_cases](security_test_cases) | Security Test Cases, Proof-of-Concept attacks.
[report](report)                           | Report of the analysis. 

## Run the Security Test Cases
You can use the provided Docker Compose file to quickly run both the original and the fixed application.

### Original Application
```bash
cd schoolmate/original_application
docker-compose up
```

### Fixed Application
```bash
cd schoolmate/fixed_application
docker-compose up
```

### Security Test Cases
The test cases are written in Java, usin JUnit and JWebUnit.
You can use the Gradle wrapper to run them:
```bash
cd security_test_cases
./gradlew check 
```

The tests will point by default to `http://localhost:2001/`.
You can use any custom location using the `BASE_URL` environment variable.
```bash
# run the tests against the original application
BASE_URL=http://localhost:2001/ ./gradlew check

# run the tests against the fixed application
BASE_URL=http://localhost:2002/ ./gradlew check
```
