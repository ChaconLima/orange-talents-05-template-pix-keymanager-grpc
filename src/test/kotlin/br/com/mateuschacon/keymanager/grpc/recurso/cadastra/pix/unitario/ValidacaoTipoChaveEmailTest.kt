package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.unitario

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.integracao.CadastroNovaChavePixEndPointTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
internal class ValidacaoTipoChaveEmailTest {

    @Test
    fun `deve validar o email`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                indentificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.EMAIL,
                valorChave = "tabajaras@tabajaras.com"
            )

        //ação
        val isValid: Boolean =
            novaChave.tipoChave!!.valida(novaChave.valorChave)

        //validação
        with(isValid){
            assertEquals( isValid, true)
        }
    }

    @Test
    fun `deve não validar email por causa do regex`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                indentificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.EMAIL,
                valorChave = "email.invalido"
            )

        //ação
        val isValid: Boolean =
            novaChave.tipoChave!!.valida(novaChave.valorChave)

        //validação
        with(isValid){
            assertEquals( isValid, false)
        }
    }

    @Test
    fun `deve não validar email por estar em branco`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                indentificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.EMAIL,
                valorChave = ""
            )

        //ação
        val isValid: Boolean =
            novaChave.tipoChave!!.valida(novaChave.valorChave)

        //validação
        with(isValid){
            assertEquals( isValid, false)
        }
    }
}