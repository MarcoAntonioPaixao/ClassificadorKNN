public class InstanciaProxima implements Comparable<InstanciaProxima> {
  double distancia;
  String classe;

  public InstanciaProxima(double distancia, String classe) {
    this.distancia = distancia;
    this.classe = classe;
  }

  public int compareTo(InstanciaProxima outraInstancia) {
    // instancia aparece antes na lista quando for maior
    if (this.distancia > outraInstancia.distancia) {
      return -1;
    }
    if (this.distancia < outraInstancia.distancia) {
      return 1;
    }
    return 0;
  }
}