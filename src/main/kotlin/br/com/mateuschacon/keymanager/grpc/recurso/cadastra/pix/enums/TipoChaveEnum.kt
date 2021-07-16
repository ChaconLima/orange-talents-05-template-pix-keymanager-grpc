package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChaveEnum(val chave:String) {
    DEFAULT_TIPO_CHAVE(chave = "DEFAULT_TIPO_CHAVE") {
        override fun valida(valorChave: String?): Boolean {
            return false
        }

        override fun outroValorParaChave(valorChave: String?): String = "DEFAULT_TIPO_CHAVE"
    },
    CPF(chave = "CPF") {
        override fun valida(valorChave: String?): Boolean {
            if (valorChave.isNullOrBlank()) return false

            if(!valorChave.matches(regex = "[0-9]+".toRegex())) return false

            return CPFValidator().run {
                initialize(null)
                isValid(valorChave,null)
            }
        }

        override fun outroValorParaChave(valorChave: String?): String = "CPF"
    },
    TELEFONE(chave = "PHONE"){
        override fun valida(valorChave: String?): Boolean {
            if (valorChave.isNullOrBlank()) return false

            return valorChave.matches(regex = "^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }

        override fun outroValorParaChave(valorChave: String?): String = "PHONE"

    },
    EMAIL(chave = "EMAIL"){
        override fun valida(valorChave: String?): Boolean {

            if (valorChave.isNullOrBlank()) return false

            return EmailValidator().run {

                initialize(null)
                isValid(valorChave, null)
            }
        }

        override fun outroValorParaChave(valorChave: String?): String = "EMAIL"
    },
    ALEATORIA(chave = "RANDOM"){
        override fun valida(valorChave: String?): Boolean {
            return valorChave.isNullOrBlank()
        }

        override fun outroValorParaChave(valorChave: String?): String = "RANDOM"
    };

    abstract fun valida(valorChave: String?): Boolean
    abstract fun outroValorParaChave(valorChave: String?): String
    companion object {
        private val map = TipoChaveEnum.values().associateBy(TipoChaveEnum::chave)
        fun reversoVindoBCB(type: String) = map[type]
    }
}