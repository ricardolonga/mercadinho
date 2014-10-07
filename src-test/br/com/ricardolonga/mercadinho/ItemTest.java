package br.com.ricardolonga.mercadinho;

import java.util.Collections;

import br.com.ricardolonga.mercadinho.dao.CategoriaDao;
import br.com.ricardolonga.mercadinho.dao.DaoMaster;
import br.com.ricardolonga.mercadinho.dao.DaoSession;
import br.com.ricardolonga.mercadinho.dao.ItemDao;
import br.com.ricardolonga.mercadinho.entity.Categoria;
import br.com.ricardolonga.mercadinho.entity.Item;
import de.greenrobot.dao.test.AbstractDaoSessionTest;

public class ItemTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

	private CategoriaDao categoriaDao;
	private ItemDao itemDao;

	public ItemTest() {
		super(DaoMaster.class);
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        
        categoriaDao = daoSession.getCategoriaDao();
        itemDao = daoSession.getItemDao();
        
        /*
         * Categorias padrões...
         */
        Categoria padaria = new Categoria(1L, "Padaria");
        Categoria carnes = new Categoria(2L, "Carnes");
        Categoria higienePessoal = new Categoria(3L, "Higiêne pessoal");
        Categoria produtosDeLimpeza = new Categoria(4L, "Produtos de limpeza");
        
        categoriaDao.insertInTx(padaria, carnes, higienePessoal, produtosDeLimpeza);
    }
	
	public void testCriarUmNovoItem() {
		Item pao = new Item();
		
		Categoria padaria = categoriaDao.load(1L);
		
		pao.setNome("Pão");
		pao.setCategoria(padaria);
		
		itemDao.insert(pao);
		
		assertNotNull(pao.getId());
	}
	
	public void testAtualizarQtdeEValores() {
		Item pao = new Item();
		
		Categoria padaria = categoriaDao.load(1L);
		
		pao.setNome("Pão");
		pao.setCategoria(padaria);
		
		itemDao.insert(pao);

		assertNotNull(pao.getId());
		
		pao.setQuantidade(2);
		pao.setValorUnitario(1.55);
		pao.setValorTotal(pao.getQuantidade() * pao.getValorUnitario());
		
		itemDao.update(pao);
		
		Item paoAtualizado = itemDao.load(pao.getId());
		
		assertEquals(Integer.valueOf(2), paoAtualizado.getQuantidade());
		assertEquals(1.55, paoAtualizado.getValorUnitario());
		assertEquals(3.10, paoAtualizado.getValorTotal());
	}
	
	public void testExcluirUmItem() {
		Item pao = new Item();
		
		Categoria padaria = categoriaDao.load(1L);
		
		pao.setNome("Pão");
		pao.setCategoria(padaria);
		
		itemDao.insert(pao);

		assertNotNull(pao.getId());
		
		itemDao.delete(pao);
		
		assertEquals(Collections.emptyList(), itemDao.loadAll());
	}

}
