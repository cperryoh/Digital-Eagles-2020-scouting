package com.team5667;

import androidx.fragment.app.Fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class tab extends Fragment {

    protected static final String ARG_SECTION_NUMBER = "section_number";
    public String tabText;
    public int layout;
    protected PageViewModel pageViewModel;
    public tab(String tabText, int layout) {
        this.tabText = tabText;
        this.layout=layout;
    }
}