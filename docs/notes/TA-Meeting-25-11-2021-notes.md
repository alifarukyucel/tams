**Question 1: The system shall v.s. as a lecturer as..**

He understands! He discussed with the TA's here and things are not very straightforward on their end. He is grading us with a given ruberic by the course. He is confident that he is right and if it not he will "defend" us against the course if it is wrong.

**Question 2: When is a microservice is too large and too small?**

Bike shop example: order processor that stores information locally. You can instantiate more of them since they all have their information locally.

Our microservices should be "stateless": the microservice just passes data.

**In depth question:** what to do with the Hour Registration microservice.

We should settle on around 3 to 5 microservices and be clever of where data is stored so that there is as little "waiting" around.

We will have one database server (postgesql) that will hosts a seperate database per microservice. 

**Question 3: "When does course selection start?"**

Starts three weeks before the course starts and ends when the course starts

**Authentication and post examples.**

He advices that people can sign up via a simple POST. Example can be given in Postman.

**Grading**

He will grade us on the requirments, assignments and refactoring.

UML is expected for the architecture.

**Gitlab**

We should do all the work on Gitlab so he can see our work.

We can just put the final documents (like diagroms) on Gitlab and show how we work on them with issues. 

When grading George is looking for the definition of task, how we split the tasks among the group, priorisation, spring reprospectives and our response to feedback.

We should react occordingly.

He will be looking into our Gitlab next week.

**When to contact George?**

When there is a "problem". Something we think is a roadblock for us which we cannot solve on our own.

**Scrum**
We want to use scrum as much as possible (and as earlier as problem).
Make some scrum issues, assign people and estimate how much time an issue will take.

Scrum reprospective: we discuss what we achieved this sprint.

**Lolipop Notation**

The more in detail we do this - the better. We should try to match the most detailed slide of the SEM lecture. However a simple diagram is also fine. 

George advices Lucidchart for this.

**Example API Request**

He thinks this is very good! This is not held against us in anyway and thumbs up for that. Try to make it during development.
