package com.example;

import java.util.ArrayList;
import java.util.List;

/** Única classe de dados: o Nó (vértice). As formas nascem da ligação entre nós. */
class No {
    final double x, y;
    final double raio;     // > 0 só no nó de um círculo; 0 nos vértices
    No anterior;           // null se não ligar a nenhuma linha
    No proximo;            // null se não ligar a nenhuma linha

    No(double x, double y)              { this(x, y, 0); }
    No(double x, double y, double raio) { this.x = x; this.y = y; this.raio = raio; }

    /** Cria a linha entre este nó e o outro. */
    void ligarA(No outro) {
        this.proximo = outro;
        outro.anterior = this;
    }

    /** Corta a linha para o próximo: os dois lados da ligação passam a null. */
    void desligarDoProximo() {
        if (proximo != null) proximo.anterior = null;
        proximo = null;
    }

    /**
     * Percorre o loop a partir deste nó.
     * Devolve null se algum nó for null (loop aberto) ou se houver mais de 4 nós.
     */
    List<No> percorrer() {
        List<No> lista = new ArrayList<>();
        No n = this;
        while (true) {
            if (n.proximo == null || n.anterior == null) return null;   // nó solto
            if (n.proximo.anterior != n) return null;                   // ligação incoerente
            lista.add(n);
            n = n.proximo;
            if (n == this) return lista;                                // loop fechado
            if (lista.size() > 4) return null;                          // forma proibida
        }
    }

    @Override public String toString() { return "(" + x + ", " + y + ")"; }
}

public class Main {

    // ---------- Construção: só se criam nós e ligações ----------

    static No criarCirculo(double cx, double cy, double raio) {
        No centro = new No(cx, cy, raio);
        centro.ligarA(centro);                  // loop de 1 nó
        return centro;
    }

    static No criarLoop(No... nos) {
        for (int i = 0; i < nos.length; i++) nos[i].ligarA(nos[(i + 1) % nos.length]);
        return nos[0];
    }

    // ---------- Identificação: proíbe tudo o que não seja as 3 formas ----------

    static String identificar(No inicio) {
        List<No> n = inicio.percorrer();
        if (n == null) throw new IllegalStateException("Proibido: loop aberto, nó nulo ou mais de 4 nós.");

        if (n.size() == 1 && inicio.raio > 0) return "CIRCULO";
        for (No v : n) if (v.raio != 0) throw new IllegalStateException("Proibido: raio num vértice.");

        if (n.size() == 3 && areaTriangulo(n) > 1e-9) return "TRIANGULO";
        if (n.size() == 4 && eRetangulo(n)) return "RETANGULO";

        throw new IllegalStateException("Proibido: esta ligação de nós não forma Triângulo, Retângulo nem Círculo.");
    }

    static double areaTriangulo(List<No> n) {
        No a = n.get(0), b = n.get(1), c = n.get(2);
        return Math.abs(a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2;
    }

    static boolean eRetangulo(List<No> n) {
        for (int i = 0; i < 4; i++) {
            No p = n.get(i), seg = n.get((i + 1) % 4), ant = n.get((i + 3) % 4);
            double v1x = seg.x - p.x, v1y = seg.y - p.y;
            double v2x = ant.x - p.x, v2y = ant.y - p.y;
            if (Math.hypot(v1x, v1y) < 1e-9 || Math.hypot(v2x, v2y) < 1e-9) return false;
            if (Math.abs(v1x * v2x + v1y * v2y) > 1e-9) return false;   // ângulo não é reto
        }
        return true;
    }

    // ---------- Impressão do anterior / atual / próximo ----------

    static void mostrar(String titulo, No inicio) {
        System.out.println("== " + titulo + " ==");
        try {
            System.out.println("Forma: " + identificar(inicio));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        No n = inicio;
        for (int i = 0; i < 5 && n != null; i++) {
            System.out.println("  anterior=" + n.anterior + "  atual=" + n + "  próximo=" + n.proximo);
            n = n.proximo;
            if (n == inicio) break;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        mostrar("Triângulo",
                criarLoop(new No(0, 0), new No(4, 0), new No(0, 3)));

        mostrar("Retângulo",
                criarLoop(new No(0, 0), new No(5, 0), new No(5, 2), new No(0, 2)));

        mostrar("Círculo", criarCirculo(1, 1, 3));

        mostrar("Pentágono (proibido)",
                criarLoop(new No(0, 0), new No(2, 0), new No(3, 2), new No(1, 3), new No(-1, 2)));

        // Loop aberto: o último nó não liga a nenhuma linha, por isso é null
        No a = new No(0, 0), b = new No(4, 0), c = new No(0, 3);
        a.ligarA(b);
        b.ligarA(c);
        mostrar("Triângulo com loop aberto (proibido)", a);

        // Cortar uma linha de um retângulo válido
        No r = criarLoop(new No(0, 0), new No(5, 0), new No(5, 2), new No(0, 2));
        r.desligarDoProximo();
        mostrar("Retângulo depois de cortar uma linha (proibido)", r);
    }
}