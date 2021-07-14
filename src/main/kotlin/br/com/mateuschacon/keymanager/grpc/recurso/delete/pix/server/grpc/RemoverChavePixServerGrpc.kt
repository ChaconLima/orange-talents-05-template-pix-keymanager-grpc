package br.com.mateuschacon.keymanager.grpc.recurso.delete.pix.server.grpc

import br.com.mateuschacon.keymanager.grpc.ChavePixExistenteRequest
import br.com.mateuschacon.keymanager.grpc.ChavePixExistenteResponse
import br.com.mateuschacon.keymanager.grpc.KeymanagerRemoverServiceGrpc
import br.com.mateuschacon.keymanager.grpc.error.hadlers.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RemoverChavePixServerGrpc(
    @Inject private val removerChavePixService: RemoverChavePixService
): KeymanagerRemoverServiceGrpc.KeymanagerRemoverServiceImplBase(){

    override fun delete(
        request: ChavePixExistenteRequest?,
        responseObserver: StreamObserver<ChavePixExistenteResponse>
    ) {

        this.removerChavePixService.delete(
            identificadorCliente = request!!.identificadorCliente,
            identificadorChavePix = request.indentificadorPix
        )
        val response =  ChavePixExistenteResponse.newBuilder()
            .setOk("Remoção Concluída")
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

}