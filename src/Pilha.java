import java.util.NoSuchElementException;

public class Pilha<E> {

	private Celula<E> topo;
	private Celula<E> fundo;

	public Pilha() {

		Celula<E> sentinela = new Celula<E>();
		fundo = sentinela;
		topo = sentinela;

	}

	public boolean vazia() {
		return fundo == topo;
	}

	public void empilhar(E item) {

		topo = new Celula<E>(item, topo);
	}

	public E desempilhar() {

		E desempilhado = consultarTopo();
		topo = topo.getProximo();
		return desempilhado;

	}

	public E consultarTopo() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha!");
		}

		return topo.getItem();

	}

	/**
	 * Cria e devolve uma nova pilha contendo os primeiros numItens elementos
	 * do topo da pilha atual.
	 * 
	 * Os elementos são mantidos na mesma ordem em que estavam na pilha original.
	 * Caso a pilha atual possua menos elementos do que o valor especificado,
	 * uma exceção será lançada.
	 *
	 * @param numItens o número de itens a serem copiados da pilha original.
	 * @return uma nova instância de Pilha<E> contendo os numItens primeiros elementos.
	 * @throws IllegalArgumentException se a pilha não contém numItens elementos.
	 */
	public Pilha<E> subPilha(int numItens) {

		// Conta os elementos disponíveis
		int count = 0;
		Celula<E> atual = topo;
		while (atual != fundo) {
			count++;
			atual = atual.getProximo();
		}

		if (count < numItens) {
			throw new IllegalArgumentException(
				"A pilha possui apenas " + count + " elemento(s); não é possível criar subpilha com " + numItens + ".");
		}

		// Coleta os numItens elementos do topo em um array
		@SuppressWarnings("unchecked")
		E[] elementos = (E[]) new Object[numItens];
		atual = topo;
		for (int i = 0; i < numItens; i++) {
			elementos[i] = atual.getItem();
			atual = atual.getProximo();
		}

		// Empilha em ordem inversa para que o topo original continue no topo da subpilha
		Pilha<E> sub = new Pilha<>();
		for (int i = numItens - 1; i >= 0; i--) {
			sub.empilhar(elementos[i]);
		}

		return sub;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		Celula<E> atual = topo;
		while (atual != fundo) {
			sb.append(atual.getItem());
			if (atual.getProximo() != fundo) {
				sb.append(", ");
			}
			atual = atual.getProximo();
		}
		sb.append("]");
		return sb.toString();
	}
}