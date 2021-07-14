package br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos

data class BankAccountRequest(
    val participant:String,
    val branch:String,
    val accountNumber:String,
    val accountType: String
)
data class OwnerRequest(
    val type:String,
    val name:String,
    val taxIdNumber:String

)
data class CreatePixKeyRequest(
    val keyType:String,
    val key: String,
    val bankAccount:BankAccountRequest,
    val owner:OwnerRequest
)
data class CreatePixKeyResponse(
    val key:String,
    val createdAt: String
)