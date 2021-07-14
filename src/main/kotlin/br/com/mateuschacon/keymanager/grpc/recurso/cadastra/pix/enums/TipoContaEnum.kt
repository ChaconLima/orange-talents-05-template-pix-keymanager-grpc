package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums

enum class TipoContaEnum() {
    CONTA_CORRENTE{
        override fun outroValorParaConta(valorChave: String?): String = "CACC"
    },
    CONTA_POUPANCA{
        override fun outroValorParaConta(valorChave: String?): String = "SVGS"
    };


    abstract fun outroValorParaConta(valorChave: String?): String
}