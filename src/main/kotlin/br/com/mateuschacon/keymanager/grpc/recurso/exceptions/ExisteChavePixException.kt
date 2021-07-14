package br.com.mateuschacon.keymanager.grpc.recurso.exceptions

import java.lang.RuntimeException

class ExisteChavePixException(
    override val message: String
): RuntimeException() {
}