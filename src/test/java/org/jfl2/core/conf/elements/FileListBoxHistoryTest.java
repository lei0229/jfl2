package org.jfl2.core.conf.elements;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 */
public class FileListBoxHistoryTest {

    @Test
    public void testIsFocus_setFocus() throws Exception {
        FileListBoxHistory hist = new FileListBoxHistory();
        assertThat(hist.isFocus(), is(false));
        hist.setFocus(false);
        assertThat(hist.isFocus(), is(false));
        hist.setFocus(true);
        assertThat(hist.isFocus(), is(true));
    }

    @Test
    public void testGetPathHistory() throws Exception {
        FileListBoxHistory hist = new FileListBoxHistory();
        assertThat(hist.getPathHistory().size(), is(0));
    }

    @Test
    public void testSetPathHistory() throws Exception {
        FileListBoxHistory hist = new FileListBoxHistory();
        List<String> list = new ArrayList<>();
        list.add("list 1");
        list.add("list 2");
        hist.setPathHistory(list);
        assertThat(hist.getPathHistory(), is(list));
        assertThat(hist.getPathHistory().size(), is(2));
    }
}
