package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.validadores

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import javax.inject.Inject
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.validation.*
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ChavePixValidador::class])
annotation class ValidarChavePix(
    val message: String = "Chave Pix inválida (\${validatedValue.tipoChave})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ChavePixValidador(): ConstraintValidator<ValidarChavePix,NovaChavePix> {
    override fun isValid(value: NovaChavePix?, context: ConstraintValidatorContext?): Boolean {


        if(value?.tipoChave == null)
            return false

        return value.tipoChave.valida(value.valorChave)
    }

}
