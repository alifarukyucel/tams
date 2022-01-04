

# TAMS documentation

## The hiring process

### Signing your contract as a TA
Once a lecturer has decided on who they want to be their TA's their contracts are created.
These contracts contain how many hours the TA is allowed to work and what their expected duties are.
To finalize the contract a TA will need to agree to these terms and they can do this by submitting 
the course id to /contracts/sign.

```json
// PUT: /contracts/sign

{
  "course": "CSE2310"
}
```

A 200 OK is returned if the request succeeds.  
If the contract isn't signed then the TA will not be able to declare hours.

## Dealing with worked hours

### Declaring worked hours
Once hours have been worked it is important to declare these to the system so that they can be reviewed.
For this a request should be made to /hours/submit with the information required to declare hours. An example can be seen below

```json
// POST: /hours/submit
{
    "course": "CSE2310",
    "workedTime": 5,
    "date": "TODO",
    "desc": "I've worked 5 hours on the shared lab"
}
```

If the requests succeeds a 200 OK is returned.  
In the case that an error is returned the hours have not been declared. See the error message for more information as to why the request didn't succeed.

### Approving and rejecting declared hours
As a lecturer it is also important that those hours eventually get paid out. This requires a lecturer to review the worked hours and mark them as approved or reject them.
In this case a request is made to /hours/approve with the ID of the worked hours and a boolean describing if the hours are accepted or rejected.
 ```json
// PUT: /hours/approve
{
    "id": "11b2b17f-886c-4e5f-8b30-240a5554a9b8"
    "accept": true
}
```

If the request succeeds a 200 OK is returned.  
If the hours had already been approved / rejected a 409 CONFLICT error is returned.

### What if I wrongfully approved hours

If hours were wrongfully approved you should contact your administrators so that they can quickly fix the error.
If you accidentally rejected certain hours feel free to contact the respective TA so that they can declare their hours again.

The system will always block you from approving hours if this were to go over the amount of hours specified in the contract.