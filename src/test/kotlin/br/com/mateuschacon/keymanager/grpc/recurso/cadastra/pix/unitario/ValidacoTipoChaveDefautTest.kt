package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.unitario

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
class ValidacoTipoChaveDefautTest {

    @Test
    fun `nao deve validar Defaut`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                identificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.DEFAULT_TIPO_CHAVE,
                valorChave = ""
            )

        //ação
        val isValid: Boolean =
            novaChave.tipoChave!!.valida(novaChave.valorChave)

        //validação
        with(isValid){
            Assertions.assertEquals(isValid, false)
        }
    }
}