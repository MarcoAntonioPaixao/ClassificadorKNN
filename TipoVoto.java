import java.util.List;

class TipoVoto {
  public static String usarModoVoto(List<InstanciaProxima> instanciasProximas, String modoVoto) {
    String classeEscolhida = "";

    if (modoVoto.equals("1")) {
      classeEscolhida = votoMajoritorio(instanciasProximas);
    } else if (modoVoto.equals("2")) {
      classeEscolhida = votoPonderadoInverso(instanciasProximas);
    } else if (modoVoto.equals("3")) {
      classeEscolhida = votoPonderadoNormalizado(instanciasProximas);
    }

    return classeEscolhida;
  }

  private static String votoMajoritorio(List<InstanciaProxima> instanciasProximas) {
    int votosParaG = 0, votosParaB = 0;

    for (int i = 0; i < instanciasProximas.size(); i++) {
      if (instanciasProximas.get(i).classe.equals("b")) {
        votosParaB++;
      } else {
        votosParaG++;
      }
    }

    if (votosParaB > votosParaG) {
      return "b";
    }
    return "g";
  }

  private static String votoPonderadoInverso(List<InstanciaProxima> instanciasProximas) {
    double votosParaB = 0, votosParaG = 0;
    for (int i = 0; i < instanciasProximas.size(); i++) {
      if (instanciasProximas.get(i).classe.equals("b")) {
        votosParaB += 1 / instanciasProximas.get(i).distancia;
      } else {
        votosParaG += 1 / instanciasProximas.get(i).distancia;
      }
    }

    if (votosParaB > votosParaG) {
      return "b";
    }
    return "g";

  }

  private static String votoPonderadoNormalizado(List<InstanciaProxima> instanciasProximas) {
    double votosParaB = 0, votosParaG = 0;
    final int NUM_INSTANCIAS = instanciasProximas.size();
    for (int i = 0; i < NUM_INSTANCIAS; i++) {
      if (instanciasProximas.get(i).classe.equals("b")) {
        votosParaB += 1 - (instanciasProximas.get(i).distancia
            - instanciasProximas.get(0).distancia / instanciasProximas.get(NUM_INSTANCIAS - 1).distancia
            - instanciasProximas.get(0).distancia);
      } else {
        votosParaG += 1 - (instanciasProximas.get(i).distancia
            - instanciasProximas.get(0).distancia / instanciasProximas.get(NUM_INSTANCIAS - 1).distancia
            - instanciasProximas.get(0).distancia);
      }
    }

    if (votosParaB > votosParaG) {
      return "b";
    }
    return "g";
  }
}