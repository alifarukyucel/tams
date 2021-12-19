Author: Maurits Kienhuis  
feel free to modify and update this document.
## A guide to testing 101

Hey everybody, welcome to this short document about how you should test your code.
In this document I'm planning to quickly explain some pitfalls of testing with our current framework.
We'll also quickly go over the requested pattern you should be following from your time in sqt!


### A short index

- Triple A developers.
- Mocking.
- Breaking through authentication.
- Resolving error codes.


### Triple A developers.

Triple A developers refers to the AAA pattern we used in SQT. AAA standing for Arrange, Act, Assert.
In the Arrange stage you are expected to declare your variables before you do anything.
This also includes mocking, setting up database connections and what not.
Then in the Act stage you would like to make only 1 function call if possible and that call should be pointed at the function you are testing .
You then finish this off in the Assert stage. Here you want to check whether the function call actually succeeded.
When doing this you have to be careful, it is very easy to mock and verify everything but this leads to quite rigid tests.
Preferable a test only checks that the function achieved its goal, not how it achieved that goal.

A quick look at the AAA pattern.

```java
class ContractServiceTest {
    @Test
    void save() {
        // arrange
        Contract contract = new ConcreteContractBuilder().netId("Gert").courseId("CSE2310").build();

        // act
        contract = contractService.save(contract);

        // assert
        Contract expected = contractRepository.getOne(
            new ContractId(contract.getNetId(), contract.getCourseId()));
        assertThat(contract).isEqualTo(expected);
    }
}
```

### Mocking

Now that triple A pattern is all fine and dandy, but you should prevent dependencies throughout the project as well.
With that we mean to say that it is important that you are testing the behaviour of the code you want to test and not some underlying system.
This is an issue when for example a dependent implementation doesn't exist yet, is very expensive, or is still being reworked.
In those cases we turn to mocking to help us on our way.
However, Spring uses something called dependency injection, which means we cannot just mock one of our services like you would in regular java.
Instead of doing that we basically introduce a **profile** for spring to use instead of the regular dependency.
By creating our profile and marking it primary spring will now use this class for its dependency injections.
Almost like we're poisoning something :p

A profile class should look something like this
```java
/**
 * A configuration profile to allow injection of a mock Course Information.
 */
@Profile("mockCourseInformation")
@Configuration
public class MockCourseInformationProfile {

    /**
     * Mocks the Course Information.
     *
     * @return A mocked Course Information.
     */
    @Bean
    @Primary
    public CourseInformation getMockCourseInformation() {
        return Mockito.mock(CourseInformation.class);
    }
}
```
Where `@Profile` tells spring the name of this profile.
`@Configuration` marks this class as part of the spring pipeline.
`@Bean` Allows Spring to use this class for the generation of the `CourseInformation` object (like a factory of some sorts).
`@Primary` Tells spring to use this bean/factory to create the CourseInformation instead of all the others.


But you aren't done yet. You also need to make sure that these profiles are activated.
```java
@ActiveProfiles({"test", "mockCourseInformation"})
class ContractControllerTest {

    @Autowired
    private CourseInformation courseInformation;
}
```
Here we use the `@ActiveProfiles` tag to tell spring what profiles we would like to activate for this test class.
In this case we want the `"test"` profile and the `"mockCourseInformation"` profile to be active.
`test` tells spring that we are running a test and is a default profile,
`mockCourseInformation` is the name of our mocked profile, this will activate the profile for dependency injection.
Now that our mocked profiles are activated we can use `@Autowired` to use springs dependency injection to feed the into our class.

Congratulations, you should now have a mocked class in your test cases!

### Breaking through authorization

Now with mocking under your belt it is time to finally write a working integration test.
The reason for this is that our modules are fully authenticated, meaning you need a valid token to do requests.
But we don't have such a token because the authentication service isn't running during testing.
This results in us having to mock the TokenVerifier and the AuthManager to play nice.

By using your knowledge of mocking you should be able to mock these classes on your own so we will look at what you need to setup.
```java
class test {
    
    @Autowired
    private TokenVerifier mockTokenVerifier;

    @Autowired
    private AuthManager mockAuthenticationManager;
    
    private String netId = "MAAKienhuis";
    
    @BeforeEach
    void setUp() {
        when(mockAuthenticationManager.getNetid()).thenReturn(netId);
        when(mockTokenVerifier.validate(anyString())).thenReturn(true);
        when(mockTokenVerifier.parseNetid(anyString())).thenReturn(netId);
    }
}
```

We can just use default mockito syntax to mock our objects. In this case we need to shut down 3 methods.
First we want the `AuthManager` return the netId of the person as who we are logging in.
This is the same `AuthManager` as you are using in the controllers.
Then we want to make sure that the `TokenVerifier` accepts anyString as a valid token,
if you wish you can also have it only accept a specific token but for a general use this will work just fine.
Lastly we also want to make sure that when the verifier is called for decoding it returns the correct netId.

Now with our mocks in place we are ready to make our request!

```java
class test {
    // mocks removed for brevity.

    @Test
    void signExistingContract() throws Exception {
        // arrange
        Model model = Model.builder().build();

        // act
        ResultActions results = mockMvc.perform(put("/test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(model))
            .header("Authorization", "Bearer Pieter"));

        // assert
        results.andExpect(status().isOk());
    }
}
```

Making requests is almost the same as in OOPP but with one small difference.
There needs to be at least a token in the header,
this is because otherwise the method that calls our (mocked) verifier isn't triggered and the authorization system will kick us out.
As long as you include `.header("Authorization", "Bearer [any String]")` the call will manage to pass through our filters.


### Resolving error codes

For debugging purposes it might be nice to double-check the state of your test before going into the Assert phase.
It might reveal something isn't working as expected.

#### 400 BAD REQUEST
The server couldn't read your request, make sure you are sending the correct model.
I've seen this also being used in the code base, but it is generally bad practice as 400 should indicate bad request syntax.
If you are throwing a bad_request manually you might want to double-check if it can be changed to one of the other below errors.

#### 401 UNAUTHORIZED
Double check that your mocking of the auth pipeline works correctly and ask others to check.
Also make sure you are sending at least a token with the request.
If you are receiving a 401 then your requests failed to get through the authentication of the module.
Don't throw this yourself.

#### 403 FORBIDDEN
But what if I want to throw 401? You throw this one.
This means they are authenticated by the server, hence not 401, but do not have the permissions to do this action.
Think of a non-responsible lecturer trying to touch responsible lecturer endpoints.

#### 404 NOT FOUND
This error will occur in 2 ways.
Either you aren't requesting the right address, double check what global map your controller has and what it is looking for locally.
Or second it is thrown by the method, often with an included reason.
In the latter case it is often paired with a `NoSuchElementException`.

#### 409 CONFLICT
For now we're using 409 to show a CONFLICT status.
This means that the method is trying to change something that would invalidate the state of our database.
We've seen it being thrown by `IllegalArgumentExceptions`.

#### 500 INTERNAL SERVER ERROR
Congrats you broke the server, it probably threw an unexpected exception.


