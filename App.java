import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

class App {

  public static void run(List<Amostra> amostras) {
    final int NUM_TESTES = 10;
    double performanceClassificador = 0;

    List<Amostra> amostrasClasseG = new LinkedList<>();
    List<Amostra> amostrasClasseB = new LinkedList<>();

    System.out.println("Escolha o modo de voto a ser usado: ");
    System.out.println("1 - voto majoritorio");
    System.out.println("2 - voto ponderado inverso");
    System.out.println("3 - voto ponderado normalizado");

    String modoVoto = "";
    Scanner scanner = new Scanner(System.in);
    modoVoto = scanner.nextLine();
    scanner.close();

    int melhorK = 0;

    {
      Amostra.separarPorClasse(amostras, amostrasClasseG, amostrasClasseB);

      List<Amostra> conjuntoTreino = new LinkedList<>();
      List<Amostra> conjuntoValidacao = new LinkedList<>();
      List<Amostra> conjuntoTeste = new LinkedList<>();

      separarConjuntos(amostrasClasseG, amostrasClasseB, conjuntoTreino, conjuntoValidacao, conjuntoTeste);

      melhorK = definirMelhorK(conjuntoTreino, conjuntoValidacao, modoVoto);
    }

    System.out.println("K escolhido: " + melhorK);

    for (int i = 0; i < NUM_TESTES; i++) {
      Amostra.separarPorClasse(amostras, amostrasClasseG, amostrasClasseB);
      double performanceClassificadorTemp = 0;

      List<Amostra> conjuntoTreino = new LinkedList<>();
      List<Amostra> conjuntoValidacao = new LinkedList<>();
      List<Amostra> conjuntoTeste = new LinkedList<>();

      separarConjuntos(amostrasClasseG, amostrasClasseB, conjuntoTreino, conjuntoValidacao, conjuntoTeste);

      performanceClassificadorTemp += avaliarClassificador(conjuntoTreino, conjuntoTeste, melhorK, modoVoto);

      performanceClassificador += performanceClassificadorTemp;

      System.out.println("Performance na iteracao " + (i + 1) + ": " + performanceClassificadorTemp);
    }

    System.out.println("Performance media do classificador: " + performanceClassificador / 10);

  }

  private static void separarConjuntos(List<Amostra> amostrasClasseG, List<Amostra> amostrasClasseB,
      List<Amostra> conjuntoTreino, List<Amostra> conjuntoValidacao, List<Amostra> conjuntoTeste) {

    separarClasse(amostrasClasseG, conjuntoTreino, conjuntoValidacao, conjuntoTeste);

    separarClasse(amostrasClasseB, conjuntoTreino, conjuntoValidacao, conjuntoTeste);

  }

  private static void separarClasse(List<Amostra> amostrasClasse, List<Amostra> conjuntoTreino,
      List<Amostra> conjuntoValidacao, List<Amostra> conjuntoTeste) {
    final int METADE_CLASSE = amostrasClasse.size() / 2;
    final int UM_QUARTO_CLASSE = amostrasClasse.size() / 4;
    int limiteMaior;
    int limiteMenor = 0;
    int numAleatorio;

    for (int i = 0; i < METADE_CLASSE; i++) {
      limiteMaior = amostrasClasse.size() - 1;
      numAleatorio = (int) (Math.random() * limiteMaior) + limiteMenor;
      Amostra amostraEscolhida = amostrasClasse.get(numAleatorio);
      conjuntoTreino.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
      amostrasClasse.remove(numAleatorio);
    }

    for (int i = 0; i < UM_QUARTO_CLASSE; i++) {
      limiteMaior = amostrasClasse.size() - 1;
      numAleatorio = (int) (Math.random() * limiteMaior) + limiteMenor;
      Amostra amostraEscolhida = amostrasClasse.get(numAleatorio);
      conjuntoValidacao.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
      amostrasClasse.remove(numAleatorio);
    }

    final int RESTANTE = amostrasClasse.size();

    for (int i = 0; i < RESTANTE; i++) {
      Amostra amostraEscolhida = amostrasClasse.get(i);
      conjuntoTeste.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
    }

    amostrasClasse.clear();
  }

  private static int definirMelhorK(List<Amostra> conjuntoTreino, List<Amostra> conjuntoValidacao, String modoVoto) {
    int melhorK = 0, eficienciaK = 0, melhorEficienciaK = -1;
    double distancia;
    List<InstanciaProxima> instanciasProximas = new LinkedList<>();
    String classeEscolhida = "";

    for (int kAtual = 1; kAtual <= 19; kAtual += 2) {
      for (int i = 0; i < conjuntoValidacao.size(); i++) {
        for (int j = 0; j < conjuntoTreino.size(); j++) {
          distancia = Amostra.calcularDistanciaEuclidiana(conjuntoValidacao.get(i), conjuntoTreino.get(j));

          if (instanciasProximas.size() < kAtual) {
            instanciasProximas.add(new InstanciaProxima(distancia, conjuntoTreino.get(j).classe));
            Collections.sort(instanciasProximas);
          } else {
            // se a distancia da instancia armazenada mais distante for maior que a
            // distancia ate instancia sendo avaliada
            if (instanciasProximas.get(0).distancia > distancia) {
              instanciasProximas.remove(0);
              instanciasProximas.add(new InstanciaProxima(distancia, conjuntoTreino.get(j).classe));
              Collections.sort(instanciasProximas);
            }
          }

        }
        classeEscolhida = TipoVoto.usarModoVoto(instanciasProximas, modoVoto);

        instanciasProximas.clear();

        if (classeEscolhida.equals(conjuntoValidacao.get(i).classe)) {
          eficienciaK++;
        }
      }
      if (eficienciaK > melhorEficienciaK) {
        melhorEficienciaK = eficienciaK;
        melhorK = kAtual;
      }
      eficienciaK = 0;
    }

    return melhorK;
  }

  private static double avaliarClassificador(List<Amostra> conjuntoTreino, List<Amostra> conjuntoTeste, int melhorK,
      String modoVoto) {
    double distancia;
    List<InstanciaProxima> instanciasProximas = new LinkedList<>();
    String classeEscolhida = "";
    int numAcertos = 0;
    double eficienciaClassificador = 0;

    for (int i = 0; i < conjuntoTeste.size(); i++) {
      for (int j = 0; j < conjuntoTreino.size(); j++) {
        distancia = Amostra.calcularDistanciaEuclidiana(conjuntoTeste.get(i), conjuntoTreino.get(j));

        if (instanciasProximas.size() < melhorK) {
          instanciasProximas.add(new InstanciaProxima(distancia, conjuntoTreino.get(j).classe));
          Collections.sort(instanciasProximas);
        } else {
          // se a distancia da instancia armazenada mais distante for maior que a
          // distancia ate instancia sendo avaliada
          if (instanciasProximas.get(0).distancia > distancia) {
            instanciasProximas.remove(0);
            instanciasProximas.add(new InstanciaProxima(distancia, conjuntoTreino.get(j).classe));
            Collections.sort(instanciasProximas);
          }
        }

      }
      classeEscolhida = TipoVoto.usarModoVoto(instanciasProximas, modoVoto);
      instanciasProximas.clear();
      if (classeEscolhida.equals(conjuntoTeste.get(i).classe)) {
        numAcertos++;
      }
    }

    eficienciaClassificador = (numAcertos / (double) conjuntoTeste.size()) * 100;

    return eficienciaClassificador;
  }
}