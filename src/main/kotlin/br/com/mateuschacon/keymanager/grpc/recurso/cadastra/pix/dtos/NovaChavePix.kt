package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos

import br.com.mateuschacon.keymanager.grpc.TipoChave
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.BankAccountRequest
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.CreatePixKeyRequest
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.CreatePixKeyResponse
import br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos.OwnerRequest
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
    fun paraNovaChavePixBcbRequest(@Valid contaAssociada: ContaAssociada): CreatePixKeyRequest{
        return CreatePixKeyRequest(
            keyType = this.tipoChave!!.outroValorParaChave(this.tipoChave.name),
            key = this.valorChave!!,
            bankAccount = BankAccountRequest(
                participant = contaAssociada.ispb,
                branch = contaAssociada.agencia,
                accountNumber = contaAssociada.numero,
                accountType = this.tipoConta!!.outroValorParaConta( this.tipoConta.name )
            ),
            owner = OwnerRequest(
                type = "NATURAL_PERSON",
                name = contaAssociada.nomeTitular,
                taxIdNumber = contaAssociada.cpfTitular
            )
        )
    }

    fun paraChavePix(novaChavePixBcbResponse: CreatePixKeyResponse,
                     @Valid contaAssociada: ContaAssociada
    ): ChavePix
    {
        return ChavePix(
            tipoConta=this.tipoConta!!,
            contaAssociada = contaAssociada,
            chave = this.tipoChave!!,
            valor = novaChavePixBcbResponse.key,
            identificadorCliente = this.identificadorCliente!!
        )
    }

}
