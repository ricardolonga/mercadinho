package br.com.ricardolonga.mercadinho.teladetalhes;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.ricardolonga.mercadinho.R;
import br.com.ricardolonga.mercadinho.dao.ItemDao;
import br.com.ricardolonga.mercadinho.entity.Item;
import br.com.ricardolonga.mercadinho.event.ItemPersistidoEvent;
import br.com.ricardolonga.mercadinho.shared.ConnectionManager;
import br.com.ricardolonga.mercadinho.shared.DefaultTextWatcher;
import de.greenrobot.event.EventBus;

public class DetalhesActivity extends Activity {

	private Item item;
	
	private ItemDao itemDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detalhes_activity);
		
		item = (Item) getIntent().getExtras().get("item");
		
		itemDao = ConnectionManager.getInstance().getDaoSession(this).getItemDao();
		
		final Button btn = (Button) findViewById(R.id.idBotaoSalvar);
		btn.setEnabled(false);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String qtde = ((EditText) findViewById(R.id.qtde)).getText().toString();
				String valorUnitario = ((EditText) findViewById(R.id.valorUnitario)).getText().toString();

				if (!qtde.isEmpty() && !valorUnitario.isEmpty()) {
					item.setQuantidade(Integer.valueOf(qtde));
					item.setValorUnitario(Double.valueOf(valorUnitario));
					item.setValorTotal(item.getQuantidade() * item.getValorUnitario());
					itemDao.update(item);
				}
				
				setResult(RESULT_OK);
				
				EventBus.getDefault().post(new ItemPersistidoEvent());
				
				finish();
			}
		});
		
		EditText valorUnitarioEditText = (EditText) findViewById(R.id.valorUnitario);
		valorUnitarioEditText.addTextChangedListener(new DefaultTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				boolean enable = s.length() != 0;
				btn.setEnabled(enable);
			}
		});
	}
	
}
