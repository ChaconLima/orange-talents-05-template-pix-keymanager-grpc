package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.unitario.enums

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.integracao.CadastroNovaChavePixEndPointTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
internal class TipoChaveCpfTest {

    @Test
    fun `deve validar o cpf`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                identificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.CPF,
                valorChave = "81958192309"
            )

        //ação
        val isValid: Boolean =
            novaChave.tipoChave!!.valida(novaChave.valorChave)

        //validação
        with(isValid){
            assertEquals(  true, isValid)
        }
    }

    @Test
    fun `deve nao validar cpf por causa do regex`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                identificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.CPF,
                valorChave = "81958.cpf.invalido.192309"
            )

        //ação
        val isValid: Boolean =
            novaChave.tipoChave!!.valida(novaChave.valorChave)

        //validação
        with(isValid){
            assertEquals( false,  isValid)
        }
    }

    @Test
    fun `deve nao validar cpf por estar em branco`(){
        //cenario
        val novaChave: NovaChavePix =
            NovaChavePix(
                identificadorCliente = UUID.randomUUID().toString(),
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                tipoChave = TipoChaveEnum.CPF,
                valorChave = ""
            )

        //ação
        val isValid: Boolean =
            novaChave.tipoChave!!.valida(novaChave.valorChave)

        //validação
        with(isValid){
            assertEquals( false, isValid )
        }
    }

    @Test
    fun `deve devolver outro valor `(){
        //senario
        val enum = TipoChaveEnum.CPF
        //ação
        val isValid: String = enum.outroValorParaChave( enum.name )
        //validacao
        with(isValid){
            Assertions.assertEquals( "CPF", isValid  )
        }

    }

    @Test
    fun `deve devolver o valor original quando passado uma variavel `(){

        //ação
        val isValid: TipoChaveEnum? = TipoChaveEnum.reversoVindoBCB("CPF")
        //validacao
        with(isValid){
            Assertions.assertEquals( TipoChaveEnum.CPF, isValid)
        }


    }
}