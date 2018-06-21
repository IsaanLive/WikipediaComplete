package com.aseanmobile.wikipediamobile.support;

import java.util.ArrayList ;

import android.app.Activity ;
import android.content.Context ;
import android.database.Cursor ;
import android.database.sqlite.SQLiteDatabase ;
import android.util.Log ;

import com.aseanmobile.wikipediamobile.model.WikiData ;

public class DataBase {

	private final String book_mark_column[] = { "id", "title", "url", "time" };
	private final String book_mark_table = "Book_Mark";

	private Context context;
	private String dbName = "Wiki_DB";
	private SQLiteDatabase myDB = null;

	public DataBase(Context con) {

		this.context = con;
		createDataBase();
	}

	public synchronized void createDataBase() {

		try {
			myDB = context.openOrCreateDatabase(dbName, Activity.MODE_WORLD_WRITEABLE, null);
			createTables ( );
		} catch (Exception e) {
			Log.e("Error", "createDataBase " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * @return true if database connection is opened. Else open the connection
	 *         and then return true.
	 */
	public synchronized boolean isOpen() {

		if (myDB != null && myDB.isOpen()) {
			return true;
		} else {
			try {
				myDB = context.openOrCreateDatabase(dbName, Activity.MODE_WORLD_WRITEABLE, null);
				if (myDB != null) {
					return true;
				}
			} catch (Exception e) {
				Log.e("Error", "Exception: isOpen " + e.toString());
				return false;
			}
			return true;
		}
	}

	public synchronized void closeConnection() {

		try {
			if (myDB != null && myDB.isOpen()) {
				myDB.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized boolean createTables() {
		boolean flag = false;
		isOpen();
		try {

			myDB.execSQL("CREATE TABLE IF NOT EXISTS " + book_mark_table + " ( " + book_mark_column[0]
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + book_mark_column[1] + " TEXT," + book_mark_column[2] + " TEXT,"
					+ book_mark_column[3] + " TEXT)");

			flag = true;
		} catch (Exception e) {
			Log.e("Error", "Exception CreateListTable" + e.toString());
		} finally {
			closeConnection();
		}
		return flag;
	}

	/**
	 * @return Delete all the data in Stories Table from database
	 */
	public synchronized boolean deleteAllBookMark() {

		boolean flag = false;
		try {
			
			if (isOpen()) {
				myDB.execSQL("DELETE FROM " + book_mark_table);
				if (Utils.hashListStoriesIds != null) {
					Utils.hashListStoriesIds.clear();
				}
			}
			flag = true;
		} catch (Exception e) {
			flag = false;
			Log.e("Error", "Exception: deleteStoriesTableComplete" + e.toString());
		} finally {
			closeConnection();
		}
		return flag;
	}

	public synchronized WikiData getBookMarkItem(int id) {

		WikiData item = null;
		Cursor c = null;
		try {
			if (isOpen()) {
				c = myDB.rawQuery("SELECT * FROM " + book_mark_table + " WHERE " + book_mark_column[0] + " = " + id + "", null);

				if (c != null && c.getCount() > 0) {
					c.moveToFirst();
					String title = c.getString(c.getColumnIndex(book_mark_column[1]));
					String url = c.getString(c.getColumnIndex(book_mark_column[2]));
					String time = c.getString(c.getColumnIndex(book_mark_column[3]));

					if (title != null) {
						title = title.replace('`', '\'');
					}
					item = new WikiData(title , url, time);
				}
			}
		} catch (Exception e) {
			item = null;
			Log.e("Error", "Exception: getRSSItem, itemID " + e.toString());
		} finally {
			if (c != null) {
				c.deactivate();
				c.close();
			}
			closeConnection();
		}
		return item;
	}

	
	public synchronized ArrayList<WikiData> getBookMarkItems(String textToSearch) {
	    
		ArrayList<WikiData> itemList = null;
		WikiData item = null;
		Cursor c = null;
		try {
			if (isOpen()) {
			    if(textToSearch == null){
			        c = myDB.rawQuery("SELECT * FROM " + book_mark_table + "", null);
			    }else{
			        c = myDB.rawQuery("SELECT * FROM " + book_mark_table + " Where lower("+book_mark_column[1]+") LIKE '%"+textToSearch+"%'", null);
			    }

				if (c != null && c.getCount() > 0) {
					c.moveToFirst();
					itemList = new ArrayList<WikiData>();
					do {
						int id = c.getInt(c.getColumnIndex(book_mark_column[0]));
						String title = c.getString(c.getColumnIndex(book_mark_column[1]));
						String url = c.getString(c.getColumnIndex(book_mark_column[2]));
						String time = c.getString(c.getColumnIndex(book_mark_column[3]));

						if (title != null) {
							title = title.replace('`', '\'');
						}
						item = new WikiData(id , title, url, time);
						itemList.add(item);
					} while (c.moveToNext());
				}
			}
		} catch (Exception e) {
			item = null;
			Log.e("Error", "Exception: getRSSItem, itemID " + e.toString());
		} finally {
			if (c != null) {
				c.deactivate();
				c.close();
			}
			closeConnection();
		}
		return itemList;
	}

	public void addNewBookMark(WikiData newBookMark) {

		try {
			if (isOpen()) {
				String newTitle = newBookMark.getTitle();
				if (newTitle != null && newTitle.length() > 0) {
					newTitle = newBookMark.getTitle().replace('\'', '`');
				}

				myDB.execSQL("INSERT INTO " + book_mark_table + " (" + book_mark_column[1] + "," + book_mark_column[2] + ","
						+ book_mark_column[3] + ") VALUES('" + newTitle + "','" + newBookMark.getUrl() + "','"
						+ newBookMark.getTime() + "')");
			}
		} catch (Exception e) {
			Log.e("Error", "Exception: storeRssItem  Type" + e.toString());
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	public void updateBookMark(WikiData updatedData) {
		try {
			if (isOpen()) {

				String newTitle = updatedData.getTitle();
				if (newTitle != null && newTitle.length() > 0) {
					newTitle = updatedData.getTitle().replace('\'', '`');
				}

				myDB.execSQL("UPDATE " + book_mark_table + " set " + book_mark_column[1] + " = '" + newTitle + "', "
						+ book_mark_column[2] + " = '" + updatedData.getUrl() + "' WHERE " + book_mark_column[0] + " = "
						+ updatedData.getId() + "");
			}
		} catch (Exception e) {
			Log.e("Error", "Exception: storeRssItem  Type" + e.toString());
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	public void deleteBookMark(WikiData deletedBookMark) {
		try {
			Log.i("zacharia","inside delete book mark "+isOpen());
			if (isOpen()) {
				Log.i("zacharia",String.valueOf(deletedBookMark.getId()));
				Log.i("zacharia",deletedBookMark.toString());
				Log.i("zacharia", "DELETE FROM " + book_mark_table + " WHERE " + book_mark_column[0] + " = " + deletedBookMark.getId()
						+ " OR "+book_mark_column[1]+" = '"+deletedBookMark.getTitle()+"'");
				myDB.execSQL("DELETE FROM " + book_mark_table + " WHERE " + book_mark_column[0] + " = " + deletedBookMark.getId()
						+ " OR "+book_mark_column[1]+" = '"+deletedBookMark.getTitle()+"' ");
				Cursor c=myDB.query(book_mark_table, null, null, null, null, null, null);
				Log.i("zacharia",String.valueOf(c.getCount()));
			}
		} catch (Exception e) {
			Log.e("Error", "Exception: storeRssItem  Type" + e.toString());
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

    public WikiData getBookMarkForURL ( String searchURL ) {
        WikiData item = null;
        Cursor c = null;
        try {
            if (isOpen()) {
                c = myDB.rawQuery("SELECT * FROM " + book_mark_table + " WHERE " + book_mark_column[2] + " = '" + searchURL + "'", null);

                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();
                    int integer = c.getInt (c.getColumnIndex(book_mark_column[0]));
                    String title = c.getString(c.getColumnIndex(book_mark_column[1]));
                    String url = c.getString(c.getColumnIndex(book_mark_column[2]));
                    String time = c.getString(c.getColumnIndex(book_mark_column[3]));

                    if (title != null) {
                        title = title.replace('`', '\'');
                    }
                    item = new WikiData(integer , title , url, time);
                }
            }
        } catch (Exception e) {
            item = null;
            Log.e("Error", "Exception: getBookMarkForURL, itemID " + e.toString());
            e.printStackTrace ( );
        } finally {
            if (c != null) {
                c.deactivate();
                c.close();
            }
            closeConnection();
        }
        return item;        
    }
}