package br.com.mateuschacon.keymanager.grpc.recurso.detalhes.pix

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
import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Singleton
@Validated
class DetalharChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val sistemaPixdoBcbClient: SistemaPixdoBcbClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun detalhamentoPorIdentificadores(
        @Valid @ValidacaoUUID identificadorCliente: String,
        @Valid @ValidacaoUUID indentificadorPix: String
    ) :ChavePix = also{
        this.logger.info("Requisição de Detalhamento Por Valor Identificadores Iniciada")
    }.let{
        this.isValid(UUID.fromString(indentificadorPix), identificadorCliente)
    }.also {
        this.logger.info("Requisição de Detalhamento PorIdentificadores Concluida")
    }


    fun detalhamentoPorValorChave(
       @Valid @NotBlank @Size(max=77) valorChavePix: String
    ): ChavePix {

        this.logger.info("Requisição de Detalhamento Por Valor Chave Iniciada")

        val possivelChave = this.isExist(valorChavePix)
        if(possivelChave.isPresent){
            return possivelChave.get().also {
                this.logger.info("Requisição de Detalhamento Por Valor Chave Concluida")
            }
        }

        val chavePix =
            this.sistemaPixdoBcbClient.detalhes(valorChavePix)
                .body() ?: throw NaoExisteChavePixException("Não existe registro de chave com o valor: $valorChavePix")

        return chavePix.paraChavePix().also {
            this.logger.info("Requisição de Detalhamento Por Valor Chave Concluida")
        }
    }

    private fun isValid(idChavePix: UUID, idCliente: String ): ChavePix =
        this.chavePixRepository.findByIdAndIdentificadorCliente( idChavePix, idCliente).let {
            if (it.isEmpty) throw NaoExisteChavePixException("não existe")
            it.get()
        }

    private fun isExist(valorChavePix: String ): Optional<ChavePix> =
        this.chavePixRepository.findByValor( valorChavePix )
}