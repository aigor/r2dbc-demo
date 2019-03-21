
# Presentation plan for "Building a Reactive DB driver with R2DBC"

## Abstract
Reactive programming is popular nowadays.
RxJava and Project Reactor made reactive programming an everyday routine.
Spring Framework brought Spring WebFlux for high-performance web-applications and Spring Cloud for reactive messaging solutions.
Now, we are tackling the last frontier where blocking approaches are still winning: database access.
During this talk, we are going to implement our own toy reactive database driver (R2DBC-complaint) and use it for accessing data via Reactive Spring Data.

## Aim

Teach the audience to work with reactive database access (R2DBC).

## Objectives

- Describe the problem of reactive database access in the Spring ecosystem.
- Describe the R2DBC initiative and R2DBC API.
- Show how to apply reactive programming (with Project Reactor) for implementing toy reactive DB driver.
- Highlight when such an approach is applicable and when it may cause issues.

## Out of scope

- The basics of reactive programming.
- Describing how R2DBC drivers work for other DBs (Postgres, MySQL, MS SQL, H2).
- Describing details of how reactive Spring Data module uses R2DBC for reactive repositories.
- How to implement transactional support with R2DBC.

## Candies for the Audience

- The participants will have an opportunity to interact with the live demo deployed to the Cloud. A mobile-friendly web-application that uses our toy driver to access data for visual analytics. 

## Short Presentation Plan

- Getting acquainted with the audience & describing the objectives.
- The introduction to the R2DBC project: the aim, API, modules, issues.
- Describing on slides the plan how we are going to implement the R2DBC driver for PrestoDB.
- Live coding demo (with live templates or git commits): implementing `r2dbc-presto` driver.
- Demoing the application that uses the `r2dbc-presto` driver.
- Summary
- Q&A

## Presentation timing (45 min)

- Getting acquainted with the audience & describing the objectives (3 min)
  - Who I am
  - Describing the objectives of the presentation
- The introduction to the R2DBC project: the aim, API, modules, issues (10 min)
  - The aim of the project & the story behind the ADBA vs R2DBC battle
  - A short introduction to R2DBC ecosystem: R2DBC API, R2DBC Client, available drivers
  - A short explanation of how reactive Spring Data uses R2DBC (similar to how reactive Mongo repositories work)
  - A short list of R2DBC Issues: not mature enough, maybe beaten by ADBA, reactive approach may not fit SQL databases at all
- Describing on slides the plan how we are going to implement the R2DBC driver for PrestoDB (5 min)
  - Lightning fast introduction to PrestoDB
  - Lisghtning fast description of PrestoDB HTTP API for querying data with SQL
  - Describing what R2DBC API we need to implement: `ConnectionFactory`, `Connection`, `Statement`, `ColumnMetadata`, `Result`
- Live codding demo (with live templates or git commits): implementing `r2dbc-presto` driver (15 min)
  - Given: We have running PrestoDB and set of integration tests
  - Implement `ConnectionFactory`
  - Implement `Connection`
  - Implement `Statement`: making an HTTP request with the reactive client (from Java 11), handling a response
  - Implement `ColumnMetadata`
  - Implement `Result` by parsing streaming results
  - Handling of batched responses (making many HTTP requests to stream all results of one request)
- Demoing the application that uses the `r2dbc-presto` driver (5 min)
  - Showing synchronous application which uses an official JDBC driver for accessing data
  - The same application uses our R2DBC driver for accessing data
  - Comparing resource consumption between synch and async applications (allocated memory, blocked threads)
- Summary (2 min)
  - Sharing useful links
- Q&A (5 min)

