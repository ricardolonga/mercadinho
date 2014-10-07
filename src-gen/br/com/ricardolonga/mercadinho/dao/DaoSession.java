package br.com.ricardolonga.mercadinho.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;
import br.com.ricardolonga.mercadinho.dao.CategoriaDao;
import br.com.ricardolonga.mercadinho.dao.ItemDao;
import br.com.ricardolonga.mercadinho.entity.Categoria;
import br.com.ricardolonga.mercadinho.entity.Item;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig categoriaDaoConfig;
    private final DaoConfig itemDaoConfig;

    private final CategoriaDao categoriaDao;
    private final ItemDao itemDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        categoriaDaoConfig = daoConfigMap.get(CategoriaDao.class).clone();
        categoriaDaoConfig.initIdentityScope(type);

        itemDaoConfig = daoConfigMap.get(ItemDao.class).clone();
        itemDaoConfig.initIdentityScope(type);

        categoriaDao = new CategoriaDao(categoriaDaoConfig, this);
        itemDao = new ItemDao(itemDaoConfig, this);

        registerDao(Categoria.class, categoriaDao);
        registerDao(Item.class, itemDao);
    }
    
    public void clear() {
        categoriaDaoConfig.getIdentityScope().clear();
        itemDaoConfig.getIdentityScope().clear();
    }

    public CategoriaDao getCategoriaDao() {
        return categoriaDao;
    }

    public ItemDao getItemDao() {
        return itemDao;
    }

}
