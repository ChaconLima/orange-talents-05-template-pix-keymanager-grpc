package br.com.mateuschacon.keymanager.grpc.recurso.clientes.dtos

data class DeletePixKeyRequest(
    val key:String,
    val participant:String
)
data class DeletePixKeyResponse(
    val deletedAt: String
)