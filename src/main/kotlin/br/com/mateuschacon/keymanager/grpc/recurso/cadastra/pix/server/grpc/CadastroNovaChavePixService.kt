package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.server.grpc

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.clientes.ContasDeClientesNoItauClient
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.InformacoesDoClienteDto
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.exceptions.ExisteChavePixException
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.repositorios.ChavePixRepository
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid

@Validated
@Singleton
class CadastroNovaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val contasDeClientesNoItauClient: ContasDeClientesNoItauClient
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun cadastro(@Valid novaChavePix: NovaChavePix): ChavePix {

        if(this.isValid(novaChavePix.valorChave!!))
            throw ExisteChavePixException("Chave Informada Já cadastrada")

        this.logger.info("Requisição de Cadastro : ${novaChavePix.hashCode()}")
        val informacoesDoCliente: InformacoesDoClienteDto =
            this.contasDeClientesNoItauClient
                .buscaContaPorTipo(
                    novaChavePix.indentificadorCliente!!,
                    novaChavePix.tipoConta!!.name
                ).body() ?: throw IllegalArgumentException("Cliente não Encontrado")

        val chavePix: ChavePix =
            novaChavePix.paraChavePix(
                informacoesDoCliente.paraContaAssociada()
            )
        this.logger.info("Cadastro Concluido: ${chavePix.hashCode()}")

        return this.chavePixRepository.save(chavePix)
    }

    fun isValid(valor: String): Boolean{
        val optional:Optional<ChavePix> =
            this.chavePixRepository.findByValor(valor)

        return optional.isPresent
    }
}