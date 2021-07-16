package br.com.mateuschacon.keymanager.grpc.recurso.detalhes.pix.integracao

import br.com.mateuschacon.keymanager.grpc.*
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.SistemaPixdoBcbClient
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.*
import br.com.mateuschacon.keymanager.grpc.recurso.delete.pix.integracao.RemoveChavePixEndPointTest
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.Participantesstrport
import br.com.mateuschacon.keymanager.grpc.recurso.repositorios.ChavePixRepository
import br.com.mateuschacon.keymanager.grpc.recurso.repositorios.ParticipantesstrportRepository
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class DetalhesChavePixEndPointTest(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val participantesstrportRepository: ParticipantesstrportRepository,
    @Inject val grpcCliente: KeymanagerDetalhamentoServiceGrpc.KeymanagerDetalhamentoServiceBlockingStub
) {
    companion object {
        val VALOR_CHAVE = UUID.randomUUID().toString()
        val CLIENTE_ID = UUID.randomUUID().toString()
        val CLIENTE_ISPB = "303030"

        val VALOR_CHAVE1 = UUID.randomUUID().toString()
        val CLIENTE_ID1 = UUID.randomUUID().toString()
        val CLIENTE_ISPB1 = "505050"
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ): KeymanagerDetalhamentoServiceGrpc.KeymanagerDetalhamentoServiceBlockingStub? {

            return KeymanagerDetalhamentoServiceGrpc.newBlockingStub(channel)
        }
    }

    @Inject
    lateinit var sistemaPixdoBcbClient: SistemaPixdoBcbClient

    @MockBean(SistemaPixdoBcbClient::class)
    fun sistemaPixdoBcbClient(): SistemaPixdoBcbClient? {
        return Mockito.mock(SistemaPixdoBcbClient::class.java)
    }


    @BeforeEach
    fun setup() {
        this.chavePixRepository.deleteAll()
        this.participantesstrportRepository.deleteAll()
    }

    @Test
    fun `deve detalhar o registro quando passar as informacoes dos identificadores da chave pix`() {

        //cenário
        val chavePix = this.dadosChavePix1()
        this.chavePixRepository.save(chavePix)

        //ação
        val response: ChavePixDetalhesResponse = this.grpcCliente.detalha(

            ChavePixDetalhesRequest.newBuilder()
                .setIdentificadoresChave(
                    ChavePixResponse.newBuilder()
                        .setIndentificadorPix(chavePix.id.toString())
                        .setIdentificadorCliente(chavePix.identificadorCliente)
                        .build()
                ).setValorChavePix("")
                .build()
        )
        //validacao
        with(response) {
            assertEquals(chavePix.identificadorCliente, response.identificadoresChave.identificadorCliente)
            assertEquals(chavePix.id.toString(), response.identificadoresChave.indentificadorPix)
            assertEquals(chavePix.chave.name, response.tipoChave.name)
            assertEquals(chavePix.valor, response.valorChave)
            assertEquals(chavePix.contaAssociada.nomeTitular, response.titular.nome)
            assertEquals(chavePix.contaAssociada.cpfTitular, response.titular.cpf)
            assertEquals(chavePix.tipoConta.name, response.contaVinculada.tipoConta.name)
            assertEquals(chavePix.contaAssociada.nomeInstituicao, response.contaVinculada.nomeInstituicaoFinanceira)
            assertEquals(chavePix.contaAssociada.agencia, response.contaVinculada.agencia)
            assertEquals(chavePix.contaAssociada.numero, response.contaVinculada.numeroConta)
            assertEquals(chavePix.contaAssociada.ispb, response.contaVinculada.ispb)
            //assertEquals(chavePix.criadoEm, response.criadoEm)
        }


    }

    @Test
    fun `deve detalhar o registro quando passar o valor da chave pix que estaja cadastrado no sistema`() {

        //cenário
        val chavePix = this.dadosChavePix1()
        this.chavePixRepository.save(chavePix)

        //ação
        val response: ChavePixDetalhesResponse = this.grpcCliente.detalha(

            ChavePixDetalhesRequest.newBuilder()
                .setIdentificadoresChave(
                    ChavePixResponse.newBuilder()
                        .setIndentificadorPix("")
                        .setIdentificadorCliente("")
                        .build()
                ).setValorChavePix(chavePix.valor)
                .build()
        )
        //validacao
        with(response) {
            assertEquals(chavePix.identificadorCliente, response.identificadoresChave.identificadorCliente)
            assertEquals(chavePix.id.toString(), response.identificadoresChave.indentificadorPix)
            assertEquals(chavePix.chave.name, response.tipoChave.name)
            assertEquals(chavePix.valor, response.valorChave)
            assertEquals(chavePix.contaAssociada.nomeTitular, response.titular.nome)
            assertEquals(chavePix.contaAssociada.cpfTitular, response.titular.cpf)
            assertEquals(chavePix.tipoConta.name, response.contaVinculada.tipoConta.name)
            assertEquals(chavePix.contaAssociada.nomeInstituicao, response.contaVinculada.nomeInstituicaoFinanceira)
            assertEquals(chavePix.contaAssociada.agencia, response.contaVinculada.agencia)
            assertEquals(chavePix.contaAssociada.numero, response.contaVinculada.numeroConta)
            assertEquals(chavePix.contaAssociada.ispb, response.contaVinculada.ispb)
            //assertEquals(chavePix.criadoEm, response.criadoEm)
        }


    }

    @Test
    fun `deve detalhar o registro quando passar o valor da chave pix que precisa ir no Sistema BCB buscar a informacao`(){

        //senario -----
        val chavePixBcb =  this.detalhesChavePixBcdResponse()
        Mockito.`when`(
            this.sistemaPixdoBcbClient
                .detalhes(key = "+55998832651")
        ).thenReturn(HttpResponse.ok(chavePixBcb))

        val participantesstrport = this.dadosParticipantesstrport()
        this.participantesstrportRepository.save(participantesstrport)

        //acao

        val response: ChavePixDetalhesResponse = this.grpcCliente.detalha(

            ChavePixDetalhesRequest.newBuilder()
                .setIdentificadoresChave(
                    ChavePixResponse.newBuilder()
                        .setIndentificadorPix("")
                        .setIdentificadorCliente("")
                        .build()
                ).setValorChavePix("+55998832651")
                .build()
        )

        //valicao
        with(response) {
            assertEquals("", response.identificadoresChave.identificadorCliente)
            assertEquals("", response.identificadoresChave.indentificadorPix)
            assertEquals(TipoChaveEnum.reversoVindoBCB(chavePixBcb.keyType)!!.name, response.tipoChave.name)
            assertEquals(chavePixBcb.key, response.valorChave)
            assertEquals(chavePixBcb.owner.name, response.titular.nome)
            assertEquals(chavePixBcb.owner.taxIdNumber, response.titular.cpf)
            assertEquals(TipoContaEnum.reversoVindoBCB(chavePixBcb.bankAccount.accountType)!!.name, response.contaVinculada.tipoConta.name)
            assertEquals(participantesstrport.nomeReduzido.toUpperCase() , response.contaVinculada.nomeInstituicaoFinanceira)
            assertEquals(chavePixBcb.bankAccount.branch, response.contaVinculada.agencia)
            assertEquals(chavePixBcb.bankAccount.accountNumber, response.contaVinculada.numeroConta)
            assertEquals(chavePixBcb.bankAccount.participant, response.contaVinculada.ispb)
            assertEquals(chavePixBcb.createdAt, response.criadoEm)
        }

    }

    @Test
    fun `nao deve detalhar quando os identificadores da chave pix nao sao correlacionados`(){
        //senario
        val chavePix_1 = this.dadosChavePix1()
        val chavePix_2 = this.dadosChavePix2()

        this.chavePixRepository.save(chavePix_1)
        this.chavePixRepository.save(chavePix_2)

        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            this.grpcCliente.detalha(

                ChavePixDetalhesRequest.newBuilder()
                    .setIdentificadoresChave(
                        ChavePixResponse.newBuilder()
                            .setIndentificadorPix( chavePix_1.id.toString())
                            .setIdentificadorCliente( chavePix_2.identificadorCliente)
                            .build()
                    ).setValorChavePix("")
                    .build()
            )
        }
        //validação
        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    fun `nao deve detalhar quando os identificadores estiverem no formato errado`(){
        //senario
        val chavePix_1 = this.dadosChavePix1()
        val chavePix_2 = this.dadosChavePix2()

        this.chavePixRepository.save(chavePix_1)
        this.chavePixRepository.save(chavePix_2)

        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            this.grpcCliente.detalha(

                ChavePixDetalhesRequest.newBuilder()
                    .setIdentificadoresChave(
                        ChavePixResponse.newBuilder()
                            .setIndentificadorPix( chavePix_1.id.toString()+"invalido")
                            .setIdentificadorCliente( chavePix_2.identificadorCliente+"invalido")
                            .build()
                    ).setValorChavePix("")
                    .build()
            )
        }
        //validação
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `nao deve detalhar quando passar o valor da chave pix que precisa ir no Sistema BCB buscar e o retron for not found`(){

        //senario -----
        val chavePixBcb =  this.detalhesChavePixBcdResponse()
        Mockito.`when`(
            this.sistemaPixdoBcbClient
                .detalhes(key = "+55998832651")
        ).thenReturn(HttpResponse.notFound())

        //acao

        val thrown = assertThrows<StatusRuntimeException> {
            this.grpcCliente.detalha(

                ChavePixDetalhesRequest.newBuilder()
                    .setIdentificadoresChave(
                        ChavePixResponse.newBuilder()
                            .setIndentificadorPix("")
                            .setIdentificadorCliente("")
                            .build()
                    ).setValorChavePix("+55998832651")
                    .build()
            )
        }

        //valicao
        with(thrown) {
            with(thrown) {
                assertEquals(Status.NOT_FOUND.code, status.code)
                assertEquals( "Não existe registro de chave com o valor: +55998832651", status.description)
            }
        }

    }


    // *************************************************************
    // Dtos
    // *************************************************************
    private fun detalhesChavePixBcdResponse(): DetalhesChavePixBcbResponse {
        return DetalhesChavePixBcbResponse(
            keyType = "PHONE",
            key = "+55998832651",
            bankAccount = BankAccountResponse(
                participant = "303030",
                branch = "404030",
                accountNumber = "0001",
                accountType = "SVGS"
            ),
            owner = OwnerResponse(
                type = "NATURAL_PERSON",
                name = "Joelma Lima",
                taxIdNumber = "90955632121"
            ),
            createdAt = "2021-07-16T12:19:25.014111"
        )
    }

    // *************************************************************
    // Entidade
    // *************************************************************
    private fun dadosChavePix1(): ChavePix {

        val informacoes = InformacoesDoClienteDto(
            tipo = TipoContaEnum.CONTA_CORRENTE,
            agencia = "0333",
            numero = "1100",
            titular = TitularDto(
                id = CLIENTE_ID,
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
            identificadorCliente = CLIENTE_ID,
            valor = VALOR_CHAVE,
            contaAssociada = informacoes.paraContaAssociada()
        )
    }

    private fun dadosChavePix2(): ChavePix {

        val informacoes = InformacoesDoClienteDto(
            tipo = TipoContaEnum.CONTA_CORRENTE,
            agencia = "0333",
            numero = "1100",
            titular = TitularDto(
                id = CLIENTE_ID1,
                nome = "Juraci da Cunha Neves",
                cpf = "63657520325"
            ),
            instituicao = InstituicaoDto(
                nome = "Tabajaras Company",
                ispb = CLIENTE_ISPB1
            )
        )

        return ChavePix(
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            chave = TipoChaveEnum.ALEATORIA,
            identificadorCliente = CLIENTE_ID1,
            valor = VALOR_CHAVE1,
            contaAssociada = informacoes.paraContaAssociada()
        )
    }

    private fun dadosParticipantesstrport() :Participantesstrport{
        return Participantesstrport(
            isbp = "303030",
            nomeReduzido = "Tabajaras Company",
            numeroCodigo = "102",
            participaCompe = "particpa",
            acessoPrincipal = "acessoPrincipal",
            nomeExtenso = "Tabajaras Company City",
            inicioOperacao = "10/10/1998"
        )
    }

}