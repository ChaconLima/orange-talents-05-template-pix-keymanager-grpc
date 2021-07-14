package br.com.mateuschacon.keymanager.grpc.recurso.delete.pix.integracao

import br.com.mateuschacon.keymanager.grpc.ChavePixExistenteRequest
import br.com.mateuschacon.keymanager.grpc.KeymanagerRemoverServiceGrpc
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.InformacoesDoClienteDto
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.InstituicaoDto
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.TitularDto
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.repositorios.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemoveChavePixEndPointTest(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val grpcCliente: KeymanagerRemoverServiceGrpc.KeymanagerRemoverServiceBlockingStub
){

    // *************************************************************
    // Conf
    // *************************************************************
    @Factory
    class Clients{
        @Bean
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ): KeymanagerRemoverServiceGrpc.KeymanagerRemoverServiceBlockingStub?{

            return KeymanagerRemoverServiceGrpc.newBlockingStub(channel)
        }
    }

    @BeforeEach
    fun setup(){
        this.chavePixRepository.deleteAll()
    }
    // *************************************************************
    // Test
    // *************************************************************
    @Test
    fun `deve excluir o dado cadastrado`(){
        //senario
        val chavePix = this.dadosChavePix()
        this.chavePixRepository.save(chavePix)

        //ação
        val response = this.grpcCliente.delete(
            ChavePixExistenteRequest.newBuilder()
                                    .setIdentificadorCliente(chavePix.identificadorCliente)
                                    .setIndentificadorPix(chavePix.id.toString())
                                    .build()
        )

        //validação
        with(response){
            assertEquals( "Remoção Concluída", response.ok)
        }
    }
    @Test
    fun `nao deve excluir o dado cadastrado pois nao encontrou no banco de dados`(){
        //senario

        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            this.grpcCliente.delete(
                ChavePixExistenteRequest.newBuilder()
                    .setIdentificadorCliente(UUID.randomUUID().toString())
                    .setIndentificadorPix(UUID.randomUUID().toString())
                    .build()
            )
        }
        //validação
        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    fun `nao deve excluir o dado cadastrado pois o cliente nao pertence a chave cadastrada`(){
        //senario
        val chavePix_1 = this.dadosChavePix()
        val chavePix_2 = this.dadosChavePix()

        this.chavePixRepository.save(chavePix_1)
        this.chavePixRepository.save(chavePix_2)

        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            this.grpcCliente.delete(
                ChavePixExistenteRequest.newBuilder()
                    .setIdentificadorCliente(chavePix_1.identificadorCliente)
                    .setIndentificadorPix(chavePix_2.id.toString())
                    .build()
            )
        }
        //validação
        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    fun `nao deve cadastrar pois foi enviado dados errados`(){
        //senario

        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            this.grpcCliente.delete(
                ChavePixExistenteRequest.newBuilder()
                    .setIdentificadorCliente("Invalido")
                    .setIndentificadorPix("Invalido")
                    .build()
            )
        }
        //validação
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }

    }

    // *************************************************************
    // Entidade
    // *************************************************************
    private fun dadosChavePix(): ChavePix {
        val idChave = UUID.randomUUID().toString()
        val idCliente = UUID.randomUUID().toString()

        val informacoes =  InformacoesDoClienteDto(
            tipo = TipoContaEnum.CONTA_CORRENTE,
            agencia = "0333",
            numero = "1100",
            titular = TitularDto(
                id = idCliente,
                nome = "Juraci da Cunha Neves",
                cpf = "63657520325"
            ),
            instituicao = InstituicaoDto(
                nome = "Tabajaras Company",
                ispb = "303030"
            )
        )

        return ChavePix(
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            chave = TipoChaveEnum.ALEATORIA,
            identificadorCliente = idCliente,
            valor = idChave,
            contaAssociada = informacoes.paraContaAssociada()
        )
    }

}