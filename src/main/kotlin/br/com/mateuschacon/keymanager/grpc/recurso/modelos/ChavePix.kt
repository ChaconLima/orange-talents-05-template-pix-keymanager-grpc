package br.com.mateuschacon.keymanager.grpc.recurso.modelos


import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class ChavePix(

    @Enumerated(value = EnumType.STRING)
    val chave: TipoChaveEnum,

    val identificadorCliente:String,

    val valor: String,

    @Enumerated(value = EnumType.STRING)
    val tipoConta: TipoContaEnum,

    @Embedded
    val contaAssociada: ContaAssociada

    ) {
    @Id
    @Column(length = 16)
    val id:UUID = UUID.randomUUID()
    val criadoEm: LocalDateTime = LocalDateTime.now()
}