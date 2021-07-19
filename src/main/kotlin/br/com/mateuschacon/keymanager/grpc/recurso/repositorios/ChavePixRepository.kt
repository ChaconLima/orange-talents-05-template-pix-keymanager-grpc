package br.com.mateuschacon.keymanager.grpc.recurso.repositorios

import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, UUID> {
    fun findByValor(valor: String): Optional<ChavePix>
    fun findByIdAndIdentificadorCliente(idChavePix: UUID, idCliente: String): Optional<ChavePix>
    fun findByIdentificadorCliente(identificadorCliente: String): List<ChavePix>

}