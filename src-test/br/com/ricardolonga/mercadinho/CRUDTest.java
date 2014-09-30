package br.com.ricardolonga.mercadinho;

import java.util.List;

import de.greenrobot.dao.test.AbstractDaoSessionTest;

public class CRUDTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

	private CategoryDao categoryDao;
	private ItemDao itemDao;

	public CRUDTest() {
		super(DaoMaster.class);
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        
        categoryDao = daoSession.getCategoryDao();
        itemDao = daoSession.getItemDao();
        
        /*
         * Default categories...
         */
        Category padaria = new Category(1L, "Padaria");
        Category carnes = new Category(2L, "Carnes");
        Category higienePessoal = new Category(3L, "Higiêne pessoal");
        Category produtosDeLimpeza = new Category(4L, "Produtos de limpeza");
        
        categoryDao.insertInTx(padaria, carnes, higienePessoal, produtosDeLimpeza);
    }
	
	public void testCreateACategory() {
		Item pao = new Item(null, "Pão", 1);
		Item queijo = new Item(null, "Queijo", 1);
		itemDao.insertInTx(pao, queijo);
		

		Category padaria = categoryDao.load(1L);
		List<Item> itemList = padaria.getItens();
		itemList.remove(pao);
		
		assertEquals(4, categoryDao.count());
		
		Category renewPadaria = categoryDao.load(1L);
		assertEquals(1, renewPadaria.getItens().size());
	}

}
