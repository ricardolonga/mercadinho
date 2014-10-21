package br.com.ricardolonga.mercadinho.telainicial;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.com.ricardolonga.mercadinho.R;
import br.com.ricardolonga.mercadinho.entity.Item;

public class ListaDeComprasAdapter extends BaseAdapter {

	private Context context;
	private List<Item> itens;

	public ListaDeComprasAdapter(Context context, List<Item> itens) {
		this.context = context;
		this.itens = itens;
	}

	@Override
	public int getCount() {
		return itens.size();
	}

	@Override
	public Object getItem(int position) {
		return itens.get(position);
	}

	@Override
	public long getItemId(int position) {
		return itens.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Item item = itens.get(position);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.item_da_lista, null);

		if (item.getNome() != null) {
			TextView titulo = (TextView) view.findViewById(R.id.titulo);
			titulo.setText(item.getNome());
		}

		if (item.getCategoria() != null) {
			TextView descricao = (TextView) view.findViewById(R.id.descricao);
			descricao.setText(item.getCategoria().getNome());
		}
		
		if (item.getValorTotal() != null) {
			TextView descricao = (TextView) view.findViewById(R.id.total);
			descricao.setText(item.getValorTotal().toString());
		}

		return view;
	}

}
