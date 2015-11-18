package org.jfl2.main

import javafx.scene.input.KeyCode
import org.jfl2.fx.controller.Jfl2Controller
import org.jfl2.fx.controller.event.input.trigger.Jfl2MouseButton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.jfl2.fx.controller.event.input.EventTargetComponentType.FILELIST_COMBOBOX
import static org.jfl2.fx.controller.event.input.EventTargetComponentType.FILELIST_TABLEVIEW
import static org.jfl2.fx.controller.event.input.EventTargetComponentType.MENU_ITEMS
import static org.jfl2.fx.controller.event.input.trigger.Jfl2ModifierKey.CTRL

// --------------------------------------------------------------------------------
// Initialization section
// --------------------------------------------------------------------------------
Logger log = LoggerFactory.getLogger(this.getClass());

// jfl2 is bound by controller
Jfl2Controller jfl2 = jfl2;     // For auto complete

jfl2.log.info("start main.groovy");

jfl2.setMaxPathHistory(20)                          // Pathの履歴最大数
jfl2.restorePathHistory()                           // Pathの履歴復帰
jfl2.restoreWindowSize().restoreWindowPosition()    // ウィンドウサイズと位置の復帰


// --------------------------------------------------------------------------------
// Input Event configurations section
// --------------------------------------------------------------------------------

// Configurations (File List)
jfl2.addEvent(FILELIST_TABLEVIEW, { eReg ->
    eReg.add(       KeyCode.RIGHT,      { jfl2.focusRight() });   // Cursor to right tab
    eReg.add(       KeyCode.LEFT,       { jfl2.focusLeft() });    // Cursor to left tab
    eReg.add(       KeyCode.UP,         { jfl2.up() });           // Cursor up
    eReg.add(       KeyCode.DOWN,       { jfl2.down() });         // Cursor down
    eReg.add(       KeyCode.PAGE_DOWN, { jfl2.nextPage() });     // Next page
    eReg.add(       KeyCode.PAGE_UP,   { jfl2.prevPage() });     // Previous page

    eReg.add(       KeyCode.L, { jfl2.focusRight() });   // Cursor to right tab
    eReg.add(       KeyCode.H, { jfl2.focusLeft() });    // Cursor to left tab
    eReg.add(       KeyCode.K, { jfl2.up() });           // Cursor up
    eReg.add(       KeyCode.J, { jfl2.down() });         // Cursor down
    eReg.add(CTRL, KeyCode.F, { jfl2.nextPage() });     // Next page
    eReg.add(CTRL, KeyCode.B, { jfl2.prevPage() });     // Previous page

    eReg.add(       KeyCode.T, { jfl2.cursorToTop() });      // Make cursor line at the top
    eReg.add(       KeyCode.B, { jfl2.cursorToBottom() });   // Make cursor line at the bottom
    eReg.add(       KeyCode.V, { jfl2.cursorToBottom() });   // Make cursor line at the bottom
    eReg.add(       KeyCode.SPACE, { jfl2.mark() });   // Make cursor line at the bottom
    eReg.add(       KeyCode.ENTER, { jfl2.chDir() });    // Execute menu
    eReg.add(       KeyCode.BACK_SPACE, { jfl2.upDir() });    // Execute menu
    //eReg.add( KeyCode.J, KEY_PRESSED, false, {e-> jfl2.down() }); // Cursor down

    eReg.add(CTRL, KeyCode.R, { jfl2.reloadAllCss() });     // Reload css files
    eReg.add(       KeyCode.Q, { jfl2.menu.show('Quit Menu') }); // Quit menu
});

// Configurations (Path Textbox)
jfl2.addEvent(FILELIST_COMBOBOX, { eReg ->
    eReg.add(       KeyCode.ENTER, { jfl2.current.setPath(jfl2.current.getComboBoxValue(), true) });
});

// Key Configurations (About Menu)
jfl2.addEvent(MENU_ITEMS, { eReg ->
    eReg.add(CTRL, KeyCode.R,       { jfl2.reloadAllCss() });  // Reload css files
    eReg.add(       KeyCode.UP,      { jfl2.menu.up() });      // Cursor up
    eReg.add(       KeyCode.DOWN,   { jfl2.menu.down() });     // Cursor down
    eReg.add(       KeyCode.K,       { jfl2.menu.up() });      // Cursor up
    eReg.add(       KeyCode.J,       { jfl2.menu.down() });    // Cursor down
    eReg.add(       KeyCode.ENTER,  { jfl2.menu.enter() });    // Execute menu
    eReg.add(       KeyCode.ESCAPE, { jfl2.menu.hide() });     // Close menu
    eReg.addAnyKeyEvent( { ev-> jfl2.menu.quickSelect(ev);} );  // Quick select

    //eReg.hover(     {log.debug("moved");});   // for debug
    eReg.add(       Jfl2MouseButton.PRIMARY_CLICK, { jfl2.menu.enter() });   // Execute menu
});


// --------------------------------------------------------------------------------
// Custom File System Configurations
// --------------------------------------------------------------------------------
//                   extension , scheme , option
jfl2.addCustomFileScheme( "jar", "jar:", ["create":false, "encoding":"UTF-8"]);
jfl2.addCustomFileScheme( "zip", "jar:", ["create":false, "encoding":"UTF-8"]);

// --------------------------------------------------------------------------------
// Menu section
// --------------------------------------------------------------------------------
//           Title        Description
jfl2.addMenu("Quit Menu", "Do you want to quit?",
    // name, quick key,   operation
    ["Quit",   KeyCode.Q, { jfl2.quit() }],
    ["Cancel", KeyCode.C, {}]
);


// ----------------------------------------
jfl2.getLog().info("finish main.groovy");

