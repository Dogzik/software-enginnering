package common.utils

import io.ktor.request.ApplicationRequest

fun getUserId(request: ApplicationRequest): Int? = request.queryParameters["user_id"]?.toInt()
