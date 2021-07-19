package br.com.mateuschacon.keymanager.grpc.recurso.listar.pix.integracao

import br.com.mateuschacon.keymanager.grpc.ChavePixClienteRequest
import br.com.mateuschacon.keymanager.grpc.KeymanagerListagemServiceGrpc
import br.com.mateuschacon.keymanager.grpc.KeymanagerRemoverServiceGrpc
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ContaAssociada
import br.com.mateuschacon.keymanager.grpc.recurso.repositorios.ChavePixRepository
import com.google.protobuf.Timestamp
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZoneId
import java.util.*
import javax.inject.Inject


@MicronautTest(transactional = false)
internal class ListagemChavePixEndPointTest(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val grpcCliente: KeymanagerListagemServiceGrpc.KeymanagerListagemServiceBlockingStub
) {
    companion object {
        val CLIENTE_ID = UUID.randomUUID().toString()
        val CLIENTE_ISPB = "303030"
    }

    // *************************************************************
    // Conf
    // *************************************************************
    @Factory
    class Clients {
        @Bean
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ): KeymanagerListagemServiceGrpc.KeymanagerListagemServiceBlockingStub? {

            return KeymanagerListagemServiceGrpc.newBlockingStub(channel)
        }
    }

    @BeforeEach
    fun setup() {
        this.chavePixRepository.deleteAll()
    }

    // *************************************************************
    // Test
    // *************************************************************
    @Test
    fun `deve listar as chaves pix de um cliente`() {

        //cenário
        val chave1 = this.dadosChavePix1()
        val chave2 = this.dadosChavePix2()
        this.chavePixRepository.save(chave1)
        this.chavePixRepository.save(chave2)
        val chaves: List<ChavePix> = listOf(chave1, chave2)

        //ação
        val response = this.grpcCliente.listar(
            ChavePixClienteRequest.newBuilder().setIdentificadorCliente(CLIENTE_ID).build()
        )
        //validação
        with(response) {

            assertEquals(2, response.chavePixDetalhesClienteResponseCount)
            assertEquals(CLIENTE_ID, response.identificadorCliente)

                var index = 0
                response.chavePixDetalhesClienteResponseList.forEach {
                    assertEquals(chaves[index].chave.name, it.tipoChave.name)
                    assertEquals(chaves[index].valor, it.valorChave)
                    assertEquals(chaves[index].tipoConta.name, it.tipoConta.name)
                    assertEquals(chaves[index].id.toString(), it.identificadorPix)
                    assertEquals(chaves[index].criadoEm.let { date ->
                        val criadoEm = date.atZone(ZoneId.of("UTC")).toInstant()
                        criadoEm.epochSecond
                    }, it.criadoEm.seconds)
                    index++
                }
        }

    }

    @Test
    fun `nao deve listar as chaves pix de um cliente quando passar o UUID errado`() {

        //cenário
        val chave1 = this.dadosChavePix1()
        val chave2 = this.dadosChavePix2()
        this.chavePixRepository.save(chave1)
        this.chavePixRepository.save(chave2)
        val chaves: List<ChavePix> = listOf(chave1, chave2)

        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            this.grpcCliente.listar(
                ChavePixClienteRequest.newBuilder().setIdentificadorCliente(CLIENTE_ID+"Invalido").build()
            )
        }

        //validação
        with(thrown) {
            assertEquals( Status.INVALID_ARGUMENT.code, status.code)
        }

    }

    @Test
    fun `nao deve listar as chaves pix de um cliente quando passar o UUID nao registrado`() {

        //cenário
        val chave1 = this.dadosChavePix1()
        val chave2 = this.dadosChavePix2()
        this.chavePixRepository.save(chave1)
        this.chavePixRepository.save(chave2)
        val chaves: List<ChavePix> = listOf(chave1, chave2)

        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            this.grpcCliente.listar(
                ChavePixClienteRequest.newBuilder().setIdentificadorCliente(UUID.randomUUID().toString()).build()
            )
        }

        //validação
        with(thrown) {
            assertEquals( Status.NOT_FOUND.code, status.code)
        }

    }

    // *************************************************************
    // Entidade
    // *************************************************************
    private fun dadosChavePix1(): ChavePix {
        return ChavePix(
            chave = TipoChaveEnum.TELEFONE,
            identificadorCliente = CLIENTE_ID,
            valor = "+34998832651",
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            contaAssociada = ContaAssociada(
                nomeInstituicao = "Tabajaras Company",
                ispb = CLIENTE_ISPB,
                nomeTitular = "Juracelma Das Dores",
                cpfTitular = "25236254122",
                agencia = "0001",
                numero = "010203"
            )
        )
    }

    private fun dadosChavePix2(): ChavePix {
        return ChavePix(
            chave = TipoChaveEnum.ALEATORIA,
            identificadorCliente = CLIENTE_ID,
            valor = UUID.randomUUID().toString(),
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            contaAssociada = ContaAssociada(
                nomeInstituicao = "Tabajaras Company",
                ispb = CLIENTE_ISPB,
                nomeTitular = "Juracelma Das Dores",
                cpfTitular = "25236254122",
                agencia = "0001",
                numero = "010203"
            )
        )
    }
}