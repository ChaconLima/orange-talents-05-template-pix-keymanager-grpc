package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.repositorios

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.modelos.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, UUID> {
    fun findByValor(valor: String): Optional<ChavePix>
}