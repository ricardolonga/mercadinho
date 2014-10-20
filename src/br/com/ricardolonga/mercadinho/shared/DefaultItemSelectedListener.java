package br.com.ricardolonga.mercadinho.shared;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public abstract class DefaultItemSelectedListener implements OnItemSelectedListener {

	@Override
	public abstract void onItemSelected(AdapterView<?> parent, View view, int position, long id);

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
