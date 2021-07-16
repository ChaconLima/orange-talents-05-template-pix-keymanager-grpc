package br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos

import br.com.mateuschacon.keymanager.grpc.TipoChave
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.modelos.ContaAssociada

data class BankAccountResponse(
    val participant:String,
    val branch:String,
    val accountNumber:String,
    val accountType: String
)
data class OwnerResponse(
    val type:String,
    val name:String,
    val taxIdNumber:String

)
data class DetalhesChavePixBcbResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: String
){
    fun paraChavePix():ChavePix {

        val tipoConta: TipoContaEnum = TipoContaEnum.valueOf(
            TipoContaEnum.reversoVindoBCB(this.bankAccount.accountType).toString())

        val tipoChave: TipoChaveEnum = TipoChaveEnum.valueOf(
            TipoChaveEnum.reversoVindoBCB(this.keyType).toString())


        return ChavePix(
            chave = tipoChave,
            identificadorCliente = "",
            valor = this.key,
            tipoConta = tipoConta,
            contaAssociada = ContaAssociada(
                nomeTitular = this.owner.name,
                nomeInstituicao = "",
                ispb = this.bankAccount.participant,
                cpfTitular = this.owner.taxIdNumber,
                agencia = this.bankAccount.branch,
                numero = this.bankAccount.accountNumber
            )
        ).also { it.modificacaoCriadoEm(this.createdAt) }
    }
}