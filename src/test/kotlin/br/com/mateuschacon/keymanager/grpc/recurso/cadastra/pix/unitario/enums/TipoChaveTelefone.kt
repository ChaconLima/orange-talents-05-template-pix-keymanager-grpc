package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.unitario.enums

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.dtos.NovaChavePix
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
internal class TipoChaveTelefone {

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
            Assertions.assertEquals( true, isValid)
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
            Assertions.assertEquals( false, isValid)
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
            Assertions.assertEquals( false, isValid)
        }
    }
    @Test
    fun `deve devolver outro valor `(){
        //senario
        val enum = TipoChaveEnum.TELEFONE
        //ação
        val isValid: String = enum.outroValorParaChave( enum.name )
        //validacao
        with(isValid){
            Assertions.assertEquals( "PHONE", isValid  )
        }

    }

    @Test
    fun `deve devolver o valor original quando passado uma variavel `(){

        //ação
        val isValid: TipoChaveEnum? = TipoChaveEnum.reversoVindoBCB("PHONE")
        //validacao
        with(isValid){
            Assertions.assertEquals( TipoChaveEnum.TELEFONE, isValid)
        }


    }
}