package br.com.ricardolonga.mercadinho.telainicial;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import br.com.ricardolonga.mercadinho.R;
import br.com.ricardolonga.mercadinho.dao.ItemDao;
import br.com.ricardolonga.mercadinho.entity.Categoria;
import br.com.ricardolonga.mercadinho.entity.Item;
import br.com.ricardolonga.mercadinho.event.ItemPersistidoEvent;
import br.com.ricardolonga.mercadinho.event.ItemRemovidoEvent;
import br.com.ricardolonga.mercadinho.event.ListaDeComprasActivityIniciadaEvent;
import br.com.ricardolonga.mercadinho.shared.ConnectionManager;
import br.com.ricardolonga.mercadinho.shared.MercadinhoApplication;
import br.com.ricardolonga.mercadinho.teladetalhes.DetalhesActivity_;
import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@EActivity(R.layout.lista_de_compras_activity)
public class ListaDeComprasActivity extends Activity {

	@App
	MercadinhoApplication app;
	
	@ViewById(R.id.nomeItem)
	EditText nomeDoItem;
	
	@ViewById(R.id.categoriaCombo)
	Spinner comboDeCategorias;
	
	@ViewById(R.id.listaDeCompras)
	ListView listaDeCompras;

	@ViewById(R.id.botaoAdicionar)
	Button botaoCadastrar;
	
	private ItemDao itemDao;

	private List<Item> itensDaLista;
	private Categoria categoriaSelecionada;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		EventBus.getDefault().register(this);
		
		itemDao = ConnectionManager.getInstance().getDaoSession(this).getItemDao();
		
		EventBus.getDefault().post(new ListaDeComprasActivityIniciadaEvent());
	}
	
	@AfterViews
	void afterView() {
		botaoCadastrar.setEnabled(false);

		carregarCategorias();
	}
	
	@Trace(tag=MercadinhoApplication.APPTAG, level=Log.DEBUG)
	private void carregarCategorias() {
		final List<Categoria> categorias = app.categorias;
		ArrayAdapter<Categoria> categoriasArrayAdapter = new ArrayAdapter<Categoria>(this, android.R.layout.simple_spinner_item, categorias);
		categoriasArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		comboDeCategorias.setAdapter(categoriasArrayAdapter);
	}
	
	@EditorAction(R.id.nomeItem)
	void acaoDeEdicao(int actionId) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			addItem();
		}
	}
	
	@TextChange(R.id.nomeItem)
	void textoAlterado(CharSequence s) {
		boolean enable = s.length() != 0;
		botaoCadastrar.setEnabled(enable);
	}
	
	@ItemSelect(R.id.categoriaCombo)
	void itemSelecionado(boolean selecionado, int position) {
		categoriaSelecionada = app.categorias.get(position);
	}
	
	@ItemClick(R.id.listaDeCompras)
	void itemClicado(int position) {
		Intent irParaDetalhes = new Intent(ListaDeComprasActivity.this, DetalhesActivity_.class);
		
		irParaDetalhes.putExtra("item", itensDaLista.get(position));
		
		startActivityForResult(irParaDetalhes, 0);
	}
	
	@ItemLongClick(R.id.listaDeCompras)
	void itemClickLongo(Item itemClicado) {
		itemDao.deleteByKey(itemClicado.getId());

		EventBus.getDefault().post(new ItemRemovidoEvent());
	}

	@Click(R.id.botaoAdicionar)
	@Trace(tag=MercadinhoApplication.APPTAG, level=Log.DEBUG)
	void addItem() {
		String itemTitle = nomeDoItem.getText().toString();

		Item item = new Item();
		item.setNome(itemTitle);
		item.setCategoria(categoriaSelecionada);

		itemDao.insert(item);
		
		nomeDoItem.setText("");

		EventBus.getDefault().post(new ItemPersistidoEvent());
	}

	public void onEvent(ItemRemovidoEvent event) {
		Crouton.makeText(this, "Removido com sucesso", Style.CONFIRM).show();
		recarregarListaDeCompras();
	}
	
	public void onEvent(ItemPersistidoEvent event) {
		Crouton.makeText(this, "Salvo com sucesso", Style.CONFIRM).show();
		recarregarListaDeCompras();
	}
	
	public void onEvent(ListaDeComprasActivityIniciadaEvent event) {
		recarregarListaDeCompras();
	}
	
	@Trace(tag=MercadinhoApplication.APPTAG, level=Log.DEBUG)
	void recarregarListaDeCompras() {
		itensDaLista = itemDao.queryRawCreate(", categoria C WHERE T.ID_CATEGORIA=C._ID ORDER BY C.NOME ASC").list();
		listaDeCompras.setAdapter(new ListaDeComprasAdapter(this, itensDaLista));
		
		double total = 0;
		
		for (Item item : itensDaLista) {
			total += item.getValorTotal() == null ? 0 : item.getValorTotal();
		}
		
		((TextView) findViewById(R.id.totalTextView)).setText("Total R$: " + total);
	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

}
