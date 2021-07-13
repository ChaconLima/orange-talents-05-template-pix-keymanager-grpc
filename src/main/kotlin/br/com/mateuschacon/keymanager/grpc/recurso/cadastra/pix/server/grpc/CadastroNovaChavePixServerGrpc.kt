package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.server.grpc

import br.com.mateuschacon.keymanager.grpc.ChavePixResponse
import br.com.mateuschacon.keymanager.grpc.KeymanagerServiceGrpc
import br.com.mateuschacon.keymanager.grpc.NovaChavePixRequest
import br.com.mateuschacon.keymanager.grpc.error.hadlers.ErrorHandler
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.modelos.ChavePix
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ErrorHandler
class CadastroNovaChavePixServerGrpc(
    @Inject private val novaChavePixService: CadastroNovaChavePixService,
) : KeymanagerServiceGrpc.KeymanagerServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun registra(
        request: NovaChavePixRequest,
        responseObserver: StreamObserver<ChavePixResponse>
    ) {

        val chavePix: ChavePix =
            this.novaChavePixService.cadastro(request.paraNovaChavePix())

        val response =  ChavePixResponse.newBuilder()
            .setIndentificadorPix( chavePix.id.toString())
            .build()


        responseObserver.onNext(response)
        responseObserver.onCompleted()

    }
}
