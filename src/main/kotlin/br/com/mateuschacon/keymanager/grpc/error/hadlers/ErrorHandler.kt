package br.com.mateuschacon.keymanager.grpc.error.hadlers

import io.micronaut.aop.Around
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*


@MustBeDocumented
@Retention(RUNTIME)
@Target(CLASS, FIELD, TYPE)
@Around
annotation class ErrorHandler
