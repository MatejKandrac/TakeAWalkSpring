package com.kandrac.tomco.takeawalkspring.Dto

import java.beans.ConstructorProperties

data class RegisterDto
@ConstructorProperties("email", "password, username")
constructor(val email: String, val password: String, val username: String)