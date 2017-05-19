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
