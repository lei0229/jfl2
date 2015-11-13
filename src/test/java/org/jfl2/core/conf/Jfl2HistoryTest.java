package org.jfl2.core.conf;

import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mockit.Expectations;
import mockit.Mocked;
import org.hamcrest.CoreMatchers;
import org.jfl2.fx.controller.Jfl2Controller;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class Jfl2HistoryTest {
    @Mocked
    Jfl2Controller jfl2;
    @Mocked
    Stage stage;

    @Test
    public void testRemoveNull() throws Exception {
        Jfl2History history = Jfl2History.fromXml("<jfl2_history></jfl2_history>", Jfl2History.class);
        history.removeNullProperty();
        assertThat(history.getMainFrameHistory(), notNullValue());
        assertThat(history.getLeftHistory(), notNullValue());
        assertThat(history.getRightHistory(), notNullValue());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testUnrecognizedElement() throws IOException {
        Jfl2History history = Jfl2History.fromXml("<jfl2_history nazoAttr=\"kiti\"><Unrecognized/></jfl2_history>", Jfl2History.class);
        history.removeNullProperty();
        assertThat(history.getMainFrameHistory(), notNullValue());
        assertThat(history.getLeftHistory(), notNullValue());
        assertThat(history.getRightHistory(), notNullValue());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testXml() throws IOException {
        Jfl2History history = Jfl2History.fromXml("<jfl2_history><filelist_left><focus>true</focus><path_history>first</path_history><path_history>second</path_history><path_history>third</path_history></filelist_left></jfl2_history>", Jfl2History.class);
//        Jfl2History history = Jfl2History.fromJson("<jfl2_history></jfl2_history>", Jfl2History.class);
        assertThat(history.getMainFrameHistory(), nullValue());
        assertThat(history.getLeftHistory(), notNullValue());
        assertThat(history.getRightHistory(), nullValue());

        assertThat(history.getLeftHistory().isFocus(), is(true));
        assertThat(history.getLeftHistory().getPathHistory().size(), is(3));
        assertThat(history.getLeftHistory().getPathHistory().get(0), is("first"));
        assertThat(history.getLeftHistory().getPathHistory().get(1), is("second"));
        assertThat(history.getLeftHistory().getPathHistory().get(2), is("third"));

        String xmlResult = "{\"main_frame\":null,\"filelist_left\":{\"focus\":true,\"path_history\":[\"first\",\"second\",\"third\"]},\"filelist_right\":null}";
        assertThat(history.toJson(), is(xmlResult));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testJson() throws IOException {
        String json = "{\"filelist_left\":{\"focus\":\"true\", \"path_history\":[\"first\",\"second\",\"third\"]}}";

        Jfl2History history = Jfl2History.fromJson(json, Jfl2History.class);
        assertThat(history.getMainFrameHistory(), nullValue());
        assertThat(history.getLeftHistory(), notNullValue());
        assertThat(history.getRightHistory(), nullValue());

        assertThat(history.getLeftHistory().isFocus(), is(true));
        assertThat(history.getLeftHistory().getPathHistory().size(), is(3));
        assertThat(history.getLeftHistory().getPathHistory().get(0), is("first"));
        assertThat(history.getLeftHistory().getPathHistory().get(1), is("second"));
        assertThat(history.getLeftHistory().getPathHistory().get(2), is("third"));
        String jsonResult = "{\"main_frame\":null,\"filelist_left\":{\"focus\":true,\"path_history\":[\"first\",\"second\",\"third\"]},\"filelist_right\":null}";
        assertThat(history.toJson(), is(jsonResult));
    }

    @Test
    public void testRemoveNullProperty() throws Exception{
        Jfl2History history = new Jfl2History();
        assertThat(history.getMainFrameHistory(), nullValue());
        assertThat(history.getLeftHistory(), nullValue());
        assertThat(history.getRightHistory(), nullValue());

        history.removeNullProperty();
        assertThat(history.getMainFrameHistory(), notNullValue());
        assertThat(history.getMainFrameHistory().displayX, nullValue());
        assertThat(history.getLeftHistory(), notNullValue());
        assertThat(history.getLeftHistory().getPathHistory(), notNullValue());
        assertThat(history.getRightHistory(), notNullValue());
        assertThat(history.getRightHistory().getPathHistory(), notNullValue());
    }

    /**
     * 終了処理中にExceptionが発生しても外には投げない
     * @throws Exception
     */
    public void exceptionOnQuit() throws Exception{
        Jfl2History history = new Jfl2History();
        new Expectations(){{
            jfl2.getStage();
            result = stage;
            times = 1;
            stage.setOnCloseRequest((EventHandler<WindowEvent>)any);
            times = 1;
        }};

        history.addListener(jfl2);
    }
}
