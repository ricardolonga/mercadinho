package br.com.ricardolonga.mercadinho;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class ItemTest extends AbstractDaoTestLongPk<ItemDao, Item> {

    public ItemTest() {
        super(ItemDao.class);
    }

    @Override
    protected Item createEntity(Long key) {
    	Item entity = new Item();
    	
        entity.setId(key);
        entity.setTitle("Cotonete");
        
        return entity;
    }

}