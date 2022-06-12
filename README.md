# bare-graphql ⚛️

Bare GraphQL is a **very small** set of APIs to execute GraphQL in a completely unsafe manner. 

Mainly made to be used from [Kotlin scripts](https://kotlinlang.org/docs/custom-script-deps-tutorial.html) like [update-graphql-schema.main.kts](https://github.com/apollographql/update-graphql-schema/blob/main/update-schema.main.kts#L125).

For other projects, [Apollo Kotlin](https://github.com/apollographql/apollo-kotlin) provides codegen and way more compile time guarantees.

## Installation

```kotlin
@file:DependsOn("net.mbonnin.bare-graphql:bare-graphql:0.0.1")
```

## Usage

Get the description of a [Github Repository](https://docs.github.com/en/graphql/reference/queries#repository):

```kotlin
val query = """
    {
      repository(owner: "martinbonnin", name: "bare-graphql") {
        description
      }
    }
""".trimIndent()

val headers = mapOf("Authorization" to "bearer $token")

println(graphql(operation = query, headers = headers)["data"].asMap["repository"].asMap["description"])
// "a **very small** set of APIs to execute GraphQL in a completely unsafe manner"
```

It also works with variables:

```kotlin
val query = """
    query(${'$'}owner: String!, ${'$'}name: String!){
      repository(owner: ${'$'}owner, name: ${'$'}name) {
        description
      }
    }
""".trimIndent()

val headers = mapOf("Authorization" to "bearer $token")

val variables = mapOf(
    "owner" to "martinbonnin",
    "name" to "bare-graphql",
)

println(graphql(query, headers, variables)["data"].asMap["repository"].asMap["id"])
// "a **very small** set of APIs to execute GraphQL in a completely unsafe manner"
```
