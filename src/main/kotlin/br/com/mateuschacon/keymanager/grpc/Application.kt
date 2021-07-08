package br.com.mateuschacon.keymanager.grpc

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.mateuschacon.keymanager.grpc")
		.start()
}
