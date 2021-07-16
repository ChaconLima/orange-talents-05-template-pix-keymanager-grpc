package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums

enum class TipoContaEnum(val conta:String) {
    CONTA_CORRENTE(conta = "CACC"){
        override fun outroValorParaConta(valorChave: String?): String = "CACC"
    },
    CONTA_POUPANCA(conta = "SVGS"){
        override fun outroValorParaConta(valorChave: String?): String = "SVGS"
    };

    companion object {
        private val map = TipoContaEnum.values().associateBy(TipoContaEnum::conta)
        fun reversoVindoBCB(type: String) = map[type]
    }

    abstract fun outroValorParaConta(valorChave: String?): String
}