package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos

import br.com.mateuschacon.keymanager.grpc.TipoChave
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ContaAssociada
import br.com.mateuschacon.keymanager.grpc.recurso.validadores.ValidacaoUUID
import br.com.mateuschacon.keymanager.grpc.recurso.validadores.ValidarChavePix
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ValidarChavePix
@Introspected
data class NovaChavePix(

    @field:ValidacaoUUID
    @field: NotBlank
    val identificadorCliente: String?,

    @field: NotNull
    val tipoChave: TipoChaveEnum?,

    val valorChave: String?,

    @field: NotNull
    val tipoConta: TipoContaEnum?
){
    fun paraChavePix(@Valid contaAssociada: ContaAssociada): ChavePix
    {
        return ChavePix(
            tipoConta=this.tipoConta!!,
            contaAssociada = contaAssociada,
            chave = this.tipoChave!!,
            valor =
                when(this.tipoChave.name){
                    TipoChave.ALEATORIA.name -> {
                        UUID.randomUUID().toString()
                    }
                    else -> {
                        this.valorChave
                    }
                }!!,
            identificadorCliente = this.identificadorCliente!!
        )
    }

}
