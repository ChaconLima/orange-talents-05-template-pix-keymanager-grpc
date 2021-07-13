package br.com.mateuschacon.keymanager.grpc.error.hadlers

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.exceptions.ExisteChavePixException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ErrorHandlerInterceptor : MethodInterceptor<Any,Any>{

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {

        try {
            return context.proceed()
        }catch ( e: Exception){
            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when(e){
                is ConstraintViolationException ->
                    Status.INVALID_ARGUMENT .withCause(e)
                                            .withDescription(e.message)
                                            .asRuntimeException()
                is ExisteChavePixException->
                    Status.ALREADY_EXISTS.withCause(e.cause)
                                         .withDescription(e.message)
                                         .asRuntimeException()
                else->
                    Status.NOT_FOUND.withCause(e).withDescription(e.message).asRuntimeException()
            }

            this.logger.info("Exception $status")
            responseObserver.onError(status)
        }
        return null
    }
}