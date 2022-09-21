package io.github.baryontech.nativeincluder.annotations

import RequestedFieldType

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@Repeatable()
annotation class IncludeFile(
    val source: String = "resource",
    val fieldName: String, // slash makes it generate one for you
    val type: RequestedFieldType = RequestedFieldType.STRING,
    val path: String
)
