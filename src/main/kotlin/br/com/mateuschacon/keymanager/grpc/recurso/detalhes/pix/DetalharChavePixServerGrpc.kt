package br.com.mateuschacon.keymanager.grpc.recurso.detalhes.pix

import br.com.mateuschacon.keymanager.grpc.*
import br.com.mateuschacon.keymanager.grpc.error.hadlers.ErrorHandler
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import io.grpc.stub.StreamObserver
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class DetalharChavePixServerGrpc(
    @Inject val detalhamentoChavePixService: DetalharChavePixService
): KeymanagerDetalhamentoServiceGrpc.KeymanagerDetalhamentoServiceImplBase(){

    override fun detalha(
        request: ChavePixDetalhesRequest?,
        responseObserver: StreamObserver<ChavePixDetalhesResponse>
    ) {
        val chavePix:ChavePix =
             when(request?.valorChavePix!!.isNotEmpty()){
                true -> {
                    this.detalhamentoChavePixService
                        .detalhamentoPorValorChave(
                            request.valorChavePix
                        )
                }
                else -> {
                    this.detalhamentoChavePixService
                        .detalhamentoPorIdentificadores(
                            request.identificadoresChave.identificadorCliente,
                            request.identificadoresChave.indentificadorPix
                        )
                }
            }


        val response =
            ChavePixDetalhesResponse.newBuilder()
                .setIdentificadoresChave(
                    ChavePixResponse.newBuilder()
                        .setIdentificadorCliente(chavePix.identificadorCliente)
                        .setIndentificadorPix(chavePix.id.toString().let{
                            if(chavePix.identificadorCliente.isEmpty())
                                return@let ""
                            it
                        })
                        .build()
                ).setTitular(
                    Titular.newBuilder()
                        .setNome(chavePix.contaAssociada.nomeTitular)
                        .setCpf(chavePix.contaAssociada.cpfTitular)
                        .build()
                ).setContaVinculada(
                    ContaVinculada  .newBuilder()
                        .setTipoContaValue(TipoConta.valueOf(chavePix.tipoConta.name).ordinal)
                        .setAgencia(chavePix.contaAssociada.agencia)
                        .setNumeroConta(chavePix.contaAssociada.numero)
                        .setIspb(chavePix.contaAssociada.ispb)
                        .setNomeInstituicaoFinanceira(chavePix.contaAssociada.nomeInstituicao)
                        .build()
                ).setTipoChave(
                    TipoChave.valueOf(chavePix.chave.name)
                ).setValorChave(
                    chavePix.valor
                ).setCriadoEm(
                    chavePix.criadoEm.toString()
                ).build()


        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}