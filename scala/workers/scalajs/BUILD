load("//rules:scala.bzl", "scala_binary")

scala_binary(
    name = "scalajs",
    srcs = glob(["*.scala"]),
    main_class = "rules_scala.workers.scalajs.ScalaJsWorker",
    scala = "//scala:bootstrap_3_0",
    deps = [
        "@annex//:com_github_scopt_scopt_3",
        "@scalajs_env_nodejs_2_13",
        "@scalajs_js_envs_2_13",
        "@scalajs_linker_2_13",
        "@scalajs_linker_interface_2_13",
        "@scalajs_logging_2_13",
        "@scalajs_sbt_test_adapter_2_13",
        "@scalajs_test_bridge_2_13",
        "@scalajs_test_interface_2_13",
        "//scala/common/worker",
        "//scala/workers/common",
    ],
    visibility = ["//visibility:public"],
)
