package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.exceptions

import java.lang.RuntimeException

class ExisteChavePixException(
    override val message: String
): RuntimeException() {
}