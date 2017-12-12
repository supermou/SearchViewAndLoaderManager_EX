package com.example.ray.searchviewandloadermanager_ex.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.*;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.example.ray.searchviewandloadermanager_ex.R;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

/**
 * Created by Ray on 2017/12/12.
 */

public class ContactsFragment extends Fragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor>{

    String mCurFilter;          //給連絡人使用的uri filter
    ListView listContacts;       //顯示資料的list view
    SimpleCursorAdapter mContactsAdapter;

    //附屬的activity建立完成
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listContacts = getActivity().findViewById(R.id.lstView_contacts);

        //設定一個空的listener給list view, 等loader完成後再把資料傳給adapter
        mContactsAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[]{Contacts.DISPLAY_NAME}, new int[]{android.R.id.text1}, 0);
        listContacts.setAdapter(mContactsAdapter);

        //建立新的loader並給予0為id
        getLoaderManager().initLoader(0, null, this);

        //此fragment具有option menu, 註冊!
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //加入搜尋的menu, 易可以res/menu中製作再inflate
        MenuItem searchItem = menu.add("Search");
        searchItem.setIcon(android.R.drawable.ic_menu_search);
        searchItem.setShowAsAction(SHOW_AS_ACTION_ALWAYS);

        //建立一個search view做搜尋
        SearchView searchView = new SearchView(getActivity());
        searchView.setIconifiedByDefault(true);         //圖示化
        searchView.setOnQueryTextListener(this);        //callback function也implement在此class, 因此傳this

        //當search按鈕按下, 對應的action view為search view
        searchItem.setActionView(searchView);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_main, container, false);
    }

    //開始建立Loader, 讀取資料.
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //這裡以讀取連絡人為範例
        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, Uri.encode(mCurFilter));       //若有uri filter, 設定uri的filter
        } else {
            baseUri = Contacts.CONTENT_URI;         //若沒有uri filer, 設定全部連絡人內容的uir
        }

        //要讀取連絡人的欄位
        String[] projection = new String[]{Contacts._ID, Contacts.DISPLAY_NAME, Contacts.CONTACT_STATUS, Contacts.CONTACT_PRESENCE, Contacts.PHOTO_ID, Contacts.LOOKUP_KEY};

        //SQL的where後面語法. display不為null或空字串, 且至少要有一個電話號碼.
        String whereStr = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND (" + Contacts.HAS_PHONE_NUMBER + "=1) AND (" + Contacts.DISPLAY_NAME + "!=''))";

        //建立一個CursorLoader並回傳, CursorLoader內容包括已經讀取資料的Cursor(最後的參數為一句本地語言排列)
        return new CursorLoader(getActivity(), baseUri, projection, whereStr, null, Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
    }

    //Loader建立完畢, 顯示資料.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mContactsAdapter.swapCursor(cursor);        //資料load完成, 將資料傳給adapter. 用swap更新而不用close. 因為loader會自己決定資料何時被刪除.
    }

    //Loader被destroy, 可能是activity被關掉, 或是被程式強制關掉.
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mContactsAdapter.swapCursor(null);      //被關掉後, 資料swap成null. 知錢的資料會被系統自動刪除. 不要使用close function.
    }

    //SearchView被提交, 忽略.
    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    //SearchView字串改變. 更新Loader內容.
    @Override
    public boolean onQueryTextChange(String s) {
        //重新設定uri filter, 若改變的自傳為null或空字串, 都會回傳true. 若不是空, 將使用者輸入的字串設定為新的uri filter.
        mCurFilter = !TextUtils.isEmpty(s)? s : null;
        //重新restart loader, 會重新執行onCreateLoader, 而此時uri filter不同了, 因此只顯示filter過後的連絡人.
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }
}
