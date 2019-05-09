# API Review Process

The Tilkynna team takes API usability extremely seriously. Thus, we generally review every single API that is added to the product. This page discusses how we conduct design reviews for components that are open sourced.

## Which APIs should be reviewed?

We review all API changes. 

## Process

## Steps

1. **Requester files an issue**. The issue template contains all information necessary. The description should contain a speclet that represents a sketch of the new APIs, including samples on how the APIs are being used. The goal isn't to get a complete API list, but a good handle on how the new APIs would roughly look like and in what scenarios they are being used.

2. **We assign an owner**. We'll assign a dedicated owner from our side that
sponsors the issue. This is usually the area owner for which the API proposal or design change request was filed for.

3. **Discussion**. The goal of the discussion is to help the assignee to make a
decision whether we want to pursue the proposal or not. In this phase, the goal
isn't necessarily to perform an in-depth review; rather, we want to make sure
that the proposal is actionable, i.e. has a concrete design, a sketch of the
APIs and some code samples that show how it should be used. If changes are necessary, the requester is encouraged to edit the issue description. This allows folks joining later to understand the most recent proposal. To avoid confusion, the requester should maintain a tiny change log, like a bolded "Updates:" followed by a bullet point list of the updates that were being made.

4. **Owner makes decision**. When the owner believes enough information is available to make a decision, she will update the issue accordingly:

    * **Mark for review**. If the owner believes the proposal is actionable, she will label the issue with `api-ready-for-review`.
    * **Close as not actionable**. In case the issue didn't get enough traction to be distilled into a concrete proposal, she will close the issue.
    * **Close as won't fix as proposed**. Sometimes, the issue that is raised is a good one but the owner thinks the concrete proposal is not the right way to tackle the problem. In most cases, the owner will try to steer the discussion in a direction that results in a design that we believe is appropriate. However, for some proposals the problem is at the heart of the design which can't easily be changed without starting a new proposal. In those cases, the owner will close the issue and explain the issue the design has.
    * **Close as won't fix**. Similarly, if proposal is taking the product in a direction we simply don't want to go, the issue might also get closed. In that case, the problem isn't the proposed design but in the issue itself.

5. **API gets reviewed**. The group conducting the review will take notes and provide feedback. Multiple outcomes are possible:

    * **Approved**. In this case the label `api-ready-for-review` is replaced
    with `api-approved`.
    * **Needs work**. In case we believe the proposal isn't ready yet, we'll
    replace the label `api-ready-for-review` with `api-needs-work`.
    * **Rejected**. In case we believe the proposal isn't a direction we want to go after, we simply write a comment and close the issue.

## Pull requests

Pull requests against **tilkynna** shouldn't be submitted before getting approval. Also, we don't want to get work in progress (WIP). The reason being that we want to reduce the number of pending PRs so that we can focus on the work the community expects we take action on.

If you want to collaborate with other people on the design, feel free to perform the work in a branch in your own fork. If you want to track your TODOs in the description of a PR, you can always submit a PR against your own fork. Also, feel free to advertise your PR by linking it from from the issue you filed against **Tilkynna** in the first step above.

## API Design Guidelines

The Tilkynna reporting service follows best practice RESTful design principles. RESTful is the name given for web services written by applying the REST architectural concept which focuses on system resources and how the state of the resource should be transported over HTTP protocol for different types of consumers written in different programming languages. In RESTful web service http methods like GET, POST, PUT and DELETE can be used to perform CRUD operations. JSON and XML are the two markup languages that can be used in restful web services.

#### REST Methods

* GET (Retrieve Resource)
* POST (Create Resource)
* PUT (Create or Update Resource)
* DELETE (Remove Resource)
* OPTIONS (Check which Techniques are supported)
* HEAD (Returns meta information about the request URL)

#### Security for RESTful Web Services

* Basic
* OAuth2
* Custom / Third Party security protocol

#### Content Negotiation

Different service consumers may have differing requirements for how data provided by a given service capability needs to be formatted or represented. When you put data in the payload of a HTTP message within a request or a response, you must specify the corresponding format using a Content-Type header. Usable values correspond to media types (initially called MIME types). A comprehensive list of media types is available on IANA website: http://www.iana.org/assignments/media-types/media-types.xhtml.

#### HTTP Cache

Using cache allows the REST service to scale. Caching is performed by the browser. Cache Control Headers -

* no-cache
* no-store
* max-ages
* max-age
* expire
* cache-validation
 
#### Advantages of RESTful Web Services

* Lightweight: Easy to consume from mobile devices also.
* Easy to expose: Little or no restrictions on output format and communication protocol.
Most RESTful services use HTTP protocol. 
Entire web is based on HTTP and is built for efficiency of HTTP. 
Things like HTTP caching enable RESTful services to be effective.
* High Performance: Less XML & SOAP overhead and more caching enable RESTful services to be highly performant. 

#### Best practices in using HTTP methods with RESTful Web Services

* GET: Should not update anything. Should be idempotent (same result in multiple calls). Possible Return Codes 200 (OK) + 404 (NOT FOUND) + 400 (BAD REQUEST)
* POST: Should create new resource. Ideally return JSON with link to newly created resource. Same return codes as get possible. In addition : Return code 201 (CREATED) is possible.
* PUT: Update a known resource. ex: update client details. Possible Return Codes : 200 (OK)
* DELETE: Used to delete a resource.

#### Richardson Maturity Model
[Restful API Resource Naming](https://restfulapi.net/resource-naming/)

Defines the maturity level of a RESTful Web Service. Following are the different levels and their characteristics -

* Level 0: Expose web services in REST style. Expose action based services (http://server/getPosts, http://server/deletePosts, http://server/doThis, http://server/doThat etc) using REST.
* Level 1: Expose Resources with proper URI’s (using nouns). Ex: http://server/accounts, http://server/accounts/10. However, HTTP Methods are not used.
* Level 2: Resources use proper URI's + HTTP Methods. For example, to update an account, you do a PUT to . The create an account, you do a POST to . Uri’s look like posts/1/comments/5 and accounts/1/friends/1.
* Level 3: HATEOAS (Hypermedia as the engine of application state). You will tell not only about the information being requested but also about the next possible actions that the service consumer can do. When requesting information about a facebook user, a REST service can return user details along with information about how to get his recent posts, how to get his recent comments and how to retrieve his friend’s list.

We only expose Level 2 API's per the above definition, we do not go to Level 3.

## Useful References
[Restful API Resource Naming](https://restfulapi.net/resource-naming/)



