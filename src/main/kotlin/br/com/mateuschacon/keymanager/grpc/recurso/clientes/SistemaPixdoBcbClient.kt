package br.com.mateuschacon.keymanager.grpc.recurso.clientes

import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client(value = "\${bcb.pix.url}")
interface SistemaPixdoBcbClient {

    @Post(
        value = "/api/v1/pix/keys",
        consumes = [ MediaType.APPLICATION_XML],
        produces = [ MediaType.APPLICATION_XML]
    )
    fun cadastramento(
        @Body novaChavePixBcbRequest: CreatePixKeyRequest
    ):HttpResponse<CreatePixKeyResponse>

    @Delete(
        value = "/api/v1/pix/keys/{key}",
        consumes = [ MediaType.APPLICATION_XML],
        produces = [ MediaType.APPLICATION_XML]
    )
    fun deletar(
        @PathVariable key:String,
        @Body deletePixKeyRequest: DeletePixKeyRequest
    ):HttpResponse<DeletePixKeyResponse>

    @Get(
        value = "/api/v1/pix/keys/{key}",
        consumes = [ MediaType.APPLICATION_XML],
    )
    fun detalhes(
        @PathVariable key:String
    ):HttpResponse<DetalhesChavePixBcbResponse>
}