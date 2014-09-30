package br.com.ricardolonga.mercadinho;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import br.com.ricardolonga.mercadinho.DaoMaster.DevOpenHelper;

public class ItensActivity extends ListActivity {

	private SQLiteDatabase db;

    private EditText editText;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ItemDao itemDao;

    private Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itens_activity);

        /*
         * Conexão
         */
        DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mercadinho-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        itemDao = daoSession.getItemDao();

        /* 
         * Busca
         */
        String titleColumn = ItemDao.Properties.Title.columnName;
        String orderBy = titleColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(itemDao.getTablename(), itemDao.getAllColumns(), null, null, null, null, orderBy);
        String[] from = { titleColumn };
        int[] to = { android.R.id.text1 };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to);
        setListAdapter(adapter);

        editText = (EditText) findViewById(R.id.editItemTitle);
        
        addUiListeners();
    }

    protected void addUiListeners() {
        editText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addItem();
                    return true;
                }
                
                return false;
            }
        });

        final View button = findViewById(R.id.buttonAdd);
        
        button.setEnabled(false);
        
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                button.setEnabled(enable);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void onMyButtonClick(View view) {
        addItem();
    }

    private void addItem() {
        String itemTitle = editText.getText().toString();
        
        editText.setText("");

        Item item = new Item();
        item.setTitle(itemTitle);
        
        itemDao.insert(item);

        cursor.requery();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        itemDao.deleteByKey(id);

        cursor.requery();
    }

	
}
