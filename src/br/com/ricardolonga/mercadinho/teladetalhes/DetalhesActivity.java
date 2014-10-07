package br.com.ricardolonga.mercadinho.teladetalhes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.ricardolonga.mercadinho.R;

public class DetalhesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detalhes_activity);
		
		((Button) findViewById(R.id.idBotaoSalvar)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				
				String qtde = ((EditText) findViewById(R.id.qtde)).getText().toString();
				if (!qtde.isEmpty())
					it.putExtra("qtde", Integer.valueOf(qtde));
				
				String valorUnitario = ((EditText) findViewById(R.id.valorUnitario)).getText().toString();
				if (!valorUnitario.isEmpty())
					it.putExtra("valorUnitario", Double.valueOf(valorUnitario));
				
				setResult(RESULT_OK, it);
				finish();
			}
		});
	}
	
}
