package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.integracao

import br.com.mateuschacon.keymanager.grpc.*
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.clientes.ContasDeClientesNoItauClient
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.InformacoesDoClienteDto
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.InstituicaoDto
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.TitularDto
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
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class CadastroNovaChavePixEndPointTest(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val grpcClient: KeymanagerRegistraServiceGrpc.KeymanagerRegistraServiceBlockingStub
) {
    companion object {
        val CLIENTE_ID = UUID.randomUUID()
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

        @MockBean(ContasDeClientesNoItauClient::class)
        fun contasDeClientesNoItauClient(): ContasDeClientesNoItauClient?{
            return Mockito.mock(ContasDeClientesNoItauClient::class.java)
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

            //cenário
            `when`(
                this.contasDeClientesNoItauClient
                    .buscaContaPorTipo(
                        idCliente = CLIENTE_ID.toString(),
                        tipo= "CONTA_CORRENTE")
                    ).thenReturn(HttpResponse.ok(dadosDacontaReponse())
            )

            //ação
            val chavePixResponse: ChavePixResponse = this.grpcClient.registra(
                NovaChavePixRequest .newBuilder()
                                    .setIndentificadorCliente(CLIENTE_ID.toString())
                                    .setTipoChave(TipoChave.CPF)
                                    .setValorChave("81958192309")
                                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                                    .build()
            )

            //validação
            with(chavePixResponse){
                assertEquals(CLIENTE_ID.toString(), identificadorCliente )
                assertNotNull(indentificadorPix)
            }

        }
        @Test
        fun `nao deve cadastrar de uma nova chave quando a mesma ja existe`(){

            //cenário
            val chavePix: ChavePix = this.chavePixRepository.save(
                ChavePix(
                    chave = TipoChaveEnum.CPF,
                    valor = "81958192309",
                    tipoConta = TipoContaEnum.CONTA_CORRENTE,
                    contaAssociada = dadosDacontaReponse().paraContaAssociada(),
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
        fun `nao deve cadastrar quando o sistema externo nao encontrar os dados da conta do cliente`(){
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
        fun `nao deve cadastrar quando o existe erro de validacao`(){
            //cenário

            //ação
            val thrown = assertThrows<StatusRuntimeException>{
                this.grpcClient.registra(
                    NovaChavePixRequest .newBuilder()
                        .setIndentificadorCliente(CLIENTE_ID.toString())
                        .setTipoChave(TipoChave.CPF)
                        .setValorChave("8195819230-9")
                        .setTipoConta(TipoConta.CONTA_CORRENTE)
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
        private fun dadosDacontaReponse(): InformacoesDoClienteDto {

            return InformacoesDoClienteDto(
                tipo = TipoContaEnum.CONTA_CORRENTE,
                agencia = "0333",
                numero = "1100",
                titular = TitularDto(
                    id = CLIENTE_ID.toString(),
                    nome = "Juraci da Cunha Neves",
                    cpf = "63657520325"
                ),
                instituicao = InstituicaoDto(
                    nome = "Tabajaras Company",
                    ispb = "303030"
                )
            )
        }

}
