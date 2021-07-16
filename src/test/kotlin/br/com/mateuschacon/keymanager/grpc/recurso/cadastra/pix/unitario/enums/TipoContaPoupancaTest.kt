package br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.unitario.enums

import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoChaveEnum
import br.com.mateuschacon.keymanager.grpc.recurso.cadastra.pix.enums.TipoContaEnum
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest(transactional = false)
class TipoContaPoupancaTest {

    @Test
    fun `deve devolver outro valor `(){
        //senario
        val enum = TipoContaEnum.CONTA_POUPANCA
        //ação
        val isValid: String = enum.outroValorParaConta( enum.name )
        //validacao
        with(isValid){
            Assertions.assertEquals( "SVGS", isValid  )
        }

    }

    @Test
    fun `deve devolver o valor original quando passado uma variavel `(){

        //ação
        val isValid: TipoContaEnum? = TipoContaEnum.reversoVindoBCB("SVGS")
        //validacao
        with(isValid){
            Assertions.assertEquals( TipoContaEnum.CONTA_POUPANCA, isValid)
        }


    }
}