# realm-book-example
This is a rewrite of a ["Realm tutorial" on Android Hive](http://www.androidhive.info/2016/05/android-working-with-realm-database-replacing-sqlite-core-data). Unfortunately the tutorial is extremely outdated (uses 0.82.1 even though the version 3.1.4 is out!), the code is unstructured (Realm transactions inside a click listener inside a dialog created in a long click listener); and it also misuses Realm quite heavily: 

- using `begin/commitTransaction()` instead of `executeTransaction()`
- calling `refresh()` even though the Realm instance is freshly open
- the transactions are all done on the UI thread
- the Realm instance is never closed

It also uses outdated practices or is just not up-to-date information:

- `refresh()` doesn't even exist anymore, and even when it did, in this use-case it was not needed
- uses a Migration to pre-populate the database, even though `initialData()` exists now
- claims that `null` support for primitives isn't in, even though it was added in 0.83.0
- the code relies on `commitTransaction()` immediately updating the `RealmResults<T>` and calling `adapter.notifyDataSetChanged()` manually, but that's not the case since 0.89.0 which means you need to add a change listener to the `RealmResults<T>` (which `RealmRecyclerViewAdapter` does for you automatically)

------------------------------

So with that in mind, this repository shows how to do these things right:

- uses `executeTransactionAsync()` on the UI thread
- uses `initialData()` to prepopulate the Realm
- uses `RealmManager` class (a bit stub-like because I'll have to make its content not static later) to manage number of open activities
- uses retained fragment to count open activity
- uses retained fragment to store presenter (oh, it actually has a "presenter" instead of just throwing everything in `OnClickListener`s)
- does not use `Application` subclass explicitly because of [Firebase Crash Reporting](https://firebase.google.com/docs/crash/android) for example creating multiple Application instances
- uses `RealmRecyclerViewAdapter` with asynchronous query

So yeah, this is the interesting class:

``` java
public class RealmManager {
    static Realm realm;

    static RealmConfiguration realmConfiguration;

    public static void initializeRealmConfig(Context appContext) {
        if(realmConfiguration == null) {
            setRealmConfiguration(new RealmConfiguration.Builder(appContext).initialData(new RealmInitialData())
                    .deleteRealmIfMigrationNeeded()
                    .build());
        }
    }

    public static void setRealmConfiguration(RealmConfiguration realmConfiguration) {
        RealmManager.realmConfiguration = realmConfiguration;
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    private static int activityCount = 0;

    public static Realm getRealm() {
        return realm;
    }

    public static void incrementCount() {
        if(activityCount == 0) {
            if(realm != null) {
                if(!realm.isClosed()) {
                    realm.close();
                    realm = null;
                }
            }
            realm = Realm.getDefaultInstance();
        }
        activityCount++;
    }

    public static void decrementCount() {
        activityCount--;
        if(activityCount <= 0) {
            activityCount = 0;
            realm.close();
            Realm.compactRealm(realmConfiguration);
            realm = null;
        }
    }
}
```

Which has its `RealmConfiguration` initialized in `Activity.onCreate()`, and the Realm instance itself is opened with `RealmManager.incrementCount()` from the retained fragment's constructor.

``` java
public class BooksScopeListener extends Fragment {
    BooksPresenter booksPresenter;

    public BooksScopeListener() {
        setRetainInstance(true);
        RealmManager.incrementCount();
        booksPresenter = new BooksPresenter();
    }

    @Override
    public void onDestroy() {
        RealmManager.decrementCount();
        super.onDestroy();
    }

    public BooksPresenter getPresenter() {
        return booksPresenter;
    }
}
```

Which is created in the Activity.

``` java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RealmManager.initializeRealmConfig(getApplicationContext());
        super.onCreate(savedInstanceState);
        BooksScopeListener fragment = (BooksScopeListener) getSupportFragmentManager().findFragmentByTag("SCOPE_LISTENER");
        if(fragment == null) {
            fragment = new BooksScopeListener();
            getSupportFragmentManager().beginTransaction().add(fragment, "SCOPE_LISTENER").commit();
        }
        realm = RealmManager.getRealm();
        booksPresenter = fragment.getPresenter();
```

The adapter is set up like this

``` java
recycler.setAdapter(new BooksAdapter(this, realm.where(Book.class).findAllAsync(), booksPresenter));
```
        
Where the adapter is a proper `RealmRecyclerViewAdapter`:

``` java
public class BooksAdapter extends RealmRecyclerViewAdapter<Book, BooksAdapter.BookViewHolder> {
```
        
And the writes are from the UI thread to a background thread using `executeTransactionAsync()`, found in the presenter.

``` java
    Realm realm = RealmManager.getRealm();
    realm.executeTransactionAsync(new Realm.Transaction() {
```
