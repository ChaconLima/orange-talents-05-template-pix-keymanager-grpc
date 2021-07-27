package br.com.mateuschacon.keymanager.grpc.recurso.clientes

import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.InformacoesDoClienteDto
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "\${itau.contas.url}")
interface ContasDeClientesNoItauClient {

    @Get(value = "/api/v1/clientes/{idCliente}/contas{?tipo}")
    fun buscaContaPorTipo(
        @PathVariable idCliente: String,
        @QueryValue tipo: String
    ):HttpResponse<InformacoesDoClienteDto>
}