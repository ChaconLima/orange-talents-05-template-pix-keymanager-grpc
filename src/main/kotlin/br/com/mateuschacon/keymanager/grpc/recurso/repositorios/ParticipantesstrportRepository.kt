package br.com.mateuschacon.keymanager.grpc.recurso.repositorios

import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.Participantesstrport
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ParticipantesstrportRepository: JpaRepository<Participantesstrport, String> {
    fun findByIsbp(participant: String): Optional<Participantesstrport>
}