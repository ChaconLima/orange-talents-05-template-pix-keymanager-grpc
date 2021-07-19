package br.com.mateuschacon.keymanager.grpc.recurso.listar.pix

import br.com.mateuschacon.keymanager.grpc.*
import br.com.mateuschacon.keymanager.grpc.error.hadlers.ErrorHandler
import br.com.mateuschacon.keymanager.grpc.recurso.exceptions.NaoExisteChavePixException
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.repositorios.ChavePixRepository
import br.com.mateuschacon.keymanager.grpc.recurso.validadores.ValidacaoUUID
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@ErrorHandler
class ListarChavePixserverGrpc(
    @Inject private val chavePixRepository: ChavePixRepository
): KeymanagerListagemServiceGrpc.KeymanagerListagemServiceImplBase() {

    override fun listar(
        request: ChavePixClienteRequest?,
        responseObserver: StreamObserver<ChavePixClienteResponse>
    ) = this.buscaChavePix(request!!.identificadorCliente).map { chavePix:ChavePix ->
            ChavePixClienteResponse.ChavePixDetalhesClienteResponse.newBuilder()
                .setIdentificadorPix(chavePix.id.toString())
                .setTipoChave( TipoChave.valueOf( chavePix.chave.name ))
                .setTipoConta( TipoConta.valueOf( chavePix.tipoConta.name ))
                .setValorChave( chavePix.valor )
                .setCriadoEm( chavePix.criadoEm.let{
                    val criadoEm = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds( criadoEm.epochSecond)
                        .setNanos(criadoEm.nano)
                        .build()
                })
            .build()
        }.let{
            ChavePixClienteResponse.newBuilder()
                .setIdentificadorCliente(request.identificadorCliente)
                .addAllChavePixDetalhesClienteResponse(it)
                .build()
        }.run {
            responseObserver.onNext( this )
            responseObserver.onCompleted()
        }


    @Validated
    fun buscaChavePix(@Valid @ValidacaoUUID identificadorCliente: String):List<ChavePix> =
        this.chavePixRepository.findByIdentificadorCliente(identificadorCliente).let {
            if (it.isEmpty()) throw NaoExisteChavePixException("não existe")
            it
        }

}