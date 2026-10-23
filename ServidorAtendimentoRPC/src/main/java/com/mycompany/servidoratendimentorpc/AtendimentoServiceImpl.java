package com.mycompany.servidoratendimentorpc;

import com.mycompany.atendimentorpc.AtendimentoServiceGrpc;
import com.mycompany.atendimentorpc.PedidoSenha;
import com.mycompany.atendimentorpc.Senha;
import com.mycompany.atendimentorpc.TipoAtendimento;
import io.grpc.stub.StreamObserver;

import com.mycompany.atendimentorpc.Aviso;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ServerCallStreamObserver; 


public class AtendimentoServiceImpl
        extends AtendimentoServiceGrpc.AtendimentoServiceImplBase {

    // Estado compartilhado: todos os totens usam o mesmo contador
    private int proximoNumero = 1;

    // synchronized: dois totens nunca recebem o mesmo número
    private synchronized int gerarNumero() {
        return proximoNumero++;
    }

    @Override
    public void pegarSenha(PedidoSenha req, StreamObserver<Senha> observador) {
        int numero = gerarNumero();

        String prefixo = req.getTipo() == TipoAtendimento.PRIORITARIO
                ? "P" : "N";
        String codigo = String.format("%s-%03d", prefixo, numero);

        String mensagem = req.getNome() + ", sua senha é " + codigo
                + " (totem: " + req.getLocal() + ")";

        System.out.println("Senha " + codigo + " para " + req.getNome()
                + " no totem " + req.getLocal());

        Senha resposta = Senha.newBuilder()
                .setNumero(numero)
                .setCodigo(codigo)
                .setMensagem(mensagem)
                .build();

        observador.onNext(resposta);   // envia a resposta ao totem
        observador.onCompleted();      // encerra a chamada
    }
    
    @Override 
    public void acompanharSenha(Senha req, StreamObserver<Aviso> observador){
        ServerCallStreamObserver<Aviso> fluxo = (ServerCallStreamObserver<Aviso>) observador;
        
        fluxo.setOnCancelHandler(() ->
                System.out.println("[Central] CANCELADO: o totem encerrou o acompanhamento da senha " + req.getCodigo())
        );
        
        String[] avisos = {"faltam 3", "faltam 2", "faltam 1", "sua vez, guichê 2"};
        
        try{
            for(String texto : avisos){
                if(fluxo.isCancelled()){
                    return;
                }
                Aviso aviso = Aviso.newBuilder().setMensagem(texto).build();
                fluxo.onNext(aviso);
                System.out.println("[Central] enviado para "+ req.getCodigo()+": "+texto);
                Thread.sleep(2000);
            }
            fluxo.onCompleted();
            
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
            fluxo.onError(Status.INTERNAL.withDescription("Envio interrompido").asRuntimeException());
            
        } catch (StatusRuntimeException e){
            System.out.println("[Central] Falha ao enviar (totem provavelmente fechou): "+ e.getStatus());
        }
    }
}