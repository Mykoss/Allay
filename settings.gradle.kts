rootProject.name = "Allay"

// include multi modules
include(":api")
include(":server")
include(":codegen")
include(":data")

includeBuild("protocol-local") {
    dependencySubstitution {
        substitute(module("org.allaymc.protocol:bedrock-codec"))
            .using(project(":bedrock-codec"))

        substitute(module("org.allaymc.protocol:bedrock-connection"))
            .using(project(":bedrock-connection"))
    }
}
