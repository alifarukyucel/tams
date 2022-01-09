# Functional tests

### The system shall allow users to authenticate using their netid and password, and then issue a token that is used to authenticate the user in other API requests.

#### Logging in with a valid account
1. Register a user
2. Log in with that username and password
3. Verify that a token has been issued

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/25387e78d1754100e812ce2152a50d3b32e14e1d/authentication-microservice/src/test/java/nl/tudelft/sem/tams/authentication/integration/UsersTests.java#L59
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/25387e78d1754100e812ce2152a50d3b32e14e1d/authentication-microservice/src/test/java/nl/tudelft/sem/tams/authentication/integration/UsersTests.java#L115

#### Logging in with an invalid password
1. Register a user
2. Log in with that username and another password
3. Verify that 403 has been returned

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/25387e78d1754100e812ce2152a50d3b32e14e1d/authentication-microservice/src/test/java/nl/tudelft/sem/tams/authentication/integration/UsersTests.java#L59
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/25387e78d1754100e812ce2152a50d3b32e14e1d/authentication-microservice/src/test/java/nl/tudelft/sem/tams/authentication/integration/UsersTests.java#L193

#### Logging in with an invalid username
1. Register a user
2. Log in with another username and a password
3. Verify that 403 has been returned

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/25387e78d1754100e812ce2152a50d3b32e14e1d/authentication-microservice/src/test/java/nl/tudelft/sem/tams/authentication/integration/UsersTests.java#L59
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/25387e78d1754100e812ce2152a50d3b32e14e1d/authentication-microservice/src/test/java/nl/tudelft/sem/tams/authentication/integration/UsersTests.java#L163

### The system shall allow users to register using a netid and password.

#### Registering a new user
1. Register a user with a new username and password
2. Verify the response is 200

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/25387e78d1754100e812ce2152a50d3b32e14e1d/authentication-microservice/src/test/java/nl/tudelft/sem/tams/authentication/integration/UsersTests.java#L59

#### Registering a user with an existing username
1. Register a user
2. Register another user with the same username
3. Verify the response is 400

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/25387e78d1754100e812ce2152a50d3b32e14e1d/authentication-microservice/src/test/java/nl/tudelft/sem/tams/authentication/integration/UsersTests.java#L85 

### The system shall let students candidate themselves as a TA by sending a request to the API including a short motivation statement and their grade for that course.

#### Applying to become a ta
1. Submit model
2. Verify 200
3. Verify application has been saved

**Relevant tests**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L150
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L205

#### The system shall not accept a TA application for a course if they do not submit a grade or that grade is lower than 6.0.
1. Submit model with too low grades
2. Verify 403
3. Verify application has not been saved

**Relevant tests**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L94
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L122
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L177

Respective underlying service code is boundary tested

### The system shall notify students if they have been accepted or rejected as a TA for a particular course by allowing them to submit an API request to retrieve that.

#### Check existing application for status
1. Submit application to system with user 1
2. With a lecturer account, set application status
3. Submit course id to check the status as user 1
4. Verify 200 is returned
5. Verify the returned status

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L449
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L476
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L503

#### Check non-existing application for status
1. Submit course id for which this user has no application submitted
2. Verify 404

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L421

### The system shall let users create a course including a unique course id, start date, and number of students. After the course is created, the user shall be assigned as its responsible lecturer.

#### Creating a course
1. Submit course model to endpoint
2. Verify the response is 200
3. Verify data was saved correctly

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/course-microservice/src/test/java/nl/tudelft/sem/tams/course/integration/CourseTests.java#L137

#### Creating the same course twice
1. Submit course model
2. Verify 409
3. Verify original data has not been overwritten

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/e9b609c729370b125e164f677a1ebeee23b881b0/course-microservice/src/test/java/nl/tudelft/sem/tams/course/integration/CourseTests.java#L164

### The system shall let students fetch their TA contract including course name, total amount of hours, extra information, and whether the contract is signed, in JSON format from the API.

#### Retrieving own contract
1. Create a course
2. Create a contract for a TA
3. As that TA, make a request to retrieve the contract
4. Verify response status is 200
5. Verify contract details are correct

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/9236d75c5af1c0842da8ae57a49478b52bf35be0/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/integration/ContractControllerTest.java#L339

#### Retrieving a contract of another TA
1. Create a course
2. Create a contract for a TA
3. As another TA, make a request to retrieve the contract
4. Verify response status is 403

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/9236d75c5af1c0842da8ae57a49478b52bf35be0/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/integration/ContractControllerTest.java#L360

### The system shall let students sign their TA contract by sending an API request.

#### Signing a contract
1. Submit course id for which the user has a contract
2. Verify the response is 200

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/ContractControllerTest.java#L144

#### Signing an already signed contract
1. Submit course id for which the user has a contract
2. Submit the same course id again
3. Verify the response is 409

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/ContractControllerTest.java#L165

#### Signing a non-existent contract
1. Submit course id for which the user has no contract posted
2. Verify the response is 404

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/ContractControllerTest.java#L188
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/ContractControllerTest.java#L208

### The system shall let lecturers accept students who have applied to TA a course by sending their netid, the respective course id, hours to be worked, and extra contract information to the API. The application status of that student shall then be changed to ‘accepted’.

#### Detect submission on the hiring microservice side
1. Submit contract model to the hiring service
2. Verify ta-microservice is called with correct model
3. Verify 200

**Relevant tests**:
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L883

#### Create new contract on the ta microservice side
1. Submit model, usually submitted by hiring microservice
2. Verify 200

**Relevant tests**:
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/ContractControllerTest.java#L375
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/ContractControllerTest.java#L414

### The system shall expose the amount of hours and the comments for those hours written by TAs that still need to be approved or rejected for their course for each TA to the responsible lecturers via the API. 

#### Fetch hour declarations for course you are responsible for
1. Submit course id
2. Verify 200 OK
3. Verify correct list is returned

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L398

#### Fetch hour declarations with illegal arguments
1. Submit course id, the user does not have lecturer status for this course
2. Verify 403

**Relevant tests**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L437

### The system shall let lecturers approve or reject working hours declared by TAs of their own courses by posting to the API.

#### Approve existing hours
1. Submit hour declaration id and approved state to endpoint
2. Verify 200
3. Verify hours are marked as approved and reviewed

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/66b75de0d255c736e1c237c489af1ce954f80b29/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/integration/HourControllerTest.java#L247

#### Reject hours
1. Submit hour declaration id and rejected state to endpoint
2. Verify 200
3. Verify hours are marked as reviewed but not approved

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/66b75de0d255c736e1c237c489af1ce954f80b29/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/integration/HourControllerTest.java#L268

#### Re-approve existing hours
1. Submit hour declaration id and true to endpoint
2. Submit hour declaration id and true to endpoint again
3. Verify 409

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L267

#### Approve more hours than allowed by contract
1. Create contract with 20 allowed hours
2. Submit hour declaration of 15 hours
3. Approve that hour declaration
4. Submit hour declaration of 6 hours
5. Try to approve that declaration
6. Verify 409
7. Verify declaration has not been marked approved or reviewed

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/fe182b81efab8afb6a98bc88c40cf0a46e185dd8/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/integration/HourControllerTest.java#L312

#### Approve hours you shouldn't be able to approve
1. logged-in user does not have approval permissions for this course
2. Submit hour declaration id and true to endpoint
3. Verify 403

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L290

### The system shall let lecturers request a JSON list of all TA applications for one of their courses including netid, grade, status of application, and the existence of former and present TA contracts of students for the same or other courses as well as the rating (if available).

#### Retrieve all pending TA-applications for a course you are responsible for
1. Apply for TA with different users for the course
2. Create and rate contracts for (some of) those users
3. Submit course id
4. Verify 200 OK
5. Verify the correct list is returned

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L683

#### Retrieve all pending applications for a course you are not responsible for
1. Submit course id, the user does not have lecturer status for this course
2. Verify 403

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L732

### The system shall not allow a student to candidate as TA later than 3 weeks before the course starts.

#### Apply to a course after the deadline
1. Create a course with a deadline less than 3 weeks in the future
2. Try to apply for created course
3. Verify 403

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/a4fa9f4cd4264ab83d292f76b2252076c8ccb2ba/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L245

### The system shall let lecturers reject students who have applied to TA a course by sending their netid and the respective course id.

#### Reject a student for a course you are responsible for
1. Submit a ta-application for a course
2. Submit course id and netid to reject
3. Verify 200 OK
4. Check status of ta-application to have changed to "rejected"

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L540

#### Reject a student for a course you are not responsible for
1. Submit a ta-application for a course
2. Submit course id and netid to reject
3. Verify 403
4. Check status of ta-application to not have changed

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L570

#### Reject a student that hasn't even applied
1. Submit course id and netid to reject
2. Verify 404

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L600

#### Reject a ta-application that is not pending
1. Submit a ta-application for a course
2. Submit course id and netid to accept / reject
3. Submit course id and netid to reject this again
4. Verify 409
5. Check status of ta-application to not have changed after 2

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L636

### The system shall allow students to withdraw their candidacy unless they can no longer apply as a TA.

#### Withdraw within allowed timeframe
1. Submit valid application
2. Attempt withdrawal more than 3 weeks before the course starts
3. Verify 200

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L378

Respective underlying service code is boundary tested

#### Withdraw outside allowed timeframe
1. Submit valid application
2. Attempts withdrawal less than 3 weeks before the course starts
3. Verify 403

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L513

Respective underlying service code is boundary tested

### The system shall not allow a student to candidate as TA if they have already applied to be a TA for 3 courses that quarter.

#### Allowed to become a ta
1. Submit two applications for the same user
2. Submit a third application for the same user
3. Verify 200

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L333

#### Not allowed to become a ta
1. Submit 3 applications for the same user
2. Submit a fourth application for the same user
3. Verify 403

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/controllers/HiringControllerTest.java#L288

### The system shall not allow a TA to declare hours if this were to go over the limit stated in their contract.

#### Block declaring hours if going over hours stated in contract
1. Declare hours going over possible remaining hours in contract
2. Verify 409

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L225

Respective underlying service code is boundary tested

#### Block approving hours by lecturer if going over contract
1. Make sure approving hour declaration would go over budget 
2. Submit hour declaration id and true to approve 
3. Verify 409

**Relevant tests:**

Respective underlying service code is boundary tested

### The system shall let a lecturer rate a TA based on their performance by sending an API request.

#### Normally rate a contract
1. Submit course id and net id of the student, include a respective rating between 0 and 10
2. Verify 200
3. Verify rating has been saved

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/ContractControllerTest.java#L568

#### Rate a contract with invalid ratings
1. Submit course id and net id of the student, include a respective rating outside 0 and 10
2. Verify 400
3. Verify rating did not change

**Relevant tests**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/ContractControllerTest.java#L636
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/ContractControllerTest.java#L657

Respective underlying service code is boundary tested

### The system shall not allow a lecturer to hire more than 1 TA for every 20 students in the course.

#### Block hiring when ratio between student and ta becomes too large
1. Create course of 40 students
2. Create a TA contract for that course
3. Create another TA contract for that course
4. Create a third TA contract for that course
7. Verify 400
8. Verify contract has not been saved

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/7f82e170910fdf8532e934f9b5184127a5ed7036/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/integration/ContractControllerTest.java#L415

Respective underlying service code is boundary tested

### The system shall allow a TA to specify how many hours they actually worked on the course by posting the course id and the amount of hours to the API.

#### Submit hour declaration
1. Submit hours
2. Verify 200
3. Verify hours have been properly saved

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L142

#### Submit hour declaration with missing db entries such as course or contract
1. Make sure no valid course or contract exists for which hours are declared
2. Declare hours
3. Verify 404

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L180
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L202

### The system shall upon request create a list of recommendees, the best candidates based on experience and rating. The returned list shall be in the same format as the list returned by the endpoint to retrieve all applicants.

#### Retrieve a list of the "best" candidates for a course you are responsible for 
1. Apply for TA with different users for the course
2. Create and rate contracts for (some of) those users
3. Submit course id and amount of candidates
4. Verify 200 OK
5. Verify the correct list is returned

**Relevant tests:**
-https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L838

#### Retrieve a list of the "best" candidates for a course you are responsible for with a zero-or-negative amount
1. Submit course id and a 0 / negative amount of candidates
2. Verify 200 OK
3. Verify an empty list is returned

**Relevant tests:**
-https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L783

#### Retrieve a list of the "best" candidates for a course you are responsible for with a too high amount
1. Apply for TA with different users for the course
2. Create and rate contracts for (some of) those users
3. Submit course id and an amount higher than the number of applicants.
4. Verify 200 OK
5. Verify a correct list of all applicants (sorted by recommendation) is returned.

**Relevant tests:**
-https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L803

#### Retrieve a list of the "best" candidates for a course you are not responsible for
1. Submit course id, the user does not have lecturer status for this course
2. Verify 403

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L763

### The system shall notify the students of their TA approval via an automatically sent email.

#### Send email when creating contract (and by extension approving application) 
1. As a lecturer, create a course
2. As a student, apply to be a TA for that course
3. As a lecturer, approve that application
4. Verify an approval email has been sent

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/hiring-microservice/src/test/java/nl/tudelft/sem/tams/hiring/integration/HiringControllerTest.java#L216
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/d8c0ef23d08c51ee322ce335da99aa7c9478291f/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/integration/ContractControllerTest.java#L414
    
### The system shall let the responsible lecturer of a certain course add other users as responsible lectures of that course with the same rights via an API request.

### The system shall allow any responsible lecturer to remove other responsible lecturer other than themselves from that course via an API request.

