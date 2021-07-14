package br.com.mateuschacon.keymanager.grpc.recurso.delete.pix.server.grpc

import br.com.mateuschacon.keymanager.grpc.recurso.clientes.SistemaPixdoBcbClient
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.DeletePixKeyRequest
import br.com.mateuschacon.keymanager.grpc.recurso.exceptions.NaoExisteChavePixException
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.repositorios.ChavePixRepository
import br.com.mateuschacon.keymanager.grpc.recurso.validadores.ValidacaoUUID
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class RemoverChavePixService(
    @Inject val chavePixRepository: ChavePixRepository, //1
    @Inject val sistemaPixdoBcbClient: SistemaPixdoBcbClient//1
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
            ) //2

        this.chavePixRepository.delete(chavePix) //2
        this.logger.info(" Remoção concluida ")
        //6
    }

    private fun isValid(
        idChavePix: UUID,
        idCliente: String,
    ): ChavePix {

        val optional:ChavePix =
            this.chavePixRepository.findByIdAndIdentificadorCliente( idChavePix, idCliente).let {
                if (it.isEmpty) throw NaoExisteChavePixException("não existe")
                it.get()
            }

        this.sistemaPixdoBcbClient.deletar(
            key = optional.valor,
            deletePixKeyRequest = DeletePixKeyRequest(
                key = optional.valor,
                participant = optional.contaAssociada.ispb
            )
        ).body() ?: throw IllegalArgumentException("Não Foi possivel excluir do Sistema do BCB")

        return optional
    }
}