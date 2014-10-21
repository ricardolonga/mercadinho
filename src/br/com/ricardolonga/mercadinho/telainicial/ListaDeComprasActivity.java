package br.com.ricardolonga.mercadinho.telainicial;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import br.com.ricardolonga.mercadinho.R;
import br.com.ricardolonga.mercadinho.dao.ItemDao;
import br.com.ricardolonga.mercadinho.entity.Categoria;
import br.com.ricardolonga.mercadinho.entity.Item;
import br.com.ricardolonga.mercadinho.shared.ConnectionManager;
import br.com.ricardolonga.mercadinho.shared.DefaultItemSelectedListener;
import br.com.ricardolonga.mercadinho.shared.DefaultTextWatcher;
import br.com.ricardolonga.mercadinho.shared.MercadinhoApplication;
import br.com.ricardolonga.mercadinho.teladetalhes.DetalhesActivity;

public class ListaDeComprasActivity extends Activity {

	private EditText nomeDoItem;
	private Spinner comboDeCategorias;
	private ListView listaDeCompras;

	private ItemDao itemDao;

	private List<Item> itensDaLista;
	private Categoria categoriaSelecionada;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_de_compras_activity);
		
		itemDao = ConnectionManager.getInstance().getDaoSession(this).getItemDao();

		/*
		 * UI Binders
		 */
		uiBinders();

		/*
		 * UI Listeners
		 */
		addUiListeners();
		
		/*
		 * Busca
		 */
		recarregarListaDeCompras();
	}

	private void uiBinders() {
		nomeDoItem = (EditText) findViewById(R.id.nomeItem);

		comboDeCategorias = (Spinner) findViewById(R.id.categoriaCombo);
		final List<Categoria> categorias = ((MercadinhoApplication) getApplication()).categorias;
		ArrayAdapter<Categoria> categoriasArrayAdapter = new ArrayAdapter<Categoria>(this, android.R.layout.simple_spinner_item, categorias);
		categoriasArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		comboDeCategorias.setAdapter(categoriasArrayAdapter);

		listaDeCompras = (ListView) findViewById(R.id.listaDeCompras);
	}

	protected void addUiListeners() {
		final View botaoCadastrar = findViewById(R.id.botaoAdicionar);
		botaoCadastrar.setEnabled(false);
		botaoCadastrar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addItem();
			}
		});

		nomeDoItem.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					addItem();
					return true;
				}

				return false;
			}
		});
		
		nomeDoItem.addTextChangedListener(new DefaultTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				boolean enable = s.length() != 0;
				botaoCadastrar.setEnabled(enable);
			}
		});
		
		comboDeCategorias.setOnItemSelectedListener(new DefaultItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				categoriaSelecionada = ((MercadinhoApplication) getApplication()).categorias.get(position);
			}
		});

		listaDeCompras.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent irParaDetalhes = new Intent(ListaDeComprasActivity.this, DetalhesActivity.class);
				
				irParaDetalhes.putExtra("item", itensDaLista.get(position));
				
				startActivityForResult(irParaDetalhes, 0);
			}
		});
		
		listaDeCompras.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				itemDao.deleteByKey(id);

				Toast.makeText(ListaDeComprasActivity.this, "Removido com sucesso", Toast.LENGTH_SHORT).show();
				
				recarregarListaDeCompras();
				
				return true;
			}
		});
	}

	private void addItem() {
		String itemTitle = nomeDoItem.getText().toString();

		Item item = new Item();
		item.setNome(itemTitle);
		item.setCategoria(categoriaSelecionada);

		itemDao.insert(item);
		
		Toast.makeText(this, "Salvo com sucesso", Toast.LENGTH_SHORT).show();
		
		recarregarListaDeCompras();

		nomeDoItem.setText("");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Toast.makeText(this, "Salvo com sucesso", Toast.LENGTH_SHORT).show();
			
			recarregarListaDeCompras();
		}
	}
	
	private void recarregarListaDeCompras() {
		itensDaLista = itemDao.queryRawCreate(", categoria C WHERE T.ID_CATEGORIA=C._ID ORDER BY C.NOME ASC").list();
		listaDeCompras.setAdapter(new ListaDeComprasAdapter(this, itensDaLista));
		
		double total = 0;
		
		for (Item item : itensDaLista) {
			total += item.getValorTotal() == null ? 0 : item.getValorTotal();
		}
		
		((TextView) findViewById(R.id.totalTextView)).setText("Total R$: " + total);
	}
	
}
