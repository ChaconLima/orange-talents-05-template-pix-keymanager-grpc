package br.com.mateuschacon.keymanager.grpc.recurso.delete.pix.integracao

import br.com.mateuschacon.keymanager.grpc.ChavePixExistenteRequest
import br.com.mateuschacon.keymanager.grpc.KeymanagerRemoverServiceGrpc
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.SistemaPixdoBcbClient
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.*
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.repositorios.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemoveChavePixEndPointTest(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val grpcCliente: KeymanagerRemoverServiceGrpc.KeymanagerRemoverServiceBlockingStub
){
    companion object{
        val VALOR_CHAVE =UUID.randomUUID().toString()
        val CLIENTE_ID=UUID.randomUUID().toString()
        val CLIENTE_ISPB = "303030"
    }
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

    @Inject
    lateinit var sistemaPixdoBcbClient: SistemaPixdoBcbClient

    @MockBean(SistemaPixdoBcbClient::class)
    fun sistemaPixdoBcbClient(): SistemaPixdoBcbClient?{
        return Mockito.mock(SistemaPixdoBcbClient::class.java)
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

        Mockito.`when`(
            this.sistemaPixdoBcbClient
                .deletar( key = VALOR_CHAVE, deletePixKeyRequest = this.dadosDeRemoverPixBcbRequest())
        ).thenReturn(HttpResponse.ok(this.dadosDeRemoverPixBcbResponse()))

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
    fun `nao deve excluir o dado cadastrado, pois o retorno do Sistema do BCB retornou not found`(){
        //senario
        val chavePix = this.dadosChavePix()
        this.chavePixRepository.save(chavePix)

        Mockito.`when`(
            this.sistemaPixdoBcbClient
                .deletar( key = VALOR_CHAVE, deletePixKeyRequest = this.dadosDeRemoverPixBcbRequest())
        ).thenReturn(HttpResponse.notFound())

        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            this.grpcCliente.delete(
                ChavePixExistenteRequest.newBuilder()
                    .setIdentificadorCliente(chavePix.identificadorCliente)
                    .setIndentificadorPix(chavePix.id.toString())
                    .build()
            )
        }

        //validação
        with(thrown){
            assertEquals( Status.NOT_FOUND.code, status.code)
            assertEquals("Não Foi possivel excluir do Sistema do BCB", status.description)
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
    fun `nao deve excluir pois foi enviado dados errados`(){
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
        val valorChave = VALOR_CHAVE.toString()
        val idCliente = CLIENTE_ID.toString()

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
                ispb = CLIENTE_ISPB
            )
        )

        return ChavePix(
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            chave = TipoChaveEnum.ALEATORIA,
            identificadorCliente = idCliente,
            valor = valorChave,
            contaAssociada = informacoes.paraContaAssociada()
        )
    }
    private fun dadosDeRemoverPixBcbResponse(): DeletePixKeyResponse{
        return DeletePixKeyResponse(
            deletedAt = LocalDateTime.now().toString()
        )
    }
    private fun dadosDeRemoverPixBcbRequest(): DeletePixKeyRequest {
        return DeletePixKeyRequest(
            key = VALOR_CHAVE,
            participant = CLIENTE_ISPB
        )
    }

}