package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.unitario

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
internal class ValidacaoTipoChaveTelefone {

    @Test
    fun `deve validar o telefone`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                identificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.TELEFONE,
                valorChave = "+5534998832651"
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
    fun `deve nao validar telefone por causa do regex`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                identificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.TELEFONE,
                valorChave = "telefone invalido"
            )

        //ação
        val isValid: Boolean =
            novaChave.tipoChave!!.valida(novaChave.valorChave)

        //validação
        with(isValid){
            Assertions.assertEquals(isValid, false)
        }
    }

    @Test
    fun `deve nao validar telefone por estar em branco`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                identificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.EMAIL,
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