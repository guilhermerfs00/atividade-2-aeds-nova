import java.util.NoSuchElementException;

public class Fila<E> {

    private Celula<E> inicio;
    private Celula<E> fim;

    public Fila() {
        Celula<E> sentinela = new Celula<>();
        inicio = sentinela;
        fim = sentinela;
    }

    public boolean vazia() {
        return inicio == fim;
    }

    public void enfileirar(E item) {
        fim.setProximo(new Celula<>(item));
        fim = fim.getProximo();
    }

    public E desenfileirar() {
        E item = consultarInicio();
        inicio = inicio.getProximo();
        return item;
    }

    public E consultarInicio() {
        if (vazia()) {
            throw new NoSuchElementException("Não há nenhum item na fila!");
        }
        return inicio.getProximo().getItem();
    }

    /**
     * Percorre todos os elementos da fila e conta quantas vezes o elemento
     * informado aparece, usando equals() para comparação.
     *
     * @param elemento O elemento cujas ocorrências serão contadas.
     * @return O número de ocorrências do elemento na fila.
     */
    public int contarOcorrencias(E elemento) {
        int contagem = 0;
        Celula<E> atual = inicio.getProximo();
        while (atual != null) {
            if (elemento.equals(atual.getItem())) {
                contagem++;
            }
            atual = atual.getProximo();
        }
        return contagem;
    }

    /**
     * Desenfileira os primeiros numItens elementos da fila atual, respeitando a
     * ordem de chegada, e os retorna estruturados em uma nova Fila flexível.
     * Caso a fila original possua menos de numItens itens, extrai apenas os
     * disponíveis, esvaziando a fila de origem.
     *
     * @param numItens Número máximo de itens a extrair.
     * @return Uma nova Fila contendo os elementos extraídos na mesma ordem.
     */
    public Fila<E> extrairLote(int numItens) {
        Fila<E> lote = new Fila<>();
        int extraidos = 0;
        while (!vazia() && extraidos < numItens) {
            lote.enfileirar(desenfileirar());
            extraidos++;
        }
        return lote;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Celula<E> atual = inicio.getProximo();
        while (atual != null) {
            sb.append(atual.getItem());
            if (atual.getProximo() != null) {
                sb.append(", ");
            }
            atual = atual.getProximo();
        }
        sb.append("]");
        return sb.toString();
    }
}
