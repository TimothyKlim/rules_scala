load("@rules_scala3//rules:scala.bzl", "scala_binary")

scala_binary(
    name = "lib",
    srcs = ["Example.scala"],
    deps = [
        "@annex//:org_scala_sbt_util_interface",
        "@annex//:org_scala_sbt_util_logging_2_13",
        "@annex//:org_scala_sbt_zinc_compile_core_2_13",
        "@rules_scala3//scala/workers/common",
    ],
)
