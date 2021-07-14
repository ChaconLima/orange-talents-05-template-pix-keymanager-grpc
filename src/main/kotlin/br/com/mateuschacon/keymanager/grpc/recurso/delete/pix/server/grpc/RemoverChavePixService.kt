package br.com.mateuschacon.keymanager.grpc.recurso.delete.pix.server.grpc

import br.com.mateuschacon.keymanager.grpc.recurso.exceptions.NaoExisteChavePixException
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.repositorios.ChavePixRepository
import br.com.mateuschacon.keymanager.grpc.recurso.validadores.ValidacaoUUID
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class RemoverChavePixService(
    @Inject val chavePixRepository: ChavePixRepository
){
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun delete(
        @Valid @ValidacaoUUID identificadorCliente: String,
        @Valid @ValidacaoUUID identificadorChavePix: String
    ){
        this.logger.info("Requisição de Remoção: ${identificadorChavePix}")

        val chavePix: ChavePix =
            this.isValid(
                UUID.fromString(identificadorChavePix),
                identificadorCliente
            )

        this.chavePixRepository.delete(chavePix)
        this.logger.info(" Remoção concluida ")
    }

    private fun isValid( idChavePix: UUID, idCliente: String ): ChavePix {
        val optional:Optional<ChavePix> =
            this.chavePixRepository.findByIdAndIdentificadorCliente( idChavePix, idCliente)

        if(optional.isEmpty) throw NaoExisteChavePixException("não existe")

        return optional.get()
    }
}