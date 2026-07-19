plugins {
    id("keystone.jvm.library")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":core-domain"))
    implementation(libs.conduit.core)
    implementation(libs.conduit.serialization.kotlinx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}
