# Formas Geométricas com Nós (Java)

Programa em Java que cria **Triângulos**, **Retângulos** e **Círculos** usando apenas **nós ligados entre si** (listas ligadas). Não existem classes por forma: a forma nasce da maneira como os nós se ligam, e o programa **proíbe qualquer outra forma**.

## Ideia principal

- Cada **vértice é um nó** (`No`).
- Cada **linha é a ligação** entre um nó e o seu nó seguinte.
- As linhas formam um **loop fechado**: o último nó liga ao primeiro.
- Cada nó conhece três coisas: o **nó anterior**, o **nó atual** (ele próprio) e o **nó próximo**.
- Se um nó **não liga a nenhuma linha**, o seu `anterior` ou `proximo` fica **`null`**.

## Como o programa decide a forma

O programa percorre o loop de nós e analisa o resultado:

| Ligação de nós | Condição | Forma |
|---|---|---|
| 1 nó ligado a si próprio | raio > 0 | Círculo |
| 3 nós em loop fechado | área > 0 (não colineares) | Triângulo |
| 4 nós em loop fechado | 4 ângulos retos | Retângulo |
| Qualquer outra coisa | - | **Proibido** (lança exceção) |

### O que é proibido

- Loop aberto (algum nó com `anterior` ou `proximo` a `null`)
- Mais de 4 nós (por exemplo, um pentágono)
- 3 nós em linha reta (área zero)
- 4 nós que não formam ângulos retos
- Raio definido num vértice

## Estrutura do código

```
Main.java
├── class No       -> vértice: x, y, raio, anterior, proximo
└── class Main     -> criação, identificação e testes
```

**Classe `No`**

| Membro | Função |
|---|---|
| `anterior`, `proximo` | Ligações do nó (`null` se não ligar a nenhuma linha) |
| `ligarA(outro)` | Cria a linha entre este nó e outro |
| `desligarDoProximo()` | Corta a linha: os dois lados ficam `null` |
| `percorrer()` | Percorre o loop; devolve `null` se for aberto ou tiver mais de 4 nós |

**Classe `Main`**

| Método | Função |
|---|---|
| `criarLoop(No... nos)` | Liga os nós em loop fechado |
| `criarCirculo(cx, cy, raio)` | Cria um nó de centro ligado a si próprio |
| `identificar(No)` | Devolve `TRIANGULO`, `RETANGULO` ou `CIRCULO`, ou lança exceção |
| `mostrar(titulo, No)` | Imprime a forma e o anterior / atual / próximo de cada nó |

## Exemplo de utilização

```java
// Triângulo
No t = Main.criarLoop(new No(0, 0), new No(4, 0), new No(0, 3));
System.out.println(Main.identificar(t));   // TRIANGULO

// Retângulo
No r = Main.criarLoop(new No(0, 0), new No(5, 0), new No(5, 2), new No(0, 2));
System.out.println(Main.identificar(r));   // RETANGULO

// Círculo
No c = Main.criarCirculo(1, 1, 3);
System.out.println(Main.identificar(c));   // CIRCULO

// Cortar uma linha: o nó fica null e a forma deixa de ser válida
r.desligarDoProximo();
Main.identificar(r);                       // lança IllegalStateException
```

## Requisitos

- **JDK 17 ou superior** (recomendado: Temurin 21 LTS, em adoptium.net)
- **VS Code** com a extensão **Extension Pack for Java** (Microsoft)

Para confirmar o Java instalado:

```
java -version
```

## Como executar

### No VS Code

1. Abre uma pasta nova e cria o ficheiro `Main.java` na raiz dela.
2. Cola o código.
3. Clica no botão **▶ Run** por cima do `public static void main`.

### No terminal

Na pasta do ficheiro:

```
javac Main.java
java Main
```

## Problemas comuns

| Erro | Solução |
|---|---|
| `The declared package "" does not match the expected package "com.example"` | Acrescenta `package com.example;` no topo do ficheiro, ou move-o para a raiz de uma pasta sem `com/example` |
| Botão Run não aparece | Abre a **pasta** (File > Open Folder), não só o ficheiro, e espera a extensão carregar |
| `java` não é reconhecido | Reinstala o JDK com "Add to PATH" ativo e reinicia o VS Code |
| Erro com o nome do ficheiro | O ficheiro tem de se chamar exatamente `Main.java` |

## Notas de design

- O círculo não tem cantos, por isso é representado por **um único nó** ligado a si próprio, que guarda o raio.
- A validação acontece em `identificar()`: construir nós soltos é permitido, mas **só os 3 padrões de ligação são aceites como forma**.
