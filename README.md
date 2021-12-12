Authorization: ![Authorization coverage](https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/badges/main/coverage.svg?job=test-authorization-service)
Course: ![Course coverage](https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/badges/main/coverage.svg?job=test-course-service)
Hiring: ![Hiring coverage](https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/badges/main/coverage.svg?job=test-hiring-service)
TA: ![TA coverage](https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/badges/main/coverage.svg?job=test-ta-service)

# CSE2115 - Project

### Running 
`gradle bootRun`

### Testing
```
gradle test
```

To generate a coverage report:
```
gradle jacocoTestCoverageVerification
```


And
```
gradle jacocoTestReport
```
The coverage report is generated in: build/reports/jacoco/test/html, which does not get pushed to the repo. Open index.html in your browser to see the report. 

### Static analysis
```
gradle checkStyleMain
gradle checkStyleTest
gradle pmdMain
gradle pmdTest
```

### Notes
- You should have a local .gitignore file to make sure that any OS-specific and IDE-specific files do not get pushed to the repo (e.g. .idea). These files do not belong in the .gitignore on the repo.
- If you change the name of the repo to something other than template, you should also edit the build.gradle file.
- You can add issue and merge request templates in the .gitlab folder on your repo. 

## Group Members

| 📸 | Name | Email |
|---|---|---|
| ![](https://gitlab.ewi.tudelft.nl/uploads/-/system/user/avatar/3525/avatar.png?width=400) | A.F. Yucel | A.F.Yucel@student.tudelft.nl |
| ![](https://gitlab.ewi.tudelft.nl/uploads/-/system/user/avatar/3658/avatar.png?width=400) | J.D.M. SavelKoul | J.D.M.Savelkoul@student.tudelft.nl |
| ![](https://gitlab.ewi.tudelft.nl/uploads/-/system/user/avatar/3404/avatar.png?width=400) | M. Mladenov | M.Mladenov@student.tudelft.nl |
| ![](https://gitlab.ewi.tudelft.nl/uploads/-/system/user/avatar/3883/avatar.png?width=400) | M.C.A. De Wit | M.C.A.deWit@student.tudelft.nl |
| ![](https://gitlab.ewi.tudelft.nl/uploads/-/system/user/avatar/3757/avatar.png?width=400) | M.A.A. Kienhuis | M.A.A.Kienhuis@student.tudelft.nl |
| ![](https://gitlab.ewi.tudelft.nl/uploads/-/system/user/avatar/3776/avatar.png?width=400) | W. Smit | W.Smit-1@student.tudelft.nl |


