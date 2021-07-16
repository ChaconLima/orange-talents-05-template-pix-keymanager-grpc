package br.com.mateuschacon.keymanager.grpc.recurso.modelos


import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Participantesstrport(

    @Id
    val isbp:String,


    val nomeReduzido:String,


    val numeroCodigo:String,


    val participaCompe:String,


    val acessoPrincipal:String,


    val nomeExtenso:String,


    val inicioOperacao:String
){

}