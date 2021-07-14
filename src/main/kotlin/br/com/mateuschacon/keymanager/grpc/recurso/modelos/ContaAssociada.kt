package br.com.mateuschacon.keymanager.grpc.recurso.modelos

import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank


@Embeddable
data class ContaAssociada(

    @field:NotBlank
    val nomeInstituicao: String,
    @field:NotBlank
    val ispb: String,
    @field:NotBlank
    val nomeTitular: String,
    @field:NotBlank
    val cpfTitular: String,
    val agencia: String,
    @field:NotBlank
    val numero: String
)