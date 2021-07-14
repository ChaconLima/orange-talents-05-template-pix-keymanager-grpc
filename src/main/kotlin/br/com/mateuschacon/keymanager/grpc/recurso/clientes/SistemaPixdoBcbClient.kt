package br.com.mateuschacon.keymanager.grpc.recurso.clientes

import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.CreatePixKeyRequest
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.CreatePixKeyResponse
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.DeletePixKeyRequest
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.DeletePixKeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client(value = "http://localhost:8082")
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
}