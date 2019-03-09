import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    final String FILE_PATH = "/home/marco/Desktop/personal_files/Faculdade/4_ano/aprendizado_maquina/trabalho_1/Ionosphere_Marco.csv";

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

    // System.out.println("num de amostras no arquivo: " + NUM_AMOSTRAS);
    // System.out.println("Num de campos em cada amostra: " + NUM_CAMPOS_AMOSTRA);

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

    separarClasseG(amostrasClasseG, conjuntoTreino, conjuntoValidacao, conjuntoTeste);

    separarClasseB(amostrasClasseB, conjuntoTreino, conjuntoValidacao, conjuntoTeste);

    System.out.println("Tamanho do conjunto de Treino: " + conjuntoTreino.size());
    System.out.println("Tamanho do conjunto de Validacao: " + conjuntoValidacao.size());
    System.out.println("Tamanho do conjunto de Teste: " + conjuntoTeste.size());

  }

  private static void separarClasseG(List<Amostra> amostrasClasseG, List<Amostra> conjuntoTreino,
      List<Amostra> conjuntoValidacao, List<Amostra> conjuntoTeste) {
    final int METADE_CLASSE_G = amostrasClasseG.size() / 2;
    final int UM_QUARTO_CLASSE_G = amostrasClasseG.size() / 4;
    int limiteMaior;
    int limiteMenor = 0;
    int numAleatorio;

    for (int i = 0; i < METADE_CLASSE_G; i++) {
      limiteMaior = amostrasClasseG.size() - 1;
      numAleatorio = (int) (Math.random() * limiteMaior) + limiteMenor;
      Amostra amostraEscolhida = amostrasClasseG.get(numAleatorio);
      conjuntoTreino.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
      amostrasClasseG.remove(numAleatorio);
    }

    for (int i = 0; i < UM_QUARTO_CLASSE_G; i++) {
      limiteMaior = amostrasClasseG.size() - 1;
      numAleatorio = (int) (Math.random() * limiteMaior) + limiteMenor;
      Amostra amostraEscolhida = amostrasClasseG.get(numAleatorio);
      conjuntoValidacao.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
      amostrasClasseG.remove(numAleatorio);
    }

    final int RESTANTE = amostrasClasseG.size();

    for (int i = 0; i < RESTANTE; i++) {
      Amostra amostraEscolhida = amostrasClasseG.get(i);
      conjuntoTeste.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
    }

    amostrasClasseG.clear();
  }

  private static void separarClasseB(List<Amostra> amostrasClasseB, List<Amostra> conjuntoTreino,
      List<Amostra> conjuntoValidacao, List<Amostra> conjuntoTeste) {
    final int METADE_CLASSE_B = amostrasClasseB.size() / 2;
    final int UM_QUARTO_CLASSE_B = amostrasClasseB.size() / 4;
    int limiteMaior;
    int limiteMenor = 0;
    int numAleatorio;

    for (int i = 0; i < METADE_CLASSE_B; i++) {
      limiteMaior = amostrasClasseB.size() - 1;
      numAleatorio = (int) (Math.random() * limiteMaior) + limiteMenor;
      Amostra amostraEscolhida = amostrasClasseB.get(numAleatorio);
      conjuntoTreino.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
      amostrasClasseB.remove(numAleatorio);
    }

    for (int i = 0; i < UM_QUARTO_CLASSE_B; i++) {
      limiteMaior = amostrasClasseB.size() - 1;
      numAleatorio = (int) (Math.random() * limiteMaior) + limiteMenor;
      Amostra amostraEscolhida = amostrasClasseB.get(numAleatorio);
      conjuntoValidacao.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
      amostrasClasseB.remove(numAleatorio);
    }

    final int RESTANTE = amostrasClasseB.size();

    for (int i = 0; i < RESTANTE; i++) {
      Amostra amostraEscolhida = amostrasClasseB.get(i);
      conjuntoTeste.add(new Amostra(amostraEscolhida.parametros, amostraEscolhida.classe));
    }

    amostrasClasseB.clear();
  }

}