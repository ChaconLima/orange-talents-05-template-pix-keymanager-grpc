package br.com.mateuschacon.keymanager.grpc.recurso.exceptions

import java.lang.RuntimeException

class NaoExisteChavePixException(
    override val message: String
) : RuntimeException() {
}
