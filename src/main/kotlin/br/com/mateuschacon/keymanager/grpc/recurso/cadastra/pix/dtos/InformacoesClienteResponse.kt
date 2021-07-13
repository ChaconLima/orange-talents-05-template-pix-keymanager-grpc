package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.modelos.ContaAssociada
import io.micronaut.core.annotation.Introspected


data class InstituicaoDto(
    val nome:String,
    val ispb:String
)
data class TitularDto(
    val id:String,
    val nome:String,
    val cpf: String
)

@Introspected
data class InformacoesDoClienteDto(
    val tipo:TipoContaEnum,
    val agencia:String,
    val numero: String,
    val titular:TitularDto,
    val instituicao: InstituicaoDto
) {

    fun paraContaAssociada(): ContaAssociada {
        return ContaAssociada(
            nomeInstituicao = this.instituicao.nome,
            ispb = this.instituicao.ispb,
            identificadorTitular = this.titular.id,
            nomeTitular = this.titular.nome,
            cpfTitular = this.titular.cpf,
            agencia = this.agencia,
            numero = this.numero
        )
    }
}
