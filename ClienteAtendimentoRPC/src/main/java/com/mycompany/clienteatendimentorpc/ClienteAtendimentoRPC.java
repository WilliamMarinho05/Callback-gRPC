package com.mycompany.clienteatendimentorpc;

public class ClienteAtendimentoRPC {

    public static void main(String[] args) {
        // Abre a interface gráfica do totem
        java.awt.EventQueue.invokeLater(() -> {
            new TelaTotem().setVisible(true);
        });
    }
}