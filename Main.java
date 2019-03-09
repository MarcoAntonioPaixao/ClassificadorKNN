import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    final String FILE_PATH = "/home/marco/Desktop/personal_files/Faculdade/4_ano/aprendizado_maquina/trabalho_1/Ionosphere_Marco.csv";
    final int NUM_TESTES = 10;

    List<List<String>> conteudoDB = new ArrayList<>();

    lerArquivo(FILE_PATH, conteudoDB);

    List<Amostra> amostras = new ArrayList<>();

    converterDadosAmostra(amostras, conteudoDB);

    List<Amostra> amostrasClasseG = new LinkedList<>();
    List<Amostra> amostrasClasseB = new LinkedList<>();

    Amostra.separarPorClasse(amostras, amostrasClasseG, amostrasClasseB);

    // System.out.println("Numero de amostras de classe g: " +
    // amostrasClasseG.size());
    // System.out.println("Numero de amostras de classe b: " +
    // amostrasClasseB.size());

    List<Amostra> conjuntoTreino = new LinkedList<>();
    List<Amostra> conjuntoValidacao = new LinkedList<>();
    List<Amostra> conjuntoTeste = new LinkedList<>();

    separarConjuntos(amostrasClasseG, amostrasClasseB, conjuntoTreino, conjuntoValidacao, conjuntoTeste);

    // System.out.println("Tamanho do conjunto de Treino: " +
    // conjuntoTreino.size());
    // System.out.println("Tamanho do conjunto de Validacao: " +
    // conjuntoValidacao.size());
    // System.out.println("Tamanho do conjunto de Teste: " + conjuntoTeste.size());

    int melhorK = definirMelhorK(conjuntoTreino, conjuntoValidacao);

    System.out.println("O melhor K eh: " + melhorK);

  }

  private static void lerArquivo(final String FILE_PATH, List<List<String>> conteudoDB) {
    try (Scanner scanner = new Scanner(new File(FILE_PATH))) {
      while (scanner.hasNextLine()) {
        conteudoDB.add(parseDadosDB(scanner.nextLine()));
      }
    } catch (IOException e) {
      System.out.println("Error reading file: " + e);
    }
  }

  private static List<String> parseDadosDB(String line) {
    final String COMMA_DELIMITER = ",";

    List<String> values = new ArrayList<String>();
    try (Scanner rowScanner = new Scanner(line)) {
      rowScanner.useDelimiter(COMMA_DELIMITER);
      while (rowScanner.hasNext()) {
        values.add(rowScanner.next());
      }
    } catch (Exception e) {
      System.out.println("Error reading file line by line: " + e);
    }
    return values;
  }

  private static void converterDadosAmostra(List<Amostra> amostras, List<List<String>> conteudoDB) {
    final int NUM_AMOSTRAS = conteudoDB.size();
    final int NUM_CAMPOS_AMOSTRA = conteudoDB.get(0).size();

    for (int i = 1; i < NUM_AMOSTRAS; i++) {
      Amostra amostraAtual = new Amostra(new ArrayList<Double>());
      for (int j = 0; j < NUM_CAMPOS_AMOSTRA; j++) {

        if (j < NUM_CAMPOS_AMOSTRA - 1) {
          amostraAtual.parametros.add(Double.parseDouble(conteudoDB.get(i).get(j)));
        } else {
          amostraAtual.classe = conteudoDB.get(i).get(j);
        }
      }
      amostras.add(amostraAtual);
    }

  }

  private static void separarConjuntos(List<Amostra> amostrasClasseG, List<Amostra> amostrasClasseB,
      List<Amostra> conjuntoTreino, List<Amostra> conjuntoValidacao, List<Amostra> conjuntoTeste) {

    separarClasse(amostrasClasseG, conjuntoTreino, conjuntoValidacao, conjuntoTeste);

    separarClasse(amostrasClasseB, conjuntoTreino, conjuntoValidacao, conjuntoTeste);

  }

  private static void separarClasse(List<Amostra> amostrasClasse, List<Amostra> conjuntoTreino,
      List<Amostra> conjuntoValidacao, List<Amostra> conjuntoTeste) {
    final int METADE_CLASSE_G = amostrasClasse.size() / 2;
    final int UM_QUARTO_CLASSE_G = amostrasClasse.size() / 4;
    int limiteMaior;
    int limiteMenor = 0;
    int numAleatorio;

    for (int i = 0; i < METADE_CLASSE_G; i++) {
      limiteMaior = amostrasClasse.size() - 1;
      numAleatorio = (int) (Math.random() * limiteMaior) + limiteMenor;
      Amostra amostraEscolhida = amostrasClasse.get(numAleatorio);
      conjuntoTreino.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
      amostrasClasse.remove(numAleatorio);
    }

    for (int i = 0; i < UM_QUARTO_CLASSE_G; i++) {
      limiteMaior = amostrasClasse.size() - 1;
      numAleatorio = (int) (Math.random() * limiteMaior) + limiteMenor;
      Amostra amostraEscolhida = amostrasClasse.get(numAleatorio);
      conjuntoValidacao.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
      amostrasClasse.remove(numAleatorio);
    }

    final int RESTANTE = amostrasClasseG.size();

    for (int i = 0; i < RESTANTE; i++) {
      Amostra amostraEscolhida = amostrasClasse.get(i);
      conjuntoTeste.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
    }

    amostrasClasse.clear();
  }

  private static int definirMelhorK(List<Amostra> conjuntoTreino, List<Amostra> conjuntoValidacao) {
    int melhorK = 0, eficienciaK = 0, melhorEficienciaK = -1;
    double distancia;
    List<InstanciaProxima> instanciasProximas = new LinkedList<>();
    String classeEscolhida = "";

    for (int kAtual = 1; kAtual <= 19; kAtual += 2) {
      System.out.println("K atual eh: " + kAtual);
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
        classeEscolhida = votoMajoritorio(instanciasProximas);
        instanciasProximas.clear();
        // System.out.println("Classe escolhida: " + classeEscolhida);
        // System.out.println("Classe esperada: " + conjuntoValidacao.get(i).classe);
        if (classeEscolhida.equals(conjuntoValidacao.get(i).classe)) {
          eficienciaK++;
        }
      }
      System.out.println("eficiencia K: " + eficienciaK);
      System.out.println();
      if (eficienciaK > melhorEficienciaK) {
        melhorEficienciaK = eficienciaK;
        melhorK = kAtual;
      }
      eficienciaK = 0;
    }

    return melhorK;
  }

  public static String votoMajoritorio(List<InstanciaProxima> instanciasProximas) {
    int votosParaG = 0, votosParaB = 0;

    for (int i = 0; i < instanciasProximas.size(); i++) {
      if (instanciasProximas.get(i).classe.equals("b")) {
        votosParaB++;
      } else {
        votosParaG++;
      }
    }
    // System.out.println("votos para b: " + votosParaB);
    // System.out.println("votos para g: " + votosParaG);

    if (votosParaB > votosParaG) {
      return "b";
    }
    return "g";
  }

}