package com.example;

import java.util.ArrayList;
import java.util.List;

/** Nó = vértice. Guarda o nó anterior e o nó seguinte. */
class No {
    final double x, y;
    No anterior;   // null se não ligar a nenhuma linha
    No proximo;    // null se não ligar a nenhuma linha

    No(double x, double y) { this.x = x; this.y = y; }

    /** Liga este nó ao seguinte (cria uma linha entre os dois). */
    void ligarA(No outro) {
        this.proximo = outro;
        outro.anterior = this;
    }

    /** Corta a linha entre este nó e o seguinte: ambos os lados passam a null. */
    void desligarDoProximo() {
        if (proximo != null) proximo.anterior = null;
        proximo = null;
    }

    double distanciaA(No o) { return Math.hypot(x - o.x, y - o.y); }

    @Override public String toString() { return "(" + x + ", " + y + ")"; }
}

/** Só estas 3 classes podem existir como Forma. Qualquer outra dá erro de compilação. */
sealed abstract class Forma permits Triangulo, Retangulo, Circulo {
    protected No atual;   // nó de referência (vértice atual / centro)

    No getAtual()    { return atual; }
    No getAnterior() { return atual == null ? null : atual.anterior; }
    No getProximo()  { return atual == null ? null : atual.proximo; }

    /** Avança para o nó seguinte; se for null, mantém-se onde está. */
    boolean avancar() {
        if (getProximo() == null) return false;
        atual = atual.proximo;
        return true;
    }

    /** Recua para o nó anterior; se for null, mantém-se onde está. */
    boolean recuar() {
        if (getAnterior() == null) return false;
        atual = atual.anterior;
        return true;
    }

    /** Lista os nós percorrendo a lista ligada, parando se encontrar null ou fechar o loop. */
    List<No> nos() {
        List<No> lista = new ArrayList<>();
        No n = atual;
        while (n != null && !lista.contains(n)) {
            lista.add(n);
            n = n.proximo;
        }
        return lista;
    }

    abstract boolean valida();
    abstract double area();
    abstract double perimetro();
}

final class Triangulo extends Forma {
    Triangulo(No a, No b, No c) {
        a.ligarA(b); b.ligarA(c); c.ligarA(a);   // loop fechado
        this.atual = a;
        if (!valida()) throw new IllegalArgumentException("Pontos colineares: não formam triângulo.");
    }

    @Override boolean valida() {
        List<No> n = nos();
        if (n.size() != 3 || n.get(2).proximo != n.get(0)) return false;  // loop tem de fechar
        return area() > 0;
    }

    @Override double area() {
        List<No> n = nos();
        if (n.size() != 3) return 0;
        No a = n.get(0), b = n.get(1), c = n.get(2);
        return Math.abs(a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2;
    }

    @Override double perimetro() {
        List<No> n = nos();
        if (n.size() != 3) return 0;
        return n.get(0).distanciaA(n.get(1)) + n.get(1).distanciaA(n.get(2)) + n.get(2).distanciaA(n.get(0));
    }
}

final class Retangulo extends Forma {
    private final double largura, altura;

    Retangulo(double x, double y, double largura, double altura) {
        if (largura <= 0 || altura <= 0) throw new IllegalArgumentException("Dimensões inválidas.");
        this.largura = largura; this.altura = altura;
        No a = new No(x, y), b = new No(x + largura, y),
           c = new No(x + largura, y + altura), d = new No(x, y + altura);
        a.ligarA(b); b.ligarA(c); c.ligarA(d); d.ligarA(a);   // loop fechado
        this.atual = a;
    }

    @Override boolean valida() {
        List<No> n = nos();
        return n.size() == 4 && n.get(3).proximo == n.get(0);
    }

    @Override double area()      { return valida() ? largura * altura : 0; }
    @Override double perimetro() { return valida() ? 2 * (largura + altura) : 0; }
}

/** Círculo não tem cantos: só tem o nó do centro, sem linhas ligadas (anterior e próximo = null). */
final class Circulo extends Forma {
    private final double raio;

    Circulo(double cx, double cy, double raio) {
        if (raio <= 0) throw new IllegalArgumentException("Raio inválido.");
        this.raio = raio;
        this.atual = new No(cx, cy);   // anterior = null, proximo = null
    }

    @Override boolean valida()   { return atual != null; }
    @Override double area()      { return Math.PI * raio * raio; }
    @Override double perimetro() { return 2 * Math.PI * raio; }
}

public class Main {
    public static void main(String[] args) {
        Triangulo t = new Triangulo(new No(0, 0), new No(4, 0), new No(0, 3));
        Retangulo r = new Retangulo(0, 0, 5, 2);
        Circulo   c = new Circulo(1, 1, 3);

        for (Forma f : new Forma[]{t, r, c}) {
            System.out.printf("%s | nós=%s | área=%.2f | perímetro=%.2f | válida=%b%n",
                    f.getClass().getSimpleName(), f.nos(), f.area(), f.perimetro(), f.valida());
            System.out.println("  atual=" + f.getAtual()
                    + " anterior=" + f.getAnterior()
                    + " próximo=" + f.getProximo());
        }

        // Cortar uma linha do retângulo: os nós que perdem a ligação ficam null
        System.out.println("\n-- A cortar a linha entre o 1.º e o 2.º nó do retângulo --");
        r.getAtual().desligarDoProximo();
        System.out.println("atual=" + r.getAtual()
                + " anterior=" + r.getAnterior()
                + " próximo=" + r.getProximo());   // próximo = null
        System.out.println("Retângulo válido? " + r.valida());  // false: o loop já não fecha
    }
}

// class Hexagono extends Forma { }   // ERRO DE COMPILAÇÃO: Forma é sealed e só permite as 3 formas