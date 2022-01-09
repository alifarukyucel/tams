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

### The system shall not accept a TA application for a course if they do not submit a grade or that grade is lower than 6.0.

### The system shall notify students if they have been accepted or rejected as a TA for a particular course by allowing them to submit an API request to retrieve that.

### The system shall let users create a course including a unique course id, start date, and number of students. After the course is created, the user shall be assigned as its responsible lecturer.

### The system shall let students fetch their TA contract including course name, total amount of hours, extra information, and whether the contract is signed, in JSON format from the API.

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
1. Submit hour declaration id and desired state to endpoint
2. Verify 200

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L247

#### Re-approve existing hours
1. Submit hour declaration id and true to endpoint
2. Submit hour declaration id and true to endpoint again
3. Verify 409

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L267

#### Approve hours you shouldn't be able to approve
1. logged-in user does not have approval permissions for this course
2. Submit hour declaration id and true to endpoint
3. Verify 403

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L290

### The system shall let lecturers request a JSON list of all TA applications for one of their courses including netid, grade, status of application, and the existence of former and present TA contracts of students for the same or other courses as well as the rating (if available).

### The system shall not allow a student to candidate as TA later than 3 weeks before the course starts.

### The system shall let lecturers reject students who have applied to TA a course by sending their netid and the respective course id.

### The system shall allow students to withdraw their candidacy unless they can no longer apply as a TA.

### The system shall not allow a student to candidate as TA if they have already applied to be a TA for 3 courses that quarter.

### The system shall not allow a TA to declare hours if this were to go over the limit stated in their contract.

#### Block declaring hours if going over hours stated in contract
1. Declare hours going over possible remaining hours in contract
2. Verify 409

**Relevant tests:**
- https://gitlab.ewi.tudelft.nl/cse2115/2021-2022/sem-group-13b/sem-repo-13b/-/blob/4408c65270b1fc9c2d355fb5856873aa74828ff1/ta-microservice/src/test/java/nl/tudelft/sem/tams/ta/controllers/HourControllerTest.java#L225

Respective underlying service code is also boundary tested

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
1. Save an accepted application in the hiring service
2. Mock Course information to return x students
3. Submit a new application
4. Verify 409
5. Verify application has not been saved

**Relevant tests:**


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

### The system shall have the functionality to send emails.

### The system shall notify the students of their TA approval via an automatically sent email.

### The system shall let the responsible lecturer of a certain course add other users as responsible lectures of that course with the same rights via an API request.

### The system shall allow any responsible lecturer to remove other responsible lecturer other than themselves from that course via an API request.

