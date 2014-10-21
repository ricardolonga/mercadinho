package br.com.ricardolonga.mercadinho.teladetalhes;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import br.com.ricardolonga.mercadinho.R;
import br.com.ricardolonga.mercadinho.dao.ItemDao;
import br.com.ricardolonga.mercadinho.entity.Item;
import br.com.ricardolonga.mercadinho.event.ItemPersistidoEvent;
import br.com.ricardolonga.mercadinho.shared.ConnectionManager;
import de.greenrobot.event.EventBus;

@EActivity(R.layout.detalhes_activity)
public class DetalhesActivity extends Activity {

	private Item item;
	
	private ItemDao itemDao;
	
	@ViewById(R.id.idBotaoSalvar)
	Button salvar;
	
	@ViewById(R.id.valorUnitario)
	EditText valorUnitario;
	
	@ViewById(R.id.qtde)
	EditText qtde;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		item = (Item) getIntent().getExtras().get("item");
		
		itemDao = ConnectionManager.getInstance().getDaoSession(this).getItemDao();
	}
	
	@AfterViews
	void aposInjetarOBotao() {
		salvar.setEnabled(false);
	}
	
	@TextChange(R.id.valorUnitario)
	void valorUnitarioAlterado(CharSequence s) {
		boolean enable = s.length() != 0;
		salvar.setEnabled(enable);
	}
	
	@Click(R.id.idBotaoSalvar)
	void salvarClicado() {
		String qtde = this.qtde.getText().toString();
		String valorUnitario = this.valorUnitario.getText().toString();

		if (!qtde.isEmpty() && !valorUnitario.isEmpty()) {
			item.setQuantidade(Integer.valueOf(qtde));
			item.setValorUnitario(Double.valueOf(valorUnitario));
			item.setValorTotal(item.getQuantidade() * item.getValorUnitario());
			atualizarItem();
		}
		
		EventBus.getDefault().post(new ItemPersistidoEvent());
		
		finish();
	}

	@Trace
	void atualizarItem() {
		itemDao.update(item);
	}
	
}
