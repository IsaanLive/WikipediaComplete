package com.aseanmobile.wikipediamobile.support ;

import com.aseanmobile.wikipediamobile.model.MyMenuItem ;

public class Constant {
    
    public static final String       PREF_NAME      = "lang" ;
    public static final String       LANG_KEY       = "lang_key" ;
    public static final String       BOOK_MARK_NAME = "Name" ;
    public static final CharSequence UN_MARK        = "Unmark" ;
    public static final CharSequence MARK           = "Bookmark" ;
    
    public static MyMenuItem         ALL_LANGUAGES ;
    public static MyMenuItem         SELECTED_LANGUAGE ;
    
    public static final class GoogleAnalytics {
        
        public static final String VAR_USER_ID                      = "UA-25910315-1" ;
        
        public static final String VAR_APPLICATION                  = "Android_Application" ; //
        public static final String VAR_HARDWARE                     = "Android_Hardware" ;    //
        public static final String VAR_OS                           = "Android_OS" ;          //
                                                                                               
        public static final String CATEGORY_GANERAL_APPLICATION     = "Application Launched" ; //
        public static final String CATEGORY_GANERAL_STARTED         = "Launched" ;            //
        public static final String CATEGORY_GANERAL_FINISHED        = "Exit" ;                //
                                                                                               
        public static final String PAGE_VIEW_MAIN                   = "Main Page" ;           //           
        public static final String PAGE_VIEW_FACEBOOK               = "Facebook" ;             //
        public static final String PAGE_VIEW_ABOUTUS                = "Abous Us" ;            //           
        public static final String PAGE_VIEW_MAP                    = "Map" ;                 //         
        public static final String PAGE_VIEW_CHOSE_LANGUAGE         = "Language" ;            //
                                                                                               
        public static final String CATEGORY_GANERAL                 = "General" ;             //
        public static final String CATEGORY_BOOKMARK                = "Bookmarks" ;           //
        public static final String CATEGORY_SEARCH                  = "Search" ;              //
        public static final String CATEGORY_SETTING                 = "Settings" ;            //
                                                                                               
        public static final String EVENT_BOOK_MARK_ADDED            = "Added" ;               //
        public static final String EVENT_BOOK_MARK_DELETED          = "Delete" ;              //
        public static final String EVENT_BOOK_MARK_EDIT             = "Edit" ;                //
        public static final String EVENT_BOOK_MARK_OPEN             = "Open" ;                //
        public static final String EVENT_BOOK_MARK_SEARCH           = "Search" ;              //
        public static final String EVENT_BOOK_MARK_GET_FROM_DB      = "Database" ;            //
                                                                                               
        public static final String EVENT_SEARCH_DONE                = "Search" ;              //
        public static final String EVENT_SEARCH_RESULT              = "Open Search Result" ;  //
                                                                                               
        public static final String EVENT_SETTING_CHOSE_LANG         = "Language" ;             //
        public static final String EVENT_SETTING_ABOUT_US           = "About Us" ;             //
        public static final String EVENT_SETTING_FACEBOOK           = "Facebook" ;             //
        public static final String EVENT_SETTING_MAP                = "Map" ;                  //
                                                                                               
        public static final String CATEGORY_GANERAL_REFRESH         = "Refresh" ;             //
        public static final String CATEGORY_GANERAL_HOME            = "Home" ;                //
        public static final String CATEGORY_GANERAL_LANGUAGE_CHANGE = "Switch Language" ;     //
                                                                                               
    }
}
