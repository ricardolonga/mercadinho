package br.com.ricardolonga.mercadinho.shared;

import java.util.Collections;
import java.util.List;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.Trace;

import android.app.Application;
import br.com.ricardolonga.mercadinho.dao.CategoriaDao;
import br.com.ricardolonga.mercadinho.entity.Categoria;

@EApplication
public class MercadinhoApplication extends Application {
	
	public List<Categoria> categorias = Collections.emptyList();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		carregarCategoriasPadroes();
	}

	@Trace
	void carregarCategoriasPadroes() {
		CategoriaDao categoriaDao = ConnectionManager.getInstance().getDaoSession(getApplicationContext()).getCategoriaDao();
		
		categorias = categoriaDao.loadAll();
		
		if (categorias.isEmpty()) {
	        categorias.add(new Categoria(1L, "Padaria"));
	        categorias.add(new Categoria(2L, "Carnes"));
	        categorias.add(new Categoria(3L, "Higiêne pessoal"));
	        categorias.add(new Categoria(4L, "Produtos de limpeza"));
	        categorias.add(new Categoria(5L, "Outros"));
	        
	        categoriaDao.insertInTx(categorias);
		}
	}

}
