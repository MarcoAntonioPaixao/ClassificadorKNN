import java.util.List;

class Amostra {
  public List<Double> parametros;
  public String classe = "";

  public Amostra(List<Double> parametros) {
    this.parametros = parametros;
  }

  public Amostra(List<Double> parametros, String classe) {
    this.parametros = parametros;
    this.classe = classe;
  }

  public static void separarPorClasse(List<Amostra> amostras, List<Amostra> amostrasClasseG,
      List<Amostra> amostrasClasseB) {
    for (int i = 0; i < amostras.size(); i++) {
      if (amostras.get(i).classe.equals("b")) {
        amostrasClasseB.add(new Amostra(amostras.get(i).parametros, amostras.get(i).classe));
      } else {
        amostrasClasseG.add(new Amostra(amostras.get(i).parametros, amostras.get(i).classe));
      }
    }
  }
}