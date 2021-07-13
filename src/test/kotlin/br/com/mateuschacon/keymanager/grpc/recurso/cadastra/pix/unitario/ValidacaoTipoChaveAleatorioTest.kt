package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.unitario

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
internal class ValidacaoTipoChaveAleatorioTest {

    @Test
    fun `deve validar o telefone`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                indentificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.ALEATORIA,
                valorChave = ""
            )

        //ação
        val isValid: Boolean =
            novaChave.tipoChave!!.valida(novaChave.valorChave)

        //validação
        with(isValid){
            Assertions.assertEquals(isValid, true)
        }
    }

    @Test
    fun `deve não validar telefone por causa do regex`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                indentificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.ALEATORIA,
                valorChave = "invalido"
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