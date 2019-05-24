val api = Project("graphql-api", file("graphql-api"))

val root = Project("graphql", file("."))
  .aggregate(api)