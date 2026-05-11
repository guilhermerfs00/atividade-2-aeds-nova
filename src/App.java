import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Pilha de pedidos (usada para rastrear produtos mais recentes) */
    static Pilha<Pedido> pilhaPedidos = new Pilha<>();

    /** Pilha de produtos mais recentemente pedidos */
    static Pilha<Produto> pilhaProdutosRecentes = new Pilha<>();

    /** Fila de pedidos finalizados aguardando processamento */
    static Fila<Pedido> filaPedidos = new Fila<>();

    /** Nome do arquivo onde os pedidos finalizados são persistidos */
    static final String NOME_ARQUIVO_PEDIDOS = "pedidos.txt";
        
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("7 - Processar lote de pedidos da fila");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null 
     */
    static Produto localizarProduto() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
        int idProduto = lerOpcao("Digite o código identificador do produto desejado: ", Integer.class);
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].hashCode() == idProduto) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        return produto;   
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto localizarProdutoDescricao() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	String descricao;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.equals(descricao)) {
        		produto = produtosCadastrados[i];
        		localizado = true;
    		}
        }
        
        return produto;
    }
    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
    	
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }
    
    /** 
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {
    	
    	int formaPagamento = lerOpcao("Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo", Integer.class);
    	Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
    	Produto produto;
    	int numProdutos;
    	int quantidade;
    	
    	listarTodosOsProdutos();
    	System.out.println("Incluindo produtos no pedido...");
    	numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
        	produto = localizarProdutoDescricao();
        	if (produto == null) {
        		System.out.println("Produto não encontrado");
        		i--;
        	} else {
        		quantidade = lerOpcao("Quantos itens desse produto serão incluídos no pedido?", Integer.class);
        		pedido.incluirProduto(produto, quantidade);
        	}
        }
    	
        return pedido;
    }
    
    /**
     * Finaliza um pedido: armazena na pilha de pedidos (para rastreamento de produtos
     * recentes), enfileira na fila de pedidos (aguardando processamento) e empilha
     * os produtos na pilha de produtos recentes.
     * @param pedido O pedido que deve ser finalizado.
     */
    public static void finalizarPedido(Pedido pedido) {

        if (pedido == null) {
            System.out.println("Nenhum pedido em andamento para finalizar.");
            return;
        }

        pilhaPedidos.empilhar(pedido);
        filaPedidos.enfileirar(pedido);

        // Empilha os produtos do pedido na pilha de produtos recentes
        ItemDePedido[] itens = pedido.getItensDoPedido();
        int numItens = pedido.getQuantItensDePedido();
        for (int i = 0; i < numItens; i++) {
            pilhaProdutosRecentes.empilhar(itens[i].getProduto());
        }

        System.out.println("Pedido finalizado com sucesso!");
        System.out.println(pedido.toString());
    }

    /**
     * Extrai um lote de K pedidos do início da fila (os mais antigos) e os exibe.
     * Usa o método extrairLote da classe Fila.
     */
    public static void processarLoteDePedidos() {

        if (filaPedidos.vazia()) {
            System.out.println("Não há pedidos na fila aguardando processamento.");
            return;
        }

        int k = lerOpcao("Quantos pedidos deseja processar do início da fila (K)?", Integer.class);

        Fila<Pedido> lote = filaPedidos.extrairLote(k);
        cabecalho();
        System.out.println("\n=== LOTE DE PEDIDOS PROCESSADOS ===");
        while (!lote.vazia()) {
            System.out.println(lote.desenfileirar());
            System.out.println();
        }
    }

    public static void listarProdutosPedidosRecentes() {

        if (pilhaProdutosRecentes.vazia()) {
            System.out.println("Nenhum produto pedido ainda.");
            return;
        }

        int k = lerOpcao("Quantos produtos recentes deseja visualizar (K)?", Integer.class);

        try {
            Pilha<Produto> subPilha = pilhaProdutosRecentes.subPilha(k);
            cabecalho();
            System.out.println("\n=== " + k + " PRODUTO(S) MAIS RECENTEMENTE PEDIDO(S) ===");
            while (!subPilha.vazia()) {
                System.out.println(subPilha.desempilhar());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    /**
     * Salva todos os pedidos pendentes na fila em arquivo texto, na ordem de chegada.
     * Os pedidos são extraídos da fila durante o salvamento (ocorre ao encerrar a aplicação).
     * @param nomeArquivo Nome do arquivo de destino.
     */
    static void salvarPedidos(String nomeArquivo) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo, Charset.forName("UTF-8")))) {
            while (!filaPedidos.vazia()) {
                writer.println(filaPedidos.desenfileirar().toString());
                writer.println();
            }
            System.out.println("Pedidos salvos em \"" + nomeArquivo + "\".");
        } catch (IOException e) {
            System.out.println("Erro ao salvar pedidos: " + e.getMessage());
        }
    }
    
	public static void main(String[] args) {
		
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));

        // ============================================================
        // TAREFA 1 — Teste com os dígitos da matrícula 660971 (sem repetição)
        // Dígitos únicos na ordem de aparição: 6, 0, 9, 7, 1
        // ============================================================
        Pilha<Integer> pilhaTeste = new Pilha<>();
        String matricula = "660971";
        boolean[] digitoInserido = new boolean[10];

        System.out.println("=== TAREFA 1: Inserção dos dígitos da matrícula na pilha ===");
        for (char c : matricula.toCharArray()) {
            int digito = c - '0';
            if (!digitoInserido[digito]) {
                pilhaTeste.empilhar(digito);
                digitoInserido[digito] = true;
                System.out.println("Empilhado: " + digito);
            } else {
                System.out.println("Dígito " + digito + " ignorado (repetido)");
            }
        }
        System.out.println("Conteúdo da pilha (topo -> fundo): " + pilhaTeste);

        // Teste de desempilhar
        System.out.println("Desempilhando topo: " + pilhaTeste.desempilhar());
        System.out.println("Pilha após desempilhar:            " + pilhaTeste);
        System.out.println("============================================================\n");
        // ============================================================

        // ============================================================
        // TAREFA 1 — FILA — Teste com os caracteres do nome "Guilherme Roberto"
        // ============================================================
        Fila<Character> filaTeste = new Fila<>();
        String nome = "Guilherme Roberto";

        System.out.println("=== TAREFA 1 FILA: Inserção dos caracteres do nome na fila ===");
        for (char c : nome.toCharArray()) {
            filaTeste.enfileirar(c);
            System.out.println("Enfileirado: '" + c + "'");
        }
        System.out.println("Conteúdo da fila (início -> fim): " + filaTeste);

        // Teste de contarOcorrencias
        char charContagem = 'e';
        System.out.println("Ocorrências de '" + charContagem + "' na fila: "
                + filaTeste.contarOcorrencias(charContagem));

        // Teste de desenfileirar
        System.out.println("Desenfileirando início: '" + filaTeste.desenfileirar() + "'");
        System.out.println("Fila após desenfileirar:           " + filaTeste);
        System.out.println("============================================================\n");
        // ============================================================
        
		nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        
        Pedido pedido = null;
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> finalizarPedido(pedido);
                case 6 -> listarProdutosPedidosRecentes();
                case 7 -> processarLoteDePedidos();
            }
            pausa();
        }while(opcao != 0);

        // Salva os pedidos finalizados ao encerrar a aplicação
        if (!filaPedidos.vazia()) {
            salvarPedidos(NOME_ARQUIVO_PEDIDOS);
        }

        teclado.close();    
    }
}
