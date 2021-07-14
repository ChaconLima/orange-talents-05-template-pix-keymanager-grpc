package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.server.grpc

import br.com.mateuschacon.keymanager.grpc.NovaChavePixRequest
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.TipoChave
import br.com.mateuschacon.keymanager.grpc.TipoConta
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun NovaChavePixRequest.paraNovaChavePix(): NovaChavePix =
    let { request: NovaChavePixRequest ->
        NovaChavePix(
            identificadorCliente = request.indentificadorCliente,
            tipoChave = when (request.tipoChave) {
                TipoChave.DEFAULT_TIPO_CHAVE -> null
                else -> TipoChaveEnum.valueOf(request.tipoChave.name)
            },
            valorChave = request.valorChave,
            tipoConta = when (request.tipoConta) {
                TipoConta.DEFAULT_TIPO_CONTA -> null
                else -> TipoContaEnum.valueOf(request.tipoConta.name)
            }
        )
    }
