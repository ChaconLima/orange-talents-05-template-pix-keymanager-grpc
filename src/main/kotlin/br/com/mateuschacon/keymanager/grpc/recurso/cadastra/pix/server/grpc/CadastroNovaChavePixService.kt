package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.server.grpc

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.ContasDeClientesNoItauClient
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.SistemaPixdoBcbClient
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.CreatePixKeyResponse
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.InformacoesDoClienteDto
import br.com.mateuschacon.keymanager.grpc.recurso.exceptions.ExisteChavePixException
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.repositorios.ChavePixRepository
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class CadastroNovaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository, //1
    @Inject val contasDeClientesNoItauClient: ContasDeClientesNoItauClient, //1
    @Inject val sistemaPixdoBcbClient: SistemaPixdoBcbClient //1
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun cadastro(
        @Valid novaChavePix: NovaChavePix
    ): ChavePix {

        if(this.isValid(novaChavePix.valorChave!!)) // 1
            throw ExisteChavePixException("Chave Informada Já cadastrada")

        this.logger.info("Requisição de Cadastro : ${novaChavePix.hashCode()}")
        val informacoesDoCliente: InformacoesDoClienteDto =
            this.contasDeClientesNoItauClient
                .buscaContaPorTipo(
                    novaChavePix.identificadorCliente!!,
                    novaChavePix.tipoConta!!.name
                ).body() ?: throw IllegalArgumentException("Cliente não Encontrado") //2

        val novaChavePixBcbResponse: CreatePixKeyResponse =
            this.sistemaPixdoBcbClient.cadastramento(
                novaChavePix.paraNovaChavePixBcbRequest(
                    informacoesDoCliente.paraContaAssociada()
                )
            ).body() ?: throw IllegalArgumentException("Erro ao Cadastrar no Sistema do BCB") //2

        val chavePix: ChavePix =
            novaChavePix.paraChavePix(
                novaChavePixBcbResponse,
                informacoesDoCliente.paraContaAssociada()
            ) // 1
        this.logger.info("Cadastro Concluido: ${chavePix.hashCode()}")

        return this.chavePixRepository.save(chavePix) // 9
    }

    fun isValid(valor: String): Boolean{
        val optional:Optional<ChavePix> =
            this.chavePixRepository.findByValor(valor)

        return optional.isPresent
    }
}