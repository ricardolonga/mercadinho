package br.com.ricardolonga.mercadinho.shared;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class DefaultTextWatcher implements TextWatcher {

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

}
