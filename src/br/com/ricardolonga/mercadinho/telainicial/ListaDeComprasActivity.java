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
import br.com.ricardolonga.mercadinho.R;
import br.com.ricardolonga.mercadinho.dao.ItemDao;
import br.com.ricardolonga.mercadinho.entity.Categoria;
import br.com.ricardolonga.mercadinho.entity.Item;
import br.com.ricardolonga.mercadinho.shared.ConnectionManager;
import br.com.ricardolonga.mercadinho.shared.DefaultTextWatcher;
import br.com.ricardolonga.mercadinho.shared.MercadinhoApplication;
import br.com.ricardolonga.mercadinho.teladetalhes.DetalhesActivity;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ListaDeComprasActivity extends Activity {

	private EditText nomeDoItem;
	private Spinner comboDeCategorias;
	private ListView listaDeCompras;

	private ItemDao itemDao;

	private List<Item> itensDaLista;
	private Categoria categoriaSelecionada;
	private Item itemClicado;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_de_compras_activity);

		itemDao = ConnectionManager.getInstance().getDaoSession(this).getItemDao();

		/*
		 * UI Binders
		 */
		nomeDoItem = (EditText) findViewById(R.id.nomeItem);

		comboDeCategorias = (Spinner) findViewById(R.id.categoriaCombo);
		final List<Categoria> categorias = ((MercadinhoApplication) getApplication()).categorias;
		ArrayAdapter<Categoria> categoriasArrayAdapter = new ArrayAdapter<Categoria>(this, android.R.layout.simple_spinner_item, categorias);
		categoriasArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		comboDeCategorias.setAdapter(categoriasArrayAdapter);

		listaDeCompras = (ListView) findViewById(R.id.listaDeCompras);
		
		/*
		 * Busca
		 */
		recarregarListaDeCompras();

		/*
		 * UI Listeners
		 */
		addUiListeners();
	}

	protected void addUiListeners() {
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

		final View botaoCadastrar = findViewById(R.id.botaoAdicionar);
		botaoCadastrar.setEnabled(false);
		botaoCadastrar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addItem();
			}
		});

		nomeDoItem.addTextChangedListener(new DefaultTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				boolean enable = s.length() != 0;
				botaoCadastrar.setEnabled(enable);
			}
		});
		
		comboDeCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				categoriaSelecionada = ((MercadinhoApplication) getApplication()).categorias.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		listaDeCompras.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				itemClicado = itensDaLista.get(position);
				Intent irParaDetalhes = new Intent(ListaDeComprasActivity.this, DetalhesActivity.class);
				startActivityForResult(irParaDetalhes, 0);
			}
		});
		
		listaDeCompras.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				itemDao.deleteByKey(id);

				Crouton.makeText(ListaDeComprasActivity.this, "Excluído com sucesso", Style.CONFIRM).show();
				
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
		
		Crouton.makeText(ListaDeComprasActivity.this, "Cadastrado com sucesso", Style.CONFIRM).show();

		recarregarListaDeCompras();

		nomeDoItem.setText("");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null || !data.hasExtra("qtde") || !data.hasExtra("valorUnitario")) {
			return;
		}
		
		itemClicado.setQuantidade(data.getIntExtra("qtde", 1));
		itemClicado.setValorUnitario(data.getDoubleExtra("valorUnitario", 0D));
		itemClicado.setValorTotal(itemClicado.getQuantidade() * itemClicado.getValorUnitario());
		
		itemDao.update(itemClicado);
		
		recarregarListaDeCompras();
	}
	
	private void recarregarListaDeCompras() {
		itensDaLista = itemDao.queryRawCreate(", categoria C WHERE T.ID_CATEGORIA=C._ID ORDER BY C.NOME ASC").list();
		listaDeCompras.setAdapter(new ListaDeComprasAdapter(this, itensDaLista));
	}

}
