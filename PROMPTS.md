# PROMPTS.md - Callback

Registro cronológico dos prompts enviados à IA no projeto de Sistemas Distribuídos, com espaços para o grupo registrar as respostas e decisões. O material técnico gerado nas respostas está reunido nos anexos, ao final.

## Sumário

| Seção | Conteúdo |
|---|---|
| [Prompt 1](#prompt-1--pedido-inicial-e-parte-1-arquivo-proto) | Pedido inicial e Parte 1 (`.proto`) |
| [Prompt 2](#prompt-2--explicação-didática-da-parte-1-e-proto-idêntico) | Explicação didática da Parte 1 e `.proto` idêntico |
| [Prompt 3](#prompt-3--parte-2-central-servidor) | Parte 2: Central (Servidor) |
| [Prompt 4](#prompt-4--parte-3-totem-cliente-telatotemjava) | Parte 3: Totem (`TelaTotem.java`) |
---

## Prompt 1 — Pedido inicial e Parte 1: arquivo `.proto`

### Prompt enviado à IA

Sou estudante de Sistemas Distribuídos. Tenho este projeto gRPC em Java (código abaixo). Quero acrescentar o recurso de Callback (Server Streaming) da forma MAIS SIMPLES possível, no mesmo estilo, sem bibliotecas extras (sem Spring, sem banco de dados). Faça uma parte por vez e explique cada linha do código em português.

Especificação do que deve ser acrescentado:

1. No arquivo .proto:
   - Criar a mensagem Aviso (ex: string mensagem = 1;).
   - Adicionar o método rpc AcompanharSenha (Senha) returns (stream Aviso);.
2. Na Central (Servidor):
   - Implementar o método AcompanharSenha enviando mensagens sequenciais no mesmo fluxo de resposta (ex: "faltam 3", "faltam 2", "sua vez, guichê 2").
   - Tratar o cancelamento caso o totem seja fechado no meio do envio (registrar o cancelamento na Central).
3. No Totem (Cliente):
   - Criar a chamada assíncrona para receber e exibir os avisos do stream em tempo real sem precisar fazer novas requisições.

Também preciso que me explique didaticamente:

- A diferença entre Unário (1 → 1) e Server Streaming (1 → várias respostas).
- Por que usamos StreamObserver e não return.
- Como a Central consegue enviar múltiplos avisos sem o totem fazer novas perguntas.
- Exemplos de usos reais na indústria (painel de senhas, notificações push).

Base do contrato atual (atendimento.proto):

```protobuf
syntax = "proto3";
package atendimento;
option java_package = "com.mycompany.atendimentorpc";
option java_multiple_files = true;
service AtendimentoService {
 rpc PegarSenha (PedidoSenha) returns (Senha);
}
enum TipoAtendimento {
 NORMAL = 0;
 PRIORITARIO = 1;
}
message PedidoSenha {
 string nome = 1;
 TipoAtendimento tipo = 2;
 string local = 3;
}
message Senha {
 int32 numero = 1;
 string codigo = 2;
 string mensagem = 3;
}
```

Base do serviço atual (AtendimentoServiceImpl.java):

```java
package com.mycompany.servidoratendimentorpc;
import com.mycompany.atendimentorpc.AtendimentoServiceGrpc;
import com.mycompany.atendimentorpc.PedidoSenha;
import com.mycompany.atendimentorpc.Senha;
import com.mycompany.atendimentorpc.TipoAtendimento;
import io.grpc.stub.StreamObserver;
public class AtendimentoServiceImpl extends AtendimentoServiceGrpc.AtendimentoServiceImplBase {
 private int proximoNumero = 1;
 private synchronized int gerarNumero() {
 return proximoNumero++;
 }
 @Override
 public void pegarSenha(PedidoSenha req, StreamObserver<Senha> observador) {
 int numero = gerarNumero();
 String prefixo = req.getTipo() == TipoAtendimento.PRIORITARIO ? "P" : "N";
 String codigo = String.format("%s-%03d", prefixo, numero);
 String mensagem = req.getNome() + ", sua senha é " + codigo + " (totem: " + req.getLocal() + ")";
 Senha resposta = Senha.newBuilder().setNumero(numero).setCodigo(codigo).setMensagem(mensagem).build();
 observador.onNext(resposta);
 observador.onCompleted();
 }
}
```

Por favor, forneça primeiro apenas as alterações do arquivo .proto com as explicações linha por linha, para irmos passo a passo.

### Registro da resposta e das decisões do grupo

**O que a IA respondeu:**

Respondeu com o arquivo .proto, acrescentando a declaração do serviço especifico pro streaming das mensagens e inicializou o aviso (as mensagens enviadas)

**O que aceitamos da resposta:**

Aceitamos tudo, o código gerado é simples e inicia a parte da mensagem e o server streaming corretamente no .proto 

**O que mudamos ou recusamos:**

Nada dessa resposta foi recusada

**Por quê?**

O código gerado é simples, organizado e acima de tudo, está correto

---

## Prompt 2 — Explicação didática da Parte 1 e `.proto` idêntico

### Prompt enviado à IA

Nessa parte 1, explique de forma didática o que aconteceu no código. Também aponte regras, características, conceitos ou detalhes que fazem a diferença ou são minimamente relevantes. OS documentos atendimentos.photo devem ser idênticos no servidor e cliente mas vc n deu ênfase nisso, pq?

### Registro da resposta e das decisões do grupo

**O que a IA respondeu:**

Gerou uma explicação sobre o que ele fez na resposta do primeiro prompt, abordou a necessidade do .proto ser idêntico nos dois projetos, também apontou erros comuns e citou conceitos importantes

**O que aceitamos da resposta:**

Não se aplica, pois foi um prompt para responder uma dúvida sobre a parte teórica 

**O que mudamos ou recusamos:**

Não se aplica

**Por quê?**

Não se aplica
---

## Prompt 3 — Parte 2: Central (Servidor)

### Prompt enviado à IA

N quero. Agora vamos para a segunda parte: a Central (Servidor).

Com base no .proto atualizado, preciso implementar o método AcompanharSenha na classe AtendimentoServiceImpl.
A Central deve enviar mensagens sequenciais no mesmo fluxo de resposta ("faltam 3", "faltam 2", "sua vez, guichê 2").
Se o totem for fechado durante a transmissão, a Central deve detectar e registrar o cancelamento.

Por favor, forneça o código atualizado da classe AtendimentoServiceImpl e explique cada linha em português, sem utilizar bibliotecas extras.

### Registro da resposta e das decisões do grupo

**O que a IA respondeu:**

Respondeu com o código  do AtendimentoServiceImpl.java completo, destacando o que é novo para diferenciar do que já estava lá antes

**O que aceitamos da resposta:**

Aceitamos tudo, o código gerado está correto, ele adicionou apenas os novos imports e a função responsável pelo callback. O código não utiliza nenhuma biblioteca externa ou recurso fora do material. Ele é limpo e simples, contendo uma lista de mensagens, um bloco try responsável por enviá-las com Thread.sleep para pausar entre os envios e dois blocos catch para tratar eventuais erros e cancelamentos

**O que mudamos ou recusamos:**

Nada dessa resposta foi recusado ou alterado

**Por quê?**

O código gerado é simples, organizado e correto

---

## Prompt 4 — Parte 3: Totem (Cliente), `TelaTotem.java`

### Prompt enviado à IA

Agora vamos para a terceira e última parte: o Totem (Cliente).

Preciso atualizar a classe da interface gráfica (TelaTotem.java) para que, após pegar a senha, ela chame o método de streaming AcompanharSenha para receber os avisos da Central em tempo real ("faltam 3", "faltam 2", "sua vez, guichê 2") e atualize a mensagem na tela sem travar a interface e sem precisar de novas requisições.

Por favor, forneça o código completo da classe TelaTotem.java atualizado, mantendo a estrutura Swing e explicando cada linha em português.

Aqui está o código atual da classe TelaTotem.java:

```java
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.clienteatendimentorpc;

import com.mycompany.atendimentorpc.AtendimentoServiceGrpc;
import com.mycompany.atendimentorpc.PedidoSenha;
import com.mycompany.atendimentorpc.Senha;
import com.mycompany.atendimentorpc.TipoAtendimento;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

public class TelaTotem extends javax.swing.JFrame {

    private final ManagedChannel canal;
    private final AtendimentoServiceGrpc.AtendimentoServiceBlockingStub stub;

    public TelaTotem() {
        initComponents();
        canal = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()   // sem TLS: só no laboratório
                .build();
        stub = AtendimentoServiceGrpc.newBlockingStub(canal);
        Runtime.getRuntime().addShutdownHook(new Thread(canal::shutdown));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        grupoTipo = new javax.swing.ButtonGroup();
        painelFundo = new javax.swing.JPanel();
        painelCabecalho = new javax.swing.JPanel();
        lblMarca = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblLocal = new javax.swing.JLabel();
        cbxLocal = new javax.swing.JComboBox<>();
        lblNome = new javax.swing.JLabel();
        tfNome = new javax.swing.JTextField();
        lblTipo = new javax.swing.JLabel();
        rbNormal = new javax.swing.JRadioButton();
        rbPrioritario = new javax.swing.JRadioButton();
        btnPegarSenha = new javax.swing.JButton();
        painelSenha = new javax.swing.JPanel();
        lblSuaSenha = new javax.swing.JLabel();
        lbSenha = new javax.swing.JLabel();
        lblMensagem = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Central de Senhas - Totem");
        setResizable(false);

        painelFundo.setBackground(new java.awt.Color(244, 246, 245));

        painelCabecalho.setBackground(new java.awt.Color(18, 48, 47));

        lblMarca.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblMarca.setForeground(new java.awt.Color(233, 160, 108));
        lblMarca.setText("CENTRAL DE SENHAS");

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("Pegue sua senha");

        javax.swing.GroupLayout painelCabecalhoLayout = new javax.swing.GroupLayout(painelCabecalho);
        painelCabecalho.setLayout(painelCabecalhoLayout);
        painelCabecalhoLayout.setHorizontalGroup(
            painelCabecalhoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelCabecalhoLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(painelCabecalhoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMarca)
                    .addComponent(lblTitulo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        painelCabecalhoLayout.setVerticalGroup(
            painelCabecalhoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelCabecalhoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblMarca)
                .addGap(4, 4, 4)
                .addComponent(lblTitulo)
                .addGap(16, 16, 16))
        );

        lblLocal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLocal.setForeground(new java.awt.Color(74, 90, 94));
        lblLocal.setText("Local do totem");

        cbxLocal.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        cbxLocal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Biblioteca", "Central de Relacionamento", "Financeiro" }));
        cbxLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxLocalActionPerformed(evt);
            }
        });

        lblNome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNome.setForeground(new java.awt.Color(74, 90, 94));
        lblNome.setText("Digite seu nome");

        tfNome.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        lblTipo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTipo.setForeground(new java.awt.Color(74, 90, 94));
        lblTipo.setText("Tipo de atendimento");

        rbNormal.setBackground(new java.awt.Color(244, 246, 245));
        grupoTipo.add(rbNormal);
        rbNormal.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        rbNormal.setSelected(true);
        rbNormal.setText("Normal");

        rbPrioritario.setBackground(new java.awt.Color(244, 246, 245));
        grupoTipo.add(rbPrioritario);
        rbPrioritario.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        rbPrioritario.setText("Prioritário");

        btnPegarSenha.setBackground(new java.awt.Color(201, 86, 26));
        btnPegarSenha.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnPegarSenha.setForeground(new java.awt.Color(255, 255, 255));
        btnPegarSenha.setText("PEGAR SENHA");
        btnPegarSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPegarSenhaActionPerformed(evt);
            }
        });

        painelSenha.setBackground(new java.awt.Color(251, 234, 223));

        lblSuaSenha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSuaSenha.setForeground(new java.awt.Color(142, 58, 14));
        lblSuaSenha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSuaSenha.setText("SUA SENHA");

        lbSenha.setFont(new java.awt.Font("Segoe UI", 1, 46)); // NOI18N
        lbSenha.setForeground(new java.awt.Color(18, 48, 47));
        lbSenha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbSenha.setText("---");

        lblMensagem.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblMensagem.setForeground(new java.awt.Color(74, 90, 94));
        lblMensagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMensagem.setText("Aguarde ser chamado no painel");

        javax.swing.GroupLayout painelSenhaLayout = new javax.swing.GroupLayout(painelSenha);
        painelSenha.setLayout(painelSenhaLayout);
        painelSenhaLayout.setHorizontalGroup(
            painelSenhaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelSenhaLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(painelSenhaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSuaSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMensagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        painelSenhaLayout.setVerticalGroup(
            painelSenhaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelSenhaLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblSuaSenha)
                .addGap(4, 4, 4)
                .addComponent(lbSenha)
                .addGap(4, 4, 4)
                .addComponent(lblMensagem)
                .addGap(14, 14, 14))
        );

        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(13, 107, 112));
        lblStatus.setText("Central: localhost:50051");

        javax.swing.GroupLayout painelFundoLayout = new javax.swing.GroupLayout(painelFundo);
        painelFundo.setLayout(painelFundoLayout);
        painelFundoLayout.setHorizontalGroup(
            painelFundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelCabecalho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(painelFundoLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(painelFundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLocal)
                    .addComponent(cbxLocal, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblNome)
                    .addComponent(tfNome)
                    .addComponent(lblTipo)
                    .addGroup(painelFundoLayout.createSequentialGroup()
                        .addComponent(rbNormal)
                        .addGap(30, 30, 30)
                        .addComponent(rbPrioritario))
                    .addComponent(btnPegarSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(painelSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus))
                .addGap(24, 24, 24))
        );
        painelFundoLayout.setVerticalGroup(
            painelFundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelFundoLayout.createSequentialGroup()
                .addComponent(painelCabecalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblLocal)
                .addGap(6, 6, 6)
                .addComponent(cbxLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblNome)
                .addGap(6, 6, 6)
                .addComponent(tfNome, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblTipo)
                .addGap(6, 6, 6)
                .addGroup(painelFundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbNormal)
                    .addComponent(rbPrioritario))
                .addGap(18, 18, 18)
                .addComponent(btnPegarSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(painelSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblStatus)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelFundo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelFundo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>                        
    
    private void btnPegarSenhaActionPerformed(java.awt.event.ActionEvent evt) {                                              
        String nome = tfNome.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite seu nome.");
            return;
        }

        String local = (String) cbxLocal.getSelectedItem();
        TipoAtendimento tipo = rbPrioritario.isSelected()
                ? TipoAtendimento.PRIORITARIO : TipoAtendimento.NORMAL;

        PedidoSenha pedido = PedidoSenha.newBuilder()
                .setNome(nome).setTipo(tipo).setLocal(local).build();

        try {
            Senha senha = stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                    .pegarSenha(pedido);

            lbSenha.setText(senha.getCodigo());
            tfNome.setText("");

        } catch (StatusRuntimeException e) {
            lbSenha.setText("---");
            JOptionPane.showMessageDialog(this,
                    "Falha na chamada: " + e.getStatus().getCode());
        }
    
    }                                             

    private void cbxLocalActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnPegarSenha;
    private javax.swing.JComboBox<String> cbxLocal;
    private javax.swing.ButtonGroup grupoTipo;
    private javax.swing.JLabel lbSenha;
    private javax.swing.JLabel lblLocal;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblMensagem;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSuaSenha;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel painelCabecalho;
    private javax.swing.JPanel painelFundo;
    private javax.swing.JPanel painelSenha;
    private javax.swing.JRadioButton rbNormal;
    private javax.swing.JRadioButton rbPrioritario;
    private javax.swing.JTextField tfNome;
    // End of variables declaration                    
}
```

### Registro da resposta e das decisões do grupo

**O que a IA respondeu:**

Respondeu com o código completo da TelaTotem.java, incluindo os novos imports e a declaração do stub assíncrono para o streaming. Inicializou o contexto (Context.CancellableContext) para monitorar e cancelar o fluxo de mensagens, além de utilizar o addWindowListener para encerrar o acompanhamento e fechar o canal (canal.shutdown()) caso a janela seja fechada. Também integrou a chamada da função acompanharSenha(senha) ao botão btnPegarSenha, criando as funções acompanharSenha() e encerrarAcompanhamento().

**O que aceitamos da resposta:**

Tudo, o código proposto é simples e correto 

**O que mudamos ou recusamos:**

Nada

**Por quê?**

O código gerado é simples, correto e tem tudo o que foi pedido

---

# Anexos — Material técnico gerado nas respostas

Conteúdo técnico produzido pela IA nas respostas, preservado para referência e organizado por tema (sem duplicar o que já consta nos prompts acima).

## Anexo A — Parte 1: arquivo `.proto` (resposta ao Prompt 1)

### Código completo atualizado

```protobuf
syntax = "proto3";

package atendimento;

option java_package = "com.mycompany.atendimentorpc";
option java_multiple_files = true;

service AtendimentoService {
  rpc PegarSenha (PedidoSenha) returns (Senha);

  // NOVO: Server Streaming (note a palavra "stream" antes de Aviso)
  rpc AcompanharSenha (Senha) returns (stream Aviso);
}

enum TipoAtendimento {
  NORMAL = 0;
  PRIORITARIO = 1;
}

message PedidoSenha {
  string nome = 1;
  TipoAtendimento tipo = 2;
  string local = 3;
}

message Senha {
  int32 numero = 1;
  string codigo = 2;
  string mensagem = 3;
}

// NOVO: mensagem que a Central vai enviar várias vezes
message Aviso {
  string mensagem = 1;
}
```

### Explicação linha por linha (somente o novo)

**`rpc AcompanharSenha (Senha) returns (stream Aviso);`**

| Trecho | O que significa |
|---|---|
| `rpc` | Declara um método remoto dentro do `service`. |
| `AcompanharSenha` | Nome do método. Em Java vira `acompanharSenha` (camelCase). |
| `(Senha)` | Tipo da **requisição**: o totem envia **uma única** mensagem `Senha` (a recebida em `PegarSenha`), para a Central saber qual senha acompanhar. Reaproveita a mensagem existente. |
| `returns` | Introduz o tipo da resposta. |
| `stream` | **A palavra mais importante.** A resposta é um **fluxo de mensagens** ao longo do tempo. Sem ela, o método seria unário. |
| `Aviso` | Tipo de cada mensagem do fluxo. |
| `;` | Fim da declaração. |

> O `stream` está só no `returns`: isso caracteriza o **Server Streaming** (1 requisição, N respostas). Só na requisição seria Client Streaming; nos dois, Bidirecional.

**`message Aviso { string mensagem = 1; }`**

| Trecho | O que significa |
|---|---|
| `message Aviso` | Define um novo tipo. O protobuf gera a classe Java `Aviso` (`Aviso.newBuilder()`, `getMensagem()`...). |
| `string mensagem` | Campo de texto chamado `mensagem` (ex.: `"faltam 3"`). |
| `= 1` | **Número do campo** (tag), não um valor. É o identificador usado na codificação binária; deve ser único dentro da mensagem. |

**Comentários `// NOVO: ...`:** ignorados pelo compilador, servem só para documentar.

### O que acontece ao compilar (`mvn compile`)

- É gerada a classe **`Aviso`**, usada no servidor e no cliente.
- Em `AtendimentoServiceGrpc.AtendimentoServiceImplBase` surge o método a sobrescrever no servidor:
  ```java
  void acompanharSenha(Senha request, StreamObserver<Aviso> responseObserver)
  ```
- No stub do cliente surge o método correspondente.

O retorno é `void` e o `StreamObserver<Aviso>` entra como parâmetro (explicado no Anexo C).

### Resumo

- Foram adicionados **1 método** e **1 mensagem**.
- A única diferença sintática em relação a `PegarSenha` é a palavra `stream`, que muda totalmente o comportamento.
- Nada do que existia foi alterado; `PegarSenha` continua funcionando.

---

## Anexo B — `.proto` idêntico no servidor e no cliente (resposta ao Prompt 2)

A resposta reconheceu que a ênfase deveria ter existido: o `.proto` é o **contrato compartilhado** entre as duas pontas.

### O que aconteceu (fluxo *contract-first*)

1. Você descreve o contrato no `.proto` (serviços, métodos, mensagens).
2. O `protoc` (via plugin do Maven) **gera código Java**: `Senha`, `Aviso`, `PedidoSenha` e `AtendimentoServiceGrpc`, que contém a base do servidor (`ImplBase`) e os *stubs* do cliente.
3. Servidor e cliente usam esse código gerado para se entenderem.

O `.proto` **não é código executável**: é uma descrição. Ao mudá-lo, **os dois projetos precisam conhecer a mudança**.

### O que precisa coincidir

Não precisa ser *byte a byte* idêntico (comentários e espaços não importam), mas precisa ser **equivalente no que vai pela rede**. Em projeto acadêmico, **copie o mesmo arquivo** para os dois projetos.

| Elemento | Por que importa | Se divergir |
|---|---|---|
| **`package`, nome do `service` e do `rpc`** | Formam o caminho da chamada: `/atendimento.AtendimentoService/AcompanharSenha`. | O servidor responde `UNIMPLEMENTED`. |
| **Números dos campos** (`= 1`, `= 2`...) | É o que vai na rede, **não o nome**. | Dados lidos no campo errado ou ignorados, **sem erro visível**. |
| **Tipos dos campos** | Definem como os bytes são interpretados. | Dados corrompidos ou descartados silenciosamente. |
| **Presença do `stream`** | Define o tipo de chamada. | Incompatibilidade de protocolo. |

**Pode diferir sem quebrar a comunicação:** comentários e formatação; `option java_package` e `java_multiple_files` (afetam só o código Java local, mas convém manter iguais); nomes dos campos na comunicação binária (mudar o nome quebra o código Java, pois `getMensagem()` deixaria de existir).

### Erro mais comum

Alterar o `.proto` **só no servidor** e esquecer o cliente. No cliente, o método `acompanharSenha` **não existe** no stub (erro de compilação) ou, se já estava compilado, nunca consegue chamar o novo método.

**Regra prática:**

- [ ] Copiar o `.proto` para os dois projetos.
- [ ] Recompilar os dois (`mvn clean compile`).
- [ ] Só então reiniciar servidor e cliente.

### Outras regras e conceitos

**Numeração dos campos**
- Nunca **mude** o número de um campo existente nem **reutilize** o de um campo removido (use `reserved`).
- Números de 1 a 15 ocupam 1 byte; reserve-os para os campos mais usados.

**Compatibilidade**
- **Adicionar** campos ou métodos é compatível (clientes antigos ignoram o que não conhecem). Por isso `PegarSenha` continua funcionando.
- **Remover ou alterar** tipos e números quebra tudo.

**proto3**
- Não há `null` para tipos simples: `string` vazia é `""`, `int32` é `0`.
- O primeiro valor de um `enum` deve ser `0` (por isso `NORMAL = 0`).
- A **ordem** das declarações não importa.

**Posição do `stream`**

| Onde | Tipo de chamada |
|---|---|
| Em nenhum lugar | **Unário** (1 → 1) |
| Só no `returns` | **Server Streaming** (1 → N), o nosso caso |
| Só na requisição | Client Streaming (N → 1) |
| Nos dois | Bidirecional (N ↔ N) |

No Java gerado, o servidor continua com `void` + `StreamObserver<Aviso>`; o cliente ganha o *blocking stub* (recebe um `Iterator<Aviso>`, bloqueia a thread) ou o *async stub* (recebe um `StreamObserver<Aviso>` e trata cada aviso quando chega).

**Geração de código**
- `AcompanharSenha` (PascalCase) vira `acompanharSenha` (camelCase).
- As classes ficam em `target/generated-sources`: **nunca edite**, pois são sobrescritas a cada compilação. Toda mudança é feita no `.proto`.

> **Para fixar:** o `.proto` é o **acordo** entre cliente e servidor. Valem para a rede **nomes de serviço/método, números e tipos de campos e o `stream`**. Quando o acordo muda, **os dois lados recebem o novo arquivo e são recompilados**.

---

## Anexo C — Parte 2: Central (resposta ao Prompt 3)

### Código completo (`AtendimentoServiceImpl.java`)

```java
package com.mycompany.servidoratendimentorpc;

import com.mycompany.atendimentorpc.AtendimentoServiceGrpc;
import com.mycompany.atendimentorpc.Aviso;                       // NOVO
import com.mycompany.atendimentorpc.PedidoSenha;
import com.mycompany.atendimentorpc.Senha;
import com.mycompany.atendimentorpc.TipoAtendimento;
import io.grpc.Status;                                           // NOVO
import io.grpc.StatusRuntimeException;                           // NOVO
import io.grpc.stub.ServerCallStreamObserver;                    // NOVO
import io.grpc.stub.StreamObserver;

public class AtendimentoServiceImpl extends AtendimentoServiceGrpc.AtendimentoServiceImplBase {

    private int proximoNumero = 1;

    private synchronized int gerarNumero() {
        return proximoNumero++;
    }

    @Override
    public void pegarSenha(PedidoSenha req, StreamObserver<Senha> observador) {
        int numero = gerarNumero();
        String prefixo = req.getTipo() == TipoAtendimento.PRIORITARIO ? "P" : "N";
        String codigo = String.format("%s-%03d", prefixo, numero);
        String mensagem = req.getNome() + ", sua senha é " + codigo + " (totem: " + req.getLocal() + ")";
        Senha resposta = Senha.newBuilder().setNumero(numero).setCodigo(codigo).setMensagem(mensagem).build();
        observador.onNext(resposta);
        observador.onCompleted();
    }

    // NOVO: Server Streaming
    @Override
    public void acompanharSenha(Senha req, StreamObserver<Aviso> observador) {

        ServerCallStreamObserver<Aviso> fluxo = (ServerCallStreamObserver<Aviso>) observador;

        fluxo.setOnCancelHandler(() ->
            System.out.println("[Central] CANCELADO: o totem encerrou o acompanhamento da senha "
                    + req.getCodigo())
        );

        String[] avisos = { "faltam 3", "faltam 2", "faltam 1", "sua vez, guichê 2" };

        try {
            for (String texto : avisos) {
                if (fluxo.isCancelled()) {
                    return;
                }
                Aviso aviso = Aviso.newBuilder().setMensagem(texto).build();
                fluxo.onNext(aviso);
                System.out.println("[Central] Enviado para " + req.getCodigo() + ": " + texto);
                Thread.sleep(2000);
            }
            fluxo.onCompleted();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fluxo.onError(Status.INTERNAL.withDescription("Envio interrompido").asRuntimeException());

        } catch (StatusRuntimeException e) {
            System.out.println("[Central] Falha ao enviar (totem provavelmente fechou): " + e.getStatus());
        }
    }
}
```

### Explicação linha por linha (somente o novo)

**Imports novos**

| Linha | O que significa |
|---|---|
| `Aviso` | Classe gerada a partir da `message Aviso`. |
| `io.grpc.Status` | "Código de resultado" da chamada (`OK`, `CANCELLED`, `INTERNAL`...). Usado para sinalizar erro ao totem. |
| `StatusRuntimeException` | Exceção lançada pelo gRPC quando algo falha, por exemplo ao enviar a um cliente que já saiu. |
| `ServerCallStreamObserver` | `StreamObserver` "turbinado" do servidor, com `isCancelled()` e `setOnCancelHandler(...)`: a chave para detectar o cancelamento. |

Nenhuma é biblioteca extra.

**Assinatura**

```java
@Override
public void acompanharSenha(Senha req, StreamObserver<Aviso> observador) {
```

| Trecho | Significado |
|---|---|
| `@Override` | Sobrescreve o método de `AtendimentoServiceImplBase`; o compilador avisa se nome/parâmetros estiverem errados. |
| `public void` | Não retorna nada: as respostas saem pelo `observador`. |
| `Senha req` | A requisição: **uma** mensagem, a senha a acompanhar. |
| `StreamObserver<Aviso> observador` | O "canal de saída" para enviar **vários** `Aviso`. |

**Cast**

```java
ServerCallStreamObserver<Aviso> fluxo = (ServerCallStreamObserver<Aviso>) observador;
```

O parâmetro chega como `StreamObserver` (só `onNext`, `onError`, `onCompleted`). No servidor, o objeto entregue **é** um `ServerCallStreamObserver`; o *cast* dá acesso aos recursos extras. `fluxo` e `observador` apontam para o mesmo objeto.

**Cancelamento**

```java
fluxo.setOnCancelHandler(() -> System.out.println("[Central] CANCELADO: ... " + req.getCodigo()));
```

- `setOnCancelHandler(...)`: registra código que o gRPC executa **automaticamente** se o cliente cancelar ou a conexão cair.
- `() -> ...`: lambda, a função executada no cancelamento.
- `println(...)`: é aqui que "registramos o cancelamento" (em sistema real seria um log).
- Deve ser registrado **antes** de começar a enviar.
- Só dispara em **cancelamento**; com `onCompleted` normal não aparece.

**Avisos**

```java
String[] avisos = { "faltam 3", "faltam 2", "faltam 1", "sua vez, guichê 2" };
```

Textos na ordem de envio; fixos para simplicidade (guichê 2 é o exemplo do enunciado).

**Laço**

| Linha | Função |
|---|---|
| `try { for (String texto : avisos) {` | Percorre os avisos. O `try` trata as exceções de `sleep` e envio. |
| `if (fluxo.isCancelled()) { return; }` | A cada volta pergunta "o totem ainda está lá?". Se cancelado, para sem chamar `onCompleted`/`onError` (o gRPC já encerrou a chamada). |
| `Aviso.newBuilder().setMensagem(texto).build()` | Monta o objeto (mensagens protobuf são imutáveis, criadas pelo Builder). |
| `fluxo.onNext(aviso);` | **Envia.** Cada `onNext` entrega um `Aviso` pelo **mesmo fluxo aberto**: assim 1 requisição gera N respostas. |
| `println("[Central] Enviado ...")` | Registro no console, útil na demonstração. |
| `Thread.sleep(2000);` | Pausa de 2 s, simulando a fila andando e dando tempo de fechar o totem para testar o cancelamento. |
| `fluxo.onCompleted();` | Informa ao totem que não virá mais nada (status `OK`). **Sem isso o totem espera para sempre.** |

**`catch`**

```java
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    fluxo.onError(Status.INTERNAL.withDescription("Envio interrompido").asRuntimeException());
```

- `InterruptedException`: obrigatória por causa do `Thread.sleep` (ex.: servidor sendo desligado).
- `Thread.currentThread().interrupt()`: boa prática, reativa a flag de interrupção.
- `fluxo.onError(...)`: encerra **com erro**; `Status.INTERNAL` é o código, `withDescription` o texto, `asRuntimeException()` converte para a exceção exigida.

```java
} catch (StatusRuntimeException e) {
    System.out.println("[Central] Falha ao enviar (totem provavelmente fechou): " + e.getStatus());
}
```

Rede de segurança para uma condição de corrida: o totem pode fechar *depois* do `isCancelled()` e *antes* do `onNext`; este lança `StatusRuntimeException` (`CANCELLED`). Registramos e deixamos a thread terminar.

### Por que `StreamObserver` e não `return`

- `return` só ocorre **uma vez** e encerra o método: serve apenas para 1 → 1.
- `StreamObserver` é um **canal aberto**: `onNext` quantas vezes quiser, ao longo do tempo, encerrando com `onCompleted` ou `onError`.
- Mesmo no unário (`pegarSenha`) o gRPC usa `void` + `StreamObserver`: o modelo é único; o unário é o caso particular de **um** `onNext` seguido de `onCompleted`.

### Como a Central envia vários avisos sem o totem perguntar de novo

O totem faz **uma única** chamada. O gRPC, sobre **HTTP/2**, mantém essa chamada **aberta** e a Central escreve nela a cada `onNext`. Não há *polling*: o totem escuta e cada mensagem chega assim que enviada. A chamada termina com `onCompleted`, `onError` ou cancelamento.

### Limitações e detalhes

1. **`Thread.sleep` ocupa uma thread do servidor** durante todo o acompanhamento. Funciona para poucos totens; em produção usaria `ScheduledExecutorService` ou eventos reais da fila.
2. **O handler de cancelamento roda em outra thread** (a do gRPC), então a mensagem pode aparecer um instante antes ou depois do `return`.
3. **Não chame `onNext` após `onCompleted`/`onError`** (gera exceção); aqui o `return` evita isso.
4. **O estado é simulado:** avisos fixos, independentes da fila real, para focar no streaming.

### Como testar

- **Normal:** o totem recebe os 4 avisos e a Central **não** imprime "CANCELADO".
- **Cancelamento:** fechar o totem após o 1º ou 2º aviso; a Central imprime `[Central] CANCELADO: ...` e para de enviar.

---

## Anexo D — Parte 3: Totem (resposta ao Prompt 4)

> Antes de usar: copie o `.proto` atualizado para o projeto do cliente e rode **Clean and Build**.

### Código atualizado (novidades marcadas com `// NOVO`)

```java
package com.mycompany.clienteatendimentorpc;

import com.mycompany.atendimentorpc.AtendimentoServiceGrpc;
import com.mycompany.atendimentorpc.Aviso;                          // NOVO
import com.mycompany.atendimentorpc.PedidoSenha;
import com.mycompany.atendimentorpc.Senha;
import com.mycompany.atendimentorpc.TipoAtendimento;
import io.grpc.Context;                                             // NOVO
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;                                              // NOVO
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;                                 // NOVO
import java.awt.event.WindowAdapter;                                // NOVO
import java.awt.event.WindowEvent;                                  // NOVO
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;                                  // NOVO

public class TelaTotem extends javax.swing.JFrame {

    private final ManagedChannel canal;
    private final AtendimentoServiceGrpc.AtendimentoServiceBlockingStub stub;

    // NOVO: stub assíncrono, usado só para o streaming
    private final AtendimentoServiceGrpc.AtendimentoServiceStub stubAssincrono;

    // NOVO: guarda o acompanhamento em andamento, para poder cancelá-lo
    private Context.CancellableContext contextoAcompanhamento;

    public TelaTotem() {
        initComponents();
        canal = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()   // sem TLS: só no laboratório
                .build();
        stub = AtendimentoServiceGrpc.newBlockingStub(canal);
        stubAssincrono = AtendimentoServiceGrpc.newStub(canal);     // NOVO
        Runtime.getRuntime().addShutdownHook(new Thread(canal::shutdown));

        // NOVO: ao fechar a janela, cancela o acompanhamento antes de sair
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                encerrarAcompanhamento();
                canal.shutdown();
                try {
                    canal.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        // ... código gerado pelo NetBeans, INALTERADO em relação ao arquivo original do Prompt 4 ...
    }// </editor-fold>

    private void btnPegarSenhaActionPerformed(java.awt.event.ActionEvent evt) {
        String nome = tfNome.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite seu nome.");
            return;
        }

        String local = (String) cbxLocal.getSelectedItem();
        TipoAtendimento tipo = rbPrioritario.isSelected()
                ? TipoAtendimento.PRIORITARIO : TipoAtendimento.NORMAL;

        PedidoSenha pedido = PedidoSenha.newBuilder()
                .setNome(nome).setTipo(tipo).setLocal(local).build();

        try {
            Senha senha = stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                    .pegarSenha(pedido);

            lbSenha.setText(senha.getCodigo());
            tfNome.setText("");

            lblMensagem.setText("Aguardando avisos da Central...");  // NOVO
            acompanharSenha(senha);                                  // NOVO

        } catch (StatusRuntimeException e) {
            lbSenha.setText("---");
            JOptionPane.showMessageDialog(this,
                    "Falha na chamada: " + e.getStatus().getCode());
        }
    }

    // NOVO: abre o fluxo de avisos (Server Streaming)
    private void acompanharSenha(Senha senha) {

        encerrarAcompanhamento();

        StreamObserver<Aviso> receptor = new StreamObserver<Aviso>() {

            @Override
            public void onNext(Aviso aviso) {
                SwingUtilities.invokeLater(() ->
                    lblMensagem.setText(aviso.getMensagem())
                );
            }

            @Override
            public void onError(Throwable erro) {
                Status status = Status.fromThrowable(erro);
                if (status.getCode() == Status.Code.CANCELLED) {
                    return;
                }
                SwingUtilities.invokeLater(() ->
                    lblMensagem.setText("Acompanhamento falhou: " + status.getCode())
                );
            }

            @Override
            public void onCompleted() {
                // fim do fluxo: o último aviso ("sua vez...") permanece na tela
            }
        };

        contextoAcompanhamento = Context.current().withCancellation();
        contextoAcompanhamento.run(() ->
            stubAssincrono.acompanharSenha(senha, receptor)
        );
    }

    // NOVO: cancela o acompanhamento em andamento, se houver
    private void encerrarAcompanhamento() {
        if (contextoAcompanhamento != null) {
            contextoAcompanhamento.cancel(null);
            contextoAcompanhamento = null;
        }
    }

    private void cbxLocalActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    // Variables declaration - do not modify
    // (declarações dos componentes: idênticas às do arquivo original do Prompt 4)
    // End of variables declaration
}
```

### Explicação linha por linha (somente o novo)

**Imports**

| Linha | Significado |
|---|---|
| `Aviso` | Classe gerada de `message Aviso`; tipo de cada mensagem recebida. |
| `Context` | Contexto da chamada gRPC; permite **cancelá-la** à mão. |
| `Status` | Traduz o erro em código legível (`CANCELLED`, `UNAVAILABLE`...). |
| `StreamObserver` | Mesma interface do servidor, agora usada para **receber** respostas. |
| `WindowAdapter`, `WindowEvent` | Reagir a eventos da janela (fechamento). |
| `SwingUtilities` | Atualizar a tela com segurança a partir de outra thread. |

Nenhuma é biblioteca extra.

**Atributos**

- `stubAssincrono` (`AtendimentoServiceStub`): o `stub` existente é **bloqueante**; o assíncrono retorna na hora e entrega as respostas por *callback*, mantendo a interface livre. `pegarSenha` segue usando o bloqueante.
- `contextoAcompanhamento`: "controle remoto" do acompanhamento em andamento; como atributo, o fechamento da janela consegue cancelá-lo.

**Construtor**

- `stubAssincrono = AtendimentoServiceGrpc.newStub(canal);` usa o **mesmo canal** (não abre segunda conexão).
- `addWindowListener(new WindowAdapter() {...})`: `WindowAdapter` traz todos os métodos vazios; sobrescrevemos só `windowClosing` (clique no **X**):

| Linha | O que faz |
|---|---|
| `encerrarAcompanhamento();` | Cancela o stream; a Central dispara o `setOnCancelHandler` e registra. |
| `canal.shutdown();` | Inicia o fechamento ordenado do canal. |
| `canal.awaitTermination(1, TimeUnit.SECONDS);` | Espera até 1 s para o cancelamento sair pela rede. Como a janela usa `EXIT_ON_CLOSE`, sem a espera o `System.exit` poderia matar a JVM antes do aviso chegar à Central. |
| `catch (InterruptedException ...)` | Exigido pelo `awaitTermination`; restaura a flag de interrupção. |

**`btnPegarSenhaActionPerformed`**

```java
lblMensagem.setText("Aguardando avisos da Central...");
acompanharSenha(senha);
```

Dentro do `try`, após receber a senha: a primeira linha dá retorno visual imediato; a segunda dispara o streaming passando a `Senha` (é ela que a Central usa para saber o que acompanhar). Se `pegarSenha` falhar, o acompanhamento nem começa.

**`acompanharSenha(Senha senha)`**

- `encerrarAcompanhamento();`: se o usuário pegar uma segunda senha com a primeira em andamento, cancela o fluxo antigo (evita dois streams escrevendo no mesmo rótulo).
- `new StreamObserver<Aviso>() { ... }`: o **receptor**, ouvinte acionado pelo gRPC (ideia de **callback**).

| Método | Quando roda | O que faz |
|---|---|---|
| `onNext(Aviso aviso)` | **Uma vez para cada** `onNext` da Central (4 avisos, 4 execuções). | `SwingUtilities.invokeLater(() -> lblMensagem.setText(aviso.getMensagem()))`. O callback roda em thread do gRPC, mas componentes Swing só podem ser alterados pela **EDT**; `invokeLater` agenda na thread certa. Sem ele, a tela pode falhar de forma intermitente. |
| `onError(Throwable erro)` | Fluxo termina com falha. | `Status.fromThrowable(erro)` extrai o status; se for `CANCELLED` (nosso próprio cancelamento) ignora; senão mostra "Acompanhamento falhou: ..." (ex.: `UNAVAILABLE`). |
| `onCompleted()` | A Central executou seu `onCompleted()`. | Vazio de propósito: o último aviso ("sua vez, guichê 2") permanece na tela. |

- `contextoAcompanhamento = Context.current().withCancellation();`: copia o contexto atual tornando-o **cancelável manualmente** e guarda no atributo.
- `contextoAcompanhamento.run(() -> stubAssincrono.acompanharSenha(senha, receptor));`: `.run(...)` amarra a chamada ao contexto (cancelar o contexto cancela a chamada). A chamada envia a `senha` (única requisição) e entrega o `receptor`; **retorna imediatamente** e sem valor: tudo chega depois pelo receptor, e por isso a interface não trava.

**`encerrarAcompanhamento()`**

```java
if (contextoAcompanhamento != null) {
    contextoAcompanhamento.cancel(null);
    contextoAcompanhamento = null;
}
```

O `if` evita erro se nunca houve acompanhamento; `cancel(null)` cancela (sem exceção específica como motivo) e dispara o `setOnCancelHandler` da Central; zerar a referência evita cancelar duas vezes.

### Como isso atende ao pedido

- **Sem travar a interface:** stub assíncrono + `invokeLater`.
- **Sem novas requisições:** uma única chamada `acompanharSenha`; sem laço nem timer no cliente; a Central **empurra** cada aviso para o `onNext` (oposto de *polling*).
- **Mantém a estrutura Swing:** `initComponents()` intacto; só o `lblMensagem` é reaproveitado.

### Pontos de atenção

1. **`pegarSenha` ainda é bloqueante e roda na EDT** (já era assim no código original). Com deadline de 5 s, a tela pode congelar brevemente se a Central estiver fora do ar. Não alterado por estar fora do escopo.
2. Usar o **`.proto` idêntico** nos dois projetos e recompilar ambos.
3. Servidor fora do ar: o erro cai no `onError` (`UNAVAILABLE`) e aparece no rótulo.

### Roteiro de teste

1. [ ] Subir a Central e abrir o totem.
2. [ ] Digitar um nome e clicar em **PEGAR SENHA**: o rótulo muda sozinho a cada 2 s ("faltam 3", "faltam 2", "faltam 1", "sua vez, guichê 2"), sem cliques extras.
3. [ ] Console da Central mostra `Enviado para ...` e **não** mostra `CANCELADO`.
4. [ ] Repetir e **fechar o totem no meio**: a Central imprime `[Central] CANCELADO: ...` e para de enviar.
5. [ ] Pegar uma segunda senha com a primeira em andamento: a Central registra o cancelamento da primeira e a tela mostra só os avisos da nova.

### Revisão geral e conceitos

| Peça | Papel |
|---|---|
| `.proto` | Contrato: `rpc ... returns (stream Aviso)` define o 1 → N. |
| Central | `onNext` várias vezes + `onCompleted`; `setOnCancelHandler` e `isCancelled` detectam o totem saindo. |
| Totem | `newStub` assíncrono + `StreamObserver` como ouvinte; `Context` para cancelar. |

**Unário vs. Server Streaming:** no unário o cliente pergunta e recebe uma resposta; no server streaming pergunta uma vez e fica ouvindo várias.

**Usos reais na indústria:**
- **Painel de senhas** de bancos e hospitais, atualizando a fila sem recarregar.
- **Notificações push** em tempo real (mensagens, entregas).
- **Acompanhamento de pedido ou corrida** (iFood, Uber): status e localização chegam continuamente.
- **Cotações e painéis de monitoramento** (bolsa, métricas de servidores).

---

### Checklist técnico

- [ ] Atualizar o `.proto` e **copiá-lo idêntico** para Central e Totem.
- [ ] `mvn clean compile` (ou *Clean and Build*) nos **dois** projetos.
- [ ] Atualizar `AtendimentoServiceImpl.java` (Anexo C).
- [ ] Atualizar `TelaTotem.java` (Anexo D).
- [ ] Subir a Central e depois o Totem.
- [ ] Testar o fluxo normal (4 avisos, sem "CANCELADO").
- [ ] Testar o cancelamento (fechar o Totem no meio).
- [ ] Testar uma segunda senha com a primeira em andamento.
- [ ] Revisar os slides e ensaiar o roteiro de 8 minutos.
