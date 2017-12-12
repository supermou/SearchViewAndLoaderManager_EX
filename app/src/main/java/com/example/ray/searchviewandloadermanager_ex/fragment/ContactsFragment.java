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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.ray.searchviewandloadermanager_ex.R;

/**
 * Created by Ray on 2017/12/12.
 */

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

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

        getLoaderManager().initLoader(0, null, this);
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
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_URI, Uri.encode(mCurFilter));       //若有uri filter, 設定uri的filter
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
}
