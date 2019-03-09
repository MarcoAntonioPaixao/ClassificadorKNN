import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    final String FILE_PATH = "/home/marco/Desktop/personal_files/Faculdade/4_ano/aprendizado_maquina/trabalho_1/Ionosphere_Marco.csv";

    List<List<String>> conteudoDB = new ArrayList<>();

    lerArquivo(FILE_PATH, conteudoDB);

    List<Amostra> amostras = new ArrayList<>();

    converterDadosAmostra(amostras, conteudoDB);

    System.out.println("Quantidade de amostras: " + amostras.size());

    for (int i = 0; i < amostras.size(); i++) {
      System.out.println(amostras.get(i).classe);
    }

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
          amostraAtual.amostras.add(Double.parseDouble(conteudoDB.get(i).get(j)));
        } else {
          amostraAtual.classe = conteudoDB.get(i).get(j);
        }
      }
      amostras.add(amostraAtual);
    }

  }

}