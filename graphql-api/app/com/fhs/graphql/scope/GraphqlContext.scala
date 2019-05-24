package com.fhs.graphql.scope

import com.fhs.graphql.scope.insight.InsightKind
import com.fhs.graphql.scope.order.Order
import com.fhs.graphql.scope.profile.{Profile, ProfileService}
import sangria.macros.derive.ObjectTypeDescription
import sangria.schema.{Argument, Field, ListType, ObjectType, fields}
import sangria.{macros, schema}

class GraphqlContext {
  val profileService = new ProfileService
}

trait GraphqlDefinition {

  implicit val insightSType = macros.derive.deriveEnumType[InsightKind.Value]()
  implicit val orderSType = macros.derive.deriveObjectType[Unit, Order]()

  val profileSType = macros.derive.deriveObjectType[Unit, Profile](
    ObjectTypeDescription("profile model description")
  )

  val profileSIdType = Argument("id", schema.LongType, "")
  val queryType = ObjectType("Query", fields[GraphqlContext, Unit](
    Field(
      name = "profile",
      fieldType = profileSType,
      description = Some("Returns a profile with specific `id`."),
      arguments = profileSIdType :: Nil,
      resolve = c ⇒ c.ctx.profileService.getById(c arg profileSIdType)
    ),
    Field(
      name = "profiles",
      fieldType = ListType(profileSType),
      description = Some("Returns a collecting of profile"),
      resolve = c => c.ctx.profileService.getAll()
    )
  ))
}