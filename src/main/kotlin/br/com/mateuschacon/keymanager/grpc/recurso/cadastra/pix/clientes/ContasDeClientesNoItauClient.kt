package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.clientes

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.InformacoesDoClienteDto
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "http://localhost:9091")
interface ContasDeClientesNoItauClient {

    @Get(value = "/api/v1/clientes/{idCliente}/contas{?tipo}")
    fun buscaContaPorTipo(
        @PathVariable idCliente: String,
        @QueryValue tipo: String
    ):HttpResponse<InformacoesDoClienteDto>
}