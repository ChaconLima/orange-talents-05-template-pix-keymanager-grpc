package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChaveEnum() {
    DEFAULT_TIPO_CHAVE {
        override fun valida(valorChave: String?): Boolean {
            return false
        }

        override fun outroValorParaChave(valorChave: String?): String = "DEFAULT_TIPO_CHAVE"
    },
    CPF {
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
    TELEFONE{
        override fun valida(valorChave: String?): Boolean {
            if (valorChave.isNullOrBlank()) return false

            return valorChave.matches(regex = "^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }

        override fun outroValorParaChave(valorChave: String?): String = "PHONE"

    },
    EMAIL{
        override fun valida(valorChave: String?): Boolean {

            if (valorChave.isNullOrBlank()) return false

            return EmailValidator().run {

                initialize(null)
                isValid(valorChave, null)
            }
        }

        override fun outroValorParaChave(valorChave: String?): String = "EMAIL"
    },
    ALEATORIA{
        override fun valida(valorChave: String?): Boolean {
            return valorChave.isNullOrBlank()
        }

        override fun outroValorParaChave(valorChave: String?): String = "RANDOM"
    };

    abstract fun valida(valorChave: String?): Boolean
    abstract fun outroValorParaChave(valorChave: String?): String
}