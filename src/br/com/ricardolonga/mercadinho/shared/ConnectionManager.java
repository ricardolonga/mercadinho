package br.com.ricardolonga.mercadinho.shared;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import br.com.ricardolonga.mercadinho.dao.DaoMaster;
import br.com.ricardolonga.mercadinho.dao.DaoMaster.DevOpenHelper;
import br.com.ricardolonga.mercadinho.dao.DaoSession;

public class ConnectionManager {
	
	private static final ConnectionManager INSTANCE = new ConnectionManager();

	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	
	private ConnectionManager() {}
	
	public static ConnectionManager getInstance() {
		return INSTANCE;
	}
	
	public SQLiteDatabase connect(Context ctx) {
		if (db == null || !db.isOpen()) {
	        DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx, "mercadinho-db", null);
	        db = helper.getWritableDatabase();
		}
		
		return db;
	}
	
	public DaoMaster getDaoMaster(Context ctx) {
		if (daoMaster == null) {
			daoMaster = new DaoMaster(connect(ctx));
		}
		
		return daoMaster;
	}
	
	public DaoSession getDaoSession(Context ctx) {
		if (daoSession == null) {
			daoSession = getDaoMaster(ctx).newSession();
		}
		
		return daoSession;
	}
	
	public void close() {
		if (db == null || !db.isOpen()) {
			return;
		}
		
		db.close();
		
		db = null;
		daoMaster = null;
		daoSession = null;
	}

}
