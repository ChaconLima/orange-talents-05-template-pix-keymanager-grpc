package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.integracao

import br.com.mateuschacon.keymanager.grpc.*
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.ContasDeClientesNoItauClient
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class CadastroNovaChavePixEndPointTest(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val grpcClient: KeymanagerRegistraServiceGrpc.KeymanagerRegistraServiceBlockingStub
) {
    companion object {
        val CLIENTE_ID = UUID.randomUUID()
        val CLIENTE_CPF = "81958192309"
        val CLIENTE_NOME = "Juraci da Cunha Neves"
        val CLIENTE_AGENCIA  = "0333"
        val CLIENTE_NUMERO = "1100"
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
            ): KeymanagerRegistraServiceGrpc.KeymanagerRegistraServiceBlockingStub?{

                return KeymanagerRegistraServiceGrpc.newBlockingStub(channel)
            }
        }

        @Inject
        lateinit var contasDeClientesNoItauClient: ContasDeClientesNoItauClient

        @Inject
        lateinit var sistemaPixdoBcbClient: SistemaPixdoBcbClient

        @MockBean(ContasDeClientesNoItauClient::class)
        fun contasDeClientesNoItauClient(): ContasDeClientesNoItauClient?{
            return Mockito.mock(ContasDeClientesNoItauClient::class.java)
        }

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
        fun `deve registrar uma nova chave pix`(){


            //cenário -------------------------------------------------------

            `when`(
                this.contasDeClientesNoItauClient
                    .buscaContaPorTipo(
                        idCliente = CLIENTE_ID.toString(),
                        tipo="CONTA_CORRENTE")
            ).thenReturn(HttpResponse.ok(this.dadosInformacoesDoCliente()))

            `when`(
                this.sistemaPixdoBcbClient
                    .cadastramento(this.dadosNovaChavePixBcBRequest())
            ).thenReturn(HttpResponse.ok(this.dadosNovaChavePixBdbResponse()))

            //ação  -------------------------------------------------------

            val chavePixResponse: ChavePixResponse =
                this.grpcClient.registra(this.dadosNovaChavePixRequest())


            //validação  -------------------------------------------------------

            with(chavePixResponse){
                assertEquals(CLIENTE_ID.toString() , identificadorCliente )
                assertNotNull(indentificadorPix)
            }

        }
        @Test
        fun `nao deve cadastrar de uma nova chave quando a mesma ja existe`(){

            //cenário
            val novaChavePixRequest = this.dadosNovaChavePixRequest()

            val chavePix: ChavePix = this.chavePixRepository.save(
                ChavePix(
                    chave = TipoChaveEnum.CPF,
                    valor = CLIENTE_CPF,
                    tipoConta = TipoContaEnum.CONTA_CORRENTE,
                    contaAssociada = this.dadosInformacoesDoCliente().paraContaAssociada(),
                    identificadorCliente = CLIENTE_ID.toString()
                )
           )

            //açao
            val thrown = assertThrows<StatusRuntimeException>{
                this.grpcClient.registra(
                    NovaChavePixRequest .newBuilder()
                        .setIndentificadorCliente(chavePix.identificadorCliente)
                        .setTipoChave( TipoChave.valueOf(chavePix.chave.name))
                        .setValorChave(chavePix.valor)
                        .setTipoConta(TipoConta.valueOf(chavePix.tipoConta.name))
                        .build()
                )
            }

            //verificação
            with(thrown){
                assertEquals( Status.ALREADY_EXISTS.code, status.code)
                assertEquals( "Chave Informada Já cadastrada", status.description)
            }

        }
        @Test
        fun `nao deve cadastrar quando o sistema externo do Banco Itau nao encontrar os dados da conta do cliente`(){
            //cenário
            `when`(
                this.contasDeClientesNoItauClient
                    .buscaContaPorTipo(
                        idCliente = CLIENTE_ID.toString(),
                        tipo= "CONTA_CORRENTE")
            ).thenReturn(HttpResponse.notFound())

            //açao
            val thrown = assertThrows<StatusRuntimeException>{
                this.grpcClient.registra(
                    NovaChavePixRequest .newBuilder()
                        .setIndentificadorCliente(CLIENTE_ID.toString())
                        .setTipoChave(TipoChave.ALEATORIA)
                        .setValorChave("")
                        .setTipoConta(TipoConta.CONTA_CORRENTE)
                        .build()
                )
            }
            //verificação
            with(thrown){
                assertEquals( Status.NOT_FOUND.code, status.code)
                assertEquals( "Cliente não Encontrado", status.description)
            }

        }
        @Test
        fun `nao deve cadastrar quando o sistema externo do Sistema BCB retornar erro`(){
            //cenário
            `when`(
                this.contasDeClientesNoItauClient
                    .buscaContaPorTipo(
                        idCliente = CLIENTE_ID.toString(),
                        tipo="CONTA_CORRENTE")
            ).thenReturn(HttpResponse.ok(this.dadosInformacoesDoCliente()))

            `when`(
                this.sistemaPixdoBcbClient
                    .cadastramento(this.dadosNovaChavePixBcBRequest())
            ).thenReturn(HttpResponse.notFound())

            //açao
            val thrown = assertThrows<StatusRuntimeException>{
                this.grpcClient.registra(this.dadosNovaChavePixRequest())
            }

            //verificação
            with(thrown){
                assertEquals( Status.NOT_FOUND.code, status.code)
                assertEquals( "Erro ao Cadastrar no Sistema do BCB", status.description)
            }

        }
        @Test
        fun `nao deve cadastrar quando o existe erro de validacao`(){
            //cenário

            //ação
            val thrown = assertThrows<StatusRuntimeException>{
                this.grpcClient.registra(
                    NovaChavePixRequest .newBuilder()
                        .setIndentificadorCliente(CLIENTE_ID.toString())
                        .setTipoChave(TipoChave.DEFAULT_TIPO_CHAVE)
                        .setValorChave("8195819230-9")
                        .setTipoConta(TipoConta.DEFAULT_TIPO_CONTA)
                        .build()
                )
            }
            //verificação
            with(thrown){
                assertEquals( Status.INVALID_ARGUMENT.code, status.code)
            }

        }

    // *************************************************************
    // Dtos
    // *************************************************************
        private fun dadosInformacoesDoCliente(): InformacoesDoClienteDto {

            return InformacoesDoClienteDto(
                tipo = TipoContaEnum.CONTA_CORRENTE,
                agencia = CLIENTE_AGENCIA,
                numero = CLIENTE_NUMERO,
                titular = TitularDto(
                    id = CLIENTE_ID.toString(),
                    nome = CLIENTE_NOME,
                    cpf = CLIENTE_CPF
                ),
                instituicao = InstituicaoDto(
                    nome = "Tabajaras Company",
                    ispb = CLIENTE_ISPB
                )
            )
        }

        private fun dadosNovaChavePixBdbResponse(): CreatePixKeyResponse{
            return CreatePixKeyResponse(
                key =CLIENTE_CPF,
                createdAt = LocalDateTime.now().toString()
            )
        }

        private fun dadosNovaChavePixRequest() = NovaChavePixRequest.newBuilder()
            .setIndentificadorCliente(CLIENTE_ID.toString())
            .setTipoChave(TipoChave.CPF)
            .setValorChave(CLIENTE_CPF)
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()

        private fun dadosNovaChavePixBcBRequest(): CreatePixKeyRequest{
            return CreatePixKeyRequest(
                key = CLIENTE_CPF,
                keyType = TipoChaveEnum.CPF.name,
                bankAccount = BankAccountRequest(
                    participant = CLIENTE_ISPB,
                    branch = CLIENTE_AGENCIA,
                    accountNumber = CLIENTE_NUMERO,
                    accountType = TipoContaEnum.CONTA_CORRENTE.outroValorParaConta(TipoContaEnum.CONTA_CORRENTE.name)
                ),
                owner = OwnerRequest(
                    type = "NATURAL_PERSON",
                    name = CLIENTE_NOME,
                    taxIdNumber =CLIENTE_CPF
                )
            )
        }

}
