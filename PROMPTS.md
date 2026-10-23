# Relatório de Prompts Utilizados (PROMPTS.md)

Este documento registra os prompts utilizados na IA para o desenvolvimento do projeto e a justificativa das respostas/descartes.

---

## 1. Implementação do Protocolo Protobuf (`atendimento.proto`)

### Justificativa
O prompt abaixo foi utilizado para criar a estrutura inicial do contrato de comunicação gRPC. A resposta foi aceita integralmente por definir corretamente o serviço unário e o serviço em *Server Streaming*.

<!-- INÍCIO DO CHAT -->
<div style="display: flex; flex-direction: column; gap: 12px; margin: 20px 0; font-family: sans-serif;">

  <!-- MINHA FALA (PROMPT) - DIREITA -->
  <div style="align-self: flex-end; max-width: 80%; background-color: #2b5278; color: #ffffff; padding: 12px 16px; border-radius: 15px 15px 2px 15px; font-size: 14px;">
    <strong>Usuário (Prompt):</strong><br>
    Crie o arquivo atendimento.proto com o pacote atendimento, definindo a mensagem PedidoSenha, Senha e os métodos gRPC.
  </div>

  <!-- RESPOSTA DA IA - ESQUERDA -->
  <div style="align-self: flex-start; max-width: 80%; background-color: #f0f2f5; color: #1c1e21; padding: 12px 16px; border-radius: 15px 15px 15px 2px; border: 1px solid #e4e6eb; font-size: 14px;">
    <strong>Resposta da IA (Claude):</strong><br>
    Aqui está a definição do serviço. Utilizamos o comando <code>stream</code> no método <code>AcompanharSenha</code> para permitir o envio de múltiplos avisos:
    <pre style="background: #272822; color: #f8f8f2; padding: 8px; border-radius: 5px; font-size: 12px; margin-top: 8px;">
syntax = "proto3";
service AtendimentoService {
  rpc PegarSenha (PedidoSenha) returns (Senha);
  rpc AcompanharSenha (Senha) returns (stream Aviso);
}</pre>
  </div>

</div>
<!-- FIM DO CHAT -->

---

## 2. Ajuste do Tratamento de Cancelamento na Central

### Justificativa
Este prompt foi necessário para tratar a desconexão do Totem. A primeira resposta gerada pela IA foi **descartada** pois utilizava `Thread.sleep` direto, e pedimos uma revisão para usar escuta assíncrona do gRPC.

<!-- INÍCIO DO CHAT -->
<div style="display: flex; flex-direction: column; gap: 12px; margin: 20px 0; font-family: sans-serif;">

  <!-- MINHA FALA (PROMPT) - DIREITA -->
  <div style="align-self: flex-end; max-width: 80%; background-color: #2b5278; color: #ffffff; padding: 12px 16px; border-radius: 15px 15px 2px 15px; font-size: 14px;">
    <strong>Usuário (Prompt):</strong><br>
    Como faço para a Central perceber que a janela do Totem foi fechada e cancelar o envio dos avisos?
  </div>

  <!-- RESPOSTA DA IA - ESQUERDA -->
  <div style="align-self: flex-start; max-width: 80%; background-color: #f0f2f5; color: #1c1e21; padding: 12px 16px; border-radius: 15px 15px 15px 2px; border: 1px solid #e4e6eb; font-size: 14px;">
    <strong>Resposta da IA (Claude):</strong><br>
    Você deve verificar o estado da conexão através do contexto atual da chamada: <code>Context.current().isCancelled()</code> antes de cada chamada ao <code>onNext()</code>.
  </div>

</div>
<!-- FIM DO CHAT -->